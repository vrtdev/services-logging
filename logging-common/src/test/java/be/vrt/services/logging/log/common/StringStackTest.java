package be.vrt.services.logging.log.common;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StringStackTest {

	@Test
	public void test_nullIfNoContent() {
		StringStack test = new StringStack();
		assertNull(test.peek());
		assertNull(test.pop());
	}

	@Test
	public void test_singleContentCanbeRetieved() {
		StringStack test = new StringStack();
		test.push("aaaa");

		assertEquals("aaaa", test.peek());
		assertEquals("aaaa", test.pop());
		assertNull(test.peek());
		assertNull(test.pop());
	}
	
	
	@Test
	public void test_moreContentCanbeRetieved() {
		StringStack test = new StringStack();
		test.push("bbbb");
		test.push("aaaa");

		assertEquals("aaaa", test.peek());
		assertEquals("aaaa", test.pop());
		assertEquals("bbbb", test.pop());
		assertNull(test.peek());
		assertNull(test.pop());
	}
	
	@Test
	public void test_withContentWithSeperatorAteEnd() {
		StringStack test = new StringStack();
		test.push("bbbb,");
		test.push("aaaa");

		assertEquals("aaaa", test.peek());
		assertEquals("aaaa", test.pop());
		assertEquals("bbbb", test.pop());
		assertNull(test.peek());
		assertNull(test.pop());
	}
	
		@Test
	public void test_withContentWithSeperatorInBegin() {
		StringStack test = new StringStack();
		test.push("bbbb");
		test.push(",aaaa");

		assertEquals("aaaa", test.peek());
		assertEquals("aaaa", test.pop());
		assertEquals("bbbb", test.pop());
		assertNull(test.peek());
		assertNull(test.pop());
	}
	
	public void test_withContentInConstructor() {
		StringStack test = new StringStack("aaaa,bbbb,");

		assertEquals("aaaa", test.peek());
		assertEquals("aaaa", test.pop());
		assertEquals("bbbb", test.pop());
		assertNull(test.peek());
		assertNull(test.pop());
	}
	
	public void test_withContentInConstructorAndSomePush() {
		StringStack test = new StringStack("bbbb,cccc,");
		test.push("aaaa");
		
		assertEquals("aaaa", test.peek());
		assertEquals("aaaa", test.pop());
		assertEquals("bbbb", test.pop());
		assertEquals("cccc", test.pop());
		assertNull(test.peek());
		assertNull(test.pop());
	}
}
