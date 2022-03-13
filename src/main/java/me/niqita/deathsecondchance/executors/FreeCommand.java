package me.niqita.deathsecondchance.executors;

import me.niqita.deathsecondchance.PurgatoryPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Objects;

public class FreeCommand implements CommandExecutor {
    private final HashSet<PurgatoryPlayer> inPurgatory;

    public FreeCommand(HashSet<PurgatoryPlayer> inPurgatory) {
        this.inPurgatory = inPurgatory;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) return true;
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage("Player not found.");
            return true;
        }
        PurgatoryPlayer purgatoryPlayer = inPurgatory.stream().filter(n -> Objects.equals(n.getPlayer(), player)).findFirst().orElse(null);
        if (purgatoryPlayer != null) {
            inPurgatory.remove(purgatoryPlayer);
            purgatoryPlayer.exit();
            sender.sendMessage("Done.");
        } else sender.sendMessage("The player is not in the purgatory.");
        return true;
    }
}
