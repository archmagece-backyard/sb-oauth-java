package org.scriptonbasestar.oauth.client.util;

import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//TODO tools.core로 이동
@UtilityClass
public class SBStringDigestUtil {

  private static final MessageDigest md5;
  private static final MessageDigest sha1;
  private static final MessageDigest sha256;

  static {
    try {
      md5 = MessageDigest.getInstance("MD5");
      sha1 = MessageDigest.getInstance("SHA-1");
      sha256 = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("An error is occurred in class SBStringDigestUtil static field", e);
    }
  }

  public static byte[] md5(String source) {
    return md5.digest(source.getBytes(StandardCharsets.UTF_8));
  }

  public static byte[] sha1(String source) {
    return sha1.digest(source.getBytes(StandardCharsets.UTF_8));
  }

  public static byte[] sha256(String source) {
    return sha256.digest(source.getBytes(StandardCharsets.UTF_8));
  }
}
