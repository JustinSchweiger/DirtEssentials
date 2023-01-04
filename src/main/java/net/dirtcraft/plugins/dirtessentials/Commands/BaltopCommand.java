package net.dirtcraft.plugins.dirtessentials.Commands;

import net.dirtcraft.plugins.dirtessentials.DirtEssentials;
import net.dirtcraft.plugins.dirtessentials.Manager.PlayerManager;
import net.dirtcraft.plugins.dirtessentials.Utils.Permissions;
import net.dirtcraft.plugins.dirtessentials.Utils.Strings;
import net.dirtcraft.plugins.dirtessentials.Utils.Utilities;
import net.dirtcraft.plugins.dirtessentials.Utils.gradient.GradientHandler;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BaltopCommand implements CommandExecutor, TabCompleter {
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (!sender.hasPermission(Permissions.BALTOP)) {
			sender.sendMessage(Strings.NO_PERMISSION);
			return true;
		}

		if (args.length > 1) {
			sender.sendMessage(Strings.USAGE + "/baltop [page]");
			return true;
		}

		if (args.length == 1 && !Utilities.isInteger(args[0])) {
			sender.sendMessage(Strings.USAGE + "/baltop [page]");
			return true;
		}

		Map<UUID, Double> balances = DirtEssentials.getDirtEconomy().getBalances();
		List<UUID> sorted = new ArrayList<>(balances.keySet());
		sorted.sort(Comparator.comparingDouble(balances::get).reversed());

		if (balances.size() <= 0) {
			sender.sendMessage(Strings.PREFIX + ChatColor.RED + "No balances found.");
		}

		Map<UUID, String> map = PlayerManager.getPlayerMap();
		int listEntries = Math.min(Utilities.config.economy.baltopSize, 15);
		int page = args.length > 0 ? Integer.parseInt(args[0]) : 1;

		int maxPages = (int) Math.ceil((double) balances.size() / (double) listEntries);
		if (page > maxPages || page < 1) {
			sender.sendMessage(Strings.PREFIX + "Page must be smaller or equal to: " + maxPages);
			return true;
		}

		int start = (page - 1) * listEntries;
		int end = page * listEntries;
		if (end > balances.size()) {
			end = balances.size();
		}

		sender.sendMessage("");
		sender.sendMessage(Strings.BALTOP_BAR_TOP);
		sender.sendMessage(ChatColor.GOLD + "Server Total Balance" + ChatColor.GRAY + ": " + balances.values().stream().mapToDouble(Double::doubleValue).sum() + Utilities.config.economy.currencySymbol);
		sender.sendMessage(ChatColor.YELLOW + "Top " + ChatColor.DARK_AQUA + (start + 1) + ChatColor.BLUE + " - " + ChatColor.DARK_AQUA + end + ChatColor.YELLOW + " players balance" + ChatColor.GRAY + ":");
		sender.sendMessage("");
		for (int i = start; i < end; i++) {
			UUID uuid = sorted.get(i);

			BaseComponent[] balComponent = new ComponentBuilder("")
					.append("  " + ChatColor.GREEN + (i + 1) + ". " + ChatColor.GOLD + map.get(uuid) + ChatColor.GRAY + ": " + balances.get(uuid) + Utilities.config.economy.currencySymbol)
					.create();

			sender.spigot().sendMessage(balComponent);
		}

		sender.sendMessage("");
		TextComponent bottomBar = new TextComponent(TextComponent.fromLegacyText(GradientHandler.hsvGradient("--------------------", new java.awt.Color(251, 121, 0), new java.awt.Color(247, 0, 0), GradientHandler::linear, net.md_5.bungee.api.ChatColor.STRIKETHROUGH)));
		TextComponent pagePrev;
		if (page == 1) {
			pagePrev = new TextComponent(ChatColor.GRAY + "  \u25C0 ");
			pagePrev.setBold(true);
			pagePrev.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.RED + "You are already on the first page!")));
		} else {
			pagePrev = new TextComponent(ChatColor.GREEN + "  \u25C0 ");
			pagePrev.setBold(true);
			pagePrev.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GREEN + "Previous page")));
			pagePrev.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/baltop " + (page - 1)));
		}
		bottomBar.addExtra(pagePrev);
		TextComponent pageNext;
		if (page == maxPages) {
			pageNext = new TextComponent(ChatColor.GRAY + " \u25B6  ");
			pageNext.setBold(true);
			pageNext.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.RED + "You are already on the last page!")));
		} else {
			pageNext = new TextComponent(ChatColor.GREEN + " \u25B6  ");
			pageNext.setBold(true);
			pageNext.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GREEN + "Next page")));
			pageNext.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/baltop " + (page + 1)));
		}
		bottomBar.addExtra(pageNext);
		bottomBar.addExtra(new TextComponent(TextComponent.fromLegacyText(GradientHandler.hsvGradient("--------------------", new java.awt.Color(247, 0, 0), new java.awt.Color(251, 121, 0), GradientHandler::linear, net.md_5.bungee.api.ChatColor.STRIKETHROUGH))));
		sender.spigot().sendMessage(bottomBar);
		sender.sendMessage("");

		return true;
	}

	@Nullable
	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		Map<UUID, Double> balances = DirtEssentials.getDirtEconomy().getBalances();
		int maxPages = (int) Math.ceil((double) balances.size() / (double) Math.min(Utilities.config.economy.baltopSize, 15));
		List<String> arguments = new ArrayList<>();

		if (!sender.hasPermission(Permissions.BALTOP))
			return arguments;

		if (args.length == 1) {
			arguments.add("1 - " + maxPages);
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
