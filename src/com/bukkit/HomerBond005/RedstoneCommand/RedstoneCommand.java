package com.bukkit.HomerBond005.RedstoneCommand;

import java.io.*;

import javax.swing.SwingUtilities;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

@SuppressWarnings("deprecation")
public class RedstoneCommand extends JavaPlugin{
	static PermissionManager permissions;
	public static int permissionsystem;
    static String mainDir = "plugins/RedstoneCommand";
    static File locationsfile = new File(mainDir + File.separator + "Locations.yml");
    static File configfile = new File(mainDir + File.separator + "config.yml");
    public Configuration config;
    private final RSCL blocklistener = new RSCL(this);
    public void onEnable(){
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(org.bukkit.event.Event.Type.SIGN_CHANGE, blocklistener, org.bukkit.event.Event.Priority.High, this);
        pm.registerEvent(org.bukkit.event.Event.Type.BLOCK_BREAK, blocklistener, org.bukkit.event.Event.Priority.Normal, this);
        (new File(mainDir)).mkdir();
        config = new Configuration(configfile);
        if(!configfile.exists()){
        	if(locationsfile.exists()){
	    		locationsfile.renameTo(configfile);
	    		System.out.println("[RedstoneCommand]: Locations.yml renamed to config.yml.");
	    	}else{
	            try
	            {
	                String root = "RedstoneCommands.Locations";
	                configfile.createNewFile();
	                config.setProperty(root, "{}");
	                config.setProperty("RedstoneCommands.permissionsEnabled", 0);
	                config.save();
	                System.out.println("[RedstoneCommand]: config.yml created.");
	            }
	            catch(IOException e)
	            {
	                e.printStackTrace();
	            }
	    	}
        }
        config.load();
    	try{
    		int permEn = config.getInt("RedstoneCommands.permissionsEnabled", 2);
    		if(permEn == 2){
    			System.out.println("[RedstoneCommand]: permissionsEnabled has a wrong value! Changing to 0");
	    		config.setProperty("RedstoneCommands.permissionsEnabled", 0);
    		}
    	}catch(NullPointerException e){
    		config.setProperty("RedstoneCommands.permissionsEnabled", 0);
    		System.out.println("[RedstoneCommand]: Update to v2.5. To enable permissions, change permissionsEnabled to 1.");
    	}
    	config.save();
    	config.load();
    	if(config.getInt("RedstoneCommands.permissionsEnabled", 2) == 1){
    		if(getServer().getPluginManager().isPluginEnabled("PermissionsEx")){
    		    permissions = PermissionsEx.getPermissionManager();
    		    permissionsystem = 2;
    		    System.out.println("[RedstoneCommand]: Using PermissionsEx!");
    		}else{
    			permissionsystem = 1;
    			System.out.println("[RedstoneCommand]: Using Bukkit Permissions!");
    		}
    	}else{
    		permissionsystem = 0;
    		System.out.println("[RedstoneCommand]: Defaulting to OP-only.");
    	}
        System.out.println("[RedstoneCommand]: config.yml loaded.");
        System.out.println("[RedstoneCommand] is enabled!");
    }
    public void onDisable()
    {
        System.out.println("[RedstoneCommand] is disabled!");
    }
    private void deleteRSC(Player player, String name)
    {
        if(name != null)
        {
            config.load();
            if(config.getString("RedstoneCommands.Locations." + name) != null)
            {
                int x = ((Integer)config.getProperty((new StringBuilder("RedstoneCommands.Locations.")).append(name).append(".X").toString())).intValue();
                int y = ((Integer)config.getProperty((new StringBuilder("RedstoneCommands.Locations.")).append(name).append(".Y").toString())).intValue();
                int z = ((Integer)config.getProperty((new StringBuilder("RedstoneCommands.Locations.")).append(name).append(".Z").toString())).intValue();
                World world = getServer().getWorld(config.getProperty("RedstoneCommands.Locations." + name + ".WORLD").toString());
                config.removeProperty((new StringBuilder("RedstoneCommands.Locations.")).append(name).toString());
                Location torchposition = new Location(world, x - 1, y, z);
                Location signposition = new Location(world, x, y, z);
                signposition.getChunk().load();
                torchposition.getChunk().load();
                signposition.getBlock().setType(Material.AIR);
                torchposition.getBlock().setType(Material.AIR);
                config.save();
                player.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("Successfully deleted RSC ").append(ChatColor.GOLD).append(name).toString());
            } else{
                player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("The RSC ").append(ChatColor.GOLD).append(name).append(ChatColor.RED).append(" doesn's exist.").toString());
            }
        } else{
            player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Wrong syntax! Try: /rsc delete [name]").toString());
        }
    }
    private void toggleRSC(final Player player, final String name){
        config.load();
        if(config.getString("RedstoneCommands.Locations." + name) == null){
        	player.sendMessage(ChatColor.RED + "The following RSC doesn't exist: " + ChatColor.GOLD + name);
        	  return;
        }
        int x = ((Integer)config.getProperty((new StringBuilder("RedstoneCommands.Locations.")).append(name).append(".X").toString())).intValue();
        int y = ((Integer)config.getProperty((new StringBuilder("RedstoneCommands.Locations.")).append(name).append(".Y").toString())).intValue();
        int z = ((Integer)config.getProperty((new StringBuilder("RedstoneCommands.Locations.")).append(name).append(".Z").toString())).intValue();
        World world = getServer().getWorld(config.getProperty("RedstoneCommands.Locations." + name + ".WORLD").toString());
        final Location position = new Location(world, x - 1, y, z);
        if(position.getBlock().getType() == Material.REDSTONE_TORCH_ON){
        	position.getChunk().load();
            position.getBlock().setType(Material.AIR);
            player.sendMessage(ChatColor.GREEN + "Successfully toggled RSC named " + ChatColor.GOLD + name);
        }
        else{
        	Runnable delayedrun = new Runnable(){
        		public void run(){
		            if(config.getInt("RedstoneCommands.Locations." + name + ".DELAY", 0) != 0){
		            	try {
		    				Thread.sleep(Integer.parseInt(config.getProperty("RedstoneCommands.Locations." + name + ".DELAY").toString())*1000);
		    			} catch (NumberFormatException e) {
		    				e.printStackTrace();
		    			} catch (InterruptedException e) {
		    				e.printStackTrace();
		    			}
		    			position.getChunk().load();
		    			position.getBlock().setType(Material.AIR);
		    			player.sendMessage(ChatColor.GREEN + "Successfully delayed RSC named " + ChatColor.GOLD + name);
		            }else{
		            	player.sendMessage(ChatColor.GREEN + "Successfully toggled RSC named " + ChatColor.GOLD + name);
		            }
        		}
        	};
        	SwingUtilities.invokeLater(delayedrun);
        	position.getBlock().setType(Material.REDSTONE_TORCH_ON);
        }
        config.save();
    }
    private void rscON(Player player, final String name){
    	config.load();
        if(config.getString("RedstoneCommands.Locations." + name) == null){
        	player.sendMessage(ChatColor.RED + "The following RSC doesn't exist: " + ChatColor.GOLD + name);
        	  return;
        }
        int x = ((Integer)config.getProperty((new StringBuilder("RedstoneCommands.Locations.")).append(name).append(".X").toString())).intValue();
        int y = ((Integer)config.getProperty((new StringBuilder("RedstoneCommands.Locations.")).append(name).append(".Y").toString())).intValue();
        int z = ((Integer)config.getProperty((new StringBuilder("RedstoneCommands.Locations.")).append(name).append(".Z").toString())).intValue();
        World world = getServer().getWorld(config.getProperty("RedstoneCommands.Locations." + name + ".WORLD").toString());
        final Location position = new Location(world, x - 1, y, z);
        position.getChunk().load();
        position.getBlock().setType(Material.REDSTONE_TORCH_ON);
    	player.sendMessage(ChatColor.GREEN + "Successfully turned on RSC named " + ChatColor.GOLD + name);
    }
    private void rscOFF(Player player, final String name){
    	config.load();
        if(config.getString("RedstoneCommands.Locations." + name) == null){
        	  player.sendMessage(ChatColor.RED + "The following RSC doesn't exist: " + ChatColor.GOLD + name);
        	  return;
        }
        int x = ((Integer)config.getProperty((new StringBuilder("RedstoneCommands.Locations.")).append(name).append(".X").toString())).intValue();
        int y = ((Integer)config.getProperty((new StringBuilder("RedstoneCommands.Locations.")).append(name).append(".Y").toString())).intValue();
        int z = ((Integer)config.getProperty((new StringBuilder("RedstoneCommands.Locations.")).append(name).append(".Z").toString())).intValue();
        World world = getServer().getWorld(config.getProperty("RedstoneCommands.Locations." + name + ".WORLD").toString());
        final Location position = new Location(world, x - 1, y, z);
        position.getChunk().load();
        position.getBlock().setType(Material.AIR);
        player.sendMessage(ChatColor.GREEN + "Successfully turned off RSC named " + ChatColor.GOLD + name);
    }
    private String[] listRSC(){
        config.load();
        String rscs[] = config.getNodes("RedstoneCommands.Locations").toString().split(", ");
        rscs[0] = rscs[0].substring(1);
        return rscs;
    }
    private void PLAYERlistRSC(Player player){
        player.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("Following RSCs are set:").toString());
        String rscsstring = "";
        for(int i = 0; i < listRSC().length; i++){
            if(listRSC()[i].toLowerCase().equals("}")){
                player.sendMessage((new StringBuilder()).append(ChatColor.GRAY).append("No RSCs are set.").toString());
                return;
            }
            if(i + 1 == listRSC().length)
                rscsstring = (new StringBuilder(String.valueOf(rscsstring))).append(listRSC()[i].split("=")[0]).toString();
            else
                rscsstring = (new StringBuilder(String.valueOf(rscsstring))).append(listRSC()[i].split("=")[0]).append(", ").toString();
        }
        player.sendMessage((new StringBuilder()).append(ChatColor.GOLD).append(rscsstring).toString());
    }
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String args[]){
    	Player player = null;
    	try{
    		player = (Player)sender;
    	}catch(ClassCastException e){
    		ConsoleHandler handler = new ConsoleHandler(this);
    		handler.handleConsole(sender, command, args);
    		return true;
    	}
    	try{
    		@SuppressWarnings("unused")
			String test = args[0];
    	}catch(IndexOutOfBoundsException e){
    		player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("-----RSC Help-----").toString());
            player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("/rsc [name]  ").append(ChatColor.GREEN).append("Toggles Redstone.").toString());
            player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("/rsc list  ").append(ChatColor.GREEN).append("Shows all RSCs.").toString());
            player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("/rsc delete [name]   ").append(ChatColor.GREEN).append("Deletes a RSC entry.").toString());
            player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("/rsc on [name]   ").append(ChatColor.GREEN).append("Turn on a RSC.").toString());
            player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("/rsc off [name]   ").append(ChatColor.GREEN).append("Turn off a RSC.").toString());
            player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("/rsc help   ").append(ChatColor.GREEN).append("Shows this page.").toString());
            return true;
    	}
        if(args[0].toLowerCase().equals("help")){
            player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("-----RSC Help-----").toString());
            player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("/rsc [name]  ").append(ChatColor.GREEN).append("Toggles Redstone.").toString());
            player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("/rsc list  ").append(ChatColor.GREEN).append("Shows all RSCs.").toString());
            player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("/rsc delete [name]   ").append(ChatColor.GREEN).append("Deletes a RSC entry.").toString());
            player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("/rsc on [name]   ").append(ChatColor.GREEN).append("Turn on a RSC.").toString());
            player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("/rsc off [name]   ").append(ChatColor.GREEN).append("Turn off a RSC.").toString());
            player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("/rsc help   ").append(ChatColor.GREEN).append("Shows this page.").toString());
            return true;
        }
        String name = args[0];
        if(command.getName().toLowerCase().equals("rsc")){
            if(name.toLowerCase().equals("list")){
            	if(checkPermission(player, "RSC.list")||checkPermission(player, "RSC.*"))
            		PLAYERlistRSC(player);
            	else
            		player.sendMessage(ChatColor.RED + getNoPermMsg());
            }else if(name.toLowerCase().equals("delete")){
            	if(checkPermission(player, "RSC.delete." + args[1])||checkPermission(player, "RSC.delete.*")||checkPermission(player, "RSC.*"))
            		deleteRSC(player, args[1]);
            	else
            		player.sendMessage(ChatColor.RED + getNoPermMsg());
            }else if(name.toLowerCase().equals("on")){
            	if(checkPermission(player, "RSC.use." + args[1])||checkPermission(player, "RSC.use.*")||checkPermission(player, "RSC.*"))
            		rscON(player, args[1]);
            	else
            		player.sendMessage(ChatColor.RED + getNoPermMsg());
            }else if(name.toLowerCase().equals("off")){
            	if(checkPermission(player, "RSC.use." + args[1])||checkPermission(player, "RSC.use.*")||checkPermission(player, "RSC.*"))
            		rscOFF(player, args[1]);
            	else
            		player.sendMessage(ChatColor.RED + getNoPermMsg());
            }else{
            	if(checkPermission(player, "RSC.use." + args[0])||checkPermission(player, "RSC.use.*")||checkPermission(player, "RSC.*"))
            		toggleRSC(player, name);
            	else
            		player.sendMessage(ChatColor.RED + getNoPermMsg());
            }
        }
        return true;
    }
    public static String getNoPermMsg(){
    	if(permissionsystem == 0)
    		return "You aren't an OP!";
    	else
    		return "You don't have the necessary permission!";
    }
    public static boolean checkPermission(Player player, String perm){
    	if(permissionsystem == 0){
    		return player.isOp();
    	}else if(permissionsystem == 1){
    		return player.hasPermission(perm);
    	}else if(permissionsystem == 2){
    		return permissions.has(player, perm);
    	}else{
    		return false;
    	}
    }
    // CONSOLE FUNCTIONS
	public void toggleRSCc(final String name){
        config.load();
        if(config.getString("RedstoneCommands.Locations." + name) == null){
        	  System.out.println("[RSC]: The following RSC doesn't exist: " + name);
        	  return;
        }
        int x = ((Integer)config.getProperty((new StringBuilder("RedstoneCommands.Locations.")).append(name).append(".X").toString())).intValue();
        int y = ((Integer)config.getProperty((new StringBuilder("RedstoneCommands.Locations.")).append(name).append(".Y").toString())).intValue();
        int z = ((Integer)config.getProperty((new StringBuilder("RedstoneCommands.Locations.")).append(name).append(".Z").toString())).intValue();
        World world = getServer().getWorld(config.getProperty("RedstoneCommands.Locations." + name + ".WORLD").toString());
        final Location position = new Location(world, x - 1, y, z);
        if(position.getBlock().getType() == Material.REDSTONE_TORCH_ON){
        	position.getChunk().load();
            position.getBlock().setType(Material.AIR);
            System.out.println("[RSC]: Successfully toggled RSC named " + name);
        }else{
        	Runnable delayedrun = new Runnable(){
        		public void run(){
		            if(config.getInt("RedstoneCommands.Locations." + name + ".DELAY", 0) != 0){
		            	try{
		    				Thread.sleep(Integer.parseInt(config.getProperty("RedstoneCommands.Locations." + name + ".DELAY").toString())*1000);
		    			} catch (NumberFormatException e) {
		    				e.printStackTrace();
		    			} catch (InterruptedException e) {
		    				e.printStackTrace();
		    			}
		    			position.getChunk().load();
		    			position.getBlock().setType(Material.AIR);
		    			System.out.println("[RSC]: Successfully delayed RSC named " + name);
		            }else{
		            	System.out.println("[RSC]: Successfully toggled RSC named " + name);
		            }
        		}
        	};
			SwingUtilities.invokeLater(delayedrun);
        	position.getBlock().setType(Material.REDSTONE_TORCH_ON);
        }
        config.save();
    }
    public void deleteRSCc(String name){
        if(name != null)
        {
            config.load();
            if(config.getString("RedstoneCommands.Locations." + name) != null)
            {
                int x = ((Integer)config.getProperty((new StringBuilder("RedstoneCommands.Locations.")).append(name).append(".X").toString())).intValue();
                int y = ((Integer)config.getProperty((new StringBuilder("RedstoneCommands.Locations.")).append(name).append(".Y").toString())).intValue();
                int z = ((Integer)config.getProperty((new StringBuilder("RedstoneCommands.Locations.")).append(name).append(".Z").toString())).intValue();
                World world = getServer().getWorld(config.getProperty("RedstoneCommands.Locations." + name + ".WORLD").toString());
                config.removeProperty((new StringBuilder("RedstoneCommands.Locations.")).append(name).toString());
                Location torchposition = new Location(world, x - 1, y, z);
                Location signposition = new Location(world, x, y, z);
                signposition.getChunk().load();
                torchposition.getChunk().load();
                signposition.getBlock().setType(Material.AIR);
                torchposition.getBlock().setType(Material.AIR);
                config.save();
                System.out.println("[RSC]: Successfully deleted RSC " + name);
            } else{
                System.out.println("[RSC]: The RSC " + name + " doesn's exist.");
            }
        } else{
            System.out.println("[RSC]: Wrong syntax! Try: /rsc delete [name]");
        }
    }
    public void listRSCc()
    {
       	System.out.println("[RSC]: Following RSCs are set:");
        String rscsstring = "";
        for(int i = 0; i < listRSC().length; i++){
            if(listRSC()[i].toLowerCase().equals("}")){
                System.out.println("[RSC]: No RSCs are set.");
                return;
            }
            if(i + 1 == listRSC().length)
                rscsstring = (new StringBuilder(String.valueOf(rscsstring))).append(listRSC()[i].split("=")[0]).toString();
            else
                rscsstring = (new StringBuilder(String.valueOf(rscsstring))).append(listRSC()[i].split("=")[0]).append(", ").toString();
        }
        System.out.println("[RSC]: " + rscsstring);
    }
    public void rscONc(final String name){
    	config.load();
        if(config.getString("RedstoneCommands.Locations." + name) == null){
        	System.out.println("[RSC]: The following RSC doesn't exist: " + name);
        	  return;
        }
        int x = ((Integer)config.getProperty((new StringBuilder("RedstoneCommands.Locations.")).append(name).append(".X").toString())).intValue();
        int y = ((Integer)config.getProperty((new StringBuilder("RedstoneCommands.Locations.")).append(name).append(".Y").toString())).intValue();
        int z = ((Integer)config.getProperty((new StringBuilder("RedstoneCommands.Locations.")).append(name).append(".Z").toString())).intValue();
        World world = getServer().getWorld(config.getProperty("RedstoneCommands.Locations." + name + ".WORLD").toString());
        final Location position = new Location(world, x - 1, y, z);
        position.getChunk().load();
        position.getBlock().setType(Material.REDSTONE_TORCH_ON);
    	System.out.println("[RSC]: Successfully turned on RSC named " + name);
    }
    public void rscOFFc(final String name){
    	config.load();
        if(config.getString("RedstoneCommands.Locations." + name) == null){
        	System.out.println("[RSC]: The following RSC doesn't exist: " + name);
        	  return;
        }
        int x = ((Integer)config.getProperty((new StringBuilder("RedstoneCommands.Locations.")).append(name).append(".X").toString())).intValue();
        int y = ((Integer)config.getProperty((new StringBuilder("RedstoneCommands.Locations.")).append(name).append(".Y").toString())).intValue();
        int z = ((Integer)config.getProperty((new StringBuilder("RedstoneCommands.Locations.")).append(name).append(".Z").toString())).intValue();
        World world = getServer().getWorld(config.getProperty("RedstoneCommands.Locations." + name + ".WORLD").toString());
        final Location position = new Location(world, x - 1, y, z);
        position.getChunk().load();
        position.getBlock().setType(Material.AIR);
        System.out.println("[RSC]: Successfully turned off RSC named " + name);
    }
}

