package de.scraveeee.dailyreward;

import de.scraveeee.dailyreward.commands.CommandVillager;
import de.scraveeee.dailyreward.data.AccountProvider;
import de.scraveeee.dailyreward.database.DatabaseHandler;
import de.scraveeee.dailyreward.events.PlayerEvents;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;
    private DatabaseHandler databaseHandler;
    private AccountProvider accountProvider;

    @Override
    public void onDisable() {
        this.killVillager();
        this.databaseHandler.closeConnection();
    }

    @Override
    public void onEnable() {
        instance = this;
        this.initAll();
    }

    public static Main getInstance() {
        return instance;
    }

    public DatabaseHandler getDatabaseHandler() {
        return databaseHandler;
    }

    public AccountProvider getAccountProvider() {
        return accountProvider;
    }

    private void initAll() {
        this.writeDefaults();
        if(!setupDatabase()) {
            this.getServer().shutdown();
            return;
        }
        this.accountProvider = new AccountProvider();
        Bukkit.getPluginManager().registerEvents(new PlayerEvents(), this);
        this.getCommand("setvillager").setExecutor(new CommandVillager());
        this.loadVillager();
    }

    private void writeDefaults() {
        this.getConfig().addDefault("MySQL.Host", "127.0.0.1");
        this.getConfig().addDefault("MySQL.Port", 3306);
        this.getConfig().addDefault("MySQL.User", "user");
        this.getConfig().addDefault("MySQL.Database", "database");
        this.getConfig().addDefault("MySQL.Password", "password");
        this.getConfig().addDefault("MySQL.Connection-Pool-Size", 2);
        this.getConfig().addDefault("Settings.Thread-Pool-Size", 2);
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
    }

    private boolean setupDatabase() {
        String host = this.getConfig().getString("MySQL.Host");
        String user = this.getConfig().getString("MySQL.User");
        String database = this.getConfig().getString("MySQL.Database");
        String password = this.getConfig().getString("MySQL.Password");

        int port = this.getConfig().getInt("MySQL.Port");
        int connectionPoolSize = this.getConfig().getInt("MySQL.Connection-Pool-Size");
        int threadPoolSize = this.getConfig().getInt("Settings.Thread-Pool-Size");

        this.databaseHandler = new DatabaseHandler(host, port, user, database, password, threadPoolSize, connectionPoolSize);
        return databaseHandler.openConnection();
    }

    private void loadVillager() {
        FileConfiguration configuration = this.getConfig();
        if (configuration.get("Villager") == null) return;
        Location location = (Location) configuration.get("Villager");
        Entity villager = location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        villager.setCustomName("§e§lReward");
        this.noAI(true, villager);
    }

    private void killVillager() {
        Bukkit.getWorlds().forEach(world -> world.getEntities().forEach(entity -> {
            if (entity.getType().equals(EntityType.VILLAGER) && entity.getCustomName().equals("§e§lReward")) {
                entity.remove();
            }
        }));
    }

    public void noAI(boolean noAI, Entity entity) {
        net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        NBTTagCompound tag = nmsEntity.getNBTTag();
        if (tag == null) {
            tag = new NBTTagCompound();
        }
        nmsEntity.c(tag);
        tag.setInt("NoAI", noAI ? 1 : 0);
        nmsEntity.f(tag);
    }
}
