package de.HomerBond005.RedstoneCommand;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class RSC {
	private final String name;
	private final int x;
	private final int y;
	private final int z;
	private final World world;
	private final int xchange;
	private final int ychange;
	private final int zchange;
	private final int delay;
	private final boolean dispmsgs;

	public RSC(String name, int x, int y, int z, World world, int delay,
			int xchange, int ychange, int zchange, boolean dispmsgs) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
		this.delay = delay;
		this.xchange = xchange;
		this.ychange = ychange;
		this.zchange = zchange;
		this.dispmsgs = dispmsgs;
	}

	public String getName() {
		return name;
	}

	public int getDelay() {
		return delay;
	}

	public boolean isON() {
		getTorchLocation().getChunk().load();
		if (getTorchLocation().getBlock().getType() == Material.REDSTONE_TORCH_ON)
			return true;
		else
			return false;
	}

	public Location getTorchLocation() {
		return new Location(world, x, y, z).add(xchange, ychange, zchange);
	}

	public Location getSignLocation() {
		return new Location(world, x, y, z);
	}

	public void turnON() {
		getTorchLocation().getChunk().load();
		getTorchLocation().getBlock().setType(Material.REDSTONE_TORCH_ON);
	}

	public void turnOFF() {
		getTorchLocation().getChunk().load();
		getTorchLocation().getBlock().setType(Material.AIR);
	}

	public boolean displayMessages() {
		return dispmsgs;
	}
}
