package com.ilya.economy.util;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/*
 * original - https://github.com/essentials/Essentials/blob/2.x/Essentials/src/com/earth2me/essentials/craftbukkit/InventoryWorkaround.java
 * This class can be removed when https://github.com/Bukkit/CraftBukkit/pull/193 is accepted to CraftBukkit
 */

public class InventoryUtil {
	private InventoryUtil()
	{
	}

	private static int firstPartial(final Inventory inventory, final ItemStack item, final int maxAmount)
	{
		if (item == null)
		{
			return -1;
		}
		final ItemStack[] stacks = inventory.getContents();
		for (int i = 0; i < stacks.length; i++)
		{
			final ItemStack cItem = stacks[i];
			if (cItem != null && cItem.getAmount() < maxAmount && cItem.isSimilar(item))
			{
				return i;
			}
		}
		return -1;
	}

	public static Map<Integer, ItemStack> addAllItems(final Inventory inventory, final ItemStack... items)
	{
		final Inventory fakeInventory = Bukkit.getServer().createInventory(null, inventory.getType());
		fakeInventory.setContents(inventory.getContents());
		Map<Integer, ItemStack> overFlow = addItems(fakeInventory, items);
		if (overFlow.isEmpty())
		{
			addItems(inventory, items);
			return null;
		}
		return addItems(fakeInventory, items);
	}

	public static Map<Integer, ItemStack> addItems(final Inventory inventory, final ItemStack... items)
	{
		return addOversizedItems(inventory, 0, items);
	}
	public static Map<Integer, ItemStack> addOversizedItems(final Inventory inventory, final int oversizedStacks, final ItemStack... items)
	{
		final Map<Integer, ItemStack> leftover = new HashMap<Integer, ItemStack>();

		final ItemStack[] combined = new ItemStack[items.length];
		for (ItemStack item : items)
		{
			if (item == null || item.getAmount() < 1)
			{
				continue;
			}
			for (int j = 0; j < combined.length; j++)
			{
				if (combined[j] == null)
				{
					combined[j] = item.clone();
					break;
				}
				if (combined[j].isSimilar(item))
				{
					combined[j].setAmount(combined[j].getAmount() + item.getAmount());
					break;
				}
			}
		}


		for (int i = 0; i < combined.length; i++)
		{
			final ItemStack item = combined[i];
			if (item == null || item.getType() == Material.AIR)
			{
				continue;
			}

			while (true)
			{
				final int maxAmount = oversizedStacks > item.getType().getMaxStackSize() ? oversizedStacks : item.getType().getMaxStackSize();
				final int firstPartial = firstPartial(inventory, item, maxAmount);
				
				if (firstPartial == -1)
				{
					final int firstFree = inventory.firstEmpty();

					if (firstFree == -1)
					{
						leftover.put(i, item);
						break;
					}
					else
					{
						if (item.getAmount() > maxAmount)
						{
							final ItemStack stack = item.clone();
							stack.setAmount(maxAmount);
							inventory.setItem(firstFree, stack);
							item.setAmount(item.getAmount() - maxAmount);
						}
						else
						{
							inventory.setItem(firstFree, item);
							break;
						}
					}
				}
				else
				{
					final ItemStack partialItem = inventory.getItem(firstPartial);

					final int amount = item.getAmount();
					final int partialAmount = partialItem.getAmount();

					if (amount + partialAmount <= maxAmount)
					{
						partialItem.setAmount(amount + partialAmount);
						break;
					}

					partialItem.setAmount(maxAmount);
					item.setAmount(amount + partialAmount - maxAmount);
				}
			}
		}
		return leftover;
	}
}
