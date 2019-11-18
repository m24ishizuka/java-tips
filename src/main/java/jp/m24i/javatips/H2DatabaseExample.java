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
    Connection con = null;
    try {
      con = DriverManager.getConnection(JDBC_URL);
      con.setAutoCommit(false);
      createAndDrop(con);
      insert(con);
      con.commit();

      DatabaseMetaData dbMetaData = con.getMetaData();
      output("schemas", dbMetaData.getSchemas(CATALOG_NAME, SCHEMA_NAME));
      output("tables", dbMetaData.getTables(CATALOG_NAME, SCHEMA_NAME, null, null));
      select(con);
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if (con != null) {
        try {
          con.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private static void createAndDrop(Connection con) throws Exception {
    try (
      Statement stmt = con.createStatement();
    ) {
      String[] sqls = {
          String.format("DROP SCHEMA IF EXISTS %s CASCADE", SCHEMA_NAME),
          String.format("CREATE SCHEMA %s", SCHEMA_NAME),
          String.format("CREATE TABLE %s.%s(id INT, name VARCHAR(10))", SCHEMA_NAME, TABLE_NAME)
      };
      for (String sql : sqls) {
        stmt.addBatch(sql);
      }
      stmt.executeBatch();
    }
  }

  private static void insert(Connection con) throws Exception {
    String sql = String.format("INSERT INTO %s.%s VALUES(?, ?)", SCHEMA_NAME, TABLE_NAME);
    try (
      PreparedStatement prst = con.prepareStatement(sql);
    ) {
      String[] values = {
          "honda", "yamaha", "kawasaki", "suzuki"
      };
      for (int i = 0; i < values.length; i++) {
        int id = i + 1;
        String value = values[i];
        prst.setInt(1, id);
        prst.setString(2, value);
        prst.addBatch();
      }
      prst.executeBatch();
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
