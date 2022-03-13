package me.niqita.deathsecondchance;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

public class PurgatoryPlayer {
    private final Player player;
    private final PlayerInventory inventory;

    public Player getPlayer() {
        return player;
    }

    public Location getDeathLocation() {
        return deathLocation;
    }

    private final Location deathLocation;
    private final List<ItemStack> drops;

    public PurgatoryPlayer(Player player, Location deathLocation, List<ItemStack> drops) {
        this.player = player;
        this.deathLocation = deathLocation;
        this.drops = drops;
        inventory = player.getInventory();
    }

    public void exit() {
        player.setGameMode(GameMode.SURVIVAL);
        World world = deathLocation.getWorld();
        if (world != null) player.teleport(world.getSpawnLocation());
        drops.forEach(inventory::addItem);
    }

    public List<ItemStack> getDrops() {
        return drops;
    }
}
