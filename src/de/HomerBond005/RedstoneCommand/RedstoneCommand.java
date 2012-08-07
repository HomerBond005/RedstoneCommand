/*
 * Copyright HomerBond005
 * 
 *  Published under CC BY-NC-ND 3.0
 *  http://creativecommons.org/licenses/by-nc-nd/3.0/
 */
package de.HomerBond005.RedstoneCommand;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RedstoneCommand extends JavaPlugin{
    private final RSCL blocklistener = new RSCL(this);
    PermissionsChecker pc;
    private Metrics metrics;
    private Updater updater;
    private Logger log;
    private Map<String, RSC> rscs;
    private boolean signPlaceDirectionModeEnabled;
    
    @Override
    public void onEnable(){
    	log = getLogger();
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(blocklistener, this);
        getConfig().addDefault("RedstoneCommands.Locations", new HashMap<String, Object>());
        getConfig().addDefault("RedstoneCommands.permissionsEnabled", true);
        getConfig().addDefault("RedstoneCommands.signPlaceDirectionModeEnabled", true);
        getConfig().options().copyDefaults(true);
        saveConfig();
	    reloadConfig();
		if(!getConfig().isBoolean("RedstoneCommands.permissionsEnabled")){
    		getConfig().set("RedstoneCommands.permissionsEnabled", true);
		}
    	saveConfig();
    	pc = new PermissionsChecker(this, getConfig().getBoolean("RedstoneCommands.permissionsEnabled", true));
    	signPlaceDirectionModeEnabled = getConfig().getBoolean("RedstoneCommands.signPlaceDirectionModeEnabled");
    	reloadRSCs();
    	log.log(Level.INFO, "config.yml loaded.");
        try{
        	metrics = new Metrics(this);
        	metrics.start();
        }catch(IOException e){
        	log.log(Level.WARNING, "Error while enabling Metrics.");
        }
        updater = new Updater(this);
		getServer().getPluginManager().registerEvents(updater, this);
		log.log(Level.INFO, "is enabled!");
    }
    
    @Override
    public void onDisable(){
    	log.log(Level.INFO, "is disabled!");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String args[]){
    	if(command.getName().toLowerCase().equals("rsc")){
        	if(args.length == 0)
        		args = new String[]{"help"};
    		if(sender instanceof Player){
    			Player player = (Player)sender;
            	if(args[0].toLowerCase().equals("help")){
                    player.sendMessage(ChatColor.RED+"-----RSC Help-----");
                    player.sendMessage(ChatColor.RED+"/rsc [name]  "+ChatColor.GREEN+"Toggles Redstone.");
                    player.sendMessage(ChatColor.RED+"/rsc list  "+ChatColor.GREEN+"Shows all RSCs.");
                    player.sendMessage(ChatColor.RED+"/rsc delete [name]   "+ChatColor.GREEN+"Deletes a RSC entry.");
                    player.sendMessage(ChatColor.RED+"/rsc on [name]   "+ChatColor.GREEN+"Turn on a RSC.");
                    player.sendMessage(ChatColor.RED+"/rsc off [name]   "+ChatColor.GREEN+"Turn off a RSC.");
                    player.sendMessage(ChatColor.RED+"/rsc showmsg [name] [bool]   "+ChatColor.GREEN+"Toggle display of messages.");
                    player.sendMessage(ChatColor.RED+"/rsc help   "+ChatColor.GREEN+"Shows this page.");
                    return true;
                }else if(args[0].toLowerCase().equals("list")){
                	if(pc.has(player, "RSC.list")||pc.has(player, "RSC.*")){
                		listRSC(sender);
                	}else
                		pc.sendNoPermMsg(player);
                }else if(args[0].toLowerCase().equals("delete")){
                	if(args.length == 2)
	                	if(pc.has(player, "RSC.delete."+args[1])||pc.has(player, "RSC.delete.*")||pc.has(player, "RSC.*"))
	                		player.sendMessage(deleteRSC(args[1]));
	                	else
	                		pc.sendNoPermMsg(player);
                	else
            			player.sendMessage(ChatColor.RED+"Wrong arguments! Usage: /rsc delete <name>");
                }else if(args[0].toLowerCase().equals("on")){
                	if(args.length == 2)
	                	if(pc.has(player, "RSC.use."+args[1])||pc.has(player, "RSC.use.*")||pc.has(player, "RSC.*"))
	                		sendMessageToSender(sender, args[1], turnRSCon(args[1]));
	                	else
	                		pc.sendNoPermMsg(player);
                	else
            			player.sendMessage(ChatColor.RED+"Wrong arguments! Usage: /rsc on <name>");
                }else if(args[0].toLowerCase().equals("off")){
                	if(args.length == 2)
	                	if(pc.has(player, "RSC.use."+args[1])||pc.has(player, "RSC.use.*")||pc.has(player, "RSC.*"))
	                		sendMessageToSender(sender, args[1], turnRSCoff(args[1]));
	                	else
	                		pc.sendNoPermMsg(player);
                	else
            			player.sendMessage(ChatColor.RED+"Wrong arguments! Usage: /rsc off <name>");
                }else if(args[0].toLowerCase().equals("showmsg")){
                	if(args.length == 3)
	                	if(pc.has(player, "RSC.setmsg."+args[1])||pc.has(player, "RSC.setmsg.*")||pc.has(player, "RSC.*"))
	                			player.sendMessage(setMsg(args[1], args[2]));
	                	else
	                		pc.sendNoPermMsg(player);
                	else
            			player.sendMessage(ChatColor.RED+"Wrong arguments! Usage: /rsc showmsg <name> <boolean>");
                }else{
                	if(pc.has(player, "RSC.use."+args[0])||pc.has(player, "RSC.use.*")||pc.has(player, "RSC.*"))
                		toggleRSC(args[0], sender);
                	else
                		pc.sendNoPermMsg(player);
                }
    		}else{
	    	    if(args[0].toLowerCase().equals("help")){
	    	    	sender.sendMessage(ChatColor.RED+"-----RSC Help-----");
                    sender.sendMessage(ChatColor.RED+"rsc [name]  "+ChatColor.GREEN+"Toggles Redstone.");
                    sender.sendMessage(ChatColor.RED+"rsc list  "+ChatColor.GREEN+"Shows all RSCs.");
                    sender.sendMessage(ChatColor.RED+"rsc delete [name]   "+ChatColor.GREEN+"Deletes a RSC entry.");
                    sender.sendMessage(ChatColor.RED+"rsc on [name]   "+ChatColor.GREEN+"Turn on a RSC.");
                    sender.sendMessage(ChatColor.RED+"rsc off [name]   "+ChatColor.GREEN+"Turn off a RSC.");
                    sender.sendMessage(ChatColor.RED+"rsc showmsg [name] [bool]   "+ChatColor.GREEN+"Toggle display of messages.");
                    sender.sendMessage(ChatColor.RED+"rsc help   "+ChatColor.GREEN+"Shows this page.");
	    			return true;
	    	    }else if(args[0].toLowerCase().equalsIgnoreCase("list")){
	    	    	listRSC(sender);
    	        }else if(args[0].toLowerCase().equalsIgnoreCase("delete")){
    	        	if(args.length == 2)
    	        		sender.sendMessage(deleteRSC(args[1]));
    	        	else
    	        		sender.sendMessage(ChatColor.RED+"Wrong arguments! Usage: rsc delete <name>");
    	        }else if(args[0].toLowerCase().equalsIgnoreCase("on")){
    	        	if(args.length == 2)
    	        		sendMessageToSender(sender, args[1], turnRSCon(args[1]));
    	        	else
    	        		sender.sendMessage(ChatColor.RED+"Wrong arguments! Usage: rsc on <name>");
    	        }else if(args[0].toLowerCase().equalsIgnoreCase("off")){
    	        	if(args.length == 2)
    	        		sendMessageToSender(sender, args[1], turnRSCoff(args[1]));
    	        	else
    	        		sender.sendMessage(ChatColor.RED+"Wrong arguments! Usage: rsc off <name>");
    	        }else if(args[0].toLowerCase().equalsIgnoreCase("showmsg")){
    	        	if(args.length == 3)
    	        		sender.sendMessage(setMsg(args[1], args[2]));
    	        	else
    	        		sender.sendMessage(ChatColor.RED+"Wrong arguments! Usage: rsc showmsg <name> <value>");
    	        }else{
    	        	toggleRSC(args[0], sender);
    	        }
    		}
    	}
        return true;
    }
    
    /**
     * Set the message that is displayed if a RSC is used
     * @param name The name of the RSC
     * @param valueAsString The message that should be set
     * @return An answer that could be sent to the CommandSender
     */
    private String setMsg(String name, String valueAsString){
    	name = name.toLowerCase();
        if(!rscs.containsKey(name)){
        	return ChatColor.RED+"The following RSC doesn't exist: "+ChatColor.GOLD+name;
        }
        getConfig().set("RedstoneCommands.Locations." + name + ".MSG", Boolean.parseBoolean(valueAsString));
    	saveConfig();
    	reloadRSCs();
        if(Boolean.parseBoolean(valueAsString)){
    		return ChatColor.GREEN+"Messages will be displayed when using the RSC "+ChatColor.GOLD+name;
    	}else{
    		return ChatColor.GREEN+"Messages will not be displayed when using the RSC "+ChatColor.GOLD+name;
    	}
	}

    /**
     * Turn on a RSC
     * @param name The name of the RSC
     * @return An answer that could be sent to the CommandSender
     */
	public String turnRSCon(String name){
    	name = name.toLowerCase();
        if(!rscs.containsKey(name)){
        	return ChatColor.RED+"The following RSC doesn't exist: "+ChatColor.GOLD+name;
        }
        RSC rsc = rscs.get(name);
        if(rsc.isON())
        	return ChatColor.GREEN+"The RSC "+ChatColor.GOLD+name+ChatColor.GREEN+" is already on.";
        rsc.turnON();
    	return ChatColor.GREEN+"Successfully turned on RSC named "+ChatColor.GOLD+name;
    }
    
	/**
     * Turn off a RSC
     * @param name The name of the RSC
     * @return An answer that could be sent to the CommandSender
     */
    public String turnRSCoff(String name){
    	name = name.toLowerCase();
        if(!rscs.containsKey(name)){
        	return ChatColor.RED+"The following RSC doesn't exist: "+ChatColor.GOLD+name;
        }
        RSC rsc = rscs.get(name);
        if(!rsc.isON())
        	return ChatColor.GREEN+"The RSC "+ChatColor.GOLD+name+ChatColor.GREEN+" is already off.";
        rsc.turnOFF();
        return ChatColor.GREEN+"Successfully turned off RSC named "+ChatColor.GOLD+name;
    }
    
    /**
     * Delete a RSC
     * @param name The name of the RSC
     * @return An answer that could be sent to the CommandSender
     */
    public String deleteRSC(String name){
    	name = name.toLowerCase();
        if(!rscs.containsKey(name)){
        	return ChatColor.RED+"The following RSC doesn't exist: "+ChatColor.GOLD+name;
        }
        RSC rsc = rscs.get(name);
        getConfig().set("RedstoneCommands.Locations."+rsc.getName()+".X", null);
        getConfig().set("RedstoneCommands.Locations."+rsc.getName()+".Y", null);
        getConfig().set("RedstoneCommands.Locations."+rsc.getName()+".Z", null);
        getConfig().set("RedstoneCommands.Locations."+rsc.getName()+".Xchange", null);
        getConfig().set("RedstoneCommands.Locations."+rsc.getName()+".Ychange", null);
        getConfig().set("RedstoneCommands.Locations."+rsc.getName()+".Zchange", null);
        getConfig().set("RedstoneCommands.Locations."+rsc.getName()+".WORLD", null);
        getConfig().set("RedstoneCommands.Locations."+rsc.getName()+".MSG", null);
        getConfig().set("RedstoneCommands.Locations."+rsc.getName()+".DELAY", null);
        getConfig().set("RedstoneCommands.Locations."+name, null);
        getConfig().options().copyDefaults(false);
        saveConfig();
        rsc.turnOFF();
        rsc.getSignLocation().getBlock().setType(Material.AIR);
        reloadRSCs();
        return ChatColor.GREEN+"Successfully deleted the RSC "+ChatColor.GOLD+name;
    }
    
    /**
     * List all defined RSCs
     * @param sender The CommandSender that executed the list command
     */
    public void listRSC(CommandSender sender){
    	sender.sendMessage(ChatColor.GREEN+"The following RSCs are set:");
        if(rscs.size() == 0){
        	sender.sendMessage(ChatColor.GRAY+"No RSCs are set.");
        }
        String rscsstring = "";
        for(String rsc : rscs.keySet())
        	rscsstring += rsc+ ", ";
        if(rscsstring.length() != 0)
        	rscsstring = rscsstring.substring(0, rscsstring.length()-2);
        sender.sendMessage(ChatColor.GOLD+rscsstring);
    }
    
    /**
     * Toggle a RSC
     * @param name The name of the RSC
     * @param sender The CommandSender that executed the toggle command
     */
    public void toggleRSC(String name, final CommandSender sender){
    	name = name.toLowerCase();
        if(!rscs.containsKey(name)){
        	sendMessageToSender(sender, name, ChatColor.RED+"The following RSC doesn't exist: "+ChatColor.GOLD+name);
        	return;
        }
        final RSC rsc = rscs.get(name);
        if(rsc.isON()){
        	rsc.turnOFF();
        	sendMessageToSender(sender, rsc.getName(), ChatColor.GREEN+"Successfully turned off RSC named "+ChatColor.GOLD+name);
        }else{
        	getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				@Override
				public void run(){
					if(rsc.getDelay() != 0){
						rsc.turnOFF();
						sendMessageToSender(sender, rsc.getName(), ChatColor.GREEN+"Successfully delayed RSC named "+ChatColor.GOLD+rsc.getName());
					}else{
						sendMessageToSender(sender, rsc.getName(), ChatColor.GREEN+"Successfully turned on RSC named "+ChatColor.GOLD+rsc.getName());
		            }
				}
			}, 20L*rsc.getDelay());
        	rsc.turnON();
        }
    }
    
    /**
     * Reload all RSCs from the config
     */
    public void reloadRSCs(){
    	reloadConfig();
    	rscs = new HashMap<String, RSC>();
    	ConfigurationSection sec = getConfig().getConfigurationSection("RedstoneCommands.Locations");
    	Set<String> rscnames = sec.getKeys(false);
    	for(String name : rscnames){
    		getConfig().addDefault("RedstoneCommands.Locations."+name+".Xchange", -1);
    		getConfig().addDefault("RedstoneCommands.Locations."+name+".Ychange", 0);
    		getConfig().addDefault("RedstoneCommands.Locations."+name+".Zchange", 0);
    		getConfig().addDefault("RedstoneCommands.Locations."+name+".X", 0);
    		getConfig().addDefault("RedstoneCommands.Locations."+name+".Y", 0);
    		getConfig().addDefault("RedstoneCommands.Locations."+name+".Z", 0);
    		getConfig().addDefault("RedstoneCommands.Locations."+name+".WORLD", "world");
    		getConfig().addDefault("RedstoneCommands.Locations."+name+".DELAY", 0);
    		getConfig().addDefault("RedstoneCommands.Locations."+name+".MSG", true);
    		getConfig().options().copyDefaults(true);
    		saveConfig();
    		ConfigurationSection rsc = sec.getConfigurationSection(name);
    		rscs.put(name.toLowerCase(), new RSC(name, rsc.getInt("X"), rsc.getInt("Y"), rsc.getInt("Z"), getServer().getWorld(rsc.getString("WORLD")), rsc.getInt("DELAY"), rsc.getInt("Xchange"), rsc.getInt("Ychange"), rsc.getInt("Zchange"), rsc.getBoolean("MSG")));
    	}
    }
    
    /**
     * Transfer a message to a CommandSender
     * @param sender The CommandSender that should receive the message
     * @param rsc The name of the RSC that
     * @param msg The message that should be transfered
     */
    private void sendMessageToSender(CommandSender sender, String rsc, String msg){
    	rsc = rsc.toLowerCase();
        if(!rscs.containsKey(rsc)){
        	sender.sendMessage(msg);
        }else{
        	if(rscs.get(rsc).displayMessages())
        		sender.sendMessage(msg);
        }
    }
    
    /**
     * Check if the signPlaceDirectionMode is enabled
     * @return A boolean
     */
    public boolean getSignPlaceDirectionModeEnabled(){
    	return signPlaceDirectionModeEnabled;
    }
}

