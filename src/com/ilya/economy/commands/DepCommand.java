package com.ilya.economy.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ilya.economy.Main;
import com.ilya.economy.util.Util;

import net.milkbowl.vault.economy.EconomyResponse;

public class DepCommand implements CommandExecutor {
	Main core;
	
	public DepCommand(Main core) {
		this.core = core;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		int amount = 0;
		boolean bs;
		Material m = Material.DIAMOND;
		
		if (args.length == 0) {
			p.sendMessage(ChatColor.RED + "[DiamondEconomy] Usage: /deposit <all/amount>");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("all")) {
			    ItemStack d = new ItemStack(Material.DIAMOND);
				amount = calc(p, d);
				bs = true;
		}else if (Util.isNumeric(args[0])){
			amount = Integer.parseInt(args[0]);
			bs = false;
		}else {
			p.sendMessage(ChatColor.RED + "[DiamondEconomy] Usage: /deposit <all/amount>");
			return true;
		}
		
		if (amount <= 0) {
			if (bs) {
				p.sendMessage(ChatColor.RED + "[DiamondEconomy] You don't have any diamonds in your inventory!");
				return true;
			} else {
				p.sendMessage(ChatColor.RED + "[DiamondEconomy] Enter a number greater than 0!");
				return true;
			}
		}
		
		if(p.getInventory().containsAtLeast(new ItemStack(m), amount)) {
			EconomyResponse r = Main.getEconomy().depositPlayer(p, amount);
            if(r.transactionSuccess()) {
            	p.getInventory().removeItem(new ItemStack(m, amount));
            	p.sendMessage(ChatColor.GREEN + "[DiamondEconomy] You converted the diamonds into " + amount + " money");
            }else {
            	p.sendMessage(ChatColor.RED + "[DiamondEconomy] Error!");
            }
			return true;
		}else {
			p.sendMessage(ChatColor.RED + "[DiamondEconomy] You don't have that many diamonds!");
		}
		return true;
	}
	 
	 static int calc(Player p, ItemStack s) {
	        int count = 0;
	        for (int i = 0; i < p.getInventory().getSize(); i++) {
	            ItemStack stack = p.getInventory().getItem(i);
	            if (stack == null)
	                continue;
	            if (stack.isSimilar(s)) {
	                count += stack.getAmount();
	            }
	        }
	        return count;
	 }
}