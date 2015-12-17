package be.vrt.services.logging.log.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LogTransactionTest {

	@Test
	public void testTags() {
		LogTransaction.resetThread();
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
		LogTransaction.resetThread();
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

	@Test
	public void testAsSet() {
		LogTransaction.resetThread();
		LogTransaction.tagTransaction("test1");
		LogTransaction.tagTransaction("test1");
		assertTrue(LogTransaction.isTaggedWith("test1"));
		assertEquals(1, LogTransaction.listTags().size());

		LogTransaction.resetThread();
		LogTransaction.registerId("test1");
		LogTransaction.registerId("test1");
		assertEquals(1, LogTransaction.listIds().size());

	}
}
