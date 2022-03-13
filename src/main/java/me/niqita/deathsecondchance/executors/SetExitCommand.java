package me.niqita.deathsecondchance.executors;

import me.niqita.deathsecondchance.Main;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class SetExitCommand implements CommandExecutor {
    private final String regex;
    private final FileConfiguration config;
    private final Main m;

    public SetExitCommand(FileConfiguration config, String regex, Main m) {
        this.regex = regex;
        this.config = config;
        this.m = m;
    }

    public String getStringLocation(final Location l) {
        World world = l.getWorld();
        if (world == null) return null;
        return world.getName() + regex + l.getBlockX() + regex + l.getBlockY() + regex + l.getBlockZ();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            config.set("exit-coordinates-from-purgatory", getStringLocation(((Player) sender).getLocation()));
            m.saveConfig();
            m.customReloadConfig();
            sender.sendMessage("Done.");
        } else sender.sendMessage("Only player can execute this command.");
        return true;
    }
}
