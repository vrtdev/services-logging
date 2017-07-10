package be.vrt.services.logging.log.common;

import ch.qos.logback.classic.Level;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.MDC;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class LogTransactionTest {

    private static final String USER = "BOEM... BAH";
    private static final String DEFAULT_LEVEL = Level.INFO.levelStr;
    private static final List<Level> LEVELS_ALL =
            Arrays.asList(Level.INFO, Level.ALL, Level.DEBUG, Level.ERROR, Level.OFF, Level.TRACE,
                    Level.WARN);

    @Before
    public void setUp() throws Exception {
        LogTransaction.resetThread();
    }

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
    public void testParentChildFlow() {


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

    @Test
    public void testAsSet() {
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
    public void registerUser_whenUserIsRegistered_thenIsStoredOnMDC() {
        LogTransaction.registerUser(USER);

        Assert.assertEquals(USER, LogTransaction.user());
    }

    @Test
    public void registerUser_whenUserIsEmtpy_thenUserIsUnknown() {
        LogTransaction.registerUser(null);

        Assert.assertEquals("UNKNOWN", LogTransaction.user());
    }


    @Test
    public void registerId_whenIdIsEmpty_thenUUIDGeneratedAndStored() {
        LogTransaction.registerId(null);

        Assert.assertNotNull(LogTransaction.id());
    }


    @Test
    public void registerId_whenIdIsNotEmpty_thenIdIsStoredOnMDC() {
        LogTransaction.registerId("H ALLO");

        List<String> result = LogTransaction.listIds();

        Assert.assertEquals("HALLO", result.get(0));
    }


    @Test
    public void registerId_whenIdIsNotEmptyAndPreviousIdsAreStored_thenIdIsStoredOnMDC() {
        Assert.assertTrue(LogTransaction.listIds().isEmpty());
    }

    @Test
    public void tagTransaction_whenTagIsEmpty_thenIsNotAdded() {
        LogTransaction.tagTransaction(null);

        Assert.assertTrue(LogTransaction.listTags().isEmpty());
    }

    @Test
    public void tagTransaction_whenTagIsNotEmpty_thenIsAdded() {
        LogTransaction.tagTransaction("aTag");

        Assert.assertEquals("aTag", LogTransaction.listTags().get(0));
    }

    @Test
    public void breadCrumb_whenInit_thenCounterIs0() {
        Assert.assertEquals(0, LogTransaction.breadCrumb());
    }


    @Test
    public void breadCrumb_whenIncreased_thenResultIsPlusOne() {
        LogTransaction.increaseBreadCrumb();

        Assert.assertEquals(1, LogTransaction.breadCrumb());
    }


    @Test
    public void breadCrumb_whenDecreased_thenResultIsMinusOne() {
        LogTransaction.increaseBreadCrumb();
        LogTransaction.decreaseBreadCrumb();

        Assert.assertEquals(0, LogTransaction.breadCrumb());
    }

    @Test
    public void startNewTransaction() {
        LogTransaction.startNewTransaction();

        Assert.assertNotNull(MDC.get(Constants.TRANSACTION_ID));
    }

    @Test
    public void createFlowId_whenFlowIdGiven_thenUpdatedAndReturned() {
        LogTransaction.createFlowId("aFlowId", "Jules Kabas");

        Assert.assertEquals("aFlowId", LogTransaction.flow());
    }


    @Test
    public void createFlowId_whenFlowIdNotGiven_thenNewCreatedWithUser() {
        LogTransaction.createFlowId(null, "Jules Kabas");

        Assert.assertTrue(LogTransaction.flow().contains("JulesKabas"));
    }

    @Test
    public void createFlowId_whenFlowIdAndUserNotGiven_thenNewCreatedWithUserNOT_SPECIFIED() {
        LogTransaction.createFlowId(null, null);

        Assert.assertTrue(LogTransaction.flow().contains("NOT_SPECIFIED"));
    }

    @Test
    public void getLevel_returnsDefault() throws Exception {
        assertThat(LogTransaction.getLevel(), is(DEFAULT_LEVEL));
    }

    @Test
    public void getLevel_returnsSetValue() throws Exception {
        for (Level level : LEVELS_ALL) {
            LogTransaction.setLevel(level.levelStr);
            assertThat(LogTransaction.getLevel(), is(level.levelStr));
        }
    }

    @Test
    public void setLevel_handlesNull() throws Exception {
            LogTransaction.setLevel(null);
            assertThat(LogTransaction.getLevel(), is(DEFAULT_LEVEL));
    }

    @Test
    public void resetThread_clearsLevel() throws Exception {
        for (Level level : LEVELS_ALL) {
            LogTransaction.setLevel(level.levelStr);
            assertThat(LogTransaction.getLevel(), is(level.levelStr));
            LogTransaction.resetThread();
            assertThat(LogTransaction.getLevel(), is(DEFAULT_LEVEL));
        }
    }
}
