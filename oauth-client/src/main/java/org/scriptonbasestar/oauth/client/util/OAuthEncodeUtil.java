package org.scriptonbasestar.oauth.client.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author archmagece
 * @since 2016-10-26 14
 */
public final class OAuthEncodeUtil {

  private OAuthEncodeUtil() {
    throw new AssertionError("Utility class should not be instantiated");
  }

  public static String encode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }

  public static String[] encodeArray(String[] values) {
    return Arrays.stream(values)
        .map(OAuthEncodeUtil::encode)
        .toArray(String[]::new);
  }
}
