package de.scraveeee.dailyreward.data;

import de.scraveeee.dailyreward.Main;
import de.scraveeee.dailyreward.database.ReadyExecutor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Account extends ReadyExecutor {

    private final UUID uuid;
    private long timestamp;

    public Account(UUID uuid) {
        this.uuid = uuid;
        this.timestamp = System.currentTimeMillis();
        this.loadData();
    }

    public UUID getUUID() {
        return uuid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        Main.getInstance().getDatabaseHandler().executeAsync(() -> {
            try {
                PreparedStatement statement = Main.getInstance().getDatabaseHandler().getConnection().prepareStatement("UPDATE `DailyReward` SET `Timestamp` = ? WHERE `UUID` = ?");
                statement.setLong(1, timestamp);
                statement.setString(2, this.getUUID().toString());
                Main.getInstance().getDatabaseHandler().executeUpdate(statement);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    private void loadData() {
        Main.getInstance().getDatabaseHandler().executeAsync(() -> {
            try {
                PreparedStatement statement = Main.getInstance().getDatabaseHandler().getConnection().prepareStatement("SELECT `Timestamp` FROM `DailyReward` WHERE `UUID` = ?");
                statement.setString(1, this.getUUID().toString());
                ResultSet results = statement.executeQuery();

                if (results.next()) {
                    this.timestamp = results.getLong("Timestamp");
                } else {
                    PreparedStatement insert = Main.getInstance().getDatabaseHandler().getConnection().prepareStatement("INSERT INTO `DailyReward` (`UUID`, `Timestamp`) VALUES (?, ?)");
                    insert.setString(1, this.getUUID().toString());
                    insert.setLong(2, this.getTimestamp());
                    Main.getInstance().getDatabaseHandler().executeUpdate(insert);
                }
                Main.getInstance().getDatabaseHandler().close(results);
                this.setReady(true);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }
}
