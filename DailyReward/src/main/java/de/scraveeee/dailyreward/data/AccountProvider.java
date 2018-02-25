package de.scraveeee.dailyreward.data;

import de.scraveeee.dailyreward.Main;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AccountProvider {

    private final Map<UUID, Account> accountMap;

    public AccountProvider() {
        Main.getInstance().getDatabaseHandler().executeUpdate("CREATE TABLE IF NOT EXISTS `DailyReward` (" +
                "  `Id` INT NOT NULL AUTO_INCREMENT," +
                "  `UUID` CHAR(36) NOT NULL," +
                "  `Timestamp` BIGINT NOT NULL," +
                "  PRIMARY KEY (`Id`)," +
                "  UNIQUE INDEX `UUID_UNIQUE` (`UUID` ASC))");
        this.accountMap = new ConcurrentHashMap<>();
    }

    public Map<UUID, Account> getCache() {
        return accountMap;
    }

    public void addPlayerToCache(UUID uuid) {
        if (uuid == null) {
            return;
        }
        if (accountMap.containsKey(uuid)) {
            return;
        }
        this.accountMap.put(uuid, new Account(uuid));
    }

    public void removePlayerFromCache(UUID uuid) {
        if (uuid == null) {
            return;
        }
        this.accountMap.entrySet().removeIf((Map.Entry<UUID, Account> current) -> current.getKey().equals(uuid));
    }

    public Account getPlayerFromCache(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        if (!accountMap.containsKey(uuid)) {
            return null;
        }
        return accountMap.get(uuid);
    }
}
