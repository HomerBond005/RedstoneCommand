package de.HomerBond005.RedstoneCommand;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class RSC{
	private String name;
	private int x;
	private int y;
	private int z;
	private World world;
	private int xchange;
	private int ychange;
	private int zchange;
	private int delay;
	private boolean dispmsgs;
	
	public RSC(String name, int x, int y, int z, World world, int delay, int xchange, int ychange, int zchange, boolean dispmsgs){
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
	
	public String getName(){
		return name;
	}
	
	public int getDelay(){
		return delay;
	}
	
	public boolean isON(){
		getTorchLocation().getChunk().load();
		if(getTorchLocation().getBlock().getType() == Material.REDSTONE_TORCH_ON)
			return true;
		else
			return false;
	}
	
	public Location getTorchLocation(){
		return new Location(world, x, y, z).add(xchange, ychange, zchange);
	}
	
	public Location getSignLocation(){
		return new Location(world, x, y, z);
	}
	
	public void turnON(){
		getTorchLocation().getChunk().load();
		getTorchLocation().getBlock().setType(Material.REDSTONE_TORCH_ON);
	}
	
	public void turnOFF(){
		getTorchLocation().getChunk().load();
		getTorchLocation().getBlock().setType(Material.AIR);
	}
	
	public boolean displayMessages(){
		return dispmsgs;
	}
}
