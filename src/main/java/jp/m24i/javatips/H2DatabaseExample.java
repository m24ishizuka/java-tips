package jp.m24i.javatips;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class H2DatabaseExample {

  private static final String CATALOG_NAME = "JAVATIPS";
  private static final String SCHEMA_NAME = "RINGO";
  private static final String TABLE_NAME ="MAKERS";
  private static final String JDBC_URL = String.format("jdbc:h2:./%s", CATALOG_NAME);

  public static void main(String[] args) throws Exception {
    Connection connection = null;
    try {
      connection = DriverManager.getConnection(JDBC_URL);
      connection.setAutoCommit(false);
      createAndDrop(connection);
      insert(connection);
      connection.commit();

      DatabaseMetaData dbMetaData = connection.getMetaData();
      output("schemas", dbMetaData.getSchemas(CATALOG_NAME, SCHEMA_NAME));
      output("tables", dbMetaData.getTables(CATALOG_NAME, SCHEMA_NAME, null, null));
      select(connection);
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private static void createAndDrop(Connection connection) throws Exception {
    try (
      Statement statement = connection.createStatement();
    ) {
      String[] sqls = {
          String.format("DROP SCHEMA IF EXISTS %s CASCADE", SCHEMA_NAME),
          String.format("CREATE SCHEMA %s", SCHEMA_NAME),
          String.format("CREATE TABLE %s.%s(id INT, name VARCHAR(10))", SCHEMA_NAME, TABLE_NAME)
      };
      for (String sql : sqls) {
        statement.addBatch(sql);
      }
      statement.executeBatch();
    }
  }

  private static void insert(Connection connection) throws Exception {
    String sql = String.format("INSERT INTO %s.%s VALUES(?, ?)", SCHEMA_NAME, TABLE_NAME);
    try (
      PreparedStatement ps = connection.prepareStatement(sql);
    ) {
      String[] values = {
          "honda", "yamaha", "kawasaki", "suzuki"
      };
      for (int i = 0; i < values.length; i++) {
        int id = i + 1;
        String value = values[i];
        ps.setInt(1, id);
        ps.setString(2, value);
        ps.addBatch();
      }
      ps.executeBatch();
    }
  }

  private static void select(Connection con) throws Exception {
    try (
      Statement stmt = con.createStatement();
    ) {
      String sql = String.format("SELECT * FROM %s.%s", SCHEMA_NAME, TABLE_NAME);
      output("table:makers", stmt.executeQuery(sql));
    }
  }

  private static void output(String name, ResultSet rs) throws Exception {
    try {
      System.out.println("[" + name + "]");
      ResultSetMetaData rsMetaData = rs.getMetaData();
      int columnCount = rsMetaData.getColumnCount();

      // column name
      for (int i = 1; i <= columnCount; i++) {
        System.out.print(rsMetaData.getColumnName(i) + "\t");
      }
      System.out.println();

      // value
      while (rs.next()) {
        for (int i = 1; i <= columnCount; i++) {
          System.out.print(rs.getString(i) + "\t");
        }
        System.out.println();
      }
    } finally {
      rs.close();
    }
  }

}
