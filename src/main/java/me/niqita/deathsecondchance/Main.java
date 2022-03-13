package me.niqita.deathsecondchance;

import me.niqita.deathsecondchance.executors.*;
import org.bukkit.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class Main extends JavaPlugin {
    private final String regex = ":";

    private final HashSet<PurgatoryPlayer> inPurgatory = new HashSet<>();
    private final FileConfiguration config = getConfig();
    private final Permission perm = new Permission("purgatory.GOD");

    @Override
    public void onEnable() {
        try {
            File dataFolder = getDataFolder();
            if ((!dataFolder.exists() || !dataFolder.isDirectory()) && !dataFolder.mkdir())
                throw new IOException("Cannot create DataFolder.");
            File conf = new File(dataFolder, "config.yml");
            if (!conf.exists() || !conf.isFile()) {
                if (!conf.createNewFile()) throw new IOException("Cannot create config.yml.");
                customReloadConfig();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        registerListener();
        PluginCommand setPurgatory = getCommand("set-purgatory");
        PluginCommand setExitPurgatory = getCommand("set-exit-purgatory");
        PluginCommand freePlayer = getCommand("free-player");
        PluginCommand stop = getCommand("stop-when-purgatory-empty");
        PluginCommand cancelShutdown = getCommand("cancel-stopping");
        if (setPurgatory == null || freePlayer == null || setExitPurgatory == null || stop == null || cancelShutdown == null) return;
        setPurgatory.setExecutor(new SetPurgatoryCommand(config, regex, this));
        freePlayer.setExecutor(new FreeCommand(inPurgatory));
        setExitPurgatory.setExecutor(new SetExitCommand(config, regex, this));
        StopWhenPurgatoryEmpty stopWhenPurgatoryEmpty = new StopWhenPurgatoryEmpty(perm, this, inPurgatory);
        setExitPurgatory.setExecutor(new CancelShutdown(stopWhenPurgatoryEmpty));
        stop.setExecutor(stopWhenPurgatoryEmpty);
    }

    private void registerListener() {
        String exit = config.getString("exit-coordinates-from-purgatory");
        String purgatory = config.getString("purgatory");
        if (exit == null || purgatory == null) return;
        Location exitLoc = parseWithoutVector(exit);
        Location purgatoryLoc = parse(purgatory);
        if (exitLoc == null || purgatoryLoc == null) return;
        ConfigurationSection section = config.getConfigurationSection(".messages");
        if (section == null) return;
        if (section.getString(".info") == null) section.set(".info", "Pass the test to get your resources back.");
        if (section.getString(".fail") == null)
            section.set(".fail", "Parkour has failed, your resources are stored in a chest at the place of your death.");
        saveConfig();
        Bukkit.getPluginManager().registerEvents(new EventListener(inPurgatory, purgatoryLoc, exitLoc, perm, section), this);
        if (!purgatoryLoc.getChunk().isLoaded()) purgatoryLoc.getChunk().load();
    }

    @Override
    public void onDisable() {

    }

    private Location parseWithoutVector(String s) {
        if (s == null || s.trim().equals("")) return null;
        final String[] parts = s.split(regex);
        return parts.length != 4 ? null : new Location(Bukkit.getWorld(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
    }

    private Location parse(String s) {
        if (s == null || s.trim().equals("")) return null;
        final String[] parts = s.split(regex);
        return parts.length != 6 ? null : new Location(Bukkit.getWorld(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]), Float.parseFloat(parts[4]), Float.parseFloat(parts[5]));
    }

    public void customReloadConfig() {
        Bukkit.getScheduler().cancelTasks(this);
        reloadConfig();
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        onEnable();
    }

}
