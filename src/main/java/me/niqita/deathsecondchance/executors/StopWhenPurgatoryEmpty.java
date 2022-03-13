package me.niqita.deathsecondchance.executors;

import me.niqita.deathsecondchance.Main;
import me.niqita.deathsecondchance.PurgatoryPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashSet;
import java.util.Objects;

public class StopWhenPurgatoryEmpty implements CommandExecutor {
    private final Permission perm;
    private final BukkitScheduler scheduler;
    private final Main m;
    private int ID = -1;
    private final HashSet<PurgatoryPlayer> inPurgatory;

    public StopWhenPurgatoryEmpty(Permission perm, Main m, HashSet<PurgatoryPlayer> inPurgatory) {
        this.perm = perm;
        this.m = m;
        this.inPurgatory = inPurgatory;
        scheduler = Bukkit.getScheduler();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ID = scheduler.scheduleSyncRepeatingTask(m, () -> {
            if (inPurgatory.isEmpty()) Bukkit.shutdown();
            Bukkit.getOnlinePlayers().stream().filter(player -> !player.hasPermission(perm) && inPurgatory.stream().noneMatch(p -> Objects.equals(p.getPlayer(), player))).forEach(player -> player.kickPlayer("Server closed."));
        }, 0, 10);
        sender.sendMessage("Done.");
        return true;
    }

    public int getID() {
        return ID;
    }
}
