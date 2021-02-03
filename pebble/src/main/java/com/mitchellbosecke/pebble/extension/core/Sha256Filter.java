package com.mitchellbosecke.pebble.extension.core;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

/**
 * This class implements the 'sha256' filter.
 *
 * @author Silviu Vergoti
 */
public class Sha256Filter implements Filter {

  public static final String FILTER_NAME = "sha256";

  @Override
  public List<String> getArgumentNames() {
    return null;
  }

  @Override
  public Object apply(Object input, Map<String, Object> args, PebbleTemplate self,
      EvaluationContext context, int lineNumber) throws PebbleException {
    if (input == null) {
      return null;
    }

    if (input instanceof String) {
      MessageDigest digest = null;
      byte[] encodedHash = null;
      try {
        digest = MessageDigest.getInstance("SHA-256");
        encodedHash = digest.digest(((String) input).getBytes(StandardCharsets.UTF_8));
      } catch (Exception e) {
        throw new PebbleException(e, "Hashing exception encountered\n", lineNumber, self.getName());
      }
      return bytesToHex(encodedHash);
    } else {
      throw new PebbleException(null, "Need a string to hash\n", lineNumber, self.getName());
    }
  }

  private static String bytesToHex(byte[] bytes) {
    StringBuilder hexString = new StringBuilder(2 * bytes.length);
    for (int i = 0; i < bytes.length; i++) {
      String hex = Integer.toHexString(0xff & bytes[i]);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    return hexString.toString();
  }

}
