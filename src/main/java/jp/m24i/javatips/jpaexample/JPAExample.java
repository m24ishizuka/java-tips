package jp.m24i.javatips.jpaexample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAExample {

  public static void main(String[] args) throws Exception {
    Connection connection = null;
    try {
      connection = getConnection();
      initializeDb(connection);
      connection.close();

      EntityManagerFactory factory = Persistence.createEntityManagerFactory("example-hibernate");
      EntityManager manager = factory.createEntityManager();

      manager.getTransaction().begin();
      User user = new User();
      user.setId(1);
      user.setName("Takakura");
      manager.persist(user);
      manager.getTransaction().commit();

      User anonymouse = new User();
      user.setId(-1);
      user.setName("Anonymouse");
      manager.persist(anonymouse);

      manager.close();

      connection = getConnection();
      select(connection);
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      connection.close();
    }


  }

  private static Connection getConnection() throws SQLException {
    return DriverManager.getConnection("jdbc:h2:./JAVATIPS");
  }

  private static void initializeDb(Connection connection) throws Exception {
    try (
      Statement statement = connection.createStatement();
    ) {
      String[] sqls = {
          "DROP SCHEMA IF EXISTS RINGO CASCADE",
          "CREATE SCHEMA RINGO",
          "CREATE TABLE RINGO.USERS(id INTEGER, name VARCHAR(20))"
      };
      for (String sql : sqls) {
        statement.addBatch(sql);
      }
      statement.executeBatch();
    }
  }

  private static void select(Connection connection) throws SQLException {
    try (
      Statement statement = connection.createStatement();
    ) {
      output(statement.executeQuery("SELECT * FROM RINGO.USERS"));
    }
  }

  private static void output(ResultSet rs) throws SQLException {
    try {
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
