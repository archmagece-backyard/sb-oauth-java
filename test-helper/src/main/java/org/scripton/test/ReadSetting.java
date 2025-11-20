package org.scripton.test;

import org.scriptonbasestar.oauth.client.exception.OAuthUnknownException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

public final class ReadSetting {
  private final Properties configFile;

  private ReadSetting(InputStream is) {
    configFile = new java.util.Properties();
    try {
      configFile.load(is);
    } catch (Exception e) {
      throw new OAuthUnknownException("파일이 없거나 읽는 중 오류 발생.", e);
    }
  }

  public static ReadSetting readFile(String filename) {
    try {
      return new ReadSetting(new FileInputStream(new File(filename)));
    } catch (FileNotFoundException e) {
      throw new OAuthUnknownException("파일 읽기 오류", e);
    }
  }

  public static ReadSetting readProjectResource(String filename) {
    return new ReadSetting(ClassLoader.getSystemClassLoader().getResourceAsStream(filename));
  }


  public String getProperty(String key) {
    return this.configFile.getProperty(key);
  }
}
