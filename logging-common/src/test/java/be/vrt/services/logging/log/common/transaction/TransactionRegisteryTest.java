/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.vrt.services.logging.log.common.transaction;

import be.vrt.services.logging.log.common.dto.AbstractTransactionLog;
import java.util.List;
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
	public void init() {
		registery = spy(new TransactionRegistery());
		TransactionRegistery.instance = registery;
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
	public void testRegisterTransactionIdMaxBuffer() {

		registery.bufferSize = 5;
		for (int i = 0; i < 20; i++) {
			registery.registerTransactionId("anId", "aFlowId");
		}
		assertEquals(0, registery.transactionErrorLogs.size());
		assertEquals(0, registery.transactionFailedLogs.size());
		assertEquals(0, registery.transactionLogs.size());
		assertEquals(5, registery.transactionIds.size());
	}

	@Test
	public void testRegister_AbstractTransactionLog() {
		doReturn(AbstractTransactionLog.Type.OK).when(abstractTransactionLog).getStatus();
		TransactionRegistery.register(abstractTransactionLog);

		registery = TransactionRegistery.instance;
		assertEquals(0, registery.transactionErrorLogs.size());
		assertEquals(0, registery.transactionFailedLogs.size());
		assertEquals(1, registery.transactionLogs.size());
		assertEquals(0, registery.transactionIds.size());

	}

	@Test
	public void testRegister_String_String() {

		TransactionRegistery.register("anId", "aFlowId");

		registery = TransactionRegistery.instance;

		assertEquals(0, registery.transactionErrorLogs.size());
		assertEquals(0, registery.transactionFailedLogs.size());
		assertEquals(0, registery.transactionLogs.size());
		assertEquals(1, registery.transactionIds.size());
	}

	@Test
	public void testList() {
		doReturn(AbstractTransactionLog.Type.OK).when(abstractTransactionLog).getStatus();

		TransactionRegistery.register(abstractTransactionLog);
		List<AbstractTransactionLog> l = TransactionRegistery.list();
		TransactionRegistery.register(abstractTransactionLog);

		registery = TransactionRegistery.instance;
		assertEquals(1, l.size());
		assertEquals(2, registery.transactionLogs.size());

	}

	@Test
	public void testListErrors() {
		doReturn(AbstractTransactionLog.Type.ERROR).when(abstractTransactionLog).getStatus();

		TransactionRegistery.register(abstractTransactionLog);
		List<AbstractTransactionLog> l = TransactionRegistery.listErrors();
		TransactionRegistery.register(abstractTransactionLog);

		registery = TransactionRegistery.instance;
		assertEquals(1, l.size());
		assertEquals(2, registery.transactionErrorLogs.size());
	}

	@Test
	public void testListFailures() {
		doReturn(AbstractTransactionLog.Type.FAILED).when(abstractTransactionLog).getStatus();

		TransactionRegistery.register(abstractTransactionLog);
		List<AbstractTransactionLog> l = TransactionRegistery.listFailures();
		TransactionRegistery.register(abstractTransactionLog);

		registery = TransactionRegistery.instance;
		assertEquals(1, l.size());
		assertEquals(2, registery.transactionFailedLogs.size());
	}

	@Test
	public void testListIds() {
		TransactionRegistery.register("anId", "aFlowId");

		registery = TransactionRegistery.instance;
		List<TransactionIdLog> l = TransactionRegistery.listIds();
		TransactionRegistery.register("anId", "aFlowId");

		assertEquals(1, l.size());
		assertEquals(2, registery.transactionIds.size());

	}
	
	@Test
	public void testInstance() {
		assertEquals(registery, TransactionRegistery.instance());
	}

	@Test
	public void testGetSetBufferSize() {
		registery.setBufferSize(5);
		assertEquals(5, registery.getBufferSize());
	}

	@Test
	public void testGetSetBufferSizeIds() {
		registery.setBufferSizeIds(5);
		assertEquals(5, registery.getBufferSizeIds());
	}

}
