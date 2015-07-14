package be.vrt.services.log.exposer.controller;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import be.vrt.services.logging.log.common.transaction.TransactionRegistery;
import be.vrt.services.logging.log.common.transaction.TransactionRegisteryMock;

@RunWith(MockitoJUnitRunner.class)
public class TransactionLogControllerTest {
	
	private TransactionLogController testInstance;
	
	private HttpServletRequest aRequest;
	private HttpServletResponse aResponse;
	private TransactionRegistery registery;
	
	public TransactionLogControllerTest() throws Exception {
		aRequest = mock(HttpServletRequest.class);
		aResponse = mock(HttpServletResponse.class);
		testInstance = new TransactionLogController();
		registery = TransactionRegisteryMock.doSpy();
		
		doReturn(mock(PrintWriter.class)).when(aResponse).getWriter();
	}
	
	@Test
	public void test_givenBaseNoPath_() throws Exception {
		doReturn("").when(aRequest).getPathInfo();
		
		testInstance.doGet(aRequest, aResponse);
		
	}
	
}
