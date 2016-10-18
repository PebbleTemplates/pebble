/*******************************************************************************
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.extension.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class AbsFilter implements Filter {

    @Override
    public List<String> getArgumentNames() {
        return null;
    }

    @Override
    public Number apply(Object input, Map<String, Object> args, PebbleTemplateImpl self, int lineNumber)
            throws PebbleException {
        if (input == null) {
            throw new PebbleException(null, "Can not pass null value to \"abs\" filter.", lineNumber, self.getName());
        }

        if (input instanceof Integer) {
            return Math.abs((Integer) input);
        } else if (input instanceof Byte) {
            return Math.abs((Byte) input);
        } else if (input instanceof Short) {
            return Math.abs((Short) input);
        } else if (input instanceof Float) {
            return Math.abs((Float) input);
        } else if (input instanceof Long) {
            return Math.abs((Long) input);
        } else if (input instanceof Double) {
            return Math.abs((Double) input);
        } else if (input instanceof BigDecimal) {
            return ((BigDecimal) input).abs();
        } else if (input instanceof BigInteger) {
            return ((BigInteger) input).abs();
        } else if (input instanceof Number) {
            // We make here an assumption that we have checked all special
            // cases.
            return Math.abs(((Number) input).doubleValue());
        } else {
            throw new PebbleException(null, "The 'abs' filter does require as input a number.", lineNumber,
                    self.getName());
        }
    }

}
