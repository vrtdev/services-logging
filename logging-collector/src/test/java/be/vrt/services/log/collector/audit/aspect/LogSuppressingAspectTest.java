package be.vrt.services.log.collector.audit.aspect;

import be.vrt.services.logging.api.audit.annotation.LogSuppress;
import be.vrt.services.logging.api.audit.annotation.LogUnsuppress;
import be.vrt.services.logging.log.common.LogTransaction;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

@RunWith(MockitoJUnitRunner.class)
public class LogSuppressingAspectTest {

	private Logger log = mock(Logger.class);

	@Test
	public void testSomeMethod() {
		MyInterface target = new MyClass();
		AspectJProxyFactory factory = new AspectJProxyFactory(target);
		LogSuppressingAspect aspect = new LogSuppressingAspect();
		factory.addAspect(aspect);
		MyInterface proxy = factory.getProxy();
		
		proxy.aSimpleStatement();
		

	}

	private static interface MyInterface {

		void aSimpleStatement();

		void suppressedStatement();

		void unSuppressedStatement();

	}

	private class MyClass implements MyInterface {

		
		@LogSuppress
		@Override
		public void suppressedStatement() {
			assertTrue(LogTransaction.isTaggedWith("SUPPRESSED"));
			
		}
		
		@LogUnsuppress
		@Override
		public void unSuppressedStatement() {
			assertFalse(LogTransaction.isTaggedWith("SUPPRESSED"));
			
		}
		
		
		@Override
		public void aSimpleStatement() {
			assertFalse(LogTransaction.isTaggedWith("SUPPRESSED"));
			LogTransaction.logSuppress("suppressing");
			suppressedStatement();
			assertTrue(LogTransaction.isTaggedWith("SUPPRESSED"));
			LogTransaction.logUnsuppress("suppressing");
			unSuppressedStatement();
			assertFalse(LogTransaction.isTaggedWith("SUPPRESSED"));
			
			
		}
		
	}

}
