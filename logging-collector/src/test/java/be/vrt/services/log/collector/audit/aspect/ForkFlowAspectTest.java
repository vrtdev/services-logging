package be.vrt.services.log.collector.audit.aspect;

import be.vrt.services.logging.api.audit.annotation.LogForkFlow;
import be.vrt.services.logging.log.common.LogTransaction;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

@RunWith(MockitoJUnitRunner.class)
public class ForkFlowAspectTest {

	private String parentFlow;
	private String childFlow;
	private String otherChildFlow;

	@Test
	public void testAspect() {

		parentFlow = LogTransaction.flow();

		MyInterface target = new MyClass();
		MyOtherInterface otherTarget = new MyOtherClass();

		AspectJProxyFactory factory = new AspectJProxyFactory(target);
		AspectJProxyFactory otherFactory = new AspectJProxyFactory(otherTarget);
		ForkFlowAspect aspect = new ForkFlowAspect();
		factory.addAspect(aspect);
		otherFactory.addAspect(aspect);

		MyInterface proxy = factory.getProxy();
		MyOtherInterface otherProxy = otherFactory.getProxy();

		proxy.startingChildProcess();
		proxy.startingProcess();

		otherProxy.setMyClass(proxy);
		otherProxy.startingChildProcess();
		otherProxy.startingProcess();

	}

	private static interface MyInterface {

		void startingProcess();

		void startingChildProcess();

		void someStatement();

	}

	private class MyClass implements MyInterface {

		@LogForkFlow
		@Override
		public void startingChildProcess() {
			System.out.println("another flow");
			assertNotEquals(parentFlow, LogTransaction.flow());
			childFlow = LogTransaction.flow();
			someStatement();

		}

		@Override
		public void someStatement() {
			assertEquals(childFlow, LogTransaction.flow());
		}

		@Override
		public void startingProcess() {
			assertEquals(parentFlow, LogTransaction.flow());
		}

	}

	private static interface MyOtherInterface {

		void startingProcess();

		void startingChildProcess();

		void someStatement();

		void setMyClass(MyInterface myClass);

	}

	private class MyOtherClass implements MyOtherInterface {

		MyInterface myClass;

		public void setMyClass(MyInterface myClass) {
			this.myClass = myClass;
		}

		@LogForkFlow
		@Override
		public void startingChildProcess() {
			System.out.println("another flow");
			assertNotEquals(parentFlow, LogTransaction.flow());
			otherChildFlow = LogTransaction.flow();
			myClass.startingChildProcess();
			someStatement();
		}

		@Override
		public void someStatement() {
			assertEquals(otherChildFlow, LogTransaction.flow());
		}

		@Override
		public void startingProcess() {
			assertEquals(parentFlow, LogTransaction.flow());
		}

	}

}
