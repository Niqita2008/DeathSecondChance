package me.niqita.deathsecondchance.executors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitScheduler;

public class CancelShutdown implements CommandExecutor {
    private final BukkitScheduler scheduler;
    private final StopWhenPurgatoryEmpty s;

    public CancelShutdown(StopWhenPurgatoryEmpty s) {
        this.s = s;
        scheduler = Bukkit.getScheduler();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int id = s.getID();
        if (id != -1) {
            scheduler.cancelTask(id);
            sender.sendMessage("Done.");
        } else sender.sendMessage("Server didn't stop.");
        return true;
    }
}
