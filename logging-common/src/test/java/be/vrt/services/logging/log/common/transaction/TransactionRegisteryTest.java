/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.vrt.services.logging.log.common.transaction;

import be.vrt.services.logging.log.common.dto.AbstractTransactionLog;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author lucs
 */
@RunWith(MockitoJUnitRunner.class)
public class TransactionRegisteryTest {

	private TransactionRegistery registery;
	private AbstractTransactionLog abstractTransactionLog;
	
	@Before
	public void init(){
		registery = spy(new TransactionRegistery());
		abstractTransactionLog = mock(AbstractTransactionLog.class);
	}
	
	@Test
	public void testRegisterTransactionLocalOK() {
		doReturn(AbstractTransactionLog.Type.OK).when(abstractTransactionLog).getStatus();

		registery.registerTransactionLocal(abstractTransactionLog);
		
		assertEquals(0, registery.transactionErrorLogs.size());
		assertEquals(0, registery.transactionFailedLogs.size());
		assertEquals(1, registery.transactionLogs.size());
		assertEquals(0, registery.transactionIds.size());
	}

	@Test
	public void testRegisterTransactionLocalFAIL() {
		doReturn(AbstractTransactionLog.Type.FAILED).when(abstractTransactionLog).getStatus();

		registery.registerTransactionLocal(abstractTransactionLog);
		
		assertEquals(0, registery.transactionErrorLogs.size());
		assertEquals(1, registery.transactionFailedLogs.size());
		assertEquals(1, registery.transactionLogs.size());
		assertEquals(0, registery.transactionIds.size());
	}

	@Test
	public void testRegisterTransactionLocalERROR() {
		doReturn(AbstractTransactionLog.Type.ERROR).when(abstractTransactionLog).getStatus();

		registery.registerTransactionLocal(abstractTransactionLog);
		
		assertEquals(1, registery.transactionErrorLogs.size());
		assertEquals(0, registery.transactionFailedLogs.size());
		assertEquals(1, registery.transactionLogs.size());
		assertEquals(0, registery.transactionIds.size());
	}

	
	@Test
	public void testRegisterTransactionId() {
		
		registery.registerTransactionId("anId", "aFlowId");
		assertEquals(0, registery.transactionErrorLogs.size());
		assertEquals(0, registery.transactionFailedLogs.size());
		assertEquals(0, registery.transactionLogs.size());
		assertEquals(1, registery.transactionIds.size());
	}

	@Test
	public void testRegister_AbstractTransactionLog() {
	}

	@Test
	public void testRegister_String_String() {
	}

	@Test
	public void testList() {
	}

	@Test
	public void testListErrors() {
	}

	@Test
	public void testListFailures() {
	}

	@Test
	public void testListIds() {
	}

	@Test
	public void testListFlowIds() {
	}

	@Test
	public void testGetBufferSize() {
	}

	@Test
	public void testSetBufferSize() {
	}

	@Test
	public void testGetBufferSizeIds() {
	}

	@Test
	public void testSetBufferSizeIds() {
	}

}
