package net.dirtcraft.plugins.dirtessentials.Utils;

import com.moandjiezana.toml.Toml;
import net.dirtcraft.plugins.dirtessentials.Commands.BackCommand;
import net.dirtcraft.plugins.dirtessentials.Commands.BroadcastCommand;
import net.dirtcraft.plugins.dirtessentials.Commands.HelpCommand;
import net.dirtcraft.plugins.dirtessentials.Commands.HomeCommand;
import net.dirtcraft.plugins.dirtessentials.Commands.KitCommand;
import net.dirtcraft.plugins.dirtessentials.Commands.KitsCommand;
import net.dirtcraft.plugins.dirtessentials.Config.*;
import net.dirtcraft.plugins.dirtessentials.DirtEssentials;
import net.dirtcraft.plugins.dirtessentials.Commands.*;
import net.dirtcraft.plugins.dirtessentials.Listener.ConnectionListener;
import net.dirtcraft.plugins.dirtessentials.Listener.MagmaFixListener;
import net.dirtcraft.plugins.dirtessentials.Manager.AfkManager;
import net.dirtcraft.plugins.dirtessentials.Manager.BackManager;
import net.dirtcraft.plugins.dirtessentials.Manager.MessageManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {
	public static Config config;
	public static net.dirtcraft.plugins.dirtessentials.Config.Kits kits;
	public static net.dirtcraft.plugins.dirtessentials.Data.Worth worthData;
	public static Cjm cjm;
	public static net.dirtcraft.plugins.dirtessentials.Config.Help help;
	public static Autobroadcast autobroadcast;

	public static String translate(String message, boolean stripColor) {
		Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
		Matcher matcher = pattern.matcher(message);

		while (matcher.find()) {
			String color = message.substring(matcher.start(), matcher.end());
			message = message.replace(color, ChatColor.of(color.replace("&", "")) + "");
			matcher = pattern.matcher(message);
		}

		if (stripColor) return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message));

		return ChatColor.translateAlternateColorCodes('&', message);
	}

	public static void loadConfig() {
		if (!DirtEssentials.getPlugin().getDataFolder().exists()) {
			DirtEssentials.getPlugin().getDataFolder().mkdirs();
		}
		File file = new File(DirtEssentials.getPlugin().getDataFolder(), "config.toml");
		if (!file.exists()) {
			try {
				Files.copy(DirtEssentials.getPlugin().getResource("config.toml"), file.toPath());
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		config = new Toml(new Toml().read(DirtEssentials.getPlugin().getResource("config.toml"))).read(file).to(Config.class);

		if (config.worth.name.equalsIgnoreCase("")) return;

		try {
			URL url = new URL("https://gitlab.com/DeJustinHD/dirtessentials/-/raw/main/" + config.worth.name + ".json");
			worthData = new ObjectMapper().readValue(url, net.dirtcraft.plugins.dirtessentials.Data.Worth.class);
		} catch (Exception e) {
			Utilities.log(Level.SEVERE, "Failed to load worth data!");
			worthData = null;
			e.printStackTrace();
			return;
		}

		Utilities.log(Level.INFO, "Successfully loaded worth data!");
	}

	public static void loadKits() {
		if (!DirtEssentials.getPlugin().getDataFolder().exists()) {
			DirtEssentials.getPlugin().getDataFolder().mkdirs();
		}
		File file = new File(DirtEssentials.getPlugin().getDataFolder(), "kits.toml");
		if (!file.exists()) {
			try {
				Files.copy(DirtEssentials.getPlugin().getResource("kits.toml"), file.toPath());
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		kits = new Toml(new Toml().read(DirtEssentials.getPlugin().getResource("kits.toml"))).read(file).to(net.dirtcraft.plugins.dirtessentials.Config.Kits.class);
	}

	public static void loadHelp() {
		if (!DirtEssentials.getPlugin().getDataFolder().exists()) {
			DirtEssentials.getPlugin().getDataFolder().mkdirs();
		}
		File file = new File(DirtEssentials.getPlugin().getDataFolder(), "help.toml");
		if (!file.exists()) {
			try {
				Files.copy(DirtEssentials.getPlugin().getResource("help.toml"), file.toPath());
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		help = new Toml(new Toml().read(DirtEssentials.getPlugin().getResource("help.toml"))).read(file).to(net.dirtcraft.plugins.dirtessentials.Config.Help.class);
	}

	public static void loadCjm() {
		if (!DirtEssentials.getPlugin().getDataFolder().exists()) {
			DirtEssentials.getPlugin().getDataFolder().mkdirs();
		}
		File file = new File(DirtEssentials.getPlugin().getDataFolder(), "cjm.toml");
		if (!file.exists()) {
			try {
				Files.copy(DirtEssentials.getPlugin().getResource("cjm.toml"), file.toPath());
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		cjm = new Toml(new Toml().read(DirtEssentials.getPlugin().getResource("cjm.toml"))).read(file).to(Cjm.class);
	}

	public static void loadAB() {
		if (!DirtEssentials.getPlugin().getDataFolder().exists()) {
			DirtEssentials.getPlugin().getDataFolder().mkdirs();
		}
		File file = new File(DirtEssentials.getPlugin().getDataFolder(), "autobroadcast.toml");
		if (!file.exists()) {
			try {
				Files.copy(DirtEssentials.getPlugin().getResource("autobroadcast.toml"), file.toPath());
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		autobroadcast = new Toml(new Toml().read(DirtEssentials.getPlugin().getResource("autobroadcast.toml"))).read(file).to(Autobroadcast.class);
	}

	public static void registerCommands() {
		DirtEssentials.getPlugin().getCommand("repair").setExecutor(new RepairCommand());
		DirtEssentials.getPlugin().getCommand("nbt").setExecutor(new NBTCommand());
		DirtEssentials.getPlugin().getCommand("weather").setExecutor(new WeatherCommand());
		DirtEssentials.getPlugin().getCommand("weather").setTabCompleter(new WeatherCommand());
		DirtEssentials.getPlugin().getCommand("enderchest").setExecutor(new EnderchestCommand());
		DirtEssentials.getPlugin().getCommand("enderchest").setTabCompleter(new EnderchestCommand());
		DirtEssentials.getPlugin().getCommand("top").setExecutor(new TopCommand());
		DirtEssentials.getPlugin().getCommand("broadcast").setExecutor(new BroadcastCommand());
		DirtEssentials.getPlugin().getCommand("smite").setExecutor(new SmiteCommand());
		DirtEssentials.getPlugin().getCommand("smite").setTabCompleter(new SmiteCommand());
		DirtEssentials.getPlugin().getCommand("feed").setExecutor(new FeedCommand());
		DirtEssentials.getPlugin().getCommand("feed").setTabCompleter(new FeedCommand());
		DirtEssentials.getPlugin().getCommand("heal").setExecutor(new HealCommand());
		DirtEssentials.getPlugin().getCommand("heal").setTabCompleter(new HealCommand());
		DirtEssentials.getPlugin().getCommand("unalive").setExecutor(new UnaliveCommand());
		DirtEssentials.getPlugin().getCommand("fly").setTabCompleter(new FlyCommand());
		DirtEssentials.getPlugin().getCommand("fly").setExecutor(new FlyCommand());
		DirtEssentials.getPlugin().getCommand("kill").setExecutor(new KillCommand());
		DirtEssentials.getPlugin().getCommand("kill").setTabCompleter(new KillCommand());
		DirtEssentials.getPlugin().getCommand("hat").setExecutor(new HatCommand());
		DirtEssentials.getPlugin().getCommand("clear").setExecutor(new ClearCommand());
		DirtEssentials.getPlugin().getCommand("clear").setTabCompleter(new ClearCommand());
		DirtEssentials.getPlugin().getCommand("trash").setExecutor(new TrashCommand());
		DirtEssentials.getPlugin().getCommand("gmc").setExecutor(new GmcCommand());
		DirtEssentials.getPlugin().getCommand("gmc").setTabCompleter(new GmcCommand());
		DirtEssentials.getPlugin().getCommand("gms").setExecutor(new GmsCommand());
		DirtEssentials.getPlugin().getCommand("gms").setTabCompleter(new GmsCommand());
		DirtEssentials.getPlugin().getCommand("gma").setExecutor(new GmaCommand());
		DirtEssentials.getPlugin().getCommand("gma").setTabCompleter(new GmaCommand());
		DirtEssentials.getPlugin().getCommand("gmsp").setExecutor(new GmspCommand());
		DirtEssentials.getPlugin().getCommand("gmsp").setTabCompleter(new GmspCommand());
		DirtEssentials.getPlugin().getCommand("time").setExecutor(new TimeCommand());
		DirtEssentials.getPlugin().getCommand("time").setTabCompleter(new TimeCommand());
		DirtEssentials.getPlugin().getCommand("bal").setExecutor(new BalCommand());
		DirtEssentials.getPlugin().getCommand("bal").setTabCompleter(new BalCommand());
		DirtEssentials.getPlugin().getCommand("pay").setExecutor(new PayCommand());
		DirtEssentials.getPlugin().getCommand("pay").setTabCompleter(new PayCommand());
		DirtEssentials.getPlugin().getCommand("eco").setExecutor(new EcoCommand());
		DirtEssentials.getPlugin().getCommand("eco").setTabCompleter(new EcoCommand());
		DirtEssentials.getPlugin().getCommand("baltop").setExecutor(new BaltopCommand());
		DirtEssentials.getPlugin().getCommand("baltop").setTabCompleter(new BaltopCommand());
		DirtEssentials.getPlugin().getCommand("craft").setExecutor(new CraftCommand());
		DirtEssentials.getPlugin().getCommand("cartographytable").setExecutor(new CartographytableCommand());
		DirtEssentials.getPlugin().getCommand("grindstone").setExecutor(new GrindstoneCommand());
		DirtEssentials.getPlugin().getCommand("loom").setExecutor(new LoomCommand());
		DirtEssentials.getPlugin().getCommand("smithingtable").setExecutor(new SmithingtableCommand());
		DirtEssentials.getPlugin().getCommand("stonecutter").setExecutor(new StonecutterCommand());
		DirtEssentials.getPlugin().getCommand("skull").setExecutor(new SkullCommand());
		DirtEssentials.getPlugin().getCommand("skull").setTabCompleter(new SkullCommand());
		DirtEssentials.getPlugin().getCommand("god").setExecutor(new GodCommand());
		DirtEssentials.getPlugin().getCommand("god").setTabCompleter(new GodCommand());
		DirtEssentials.getPlugin().getCommand("xp").setExecutor(new XpCommand());
		DirtEssentials.getPlugin().getCommand("xp").setTabCompleter(new XpCommand());
		DirtEssentials.getPlugin().getCommand("break").setExecutor(new BreakCommand());
		DirtEssentials.getPlugin().getCommand("speed").setExecutor(new SpeedCommand());
		DirtEssentials.getPlugin().getCommand("speed").setTabCompleter(new SpeedCommand());
		DirtEssentials.getPlugin().getCommand("enchant").setExecutor(new EnchantCommand());
		DirtEssentials.getPlugin().getCommand("enchant").setTabCompleter(new EnchantCommand());
		DirtEssentials.getPlugin().getCommand("reload").setExecutor(new ReloadCommand());
		DirtEssentials.getPlugin().getCommand("reload").setTabCompleter(new ReloadCommand());
		DirtEssentials.getPlugin().getCommand("createkit").setExecutor(new CreatekitCommand());
		DirtEssentials.getPlugin().getCommand("createkit").setTabCompleter(new CreatekitCommand());
		DirtEssentials.getPlugin().getCommand("delkit").setExecutor(new DelkitCommand());
		DirtEssentials.getPlugin().getCommand("delkit").setTabCompleter(new DelkitCommand());
		DirtEssentials.getPlugin().getCommand("kits").setExecutor(new KitsCommand());
		DirtEssentials.getPlugin().getCommand("kit").setExecutor(new KitCommand());
		DirtEssentials.getPlugin().getCommand("kit").setTabCompleter(new KitCommand());
		DirtEssentials.getPlugin().getCommand("showkit").setExecutor(new ShowkitCommand());
		DirtEssentials.getPlugin().getCommand("showkit").setTabCompleter(new ShowkitCommand());
		DirtEssentials.getPlugin().getCommand("sethome").setExecutor(new SethomeCommand());
		DirtEssentials.getPlugin().getCommand("home").setExecutor(new HomeCommand());
		DirtEssentials.getPlugin().getCommand("home").setTabCompleter(new HomeCommand());
		DirtEssentials.getPlugin().getCommand("back").setExecutor(new BackCommand());
		DirtEssentials.getPlugin().getCommand("homes").setExecutor(new HomesCommand());
		DirtEssentials.getPlugin().getCommand("delhome").setExecutor(new DelhomeCommand());
		DirtEssentials.getPlugin().getCommand("delhome").setTabCompleter(new DelhomeCommand());
		DirtEssentials.getPlugin().getCommand("otherhome").setExecutor(new OtherhomeCommand());
		DirtEssentials.getPlugin().getCommand("otherhome").setTabCompleter(new OtherhomeCommand());
		DirtEssentials.getPlugin().getCommand("tppos").setExecutor(new TpposCommand());
		DirtEssentials.getPlugin().getCommand("tppos").setTabCompleter(new TpposCommand());
		DirtEssentials.getPlugin().getCommand("seen").setExecutor(new SeenCommand());
		DirtEssentials.getPlugin().getCommand("seen").setTabCompleter(new SeenCommand());
		DirtEssentials.getPlugin().getCommand("sudo").setExecutor(new SudoCommand());
		DirtEssentials.getPlugin().getCommand("sudo").setTabCompleter(new SudoCommand());
		DirtEssentials.getPlugin().getCommand("nick").setExecutor(new NickCommand());
		DirtEssentials.getPlugin().getCommand("nick").setTabCompleter(new NickCommand());
		DirtEssentials.getPlugin().getCommand("othernick").setExecutor(new OthernickCommand());
		DirtEssentials.getPlugin().getCommand("othernick").setTabCompleter(new OthernickCommand());
		DirtEssentials.getPlugin().getCommand("itemname").setExecutor(new ItemnameCommand());
		DirtEssentials.getPlugin().getCommand("lore").setExecutor(new LoreCommand());
		DirtEssentials.getPlugin().getCommand("lore").setTabCompleter(new LoreCommand());
		DirtEssentials.getPlugin().getCommand("setwarp").setExecutor(new SetwarpCommand());
		DirtEssentials.getPlugin().getCommand("setwarp").setTabCompleter(new SetwarpCommand());
		DirtEssentials.getPlugin().getCommand("warp").setExecutor(new WarpCommand());
		DirtEssentials.getPlugin().getCommand("warp").setTabCompleter(new WarpCommand());
		DirtEssentials.getPlugin().getCommand("warps").setExecutor(new WarpsCommand());
		DirtEssentials.getPlugin().getCommand("delwarp").setExecutor(new DelwarpCommand());
		DirtEssentials.getPlugin().getCommand("delwarp").setTabCompleter(new DelwarpCommand());
		DirtEssentials.getPlugin().getCommand("setwarpicon").setExecutor(new SetwarpiconCommand());
		DirtEssentials.getPlugin().getCommand("setwarpicon").setTabCompleter(new SetwarpiconCommand());
		DirtEssentials.getPlugin().getCommand("whois").setExecutor(new WhoisCommand());
		DirtEssentials.getPlugin().getCommand("whois").setTabCompleter(new WhoisCommand());
		DirtEssentials.getPlugin().getCommand("listplayers").setExecutor(new ListplayersCommand());
		DirtEssentials.getPlugin().getCommand("help").setExecutor(new HelpCommand());
		DirtEssentials.getPlugin().getCommand("help").setTabCompleter(new HelpCommand());
		DirtEssentials.getPlugin().getCommand("afk").setExecutor(new AfkCommand());
		DirtEssentials.getPlugin().getCommand("msg").setExecutor(new MsgCommand());
		DirtEssentials.getPlugin().getCommand("msg").setTabCompleter(new MsgCommand());
		DirtEssentials.getPlugin().getCommand("reply").setExecutor(new ReplyCommand());
		DirtEssentials.getPlugin().getCommand("reply").setTabCompleter(new ReplyCommand());
		DirtEssentials.getPlugin().getCommand("socialspy").setExecutor(new SocialspyCommand());
		DirtEssentials.getPlugin().getCommand("msgtoggle").setExecutor(new MsgtoggleCommand());
		DirtEssentials.getPlugin().getCommand("tptoggle").setExecutor(new TptoggleCommand());
		DirtEssentials.getPlugin().getCommand("tpall").setExecutor(new TpallCommand());
		DirtEssentials.getPlugin().getCommand("tp").setExecutor(new TpCommand());
		DirtEssentials.getPlugin().getCommand("tp").setTabCompleter(new TpCommand());
		DirtEssentials.getPlugin().getCommand("tphere").setExecutor(new TphereCommand());
		DirtEssentials.getPlugin().getCommand("tphere").setTabCompleter(new TphereCommand());
		DirtEssentials.getPlugin().getCommand("tpa").setExecutor(new TpaCommand());
		DirtEssentials.getPlugin().getCommand("tpa").setTabCompleter(new TpaCommand());
		DirtEssentials.getPlugin().getCommand("tpaccept").setExecutor(new TpacceptCommand());
		DirtEssentials.getPlugin().getCommand("tpdeny").setExecutor(new TpdenyCommand());
		DirtEssentials.getPlugin().getCommand("tpahere").setExecutor(new TpahereCommand());
		DirtEssentials.getPlugin().getCommand("tpahere").setTabCompleter(new TpahereCommand());
		DirtEssentials.getPlugin().getCommand("tpacancel").setExecutor(new TpacancelCommand());
		DirtEssentials.getPlugin().getCommand("spawn").setExecutor(new SpawnCommand());
		DirtEssentials.getPlugin().getCommand("spawn").setTabCompleter(new SpawnCommand());
		DirtEssentials.getPlugin().getCommand("setspawn").setExecutor(new SetspawnCommand());
		DirtEssentials.getPlugin().getCommand("worths").setExecutor(new WorthsCommand());
		DirtEssentials.getPlugin().getCommand("worth").setExecutor(new WorthCommand());
		DirtEssentials.getPlugin().getCommand("worth").setTabCompleter(new WorthCommand());
		DirtEssentials.getPlugin().getCommand("homebalance").setExecutor(new HomebalanceCommand());
		DirtEssentials.getPlugin().getCommand("homebalance").setTabCompleter(new HomebalanceCommand());
		DirtEssentials.getPlugin().getCommand("toggleautobroadcast").setExecutor(new ToggleautobroadcastCommand());
		DirtEssentials.getPlugin().getCommand("note").setExecutor(new NoteCommand());
		DirtEssentials.getPlugin().getCommand("note").setTabCompleter(new NoteCommand());
		DirtEssentials.getPlugin().getCommand("blockzap").setExecutor(new BlockzapCommand());
		DirtEssentials.getPlugin().getCommand("blockzap").setTabCompleter(new BlockzapCommand());
		DirtEssentials.getPlugin().getCommand("entityzap").setExecutor(new EntityzapCommand());
		DirtEssentials.getPlugin().getCommand("entityzap").setTabCompleter(new EntityzapCommand());
		DirtEssentials.getPlugin().getCommand("radiuszap").setExecutor(new RadiusZapCommand());
		DirtEssentials.getPlugin().getCommand("radiuszap").setTabCompleter(new RadiusZapCommand());
		DirtEssentials.getPlugin().getCommand("respect").setExecutor(new RespectCommand());
		DirtEssentials.getPlugin().getCommand("shrug").setExecutor(new ShrugCommand());
		DirtEssentials.getPlugin().getCommand("worldname").setExecutor(new WorldnameCommand());
		DirtEssentials.getPlugin().getCommand("exportrecipes").setExecutor(new ExportrecipesCommand());
		DirtEssentials.getPlugin().getCommand("listautobroadcasts").setExecutor(new ListautobroadcastsCommand());
		DirtEssentials.getPlugin().getCommand("bill").setExecutor(new BillCommand());
		DirtEssentials.getPlugin().getCommand("bill").setTabCompleter(new BillCommand());
		DirtEssentials.getPlugin().getCommand("resetplayerdata").setExecutor(new ResetPlayerDataCommand());
		DirtEssentials.getPlugin().getCommand("resetplayerdata").setTabCompleter(new ResetPlayerDataCommand());
		DirtEssentials.getPlugin().getCommand("kickall").setExecutor(new KickallCommand());
		DirtEssentials.getPlugin().getCommand("buyhome").setExecutor(new BuyhomeCommand());
		DirtEssentials.getPlugin().getCommand("gc").setExecutor(new GcCommand());
	}

	public static void registerListeners() {
		DirtEssentials.getPlugin().getServer().getPluginManager().registerEvents(new FlyCommand(), DirtEssentials.getPlugin());
		DirtEssentials.getPlugin().getServer().getPluginManager().registerEvents(new ConnectionListener(), DirtEssentials.getPlugin());
		DirtEssentials.getPlugin().getServer().getPluginManager().registerEvents(new GodCommand(), DirtEssentials.getPlugin());
		DirtEssentials.getPlugin().getServer().getPluginManager().registerEvents(new ShowkitCommand(), DirtEssentials.getPlugin());
		DirtEssentials.getPlugin().getServer().getPluginManager().registerEvents(new BackManager(), DirtEssentials.getPlugin());
		DirtEssentials.getPlugin().getServer().getPluginManager().registerEvents(new WarpsCommand(), DirtEssentials.getPlugin());
		DirtEssentials.getPlugin().getServer().getPluginManager().registerEvents(new AfkManager(), DirtEssentials.getPlugin());
		DirtEssentials.getPlugin().getServer().getPluginManager().registerEvents(new MessageManager(), DirtEssentials.getPlugin());
		DirtEssentials.getPlugin().getServer().getPluginManager().registerEvents(new MagmaFixListener(), DirtEssentials.getPlugin());
	}

	public static void log(Level level, String msg) {
		DirtEssentials.getPlugin().getLogger().log(level, msg);
	}

	public static void disablePlugin() {
		DirtEssentials.getPlugin().getServer().getPluginManager().disablePlugin(DirtEssentials.getPlugin());
	}

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException | NullPointerException e) {
			return false;
		}

		return true;
	}

	public static boolean isDouble(String s) {
		try {
			Double.parseDouble(s);
		} catch (NumberFormatException | NullPointerException e) {
			return false;
		}

		return true;
	}

	public static boolean isFloat(String s) {
		try {
			Float.parseFloat(s);
		} catch (NumberFormatException | NullPointerException e) {
			return false;
		}

		return true;
	}

	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();

		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static List<Player> getOnlineStaff() {
		List<Player> staff = new ArrayList<>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.hasPermission("dirtessentials.staff")) {
				staff.add(player);
			}
		}
		return staff;
	}
}
