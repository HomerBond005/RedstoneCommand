/*
 * Copyright HomerBond005
 * 
 *  Published under CC BY-NC-ND 3.0
 *  http://creativecommons.org/licenses/by-nc-nd/3.0/
 */
package de.HomerBond005.RedstoneCommand;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

public class RSCL implements Listener {
	public static RedstoneCommand plugin;

	public RSCL(RedstoneCommand redstoneCommand) {
		RSCL.plugin = redstoneCommand;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getBlock().getType() == Material.SIGN || event.getBlock().getType() == Material.SIGN_POST) {
			Sign sign = (Sign) event.getBlock().getState();
			if (sign.getLine(0).equalsIgnoreCase("[rsc]")) {
				event.getPlayer().sendMessage(ChatColor.RED + "Please remove this RSC via /rsc delete " + sign.getLine(1) + "!");
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onSignChange(SignChangeEvent event) {
		RSCL.plugin.reloadConfig();
		Player player = event.getPlayer();
		BlockState state = event.getBlock().getState();
		if (state instanceof Sign) {
			if (event.getLine(0).equalsIgnoreCase("[rsc]")) {
				if (!RSCL.plugin.pc.has(player, "RSC.create") || !RSCL.plugin.pc.has(player, "RSC.*")) {
					RSCL.plugin.pc.sendNoPermMsg(player);
					event.getBlock().setType(Material.AIR);
					ItemStack signpost = new ItemStack(323, 1);
					player.getInventory().addItem(new ItemStack[] { signpost });
					return;
				}
				if (RSCL.plugin.getConfig().getString("RedstoneCommands.Locations." + event.getLine(1)) != null) {
					player.sendMessage(ChatColor.RED + "The RSC " + ChatColor.GOLD + event.getLine(1) + ChatColor.RED + " already exists.");
					event.getBlock().setType(Material.AIR);
					ItemStack signpost = new ItemStack(323, 1);
					player.getInventory().addItem(new ItemStack[] { signpost });
					return;
				}
				RSCL.plugin.reloadConfig();
				int delay = 0;
				try {
					delay = Integer.parseInt(event.getLine(2));
				} catch (NumberFormatException error) {
					if (!event.getLine(2).isEmpty()) {
						player.sendMessage(ChatColor.GOLD + event.getLine(2) + ChatColor.RED + " is not a number! Saved with a delay of 0 sec.");
					}
					event.setLine(2, "0");
				}
				BlockFace direction;
				if (!event.getLine(3).isEmpty()) {
					try {
						direction = BlockFace.valueOf(event.getLine(3).toUpperCase());
					} catch (IllegalArgumentException e) {
						player.sendMessage(ChatColor.RED + "Wrong torch direction!");
						player.sendMessage(ChatColor.RED + "Possible values are: NORTH, EAST, SOUTH, WEST, UP, DOWN, NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST");
						event.getBlock().setType(Material.AIR);
						player.getInventory().addItem(new ItemStack[] { new ItemStack(323, 1) });
						return;
					}
				} else if (RSCL.plugin.getSignPlaceDirectionModeEnabled()) {
					byte signdata = event.getBlock().getData();
					if (event.getBlock().getState().getType() == Material.WALL_SIGN) {
						if (signdata == 4)
							direction = BlockFace.NORTH;
						else if (signdata == 2)
							direction = BlockFace.EAST;
						else if (signdata == 5)
							direction = BlockFace.SOUTH;
						else if (signdata == 3)
							direction = BlockFace.WEST;
						else
							direction = BlockFace.NORTH;
					} else {
						if (signdata == 4)
							direction = BlockFace.NORTH;
						else if (signdata == 8)
							direction = BlockFace.EAST;
						else if (signdata == 12)
							direction = BlockFace.SOUTH;
						else if (signdata == 0)
							direction = BlockFace.WEST;
						else if (signdata > 0 && signdata < 4)
							direction = BlockFace.NORTH_WEST;
						else if (signdata > 4 && signdata < 8)
							direction = BlockFace.NORTH_EAST;
						else if (signdata > 8 && signdata < 12)
							direction = BlockFace.SOUTH_EAST;
						else if (signdata > 12)
							direction = BlockFace.SOUTH_WEST;
						else
							direction = BlockFace.NORTH;
					}
				} else {
					direction = BlockFace.NORTH;
				}
				player.sendMessage(ChatColor.GREEN + "Redstone torch will be placed: " + direction);
				RSCL.plugin.getConfig().set("RedstoneCommands.Locations." + event.getLine(1) + ".X", Integer.valueOf(event.getBlock().getX()));
				RSCL.plugin.getConfig().set("RedstoneCommands.Locations." + event.getLine(1) + ".Y", Integer.valueOf(event.getBlock().getY()));
				RSCL.plugin.getConfig().set("RedstoneCommands.Locations." + event.getLine(1) + ".Z", Integer.valueOf(event.getBlock().getZ()));
				RSCL.plugin.getConfig().set("RedstoneCommands.Locations." + event.getLine(1) + ".WORLD", event.getBlock().getWorld().getName());
				RSCL.plugin.getConfig().set("RedstoneCommands.Locations." + event.getLine(1) + ".Xchange", direction.getModX());
				RSCL.plugin.getConfig().set("RedstoneCommands.Locations." + event.getLine(1) + ".Ychange", direction.getModY());
				RSCL.plugin.getConfig().set("RedstoneCommands.Locations." + event.getLine(1) + ".Zchange", direction.getModZ());
				RSCL.plugin.getConfig().set("RedstoneCommands.Locations." + event.getLine(1) + ".DELAY", delay);
				if (delay != 0) {
					event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation().add(direction.getModX(), direction.getModY(), direction.getModZ())).setType(Material.AIR);
				} else {
					event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation().add(direction.getModX(), direction.getModY(), direction.getModZ())).setType(Material.REDSTONE_TORCH_ON);
				}
				RSCL.plugin.saveConfig();
				RSCL.plugin.reloadRSCs();
				player.sendMessage(ChatColor.GREEN + "Successfully created RSC named " + ChatColor.GOLD + event.getLine(1));
			}
		}
	}
}
