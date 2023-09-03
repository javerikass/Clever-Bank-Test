package by.clevertec.cleverbank.jdbc;

import java.io.IOException;
import java.util.Properties;

public class PropertiesManager {

  private static final Properties properties = new Properties();

  private PropertiesManager() {
    throw new UnsupportedOperationException();
  }

  static {
    try {
      properties.load(
          PropertiesManager.class.getClassLoader().getResourceAsStream("application.yml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static String getProperty(String key) {
    return properties.getProperty(key);
  }

}

