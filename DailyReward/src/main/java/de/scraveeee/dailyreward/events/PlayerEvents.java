package de.scraveeee.dailyreward.events;

import de.scraveeee.dailyreward.ItemBuilder;
import de.scraveeee.dailyreward.Main;
import de.scraveeee.dailyreward.data.Account;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PlayerEvents implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Main.getInstance().getAccountProvider().addPlayerToCache(player.getUniqueId());

        Account account = Main.getInstance().getAccountProvider().getPlayerFromCache(player.getUniqueId());
        account.executeIfReady(() -> System.out.println(account.getTimestamp()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Main.getInstance().getAccountProvider().removePlayerFromCache(player.getUniqueId());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR && event.getClickedInventory() != null && event.getClickedInventory().getName().equals("§e§lReward")) {
            event.setCancelled(true);
            if (event.getSlot() == 4) {
                if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith("§a§lBelohnung §7§o<Klick>")) {
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                    player.sendMessage("§aHGW :D");
                    player.closeInventory();

                    Account account = Main.getInstance().getAccountProvider().getPlayerFromCache(player.getUniqueId());
                    account.setTimestamp(account.getTimestamp() + TimeUnit.DAYS.toMillis(1));
                    return;
                }
                Account account = Main.getInstance().getAccountProvider().getPlayerFromCache(player.getUniqueId());
                player.sendMessage("§cVerfügbar am §6§l" + new SimpleDateFormat("dd.MM.yyy 'um' HH:mm:ss 'Uhr'").format(new Date(account.getTimestamp())));
                player.closeInventory();
            }
        }
    }

    @EventHandler
    public void onEntiyInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        event.setCancelled(true);

        if (entity.getType().equals(EntityType.VILLAGER) && entity.getCustomName().equals("§e§lReward")) {
            Inventory inventory = this.getRewardInventory(player);
            if (inventory == null) return;
            player.openInventory(inventory);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity().getType().equals(EntityType.VILLAGER) && event.getEntity().getCustomName().equals("§e§lReward")) {
            event.setCancelled(true);
        }
    }

    private Inventory getRewardInventory(Player player) {
        Account account = Main.getInstance().getAccountProvider().getPlayerFromCache(player.getUniqueId());
        if (!account.isReady()) {
            player.sendMessage("§cBitte warte noch einen Augenblick.");
            return null;
        }
        Inventory inventory = Bukkit.createInventory(player, 9, "§e§lReward");
        inventory.setItem(4, System.currentTimeMillis() >= account.getTimestamp() ? new ItemBuilder(Material.INK_SACK).durability(10).clearLore().name("§a§lBelohnung §7§o<Klick>") : new ItemBuilder(Material.INK_SACK).durability(8).clearLore().name("§c§lVerfügbar am §6" + new SimpleDateFormat("dd.MM.yyy 'um' HH:mm:ss 'Uhr'").format(new Date(account.getTimestamp()))));
        return inventory;
    }
}
