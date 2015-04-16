package be.vrt.services.log.collector.audit.aspect;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.junit.Test;

import be.vrt.services.log.collector.audit.dto.ErrorDto;
import be.vrt.services.log.collector.transaction.dto.TransactionLogDto;


public class AuditAspectTest {
	
	private AuditAspect auditAspect = new AuditAspect();
	
	@Test
	public void cloneParameter_givenNull_returnsNull() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		Object param = null;
		Object result = auditAspect.cloneParameter(param);
		assertNull(result);
	}
	
	@Test
	public void cloneParameter_givenInteger_returnsInteger() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		Integer param = new Integer(5);
		Object result = auditAspect.cloneParameter(param);
		assertTrue(result instanceof Integer);
		assertEquals(param, (Integer) result);
	}
	
	@Test
	public void cloneParameter_givenString_returnsString() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		String param = new String("test");
		Object result = auditAspect.cloneParameter(param); 
		assertTrue(result instanceof String);
		assertEquals(param, (String) result);
	}
	
	@Test
	public void cloneParameter_givenLong_returnsLong() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		Long param = new Long(5);
		Object result = auditAspect.cloneParameter(param); 
		assertTrue(result instanceof Long);
		assertEquals(param, (Long) result);
	}
	
	@Test
	public void cloneParameter_givenDate_returnsDate() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		Date param = new Date();
		Object result = auditAspect.cloneParameter(param); 
		assertTrue(result instanceof Date);
		assertEquals(param.getTime(), ((Date) result).getTime());
	}
	
	@Test
	public void cloneParameter_givenPrimitiveInt_returnsPrimitiveInt() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		int param = 5;
		Object result = auditAspect.cloneParameter(param); 
		assertEquals(param, result);
	}
	
	@Test
	public void cloneParameter_givenChar_returnsCharacter() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		char param = 't';
		Object result = auditAspect.cloneParameter(param); 
		assertEquals(param, result);
	}
	
	@Test
	public void cloneParameter_givenBean_returnsBean() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		TransactionLogDto dto = new TransactionLogDto();
		dto.setDuration(1234L);
		dto.setUser("test");
		Object result = auditAspect.cloneParameter(dto); 
		assertTrue(result instanceof TransactionLogDto);
		assertEquals(dto.toString(), ((TransactionLogDto) result).toString());
	}
	
	@Test
	public void cloneParameter_givenThrowable_returnsThrowable() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		Throwable param = new Throwable("test");
		Object result = auditAspect.cloneParameter(param); 
		assertTrue(result instanceof ErrorDto);
		assertEquals(param.getMessage(), ((ErrorDto) result).getMessage());
	}
}
