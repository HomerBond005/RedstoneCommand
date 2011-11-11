package com.bukkit.HomerBond005.RedstoneCommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ConsoleHandler {
	public static RedstoneCommand plugin;
	public ConsoleHandler(RedstoneCommand rsc){
		plugin = rsc;
	}
	public void handleConsole(CommandSender sender, Command command, String args[]){
		try{
			@SuppressWarnings("unused")
			String test = args[0];
		}catch(IndexOutOfBoundsException e){
			System.out.println("[RSC]: -----RSC Help-----");
			System.out.println("[RSC]: rsc [name]  Toggles Redstone.");
			System.out.println("[RSC]: rsc list  shows all RSCs.");
			System.out.println("[RSC]: rsc delete [name] Deletes a RSC entry.");
			System.out.println("[RSC]: rsc help Shows this page.");
			return;
		}
	    if(args[0].toLowerCase().equals("help"))
	    {
	    	System.out.println("[RSC]: -----RSC Help-----");
			System.out.println("[RSC]: rsc [name]  Toggles Redstone.");
			System.out.println("[RSC]: rsc list  shows all RSCs.");
			System.out.println("[RSC]: rsc delete [name] Deletes a RSC entry.");
			System.out.println("[RSC]: rsc help Shows this page.");
			return;
	    }
	    String name = args[0];
	    if(command.getName().toLowerCase().equals("rsc")){
	        if(name.toLowerCase().equals("list")){
	        	plugin.listRSCc();
	        } else if(name.toLowerCase().equals("delete")){
	        	plugin.deleteRSCc(args[1]);
	        }else{
	        	plugin.toggleRSCc(name);
	        }
	    }
	}
}
