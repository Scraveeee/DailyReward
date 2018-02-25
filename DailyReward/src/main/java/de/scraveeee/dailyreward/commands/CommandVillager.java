package de.scraveeee.dailyreward.commands;

import de.scraveeee.dailyreward.Main;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class CommandVillager implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("§cDazu hast du keine Berechtigung.");
        }
        if (args.length > 1) {
            sender.sendMessage("§cVerwendung: §b/setvillager");
            return true;
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            this.setVillagerLocation(player.getLocation());
            player.sendMessage("§aDie Position wurde erfolgreich gesetzt.");
        }
        return true;
    }

    private void setVillagerLocation(Location location) {
        FileConfiguration configuration = Main.getInstance().getConfig();
        configuration.set("Villager", location);
        Main.getInstance().saveConfig();

        Entity villager = location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        villager.setCustomName("§e§lReward");
        Main.getInstance().noAI(true, villager);
    }
}
