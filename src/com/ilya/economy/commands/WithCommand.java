package com.ilya.economy.commands;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ilya.economy.Main;
import com.ilya.economy.util.InventoryUtil;
import com.ilya.economy.util.Util;

@SuppressWarnings("deprecation")
public class WithCommand implements CommandExecutor {
	Main core;
	
	public WithCommand(Main core) {
		this.core = core;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Map<Integer, ItemStack> l;
		Player p = (Player) sender;
		boolean bs = false;
		double amount = 0;
		
		if (args.length == 0) {
			p.sendMessage(ChatColor.RED + "[DiamondEconomy] Usage: /withdraw <all/amount>");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("all")) {
				amount = Main.getEconomy().getBalance(p.getName());
				bs = true;
		}else if (Util.isNumeric(args[0])){
			amount = Integer.parseInt(args[0]);
			bs = false;
		}else {
			p.sendMessage(ChatColor.RED + "[DiamondEconomy] Usage: /withdraw <all/amount>");
			return true;
		}
		
		if (amount <= 0) {
			if (bs) {
				p.sendMessage(ChatColor.RED + "[DiamondEconomy] You don't have any diamonds in your inventory!");
			} else {
				p.sendMessage(ChatColor.RED + "[DiamondEconomy] Enter a number greater than 0!");
			}
			return true;
		}
		
		int withdrawAmount = (int) amount;
		int messageAmount = withdrawAmount;
		if (withdrawAmount > Main.getEconomy().getBalance(p.getName())) {
			return true;
		}
		ItemStack item = new ItemStack(Material.DIAMOND);
		item.setAmount(withdrawAmount);
		l = InventoryUtil.addItems(p.getInventory(), item);
		
		for (ItemStack i : l.values()) {
			int famount = i.getAmount();
			sender.sendMessage(ChatColor.RED + "[DiamondEconomy] " + famount + " diamonds could not fit in the inventory and were returned to the balance!");
			Main.getEconomy().depositPlayer(p.getName(), famount);
			messageAmount -= famount;
		}
		sender.sendMessage(ChatColor.GREEN + "[DiamondEconomy] The money was written off and turned into diamonds: " + messageAmount);
		Main.getEconomy().withdrawPlayer(p.getName(), withdrawAmount);
		return true;
	}
}
