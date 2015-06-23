/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.vrt.services.logging.log.common.transaction;

import java.util.Date;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author lucs
 */
public class TransactionIdLogTest {

	public TransactionIdLogTest() {
	}

	@Test
	public void testGetDate() throws InterruptedException {
		Date date = new Date();
		Thread.sleep(50);
		TransactionIdLog log = new TransactionIdLog("anId", "aFlow");
		Thread.sleep(50);
		assertTrue(log.getDate().after(date));
		assertTrue(log.getDate().before(new Date()));
		
	}

	@Test
	public void testGetTransactionId() {
		TransactionIdLog log = new TransactionIdLog("anId", "aFlow");
		assertEquals("anId", log.getTransactionId());
	}

	@Test
	public void testGetFlowId() {
		TransactionIdLog log = new TransactionIdLog("anId", "aFlow");
		assertEquals("aFlow", log.getFlowId());
	}

}
