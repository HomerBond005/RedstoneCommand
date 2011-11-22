package com.bukkit.HomerBond005.RedstoneCommand;

import java.io.File;
import java.io.FileInputStream;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.Configuration;

@SuppressWarnings("deprecation")
public class RSCL extends BlockListener{
    static String mainDir = "plugins/RedstoneCommand";
    static File Locations = new File(mainDir + File.separator + "Locations.yml");
    FileInputStream LocationsInput;
    public Configuration config;
    public static RedstoneCommand plugin;
    public RSCL(RedstoneCommand redstoneCommand){
        config = new Configuration(Locations);
        plugin = redstoneCommand;
    }
	public void onSignChange(SignChangeEvent event){
    	config.load();
        Player player = event.getPlayer();
        BlockState state = event.getBlock().getState();
        if(state instanceof Sign){
        	if(event.getLine(0).equalsIgnoreCase("[rsc]")){
	            if(RedstoneCommand.permissionsystem == 0){
	                if(player.isOp()){
                        if(config.getString("RedstoneCommands.Locations." + event.getLine(1)) != null){
                            player.sendMessage(ChatColor.RED + "The RSC " + ChatColor.GOLD + event.getLine(1) + ChatColor.RED + " already exists.");
                            event.getBlock().setType(Material.AIR);
                            ItemStack signpost = new ItemStack(323, 1);
                            player.getInventory().addItem(new ItemStack[] {
                                signpost
                            });
                            return;
                        }
                        event.getBlock().getFace(BlockFace.NORTH).setType(Material.REDSTONE_TORCH_ON);
                        config.load();
                        config.setProperty((new StringBuilder("RedstoneCommands.Locations.")).append(event.getLine(1)).append(".X").toString(), Integer.valueOf(event.getBlock().getX()));
                        config.setProperty((new StringBuilder("RedstoneCommands.Locations.")).append(event.getLine(1)).append(".Y").toString(), Integer.valueOf(event.getBlock().getY()));
                        config.setProperty((new StringBuilder("RedstoneCommands.Locations.")).append(event.getLine(1)).append(".Z").toString(), Integer.valueOf(event.getBlock().getZ()));
                        config.setProperty((new StringBuilder("RedstoneCommands.Locations.")).append(event.getLine(1)).append(".WORLD").toString(), event.getBlock().getWorld().getName());
                    	int thirdline = 0;
                    	try{
                    		thirdline = Integer.parseInt(event.getLine(2));
                    	}catch(NumberFormatException error){
                    		if(event.getLine(2).toString() != ""&&event.getLine(2) != null){
                    			player.sendMessage(ChatColor.GOLD + event.getLine(2) + ChatColor.RED + " is not a number! Saved with a delay of 0 sec.");
                    		}
                    	}
                    	if(thirdline != 0){
                    		event.getBlock().getFace(BlockFace.NORTH).setType(Material.AIR);
                    	}
                    	config.setProperty((new StringBuilder("RedstoneCommands.Locations.")).append(event.getLine(1)).append(".DELAY").toString(), thirdline);
                        config.save();
                        player.sendMessage(ChatColor.GREEN + "Successfully created RSC named " + ChatColor.GOLD + event.getLine(1));
	                }else{
	                    player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("You aren't an OP!").toString());
	                    event.getBlock().setType(Material.AIR);
	                	ItemStack signpost = new ItemStack(323, 1);
	                    player.getInventory().addItem(new ItemStack[] {
	                        signpost
	                    });
	                }
	            }else if(RedstoneCommand.permissionsystem == 1){
	            	if(player.hasPermission("RSC.create")||player.hasPermission("RSC.*")){
                        if(config.getString("RedstoneCommands.Locations." + event.getLine(1)) != null){
                            player.sendMessage(ChatColor.RED + "The RSC " + ChatColor.GOLD + event.getLine(1) + ChatColor.RED + " already exists.");
                            event.getBlock().setType(Material.AIR);
                            ItemStack signpost = new ItemStack(323, 1);
                            player.getInventory().addItem(new ItemStack[] {
                                signpost
                            });
                            return;
                        }
                        event.getBlock().getFace(BlockFace.NORTH).setType(Material.REDSTONE_TORCH_ON);
                        config.load();
                        config.setProperty((new StringBuilder("RedstoneCommands.Locations.")).append(event.getLine(1)).append(".X").toString(), Integer.valueOf(event.getBlock().getX()));
                        config.setProperty((new StringBuilder("RedstoneCommands.Locations.")).append(event.getLine(1)).append(".Y").toString(), Integer.valueOf(event.getBlock().getY()));
                        config.setProperty((new StringBuilder("RedstoneCommands.Locations.")).append(event.getLine(1)).append(".Z").toString(), Integer.valueOf(event.getBlock().getZ()));
                        config.setProperty((new StringBuilder("RedstoneCommands.Locations.")).append(event.getLine(1)).append(".WORLD").toString(), event.getBlock().getWorld().getName());
                    	int thirdline = 0;
                    	try{
                    		thirdline = Integer.parseInt(event.getLine(2));
                    	}catch(NumberFormatException error){
                    		if(event.getLine(2).toString() != ""&&event.getLine(2) != null){
                    			player.sendMessage(ChatColor.GOLD + event.getLine(2) + ChatColor.RED + " is not a number! Saved with a delay of 0 sec.");
                    		}
                    	}
                    	if(thirdline != 0){
                    		event.getBlock().getFace(BlockFace.NORTH).setType(Material.AIR);
                    	}
                    	config.setProperty((new StringBuilder("RedstoneCommands.Locations.")).append(event.getLine(1)).append(".DELAY").toString(), thirdline);
                        config.save();
                        player.sendMessage(ChatColor.GREEN + "Successfully created RSC named " + ChatColor.GOLD + event.getLine(1));
	            	}else{
	                	player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("You don't have the nessecary permission!").toString());
	                	event.getBlock().setType(Material.AIR);
	                	ItemStack signpost = new ItemStack(323, 1);
	                    player.getInventory().addItem(new ItemStack[] {
	                        signpost
	                    });
	            	}
	            }
            }
        }
    }
}
