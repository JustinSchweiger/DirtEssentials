package net.dirtcraft.plugins.dirtessentials.Commands;

import net.dirtcraft.plugins.dirtessentials.Data.Home;
import net.dirtcraft.plugins.dirtessentials.Database.DatabaseOperations;
import net.dirtcraft.plugins.dirtessentials.Manager.HomeManager;
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
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OtherhomeCommand implements CommandExecutor, TabCompleter {
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Strings.NO_CONSOLE);
			return true;
		}

		if (!sender.hasPermission(Permissions.OTHERHOME)) {
			sender.sendMessage(Strings.NO_PERMISSION);
			return true;
		}

		if ((args.length < 2) || (args.length > 3)) {
			sender.sendMessage(Strings.USAGE + "/otherhome <player> <list|teleport|delete> <name|page>");
			return true;
		}

		UUID uuid = PlayerManager.getUuid(args[0]);
		if (uuid == null) {
			sender.sendMessage(Strings.PLAYER_NOT_FOUND);
			return true;
		}

		String action = args[1].toLowerCase();
		if (!action.equalsIgnoreCase("teleport") && !action.equalsIgnoreCase("delete") && !action.equalsIgnoreCase("list")) {
			sender.sendMessage(Strings.USAGE + "/otherhome <player> <teleport|list|delete> <name|page>");
			return true;
		}

		List<Home> homes = HomeManager.getHomes(uuid);

		if (homes == null || homes.isEmpty()) {
			sender.sendMessage(Strings.PREFIX + "That player doesn't have any homes!");
			return true;
		}

		if (action.equalsIgnoreCase("list")) {
			int listEntries = Math.min(Utilities.config.home.homesSize, 15);

			int page;
			try {
				page = (args.length == 3) ? Integer.parseInt(args[2]) : 1;
			} catch (NumberFormatException e) {
				page = 1;
			}

			int maxPages = (int) Math.ceil((double) homes.size() / (double) listEntries);
			if (page > maxPages || page < 1) {
				sender.sendMessage(Strings.PREFIX + "Page must be smaller or equal to: " + maxPages);
				return true;
			}

			int start = (page - 1) * listEntries;
			int end = page * listEntries;
			if (end > homes.size()) {
				end = homes.size();
			}

			sender.sendMessage("");
			sender.sendMessage(Strings.HOMES_BAR_TOP);
			sender.sendMessage(ChatColor.GRAY + args[0] + " currently has " + ChatColor.AQUA + homes.size() + ChatColor.GRAY + " / " + ChatColor.AQUA + HomeManager.getHomeBalance(uuid) + ChatColor.GRAY + " homes set.");
			sender.sendMessage("");
			for (int i = start; i < end; i++) {
				Home home = homes.get(i);

				BaseComponent[] homeComponent = new ComponentBuilder("")
						.append(ChatColor.GOLD + home.getName())
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
								ChatColor.GOLD + "World" + ChatColor.GRAY + ": " + ChatColor.BLUE + home.getWorld() + "\n" +
										ChatColor.GOLD + "Coords" + ChatColor.GRAY + ": " + ChatColor.BLUE + Utilities.round(home.getX(), 2) + " " + Utilities.round(home.getY(), 2) + " " + Utilities.round(home.getZ(), 2) + "\n" +
										ChatColor.GOLD + "Yaw" + ChatColor.GRAY + ": " + ChatColor.BLUE + home.getYaw() + "\n" +
										ChatColor.GOLD + "Pitch" + ChatColor.GRAY + ": " + ChatColor.BLUE + home.getPitch() + "\n" +
										"\n" +
										ChatColor.AQUA + "Click to teleport to this home!")
						))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/otherhome " + args[0] + " teleport " + home.getName()))
						.create();

				BaseComponent[] deleteComponent = new ComponentBuilder("")
						.append(ChatColor.GRAY + "[" + ChatColor.RED + "\u2715" + ChatColor.GRAY + "]")
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.RED + "Delete this home")))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/otherhome " + args[0] + " delete " + home.getName()))
						.create();

				ComponentBuilder message = new ComponentBuilder();
				message.append(ChatColor.BLUE + "  - ").event((ClickEvent) null).event((HoverEvent) null);

				if (sender.hasPermission(Permissions.DELHOME_OTHER)) {
					message.append(deleteComponent);
					message.append(" ").event((ClickEvent) null).event((HoverEvent) null);
				}

				message.append(homeComponent);

				sender.spigot().sendMessage(message.create());
			}

			sender.sendMessage("");
			TextComponent bottomBar = new TextComponent(TextComponent.fromLegacyText(GradientHandler.hsvGradient("-----------------", new java.awt.Color(251, 121, 0), new java.awt.Color(247, 0, 0), GradientHandler::linear, net.md_5.bungee.api.ChatColor.STRIKETHROUGH)));
			TextComponent pagePrev;
			if (page == 1) {
				pagePrev = new TextComponent(ChatColor.GRAY + "  \u25C0 ");
				pagePrev.setBold(true);
				pagePrev.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.RED + "You are already on the first page!")));
			} else {
				pagePrev = new TextComponent(ChatColor.GREEN + "  \u25C0 ");
				pagePrev.setBold(true);
				pagePrev.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GREEN + "Previous page")));
				pagePrev.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/otherhome " + args[0] + " list " + (page - 1)));
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
				pageNext.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/otherhome " + args[0] + " list " + (page + 1)));
			}
			bottomBar.addExtra(pageNext);
			bottomBar.addExtra(new TextComponent(TextComponent.fromLegacyText(GradientHandler.hsvGradient("-----------------", new java.awt.Color(247, 0, 0), new java.awt.Color(251, 121, 0), GradientHandler::linear, net.md_5.bungee.api.ChatColor.STRIKETHROUGH))));
			sender.spigot().sendMessage(bottomBar);
			sender.sendMessage("");

			return true;
		}

		String name = args[2];

		if (homes.stream().noneMatch(home -> home.getName().equalsIgnoreCase(name))) {
			sender.sendMessage(Strings.PREFIX + "That player doesn't have a home with that name!");
			return true;
		}

		Home home = homes.stream().filter(h -> h.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
		if (home == null) {
			sender.sendMessage(Strings.PREFIX + "Something went wrong!");
			return true;
		}

		Player player = (Player) sender;

		switch (action) {
			case "teleport":
				player.teleport(home.getLocation());
				player.sendMessage(Strings.PREFIX + "Teleported to " + ChatColor.GOLD + args[0] + ChatColor.GRAY + "'s home " + ChatColor.AQUA + home.getName() + ChatColor.GRAY + "!");
				break;
			case "delete":
				if (!sender.hasPermission(Permissions.DELHOME_OTHER)) {
					sender.sendMessage(Strings.NO_PERMISSION);
					return true;
				}
				HomeManager.removeHome(uuid, home.getName());
				DatabaseOperations.removeHome(player.getUniqueId(), name);
				player.sendMessage(Strings.PREFIX + "Deleted " + ChatColor.GOLD + args[0] + ChatColor.GRAY + "'s home " + ChatColor.AQUA + home.getName() + ChatColor.GRAY + "!");
				break;
		}

		return true;
	}

	@Nullable
	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		List<String> arguments = new ArrayList<>();

		if (!sender.hasPermission(Permissions.OTHERHOME))
			return arguments;

		if (args.length == 1) {
			arguments.addAll(PlayerManager.getAllPlayerNames());
		} else if (args.length == 2) {
			arguments.add("list");
			arguments.add("teleport");
			arguments.add("delete");
		} else if (args.length == 3) {
			UUID uuid = PlayerManager.getUuid(args[0]);
			if (uuid == null)
				return arguments;

			List<Home> homes = HomeManager.getHomes(uuid);
			if (homes == null || homes.isEmpty())
				return arguments;

			if (args[1].equalsIgnoreCase("delete") && !sender.hasPermission(Permissions.DELHOME_OTHER))
				return arguments;

			homes.forEach(home -> arguments.add(home.getName()));
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
