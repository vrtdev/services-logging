package be.vrt.services.log.collector.audit.aspect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;

import org.junit.Test;

import be.vrt.services.log.collector.audit.dto.ErrorDto;
import be.vrt.services.log.collector.transaction.dto.TransactionLogDto;


public class SimpleAuditAspectTest {
	
	private SimpleAuditAspect auditAspect = new SimpleAuditAspect();
	
	@Test
	public void cloneParameter_givenNull_returnsNull() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		Object param = null;
		Object result = auditAspect.cloneArgument(param);
		assertNull(result);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void cloneParameter_givenInteger_returnsInteger() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		Integer param = new Integer(5);
		Object result = auditAspect.cloneArgument(param);
		
		assertTrue(result instanceof HashMap);
		HashMap<String, Object> cloneParam = (HashMap<String, Object>) result;
		assertTrue(cloneParam.get("anInteger") instanceof Integer);
		assertEquals(param, cloneParam.get("anInteger"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void cloneParameter_givenString_returnsString() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		String param = new String("test");
		Object result = auditAspect.cloneArgument(param); 
		
		assertTrue(result instanceof HashMap);
		HashMap<String, Object> cloneParam = (HashMap<String, Object>) result;
		assertTrue(cloneParam.get("aString") instanceof String);
		assertEquals(param, cloneParam.get("aString"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void cloneParameter_givenLong_returnsLong() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		Long param = new Long(5);
		Object result = auditAspect.cloneArgument(param); 
		
		assertTrue(result instanceof HashMap);
		HashMap<String, Object> cloneParam = (HashMap<String, Object>) result;
		assertTrue(cloneParam.get("aLong") instanceof Long);
		assertEquals(param, cloneParam.get("aLong"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void cloneParameter_givenShort_returnsShort() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		Short param = new Short("5");
		Object result = auditAspect.cloneArgument(param); 
		
		assertTrue(result instanceof HashMap);
		HashMap<String, Object> cloneParam = (HashMap<String, Object>) result;
		assertTrue(cloneParam.get("aShort") instanceof Short);
		assertEquals(param, cloneParam.get("aShort"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void cloneParameter_givenDouble_returnsDouble() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		Double param = new Double(5);
		Object result = auditAspect.cloneArgument(param); 
		
		assertTrue(result instanceof HashMap);
		HashMap<String, Object> cloneParam = (HashMap<String, Object>) result;
		assertTrue(cloneParam.get("aDouble") instanceof Double);
		assertEquals(param, cloneParam.get("aDouble"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void cloneParameter_givenBoolean_returnsBoolean() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		Boolean param = Boolean.TRUE;
		Object result = auditAspect.cloneArgument(param); 
		
		assertTrue(result instanceof HashMap);
		HashMap<String, Object> cloneParam = (HashMap<String, Object>) result;
		assertTrue(cloneParam.get("aBoolean") instanceof Boolean);
		assertEquals(param, cloneParam.get("aBoolean"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void cloneParameter_givenDate_returnsDate() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		Date param = new Date();
		Object result = auditAspect.cloneArgument(param); 
		
		assertTrue(result instanceof HashMap);
		HashMap<String, Object> cloneParam = (HashMap<String, Object>) result;
		assertTrue(cloneParam.get("aDate") instanceof Date);
		assertEquals(param, cloneParam.get("aDate"));
		assertEquals(param.getTime(), ((Date) cloneParam.get("aDate")).getTime());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void cloneParameter_givenChar_returnsCharacter() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		char param = 't';
		Object result = auditAspect.cloneArgument(param); 
		
		assertTrue(result instanceof HashMap);
		HashMap<String, Object> cloneParam = (HashMap<String, Object>) result;
		assertTrue(cloneParam.get("aCharacter") instanceof Character);
		assertEquals(param, cloneParam.get("aCharacter"));
	}
	
	@Test
	public void cloneParameter_givenBean_returnsBean() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		TransactionLogDto dto = new TransactionLogDto();
		dto.setDuration(1234L);
		dto.setUser("test");
		Object result = auditAspect.cloneArgument(dto); 
		assertTrue(result instanceof TransactionLogDto);
		assertEquals(dto.toString(), ((TransactionLogDto) result).toString());
	}
	
	@Test
	public void cloneParameter_givenThrowable_returnsThrowable() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		Throwable param = new Throwable("test");
		Object result = auditAspect.cloneArgument(param); 
		assertTrue(result instanceof ErrorDto);
		assertEquals(param.getMessage(), ((ErrorDto) result).getMessage());
	}
}
