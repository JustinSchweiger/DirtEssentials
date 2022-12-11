package net.dirtcraft.plugins.dirtessentials.Data;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class Warp {
	private String name;
	private Location location;
	private ItemStack icon;

	public Warp(String name, Location location, ItemStack icon) {
		this.name = name;
		this.location = location;
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public ItemStack getIcon() {
		return icon;
	}

	public void setIcon(ItemStack icon) {
		this.icon = icon;
	}
}
