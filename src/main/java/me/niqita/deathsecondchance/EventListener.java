package me.niqita.deathsecondchance;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;

import java.util.*;

public class EventListener implements Listener {

    private final HashSet<PurgatoryPlayer> inPurgatory;
    private final Location exit;
    private final Location purgatory;
    private final Permission perm;
    private final String info;
    private final String fail;

    public EventListener(HashSet<PurgatoryPlayer> inPurgatory, Location purgatory, Location exit, Permission perm, ConfigurationSection config) {
        this.inPurgatory = inPurgatory;
        this.exit = exit;
        this.purgatory = purgatory;
        this.perm = perm;
        info = config.getString(".info", "Pass the test to get your resources back.");
        fail = config.getString(".fail", "Parkour has failed, your resources are stored in a chest at the place of your death.");
    }

    @EventHandler
    public void moveEvent(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        PurgatoryPlayer purgatoryPlayer = inPurgatory.stream().filter(n -> Objects.equals(n.getPlayer(), player)).findFirst().orElse(null);
        if (purgatoryPlayer == null || player.getLocation().distance(exit) > 0.5) return;
        purgatoryPlayer.exit();
        inPurgatory.remove(purgatoryPlayer);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (inPurgatory.stream().noneMatch(n -> Objects.equals(n.getPlayer(), player))) return;
        player.teleport(purgatory);
        player.setGameMode(GameMode.ADVENTURE);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        if (inPurgatory.stream().anyMatch(p -> Objects.equals(p.getPlayer(), player))) e.setRespawnLocation(purgatory);
    }

    @EventHandler
    public void deathEvent(PlayerDeathEvent e) {
        e.setDeathMessage(null);
        Player player = e.getEntity();
        List<ItemStack> drops = e.getDrops();
        if (player.hasPermission(perm)) {
            e.setKeepInventory(true);
            e.setKeepLevel(true);
            e.setDroppedExp(0);
            drops.clear();
            return;
        }
        PurgatoryPlayer purgatoryPlayer = inPurgatory.stream().filter(n -> Objects.equals(n.getPlayer(), player)).findFirst().orElse(null);
        if (purgatoryPlayer == null) {
            if (drops.isEmpty()) return;
            player.setGameMode(GameMode.ADVENTURE);
            inPurgatory.add(new PurgatoryPlayer(player, player.getLocation(), new ArrayList<>(drops)));
            drops.clear();
            player.sendMessage(info);
            return;
        }
        inPurgatory.remove(purgatoryPlayer);
        drops.clear();
        player.setGameMode(GameMode.SURVIVAL);
        Collection<ItemStack> stacks = purgatoryPlayer.getDrops();
        if (stacks.isEmpty()) return;
        Location deathLocation = purgatoryPlayer.getDeathLocation();
        Block block = deathLocation.getBlock();
        block.setType(Material.CHEST);
        Inventory container = ((Chest) block.getState()).getInventory();
        player.sendMessage(fail);
        if (stacks.size() < 28) {
            stacks.forEach(container::addItem);
            return;
        }
        stacks.stream().limit(27).forEach(container::addItem);
        Location clone = deathLocation.clone();
        clone.setY(clone.getBlockY() + 1);
        block = clone.getBlock();
        block.setType(Material.CHEST);
        container = ((Chest) block.getState()).getInventory();
        stacks.stream().skip(27).forEach(container::addItem);
    }

}
