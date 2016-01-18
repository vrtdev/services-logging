package be.vrt.services.log.collector.transaction.job;

import be.vrt.services.log.collector.exception.ErrorException;
import be.vrt.services.log.collector.exception.FailureException;
import be.vrt.services.log.collector.transaction.dto.JobTransactionLogDto;
import be.vrt.services.logging.log.common.dto.AbstractTransactionLog;
import be.vrt.services.logging.log.common.transaction.TransactionRegistery;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JobRegisterTest {

    public static final String RESULT = "snodaard";
    public static final String MESSAGE = "aMessage";

    @Mock
    private JobCallable<String> jobCallable;

    @Test
    public void execute_givenNoExceptionThenResultIsReturned() throws FailureException {
        String result = JobRegister.execute(new JobCallable<String>() {
            @Override
            public String call() throws FailureException {
                return RESULT;
            }
        });

        Assert.assertEquals(RESULT, result);
    }

    @Test(expected = FailureException.class)
    public void execute_givenFailureExceptionThenExceptionIsRethrown() throws FailureException {
        Mockito.when(jobCallable.call()).thenThrow(new FailureException("aMessage"));

        try {
            JobRegister.execute(jobCallable);
        } catch (FailureException fex){
            for (AbstractTransactionLog abstractTransactionLog : TransactionRegistery.list()) {
                if (abstractTransactionLog instanceof JobTransactionLogDto) {
                    JobTransactionLogDto jobTransactionLogDto = (JobTransactionLogDto) abstractTransactionLog;
                    if (jobTransactionLogDto.getErrorReason().contains(MESSAGE)) {
                        throw fex;
                    }
                }
            }
        }
        Assert.fail("The transactionRegistery should contain a JobTransactionLogDto " + MESSAGE + " in its errorReason .");
    }

    @Test(expected = ErrorException.class)
    public void execute_givenErrorExceptionThenExceptionIsRethrown() throws FailureException {
        Mockito.when(jobCallable.call()).thenThrow(new ErrorException("aMessage"));

        try {
            JobRegister.execute(jobCallable);
        } catch (ErrorException eex){
            for (AbstractTransactionLog abstractTransactionLog : TransactionRegistery.list()) {
                if (abstractTransactionLog instanceof JobTransactionLogDto) {
                    JobTransactionLogDto jobTransactionLogDto = (JobTransactionLogDto) abstractTransactionLog;
                    if (jobTransactionLogDto.getErrorReason().contains(MESSAGE)) {
                        throw eex;
                    }
                }
            }
        }
        Assert.fail("The transactionRegistery should contain a JobTransactionLogDto " + MESSAGE + " in its errorReason .");
    }

}