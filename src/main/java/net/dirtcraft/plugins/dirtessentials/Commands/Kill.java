package net.dirtcraft.plugins.dirtessentials.Commands;

import net.dirtcraft.plugins.dirtessentials.Utils.Permissions;
import net.dirtcraft.plugins.dirtessentials.Utils.Strings;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Kill implements CommandExecutor, TabCompleter {
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (!sender.hasPermission(Permissions.KILL)) {
			sender.sendMessage(Strings.NO_PERMISSION);
			return true;
		}

		if (args.length < 1) {
			sender.sendMessage(Strings.USAGE + "/kill <player>");
			return true;
		}

		Player target = Bukkit.getPlayer(args[0]);
		if (target == null) {
			sender.sendMessage(Strings.PLAYER_NOT_FOUND);
			return true;
		}

		if (God.getGodPlayers().contains(target.getUniqueId())) {
			sender.sendMessage(Strings.PREFIX + ChatColor.RED + "You cannot kill a player in god mode!");
			return true;
		}

		target.setHealth(0);
		sender.sendMessage(Strings.PREFIX + "Killed " + ChatColor.GOLD + target.getName());
		return true;
	}

	@Nullable
	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		List<String> arguments = new ArrayList<>();

		if (!sender.hasPermission(Permissions.KILL)) return arguments;

		if (args.length == 1) {
			arguments.addAll(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()));
		}

		List<String> tabResults = new ArrayList<>();
		for (String argument : arguments) {
			if (argument.toLowerCase().contains(args[args.length - 1].toLowerCase())) {
				tabResults.add(argument);
			}
		}

		return tabResults;
	}
}
