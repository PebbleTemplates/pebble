package com.mitchellbosecke.pebble.extension.core;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.mitchellbosecke.pebble.template.EvaluationContext;

import static org.mockito.Mockito.when;

/**
 * @author Vladimir Loshchin
 */
@RunWith(MockitoJUnitRunner.class)
public class DateFilterTest {

	@Mock
	private EvaluationContext ctx; 
	
	@Test
	public void testRussianLocale() {
		when(ctx.getLocale()).thenReturn(new Locale("ru", "RU"));
		
		DateFilter filter = new DateFilter();
		Map<String, Object> params = new HashMap<>();
		params.put("_context", ctx);
		params.put("format", "E, MMM d");
		assertEquals("Ср, фев 7", filter.apply(
				new Date(1518004210000l), params).toString());
	}
	
	@Test
	public void testEnglishLocale() {
		when(ctx.getLocale()).thenReturn(new Locale("en", "US"));
		
		DateFilter filter = new DateFilter();
		Map<String, Object> params = new HashMap<>();
		params.put("_context", ctx);
		params.put("format", "E, MMM d");
		assertEquals("Wed, Feb 7", filter.apply(
				new Date(1518004210000l), params).toString());
	}
	
	@Test
	public void testUnitTimestampMsParam() {
		when(ctx.getLocale()).thenReturn(new Locale("en", "US"));
		
		DateFilter filter = new DateFilter();
		Map<String, Object> params = new HashMap<>();
		params.put("_context", ctx);
		params.put("format", "yyyy-MM-dd");
		assertEquals("2018-02-07", filter.apply(
				1518004210000l, params).toString());
	}
}
