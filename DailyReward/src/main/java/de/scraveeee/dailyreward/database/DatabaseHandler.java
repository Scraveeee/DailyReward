package de.scraveeee.dailyreward.database;

import java.sql.*;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DatabaseHandler {

    private final ExecutorService executorService;
    private Connection connection;

    private final String host;
    private final String user;
    private final String database;
    private final String password;

    private final int port;
    private final int threadPoolSize;
    private final int connectionPoolSize;

    public DatabaseHandler(String host, int port, String user, String database, String password, int threadPoolSize, int connectionPoolSize) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.database = database;
        this.password = password;
        this.threadPoolSize = threadPoolSize;
        this.connectionPoolSize = connectionPoolSize;
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
    }

    public boolean openConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            Properties properties = new Properties();
            properties.setProperty("user", user);
            properties.setProperty("password", password);
            properties.setProperty("port", String.valueOf(port));
            this.connection = DriverManager.getConnection(String.format("jdbc:mysql://%s/%s", host, database), properties);

            this.refreshConnection();
            return true;
        } catch (SQLException | ClassNotFoundException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            this.connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void executeUpdate(PreparedStatement statement) {
        try {
            if (statement != null) {
                statement.executeUpdate();
                statement.close();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void executeUpdate(String query) {
        try {
            if (query != null) {
                PreparedStatement statement = this.connection.prepareStatement(query);
                statement.executeUpdate();
                statement.close();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void close(ResultSet resultSet, PreparedStatement... statements) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            for (PreparedStatement statement : statements) statement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private void refreshConnection() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (!connection.isClosed()) {
                        PreparedStatement statement = getConnection().prepareStatement("/* ping */ SELECT 1");
                        ResultSet resultSet = statement.executeQuery();
                        close(resultSet, statement);
                    }
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        }, TimeUnit.SECONDS.toMillis(20L));
    }

    public void executeAsync(Runnable runnable) {
        executorService.execute(runnable);
    }
}
