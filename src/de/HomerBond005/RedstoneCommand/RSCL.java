/*
 * Copyright HomerBond005
 * 
 *  Published under CC BY-NC-ND 3.0
 *  http://creativecommons.org/licenses/by-nc-nd/3.0/
 */
package de.HomerBond005.RedstoneCommand;

import java.io.File;
import java.io.FileInputStream;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

public class RSCL implements Listener{
    static String mainDir = "plugins/RedstoneCommand";
    static File configfile = new File(mainDir + File.separator + "config.yml");
    FileInputStream LocationsInput;
    public FileConfiguration config;
    public static RedstoneCommand plugin;
    public RSCL(RedstoneCommand redstoneCommand){
        config = YamlConfiguration.loadConfiguration(configfile);
        plugin = redstoneCommand;
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event){
    	if(event.getBlock().getType() == Material.SIGN||event.getBlock().getType() == Material.SIGN_POST){
    		Sign sign = (Sign) event.getBlock().getState();
    		if(sign.getLine(0).equalsIgnoreCase("[rsc]")){
    			event.getPlayer().sendMessage(ChatColor.RED + "Please remove this RSC via /rsc delete " + sign.getLine(1) + "!");
    			event.setCancelled(true);
    		}
    	}
    }
    @EventHandler(priority = EventPriority.HIGH)
	public void onSignChange(SignChangeEvent event){
    	try{
    		config.load(configfile);
    	}catch(Exception e){}
        Player player = event.getPlayer();
        BlockState state = event.getBlock().getState();
        if(state instanceof Sign){
        	if(event.getLine(0).equalsIgnoreCase("[rsc]")){
	            if(!plugin.pc.has(player, "RSC.create")||!plugin.pc.has(player, "RSC.*")){
                    plugin.pc.sendNoPermMsg(player);
                    event.getBlock().setType(Material.AIR);
                	ItemStack signpost = new ItemStack(323, 1);
                    player.getInventory().addItem(new ItemStack[] {
                        signpost
                    });
                    return;
	            }
	            if(config.getString("RedstoneCommands.Locations." + event.getLine(1)) != null){
                    player.sendMessage(ChatColor.RED + "The RSC " + ChatColor.GOLD + event.getLine(1) + ChatColor.RED + " already exists.");
                    event.getBlock().setType(Material.AIR);
                    ItemStack signpost = new ItemStack(323, 1);
                    player.getInventory().addItem(new ItemStack[] {
                        signpost
                    });
                    return;
                }
                event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation().add(-1, 0, 0)).setType(Material.REDSTONE_TORCH_ON);
                try {
					config.load(configfile);
				}catch(Exception e){}
                config.set("RedstoneCommands.Locations." + event.getLine(1) + ".X", Integer.valueOf(event.getBlock().getX()));
                config.set("RedstoneCommands.Locations." + event.getLine(1) + ".Y", Integer.valueOf(event.getBlock().getY()));
                config.set("RedstoneCommands.Locations." + event.getLine(1) + ".Z", Integer.valueOf(event.getBlock().getZ()));
                config.set("RedstoneCommands.Locations." + event.getLine(1) + ".WORLD", event.getBlock().getWorld().getName());
            	int thirdline = 0;
            	try{
            		thirdline = Integer.parseInt(event.getLine(2));
            	}catch(NumberFormatException error){
            		if(!event.getLine(2).isEmpty()){
            			player.sendMessage(ChatColor.GOLD + event.getLine(2) + ChatColor.RED + " is not a number! Saved with a delay of 0 sec.");
            		}
            		event.setLine(2, "0");
            	}
            	if(thirdline != 0){
            		event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation().add(-1, 0, 0)).setType(Material.AIR);
            	}
            	config.set("RedstoneCommands.Locations." + event.getLine(1) + ".DELAY", thirdline);
            	try{
                	config.save(configfile);
        		}catch(Exception e){}
                player.sendMessage(ChatColor.GREEN + "Successfully created RSC named " + ChatColor.GOLD + event.getLine(1));
            }
        }
    }
}
