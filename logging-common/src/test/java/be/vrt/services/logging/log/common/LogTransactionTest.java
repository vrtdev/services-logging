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
public class LogTransactionTest {

	@Test
	public void testTags() {

		assertFalse(LogTransaction.isTaggedWith("test1"));

		LogTransaction.tagTransaction("test1");
		assertTrue(LogTransaction.isTaggedWith("test1"));
		assertFalse(LogTransaction.isTaggedWith("test2"));

		LogTransaction.tagTransaction("test2");
		assertTrue(LogTransaction.isTaggedWith("test2"));
		assertFalse(LogTransaction.isTaggedWith("test"));

		LogTransaction.untagTransaction("test2");
		assertTrue(LogTransaction.isTaggedWith("test1"));
		assertFalse(LogTransaction.isTaggedWith("test2"));

		LogTransaction.untagTransaction("test2");
		assertTrue(LogTransaction.isTaggedWith("test1"));
		assertFalse(LogTransaction.isTaggedWith("test2"));

		LogTransaction.untagTransaction("test1");
		assertFalse(LogTransaction.isTaggedWith("test1"));
		assertFalse(LogTransaction.isTaggedWith("test2"));

		LogTransaction.untagTransaction("test");
		assertFalse(LogTransaction.isTaggedWith("test"));
		assertFalse(LogTransaction.isTaggedWith("test1"));
		assertFalse(LogTransaction.isTaggedWith("test2"));

		LogTransaction.tagTransaction("test[][][]1");
		assertTrue(LogTransaction.isTaggedWith("test1"));
		LogTransaction.untagTransaction("test1");
	}

	@Test
	public void testTagsRegex() {
		LogTransaction.tagTransaction("test[][][]1");
		assertTrue(LogTransaction.isTaggedWith("test1"));
		LogTransaction.untagTransaction("test1");
		
		LogTransaction.tagTransaction("test-1");
		assertTrue(LogTransaction.isTaggedWith("test-1"));
		LogTransaction.untagTransaction("test-1");
		
		LogTransaction.tagTransaction("!!!test-1???   ");
		assertTrue(LogTransaction.isTaggedWith("test-1"));
		LogTransaction.untagTransaction("test-1");
		
	}
	
}
