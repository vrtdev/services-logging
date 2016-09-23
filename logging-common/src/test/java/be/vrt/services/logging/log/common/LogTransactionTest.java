package be.vrt.services.logging.log.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.MDC;

@RunWith(MockitoJUnitRunner.class)
public class LogTransactionTest {

	public static final String USER = "BOEM... BAH";

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
	public void testParentChildFlow() {
		
		
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

	@Test
	public void registerUser_whenUserIsRegistered_thenIsStoredOnMDC(){
		LogTransaction.resetThread();
		LogTransaction.registerUser(USER);

		Assert.assertEquals(USER, LogTransaction.user());
	}

	@Test
	public void registerUser_whenUserIsEmtpy_thenUserIsUnknown(){
		LogTransaction.resetThread();
		LogTransaction.registerUser(null);

		Assert.assertEquals("UNKNOWN", LogTransaction.user());
	}


	@Test
	public void registerId_whenIdIsEmpty_thenUUIDGeneratedAndStored(){
		LogTransaction.resetThread();
		LogTransaction.registerId(null);

		Assert.assertNotNull(LogTransaction.id());
	}


	@Test
	public void registerId_whenIdIsNotEmpty_thenIdIsStoredOnMDC(){
		LogTransaction.resetThread();
		LogTransaction.registerId("H ALLO");

		List<String> result = LogTransaction.listIds();

		Assert.assertEquals("HALLO", result.get(0));
	}


	@Test
	public void registerId_whenIdIsNotEmptyAndPreviousIdsAreStored_thenIdIsStoredOnMDC(){
		LogTransaction.resetThread();

		Assert.assertTrue(LogTransaction.listIds().isEmpty());

	}

	@Test
	public void tagTransaction_whenTagIsEmpty_thenIsNotAdded(){
		LogTransaction.resetThread();

		LogTransaction.tagTransaction(null);

		Assert.assertTrue(LogTransaction.listTags().isEmpty());
	}

	@Test
	public void tagTransaction_whenTagIsNotEmpty_thenIsAdded(){
		LogTransaction.resetThread();

		LogTransaction.tagTransaction("aTag");

		Assert.assertEquals("aTag", LogTransaction.listTags().get(0));
	}

	@Test
	public void breadCrumb_whenInit_thenCounterIs0(){
		LogTransaction.resetThread();

		Assert.assertEquals(0, LogTransaction.breadCrumb());
	}


	@Test
	public void breadCrumb_whenIncreased_thenResultIsPlusOne(){
		LogTransaction.resetThread();

		LogTransaction.increaseBreadCrumb();

		Assert.assertEquals(1, LogTransaction.breadCrumb());
	}


	@Test
	public void breadCrumb_whenDecreased_thenResultIsMinusOne(){
		LogTransaction.resetThread();

		LogTransaction.increaseBreadCrumb();
		LogTransaction.decreaseBreadCrumb();

		Assert.assertEquals(0, LogTransaction.breadCrumb());
	}

	@Test
	public void startNewTransaction(){
		LogTransaction.resetThread();

		LogTransaction.startNewTransaction();

		Assert.assertNotNull(MDC.get(Constants.TRANSACTION_ID));
	}

	@Test
	public void createFlowId_whenFlowIdGiven_thenUpdatedAndReturned(){
		LogTransaction.resetThread();

		LogTransaction.createFlowId("aFlowId", "Jules Kabas");

		Assert.assertEquals("aFlowId", LogTransaction.flow());
	}



	@Test
	public void createFlowId_whenFlowIdNotGiven_thenNewCreatedWithUser(){
		LogTransaction.resetThread();

		LogTransaction.createFlowId(null, "Jules Kabas");

		Assert.assertTrue(LogTransaction.flow().contains("JulesKabas"));
	}

	@Test
	public void createFlowId_whenFlowIdAndUserNotGiven_thenNewCreatedWithUserNOT_SPECIFIED(){
		LogTransaction.resetThread();

		LogTransaction.createFlowId(null, null);

		Assert.assertTrue(LogTransaction.flow().contains("NOT_SPECIFIED"));
	}
}
