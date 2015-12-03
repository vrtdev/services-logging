package be.vrt.services.logging.log.common.dto;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author lucs
 */
public class AbstractTransactionLogTest {
	
	private AbstractTransactionLog abstractTransactionLog;

	@Before
	public void init(){
		abstractTransactionLog = new AbstractTransactionLog() {

			@Override
			public String getType() {
				return "";
			}
		};
	}

	@Test
	public void testGetSetType() {
		abstractTransactionLog.setDuration(1L);
		assertEquals(1L, abstractTransactionLog.getDuration());
		
		abstractTransactionLog.setErrorReason("");
		assertEquals("", abstractTransactionLog.getErrorReason());
		
		abstractTransactionLog.setDuration(1L);
		assertEquals(1L, abstractTransactionLog.getDuration());
		
		abstractTransactionLog.setDuration(1L);
		assertEquals(1L, abstractTransactionLog.getDuration());
	}

	
	public class AbstractTransactionLogImpl extends AbstractTransactionLog {

		public String getType() {
			return "";
		}
	}
	
}
