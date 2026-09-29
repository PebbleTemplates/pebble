package io.pebbletemplates.pebble.node.expression;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class NumericComparisonExpressionTest extends ExpressionTest {

	@Test
	void testEqualsWithVaryingNumericTypes() throws IOException {
		testAllNumericTypes("{{ left == right }}", "==");
	}

	@Test
	void testGreaterThanWithVaryingNumericTypes() throws IOException {
		testAllNumericTypes("{{ left > right }}", ">");
	}

	@Test
	void testLessThanWithVaryingNumericTypes() throws IOException {
		testAllNumericTypes("{{ left < right }}", "<");
	}

	private void testAllNumericTypes(String template, String operator) throws IOException {
		String shouldBeEqual = "false";
		String shouldBeLessThan = "false";
		String shouldBeGreaterThan = "false";

		switch (operator) {
			case "==": shouldBeEqual = "true"; break;
			case "<": shouldBeLessThan = "true"; break;
			case ">": shouldBeGreaterThan = "true"; break;
		}

		this.testExpression(template, shouldBeEqual, contextOf(579541, 579541));
		this.testExpression(template, shouldBeLessThan, contextOf(579541, 579542));
		this.testExpression(template, shouldBeGreaterThan, contextOf(579542, 579541));

		this.testExpression(template, shouldBeEqual, contextOf(579541.4524, 579541.4524));
		this.testExpression(template, shouldBeLessThan, contextOf(579541.4524, 579541.4525));
		this.testExpression(template, shouldBeGreaterThan, contextOf(579541.4525, 579541.4524));

		this.testExpression(template, shouldBeEqual, contextOf(325055142682428416L, 325055142682428416L));
		this.testExpression(template, shouldBeLessThan, contextOf(325055142682428416L, 325055142682428417L));
		this.testExpression(template, shouldBeGreaterThan, contextOf(325055142682428417L, 325055142682428416L));

		this.testExpression(template, shouldBeEqual, contextOf(new BigInteger("325055142682428416"), new BigInteger("325055142682428416")));
		this.testExpression(template, shouldBeLessThan, contextOf(new BigInteger("325055142682428416"), new BigInteger("325055142682428417")));
		this.testExpression(template, shouldBeGreaterThan, contextOf(new BigInteger("325055142682428417"), new BigInteger("325055142682428416")));

		this.testExpression(template, shouldBeEqual, contextOf(new BigDecimal("325055142682428416.325055142682428416"), new BigDecimal("325055142682428416.325055142682428416")));
		this.testExpression(template, shouldBeLessThan, contextOf(new BigDecimal("325055142682428416.325055142682428416"), new BigDecimal("325055142682428416.325055142682428417")));
		this.testExpression(template, shouldBeGreaterThan, contextOf(new BigDecimal("325055142682428416.325055142682428417"), new BigDecimal("325055142682428416.325055142682428416")));
	}

	private Map<String, Object> contextOf(Object left, Object right) {
		HashMap<String, Object> context = new HashMap(2);
		context.put("left", left);
		context.put("right", right);

		return context;
	}

}
