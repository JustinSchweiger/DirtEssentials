package net.dirtcraft.plugins.dirtessentials.Commands;

import net.dirtcraft.plugins.dirtessentials.Manager.MessageManager;
import net.dirtcraft.plugins.dirtessentials.Utils.Permissions;
import net.dirtcraft.plugins.dirtessentials.Utils.Strings;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class MsgCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Strings.NO_CONSOLE);
			return true;
		}

		if (!sender.hasPermission(Permissions.MSG)) {
			sender.sendMessage(Strings.NO_PERMISSION);
			return true;
		}

		if (args.length <= 1) {
			sender.sendMessage(Strings.USAGE + "/msg <player> <message>");
			return true;
		}

		if (Bukkit.getOnlinePlayers().stream().noneMatch(player -> player.getName().equalsIgnoreCase(args[0]))) {
			sender.sendMessage(Strings.PLAYER_NOT_FOUND);
			return true;
		}

		Player target = Bukkit.getPlayer(args[0]);
		if (target == null) return true;
		if (!MessageManager.canReceiveMsg(target.getUniqueId())) {
			sender.sendMessage(Strings.PREFIX + "This player has disabled private messages!");
			return true;
		}

		Player player = (Player) sender;
		if (target == sender) {
			sender.sendMessage(Strings.PREFIX + "You can't message yourself!");
			return true;
		}

		StringBuilder messageString = new StringBuilder();
		for (int i = 1; i < args.length; i++) {
			messageString.append(args[i]).append(" ");
		}

		MessageManager.message(player, target, messageString.toString());
		MessageManager.setLastMsg(target.getUniqueId(), player.getUniqueId());
		return true;
	}

	@Nullable
	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		List<String> arguments = new ArrayList<>();

		if (!sender.hasPermission(Permissions.MSG)) return arguments;

		if (args.length == 1) {
			arguments.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
		} else if (args.length > 1) {
			arguments.add("<message>");
		}

		List<String> tabResults = new ArrayList<>();
		for (String argument : arguments) {
			if (argument.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
				tabResults.add(argument);
			}
		}

		return tabResults;
	}
}
