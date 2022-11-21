package net.dirtcraft.plugins.dirtessentials.Economy;

import net.dirtcraft.plugins.dirtessentials.Database.Callbacks.CreatePlayer;
import net.dirtcraft.plugins.dirtessentials.Database.EconomyOperations;
import net.dirtcraft.plugins.dirtessentials.Utils.Utilities;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class DirtEconomy implements Economy {

	private final Map<UUID, Double> balances = new HashMap<>();;

	public final Map<UUID, Double> getBalances() {
		return balances;
	}

	public DirtEconomy() {
		EconomyOperations.getBalances(balances -> {
			this.balances.putAll(balances);
			Utilities.log(Level.INFO, "Loaded " + this.balances.size() + " balances from the database.");
		});
	}

	public void addAccount(UUID uuid, double balance) {
		balances.put(uuid, balance);
	}

	/**
	 * Checks if economy method is enabled.
	 *
	 * @return Success or Failure
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}

	/**
	 * Gets name of economy method
	 *
	 * @return Name of Economy Method
	 */
	@Override
	public String getName() {
		return "DirtEconomy";
	}

	/**
	 * Returns true if the given implementation supports banks.
	 *
	 * @return true if the implementation supports banks
	 */
	@Override
	public boolean hasBankSupport() {
		return false;
	}

	/**
	 * Some economy plugins round off after a certain number of digits.
	 * This function returns the number of digits the plugin keeps
	 * or -1 if no rounding occurs.
	 *
	 * @return number of digits after the decimal point kept
	 */
	@Override
	public int fractionalDigits() {
		return 2;
	}

	/**
	 * Format amount into a human-readable String This provides translation into
	 * economy specific formatting to improve consistency between plugins.
	 *
	 * @param amount to format
	 *
	 * @return Human-readable string describing amount
	 */
	@Override
	public String format(double amount) {
		return amount + Utilities.config.economy.currencySymbol;
	}

	/**
	 * Returns the name of the currency in plural form.
	 * If the economy being used does not support currency names then an empty string will be returned.
	 *
	 * @return name of the currency (plural)
	 */
	@Override
	public String currencyNamePlural() {
		return "Dollars";
	}

	/**
	 * Returns the name of the currency in singular form.
	 * If the economy being used does not support currency names then an empty string will be returned.
	 *
	 * @return name of the currency (singular)
	 */
	@Override
	public String currencyNameSingular() {
		return "Dollar";
	}

	/**
	 * @param playerName
	 *
	 * @deprecated As of VaultAPI 1.4 use {@link #hasAccount(OfflinePlayer)} instead.
	 */
	@Override
	public boolean hasAccount(String playerName) {
		Player player = Bukkit.getPlayer(playerName);
		if (player == null) return false;

		return balances.get(player.getUniqueId()) != null;
	}

	/**
	 * Checks if this player has an account on the server yet
	 * This will always return true if the player has joined the server at least once
	 * as all major economy plugins auto-generate a player account when the player joins the server
	 *
	 * @param player to check
	 *
	 * @return if the player has an account
	 */
	@Override
	public boolean hasAccount(OfflinePlayer player) {
		return balances.get(player.getUniqueId()) != null;
	}

	/**
	 * @param playerName
	 * @param worldName
	 *
	 * @deprecated As of VaultAPI 1.4 use {@link #hasAccount(OfflinePlayer, String)} instead.
	 */
	@Override
	public boolean hasAccount(String playerName, String worldName) {
		return hasAccount(playerName);
	}

	/**
	 * Checks if this player has an account on the server yet on the given world
	 * This will always return true if the player has joined the server at least once
	 * as all major economy plugins auto-generate a player account when the player joins the server
	 *
	 * @param player    to check in the world
	 * @param worldName world-specific account
	 *
	 * @return if the player has an account
	 */
	@Override
	public boolean hasAccount(OfflinePlayer player, String worldName) {
		return hasAccount(player);
	}

	/**
	 * @param playerName
	 *
	 * @deprecated As of VaultAPI 1.4 use {@link #getBalance(OfflinePlayer)} instead.
	 */
	@Override
	public double getBalance(String playerName) {
		Player player = Bukkit.getPlayer(playerName);
		if (player == null) return 0;

		return balances.get(player.getUniqueId());
	}

	/**
	 * Gets balance of a player
	 *
	 * @param player of the player
	 *
	 * @return Amount currently held in players account
	 */
	@Override
	public double getBalance(OfflinePlayer player) {
		if (!balances.containsKey(player.getUniqueId())) {
			createPlayerAccount(player);
			return Utilities.config.economy.defaultMoney;
		}

		return balances.get(player.getUniqueId());
	}

	/**
	 * @param playerName
	 * @param world
	 *
	 * @deprecated As of VaultAPI 1.4 use {@link #getBalance(OfflinePlayer, String)} instead.
	 */
	@Override
	public double getBalance(String playerName, String world) {
		return getBalance(playerName);
	}

	/**
	 * Gets balance of a player on the specified world.
	 * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the global balance will be returned.
	 *
	 * @param player to check
	 * @param world  name of the world
	 *
	 * @return Amount currently held in players account
	 */
	@Override
	public double getBalance(OfflinePlayer player, String world) {
		return getBalance(player);
	}

	/**
	 * @param playerName
	 * @param amount
	 *
	 * @deprecated As of VaultAPI 1.4 use {@link #has(OfflinePlayer, double)} instead.
	 */
	@Override
	public boolean has(String playerName, double amount) {
		Player player = Bukkit.getPlayer(playerName);
		if (player == null) return false;

		return balances.get(player.getUniqueId()) >= amount;
	}

	/**
	 * Checks if the player account has the amount - DO NOT USE NEGATIVE AMOUNTS
	 *
	 * @param player to check
	 * @param amount to check for
	 *
	 * @return True if <b>player</b> has <b>amount</b>, False else wise
	 */
	@Override
	public boolean has(OfflinePlayer player, double amount) {
		return balances.get(player.getUniqueId()) >= amount;
	}

	/**
	 * @param playerName
	 * @param worldName
	 * @param amount
	 *
	 * @deprecated As of VaultAPI 1.4 use @{link {@link #has(OfflinePlayer, String, double)} instead.
	 */
	@Override
	public boolean has(String playerName, String worldName, double amount) {
		return has(playerName, amount);
	}

	/**
	 * Checks if the player account has the amount in a given world - DO NOT USE NEGATIVE AMOUNTS
	 * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the global balance will be returned.
	 *
	 * @param player    to check
	 * @param worldName to check with
	 * @param amount    to check for
	 *
	 * @return True if <b>player</b> has <b>amount</b>, False else wise
	 */
	@Override
	public boolean has(OfflinePlayer player, String worldName, double amount) {
		return has(player, amount);
	}

	/**
	 * @param playerName
	 * @param amount
	 *
	 * @deprecated As of VaultAPI 1.4 use {@link #withdrawPlayer(OfflinePlayer, double)} instead.
	 */
	@Override
	public EconomyResponse withdrawPlayer(String playerName, double amount) {
		Player player = Bukkit.getPlayer(playerName);
		if (player == null) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player not found");

		double balance = balances.get(player.getUniqueId());

		if (amount < 0) return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, "No negative amount");

		if (balance < amount) return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, "Not enough money");

		balances.put(player.getUniqueId(), balance - amount);
		EconomyOperations.withdrawMoney(player.getUniqueId(), amount);
		return new EconomyResponse(amount, balance - amount, EconomyResponse.ResponseType.SUCCESS, "");
	}

	/**
	 * Withdraw an amount from a player - DO NOT USE NEGATIVE AMOUNTS
	 *
	 * @param player to withdraw from
	 * @param amount Amount to withdraw
	 *
	 * @return Detailed response of transaction
	 */
	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
		double balance = balances.get(player.getUniqueId());

		if (amount < 0) return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, "No negative amount");

		if (balance < amount) return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, "Not enough money");

		balances.put(player.getUniqueId(), balance - amount);
		EconomyOperations.withdrawMoney(player.getUniqueId(), amount);
		return new EconomyResponse(amount, balance - amount, EconomyResponse.ResponseType.SUCCESS, "");
	}

	/**
	 * @param playerName
	 * @param worldName
	 * @param amount
	 *
	 * @deprecated As of VaultAPI 1.4 use {@link #withdrawPlayer(OfflinePlayer, String, double)} instead.
	 */
	@Override
	public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
		return withdrawPlayer(playerName, amount);
	}

	/**
	 * Withdraw an amount from a player on a given world - DO NOT USE NEGATIVE AMOUNTS
	 * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the global balance will be returned.
	 *
	 * @param player    to withdraw from
	 * @param worldName - name of the world
	 * @param amount    Amount to withdraw
	 *
	 * @return Detailed response of transaction
	 */
	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
		return withdrawPlayer(player, amount);
	}

	/**
	 * @param playerName
	 * @param amount
	 *
	 * @deprecated As of VaultAPI 1.4 use {@link #depositPlayer(OfflinePlayer, double)} instead.
	 */
	@Override
	public EconomyResponse depositPlayer(String playerName, double amount) {
		Player player = Bukkit.getPlayer(playerName);
		if (player == null) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player not found");

		double balance = balances.get(player.getUniqueId());
		balances.put(player.getUniqueId(), balance + amount);
		EconomyOperations.depositMoney(player.getUniqueId(), amount);
		return new EconomyResponse(amount, balance + amount, EconomyResponse.ResponseType.SUCCESS, "");
	}

	/**
	 * Deposit an amount to a player - DO NOT USE NEGATIVE AMOUNTS
	 *
	 * @param player to deposit to
	 * @param amount Amount to deposit
	 *
	 * @return Detailed response of transaction
	 */
	@Override
	public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
		double balance = balances.get(player.getUniqueId());
		balances.put(player.getUniqueId(), balance + amount);
		EconomyOperations.depositMoney(player.getUniqueId(), amount);
		return new EconomyResponse(amount, balance + amount, EconomyResponse.ResponseType.SUCCESS, "");
	}

	/**
	 * @param playerName
	 * @param worldName
	 * @param amount
	 *
	 * @deprecated As of VaultAPI 1.4 use {@link #depositPlayer(OfflinePlayer, String, double)} instead.
	 */
	@Override
	public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
		return depositPlayer(playerName, amount);
	}

	/**
	 * Deposit an amount to a player - DO NOT USE NEGATIVE AMOUNTS
	 * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the global balance will be returned.
	 *
	 * @param player    to deposit to
	 * @param worldName name of the world
	 * @param amount    Amount to deposit
	 *
	 * @return Detailed response of transaction
	 */
	@Override
	public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
		return depositPlayer(player, amount);
	}

	/**
	 * @param name
	 * @param player
	 *
	 * @deprecated As of VaultAPI 1.4 use {{@link #createBank(String, OfflinePlayer)} instead.
	 */
	@Override
	public EconomyResponse createBank(String name, String player) {
		return null;
	}

	/**
	 * Creates a bank account with the specified name and the player as the owner
	 *
	 * @param name   of account
	 * @param player the account should be linked to
	 *
	 * @return EconomyResponse Object
	 */
	@Override
	public EconomyResponse createBank(String name, OfflinePlayer player) {
		return null;
	}

	/**
	 * Deletes a bank account with the specified name.
	 *
	 * @param name of the back to delete
	 *
	 * @return if the operation completed successfully
	 */
	@Override
	public EconomyResponse deleteBank(String name) {
		return null;
	}

	/**
	 * Returns the amount the bank has
	 *
	 * @param name of the account
	 *
	 * @return EconomyResponse Object
	 */
	@Override
	public EconomyResponse bankBalance(String name) {
		return null;
	}

	/**
	 * Returns true or false whether the bank has the amount specified - DO NOT USE NEGATIVE AMOUNTS
	 *
	 * @param name   of the account
	 * @param amount to check for
	 *
	 * @return EconomyResponse Object
	 */
	@Override
	public EconomyResponse bankHas(String name, double amount) {
		return null;
	}

	/**
	 * Withdraw an amount from a bank account - DO NOT USE NEGATIVE AMOUNTS
	 *
	 * @param name   of the account
	 * @param amount to withdraw
	 *
	 * @return EconomyResponse Object
	 */
	@Override
	public EconomyResponse bankWithdraw(String name, double amount) {
		return null;
	}

	/**
	 * Deposit an amount into a bank account - DO NOT USE NEGATIVE AMOUNTS
	 *
	 * @param name   of the account
	 * @param amount to deposit
	 *
	 * @return EconomyResponse Object
	 */
	@Override
	public EconomyResponse bankDeposit(String name, double amount) {
		return null;
	}

	/**
	 * @param name
	 * @param playerName
	 *
	 * @deprecated As of VaultAPI 1.4 use {{@link #isBankOwner(String, OfflinePlayer)} instead.
	 */
	@Override
	public EconomyResponse isBankOwner(String name, String playerName) {
		return null;
	}

	/**
	 * Check if a player is the owner of a bank account
	 *
	 * @param name   of the account
	 * @param player to check for ownership
	 *
	 * @return EconomyResponse Object
	 */
	@Override
	public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
		return null;
	}

	/**
	 * @param name
	 * @param playerName
	 *
	 * @deprecated As of VaultAPI 1.4 use {{@link #isBankMember(String, OfflinePlayer)} instead.
	 */
	@Override
	public EconomyResponse isBankMember(String name, String playerName) {
		return null;
	}

	/**
	 * Check if the player is a member of the bank account
	 *
	 * @param name   of the account
	 * @param player to check membership
	 *
	 * @return EconomyResponse Object
	 */
	@Override
	public EconomyResponse isBankMember(String name, OfflinePlayer player) {
		return null;
	}

	/**
	 * Gets the list of banks
	 *
	 * @return the List of Banks
	 */
	@Override
	public List<String> getBanks() {
		return null;
	}

	/**
	 * @param playerName
	 *
	 * @deprecated As of VaultAPI 1.4 use {{@link #createPlayerAccount(OfflinePlayer)} instead.
	 */
	@Override
	public boolean createPlayerAccount(String playerName) {
		OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
		createPlayerAccount(player);

		return true;
	}

	/**
	 * Attempts to create a player account for the given player
	 *
	 * @param player OfflinePlayer
	 *
	 * @return if the account creation was successful
	 */
	@Override
	public boolean createPlayerAccount(OfflinePlayer player) {
		return EconomyOperations.createPlayerBalance(player.getUniqueId());
	}

	/**
	 * @param playerName
	 * @param worldName
	 *
	 * @deprecated As of VaultAPI 1.4 use {{@link #createPlayerAccount(OfflinePlayer, String)} instead.
	 */
	@Override
	public boolean createPlayerAccount(String playerName, String worldName) {
		return createPlayerAccount(playerName);
	}

	/**
	 * Attempts to create a player account for the given player on the specified world
	 * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this then false will always be returned.
	 *
	 * @param player    OfflinePlayer
	 * @param worldName String name of the world
	 *
	 * @return if the account creation was successful
	 */
	@Override
	public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
		return createPlayerAccount(player);
	}
}
