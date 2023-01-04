package net.dirtcraft.plugins.dirtessentials.Commands;

import net.dirtcraft.plugins.dirtessentials.Utils.Strings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ShrugCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Strings.NO_CONSOLE);
			return false;
		}

		StringBuilder messageString = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			messageString.append(args[i]).append(" ");
		}
		messageString.append("\u00AF\\_(\u30C4)_/\u00AF");

		((Player) sender).chat(messageString.toString());
		return true;
	}
}
