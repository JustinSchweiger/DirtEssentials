package net.dirtcraft.plugins.dirtessentials.Commands;

import net.dirtcraft.plugins.dirtessentials.Utils.Permissions;
import net.dirtcraft.plugins.dirtessentials.Utils.Strings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class KickallCommand implements CommandExecutor {
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (!sender.hasPermission(Permissions.KICKALL)) {
			sender.sendMessage(Strings.NO_PERMISSION);
			return true;
		}
			
		String name = (sender instanceof Player) ? sender.getName() : "CONSOLE";
		String color = (sender instanceof Player) ? "GOLD" : "RED";
		
		StringBuilder messageString = new StringBuilder();
		if (args.length >= 1) {
			for (int i = 0; i < args.length; i++) {
				messageString.append(args[i]).append(" ");
			}
		} else {
			messageString.append("Feel free to rejoin!");
		}
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.hasPermission(Permissions.KICKALL_EXEMPT)) continue;
			player.kickPlayer(ChatColor.GRAY + "\n\nKicked by " + ChatColor.valueOf(color) + name + "\n" + ChatColor.GRAY + messageString);
		}
		
		return true;
	}
}
