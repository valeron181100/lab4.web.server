package main.java.database;

import main.java.utilities.Pair;

import java.sql.*;

public class JDBCConnector extends DBConfigs {

    private final String DB_URL = "jdbc:postgresql://"+dbHost+":"+dbPort+"/" + dbName;
    private final String USER = dbUser;
    private final String PASS = dbPassword;


    private static final FileLogger logger = new FileLogger();

    private Connection connection;

    public JDBCConnector() {
        boolean firstCreation = true;
        while(true) {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(DB_URL, USER, PASS);
                System.out.println("\nСоединение с БД установлено!");
                break;
            } catch (ClassNotFoundException e) {
                System.err.println("Ошибка JDBC драйвер не найден!");
                System.exit(1);
            } catch (SQLException e) {
                if (e.getSQLState().equals("08001")) {
                    if(firstCreation) {
                        System.out.println("Ошибка: невозможно подключиться к серверу БД, так как сервер не доступен!");
                        System.out.println("Попытка подключения...");
                        firstCreation = false;
                    }
                }
            }
        }
        int k =0;
    }

    public Connection getConnection() {
        return connection;
    }

    /**
     * Executing a query and returning resultSet. After calling this method and processing resultSet, you must call PreparedStatement.close() method.
     * @param query SQL query
     * @return Pair of PreparedStatement and ResultSet
     */
    public Pair<PreparedStatement, ResultSet> execSQLQuery(String query){
            try {
                PreparedStatement statement = connection.
                        prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();
                return new Pair<>(statement, resultSet);
            } catch (SQLException e) {
                if (e.getSQLState().equals("08001") || e.getSQLState().equals("08006")) {
                    resetConnection();
                    return execSQLQuery(query);
                }
                else {
                    logger.log(e.getMessage() + "\nSqlState = " + e.getSQLState());
                    return null;
                }
            }
    }

    private boolean resetConnection(){
        boolean firstCreation = true;
        while(true) {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(DB_URL, USER, PASS);
                System.out.println("\nСоединение с БД установлено!");
                return true;
            } catch (ClassNotFoundException e) {
                System.err.println("Ошибка JDBC драйвер не найден!");
                System.exit(1);
            } catch (SQLException e) {
                if (e.getSQLState().equals("08001")) {
                    if(firstCreation) {
                        firstCreation = false;
                    }
                }
            }
        }
    }

    public boolean execSQLUpdate(String query){
        try (PreparedStatement statement = connection.
                prepareStatement(query)) {
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getSQLState().equals("08001") || e.getSQLState().equals("08006")) {
                resetConnection();
                return execSQLUpdate(query);
            }
            else {
                logger.log(e.getMessage() + "\nSqlState = " + e.getSQLState());
                return false;
            }
        }
    }
}

