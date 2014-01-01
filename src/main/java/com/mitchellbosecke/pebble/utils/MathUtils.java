package com.mitchellbosecke.pebble.utils;

public class MathUtils {

	private enum Operation {
		ADD, SUBTRACT, MULTIPLICATION, DIVISION, MODULUS
	};

	public static Object add(Object op1, Object op2) {
		return wideningConversionOperation(op1, op2, Operation.ADD);
	}
	public static Object subtract(Object op1, Object op2) {
		return wideningConversionOperation(op1, op2, Operation.SUBTRACT);
	}
	public static Object multiply(Object op1, Object op2) {
		return wideningConversionOperation(op1, op2, Operation.MULTIPLICATION);
	}
	public static Object divide(Object op1, Object op2) {
		return wideningConversionOperation(op1, op2, Operation.DIVISION);
	}
	public static Object mod(Object op1, Object op2) {
		return wideningConversionOperation(op1, op2, Operation.MODULUS);
	}

	private static Object wideningConversionOperation(Object op1, Object op2, Operation operation){

	    // check for null values?

	    if( op1 instanceof String || op2 instanceof String){
	        return stringOperation(String.valueOf(op1), String.valueOf(op2), operation);
	    }

	    if( !(op1 instanceof Number) || !(op2 instanceof Number) ){
	        throw new RuntimeException("invalid operands for mathematical operator [+]");
	    }

	    if(op1 instanceof Double || op2 instanceof Double){
	        return doubleOperation(((Number)op1).doubleValue(), ((Number)op2).doubleValue(), operation);
	    }

	    if(op1 instanceof Float || op2 instanceof Float){
	        return floatOperation(((Number)op1).floatValue(), ((Number)op2).floatValue(), operation);
	    }

	    if(op1 instanceof Long || op2 instanceof Long){
	        return longOperation(((Number)op1).longValue(), ((Number)op2).longValue(), operation);
	    }

	    return integerOperation(((Number)op1).intValue(), ((Number)op2).intValue(), operation);
	}

	private static Object stringOperation(String fl1, String fl2, Operation operation) {
		switch (operation) {
			case ADD:
				return fl1 + fl2;
			default:
				throw new RuntimeException("Invalid operand types for operation [" + operation.toString() + "]");
		}
	}
	
	private static Object doubleOperation(Double fl1, Double fl2, Operation operation) {
		switch (operation) {
			case ADD:
				return fl1 + fl2;
			case SUBTRACT:
				return fl1 - fl2;
			case MULTIPLICATION:
				return fl1 * fl2;
			case DIVISION:
				return fl1 / fl2;
			case MODULUS:
				return fl1 % fl2;
			default:
				return null;
		}
	}
	
	private static Object floatOperation(Float fl1, Float fl2, Operation operation) {
		switch (operation) {
			case ADD:
				return fl1 + fl2;
			case SUBTRACT:
				return fl1 - fl2;
			case MULTIPLICATION:
				return fl1 * fl2;
			case DIVISION:
				return fl1 / fl2;
			case MODULUS:
				return fl1 % fl2;
			default:
				return null;
		}
	}
	
	private static Object longOperation(Long fl1, Long fl2, Operation operation) {
		switch (operation) {
			case ADD:
				return fl1 + fl2;
			case SUBTRACT:
				return fl1 - fl2;
			case MULTIPLICATION:
				return fl1 * fl2;
			case DIVISION:
				return fl1 / fl2;
			case MODULUS:
				return fl1 % fl2;
			default:
				return null;
		}
	}
	
	private static Object integerOperation(Integer fl1, Integer fl2, Operation operation) {
		switch (operation) {
			case ADD:
				return fl1 + fl2;
			case SUBTRACT:
				return fl1 - fl2;
			case MULTIPLICATION:
				return fl1 * fl2;
			case DIVISION:
				return fl1 / fl2;
			case MODULUS:
				return fl1 % fl2;
			default:
				return null;
		}
	}

}
