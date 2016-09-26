/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2016 by Alexander Brandt
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.extension.core;

import com.mitchellbosecke.pebble.extension.Filter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Obfuscates Strings. Use with {{ email | obfuscate | raw }}, where email is the string to obuscate.
 * 
 * @author Alexander Brandt
 *
 */
public class ObfuscateFilter implements Filter {
    @Override
    public List<String> getArgumentNames() {
        return Collections.emptyList();
    }

    @Override
    public Object apply(Object input, Map<String, Object> args){
        if(input == null){
            return null;
        }
        String str = (String) input;
        return obfuscate(str);
    }

    /* The following code is taken from PegDown:
    https://github.com/sirthias/pegdown/blob/master/src/main/java/org/pegdown/FastEncoder.java
     */
    private Random random = new Random(0x2626);

    public String obfuscate(String email) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < email.length(); i++) {
            char c = email.charAt(i);
            switch (random.nextInt(5)) {
                case 0:
                case 1:
                    sb.append("&#").append((int) c).append(';');
                    break;
                case 2:
                case 3:
                    sb.append("&#x").append(Integer.toHexString(c)).append(';');
                    break;
                case 4:
                    String encoded = encode(c);
                    if (encoded != null) sb.append(encoded); else sb.append(c);
            }
        }
        return sb.toString();
    }

    public String encode(char c) {
        switch (c) {
            case '&':  return "&amp;";
            case '<':  return "&lt;";
            case '>':  return "&gt;";
            case '"':  return "&quot;";
            case '\'': return "&#39;";
        }
        return null;
    }
}