package net.dirtcraft.plugins.dirtessentials.Commands;

import net.dirtcraft.plugins.dirtessentials.Manager.SpawnManager;
import net.dirtcraft.plugins.dirtessentials.Utils.Permissions;
import net.dirtcraft.plugins.dirtessentials.Utils.Strings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetspawnCommand implements CommandExecutor {
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Strings.NO_CONSOLE);
			return true;
		}

		if (!sender.hasPermission(Permissions.SETSPAWN)) {
			sender.sendMessage(Strings.NO_PERMISSION);
			return true;
		}

		Player player = (Player) sender;
		SpawnManager.setLocation(player.getLocation());
		player.sendMessage(Strings.PREFIX + "Spawn set!");
		return true;
	}
}
