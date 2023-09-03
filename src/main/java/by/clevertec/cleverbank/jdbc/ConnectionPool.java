package by.clevertec.cleverbank.jdbc;

import by.clevertec.cleverbank.util.UtilBD;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectionPool {

  private static HikariDataSource dataSource;

  private ConnectionPool() {
  }

  public static synchronized HikariDataSource getDataSource() {
    if (dataSource == null) {
      HikariConfig config = new HikariConfig();
      config.setDriverClassName(PropertiesManager.getProperty(UtilBD.DB_DRIVER));
      config.setJdbcUrl(PropertiesManager.getProperty(UtilBD.DB_URL_KEY));
      config.setUsername(PropertiesManager.getProperty(UtilBD.DB_USER_KEY));
      config.setPassword(PropertiesManager.getProperty(UtilBD.DB_PASS_KEY));
      dataSource = new HikariDataSource(config);
    }
    return dataSource;
  }

  public static synchronized void closeDataSource() {
    if (dataSource != null) {
      dataSource.close();
      dataSource = null;
    }
  }
}
