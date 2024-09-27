package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        Connection connection = DriverManager.getConnection(
                ConnectionData.URL, ConnectionData.USER, ConnectionData.PASSWORD
        );
        createDatabase(connection);
        useDatabase(connection);
        createTable(connection);


        Course course1 = new Course("first", 10);
        Course course2 = new Course("second", 8);
        add(connection, course1);
        add(connection, course2);

        try (SessionFactory sessionFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Course.class)
                .buildSessionFactory()) {
            Session session = sessionFactory.getCurrentSession();

            session.beginTransaction();

            Course courseH = new Course("Hibernate commit", 111);
            session.save(courseH);
            System.out.println("Object saved!");

            session.getTransaction().commit();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static void createDatabase(Connection connection) throws SQLException {
        String sql = "CREATE DATABASE IF NOT EXISTS schoolDB;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.execute();
        }
    }

    private static void useDatabase(Connection connection) throws SQLException {
        String useDatabaseSql = "USE schoolDB;";
        PreparedStatement useDatabaseStatement = connection.prepareStatement(useDatabaseSql);
        useDatabaseStatement.execute(useDatabaseSql);
    }

    private static void deleteDatabase(Connection connection) throws SQLException {
        String deleteSql = "DROP DATABASE schoolDB;";
        PreparedStatement statement = connection.prepareStatement(deleteSql);
        {
            statement.execute(deleteSql);
        }
    }

    private static void createTable(Connection connection) throws SQLException {
        String createTable = "CREATE TABLE IF NOT EXISTS Courses (id INT AUTO_INCREMENT PRIMARY KEY, title VARCHAR(255), duration INT);";
        PreparedStatement statement = connection.prepareStatement(createTable);
        statement.execute();
    }

    private static void add(Connection connection, Course course) {
        String insertPersonSQL = "INSERT INTO Courses (title, duration) VALUES(?,?);";
        try (PreparedStatement statement = connection.prepareStatement(insertPersonSQL)) {
            statement.setString(1, course.getTitle());
            statement.setInt(2, course.getDuration());
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}

