
package kookaburra.minecraft.sabotage.commands;

import kookaburra.minecraft.commands.CommandBase;
import kookaburra.minecraft.player.SharedPlayer;
import kookaburra.minecraft.sabotage.Game;
import kookaburra.minecraft.sabotage.MirrorIllusion;
import kookaburra.minecraft.sabotage.listeners.Events;
import kookaburra.minecraft.util.FukkitUtil;
import kookaburra.minecraft.util.ItemUtil;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Shop extends CommandBase
{
	public Shop()
	{
		super("shop");
	}

	@Override
	public boolean Execute(final SharedPlayer sp, String[] args)
	{
		Player player = sp.getPlayer();
		if (!Game.hasStarted)
		{
			player.sendMessage(ChatColor.GOLD + "You can only use this during the game.");
			return true;
		}
		if (args.length == 0)
		{
			player.sendMessage(ChatColor.GRAY + "****Shop****");
			if (Game.isDetective(player.getPlayer()))
			{
				player.sendMessage(new String[]{"1 " + ChatColor.DARK_AQUA + " Compass " + ChatColor.GREEN + ChatColor.ITALIC + " 20 Karma " + ChatColor.DARK_AQUA + " Points at the closes player.", "2 " + ChatColor.DARK_AQUA + " Map " + ChatColor.GREEN + ChatColor.ITALIC + " 40 Karma " + ChatColor.DARK_AQUA + " Get a map!", "3 " + ChatColor.DARK_AQUA + " Speed II " + ChatColor.GREEN + ChatColor.ITALIC + " 40 Karma " + ChatColor.DARK_AQUA + " Activates immediately", "4 " + ChatColor.DARK_AQUA + " Insight " + ChatColor.GREEN + ChatColor.ITALIC + " 60 Karma " + ChatColor.DARK_AQUA + " Right click 1 person and see if what they are!",});
			}
			else if (Game.isInnocent(player.getPlayer()))
			{
				player.sendMessage(new String[]{"1 " + ChatColor.DARK_AQUA + " Compass " + ChatColor.GREEN + ChatColor.ITALIC + " 20 Karma " + ChatColor.DARK_AQUA + " Points at the closes player.", "2 " + ChatColor.DARK_AQUA + " Map " + ChatColor.GREEN + ChatColor.ITALIC + " 40 Karma " + ChatColor.DARK_AQUA + " Get a map!", "3 " + ChatColor.DARK_AQUA + " Speed II " + ChatColor.GREEN + ChatColor.ITALIC + " 40 Karma " + ChatColor.DARK_AQUA + " Activates immediately",
						// "4 " + ChatColor.DARK_AQUA + " TNT (5x) " +
						// ChatColor.GREEN + ChatColor.ITALIC + " 40 Karma " +
						// ChatColor.DARK_AQUA + " Activates when placed",
				"5 " + ChatColor.DARK_AQUA + " Second Wind " + ChatColor.GREEN + ChatColor.ITALIC + " 60 Karma " + ChatColor.DARK_AQUA + " Gain 4-6 hearts more when you're dying!", "6 " + ChatColor.DARK_AQUA + " Mirror Illusion " + ChatColor.GREEN + ChatColor.ITALIC + " 100 Karma " + ChatColor.DARK_AQUA + " For 40 seconds, the map is filled with mirrored images of players, all with a green name. Take this chance to attack the person you suspect!",});
			}
			else if (Game.isSaboteur(player.getPlayer()))
			{
				player.sendMessage(new String[]{"1 " + ChatColor.DARK_AQUA + " Compass " + ChatColor.GREEN + ChatColor.ITALIC + " 20 Karma " + ChatColor.DARK_AQUA + " Points at the closes player.", "2 " + ChatColor.DARK_AQUA + " Surprise! " + ChatColor.GREEN + ChatColor.ITALIC + " 30 Karma " + ChatColor.DARK_AQUA + " Get a chest you can place down that explodes when you open it!", "3 " + ChatColor.DARK_AQUA + " Map " + ChatColor.GREEN + ChatColor.ITALIC + " 40 Karma " + ChatColor.DARK_AQUA + " Get a map!", "4 " + ChatColor.DARK_AQUA + " Speed II " + ChatColor.GREEN + ChatColor.ITALIC + " 40 Karma " + ChatColor.DARK_AQUA + " Activates immediately", "5 " + ChatColor.DARK_AQUA + " Hack Revelation " + ChatColor.GREEN + ChatColor.ITALIC + " 40 Karma " + ChatColor.DARK_AQUA + " Hack the Saboteur Detector and come out clean! One time use.", "6 " + ChatColor.DARK_AQUA + " Martyr " + ChatColor.GREEN + ChatColor.ITALIC + " 50 Karma " + ChatColor.DARK_AQUA + " Your body will explode when you die.", "7 " + ChatColor.DARK_AQUA + " Invisibility for 30 seconds. " + ChatColor.GREEN + ChatColor.ITALIC + " 60 Karma",});
			}
			player.sendMessage("To buy any of the perks, say /shop (perk id)");
			return true;
		}
		else if (args.length >= 1)
		{
			if (Game.isDetective(player))
			{
				if (args[0].equalsIgnoreCase("1"))
				{
					if (Game.buyWithKarma(player, 20))
					{
						player.getInventory().addItem(new ItemStack(Material.COMPASS));
					}
				}
				else if (args[0].equalsIgnoreCase("2"))
				{
					if (Game.buyWithKarma(player, 40))
					{
						player.getInventory().setItem(player.getInventory().firstEmpty(), FukkitUtil.makeStaticMap(player.getPlayer(), Game.map, 1, Game.map.getSpawnLocation(), (short) 1337));
					}
				}
				else if (args[0].equalsIgnoreCase("3"))
				{
					if (Game.buyWithKarma(player, 40))
					{
						player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60 * 20, 1));
					}
				}
				else if (args[0].equalsIgnoreCase("4"))
				{
					if (Game.buyWithKarma(player, 60))
					{
						Events.insight.add(player.getName());
					}
				}
				else
				{
					player.sendMessage(ChatColor.GRAY + "Can't find that item!");
				}
			}
			else if (Game.isInnocent(player))
			{
				if (args[0].equalsIgnoreCase("1"))
				{
					if (Game.buyWithKarma(player, 20))
					{
						player.getInventory().addItem(new ItemStack(Material.COMPASS));
					}
				}
				else if (args[0].equalsIgnoreCase("2"))
				{
					if (Game.buyWithKarma(player, 40))
					{
						player.getInventory().setItem(player.getInventory().firstEmpty(), FukkitUtil.makeStaticMap(player.getPlayer(), Game.map, 1, Game.map.getSpawnLocation(), (short) 1337));
					}
				}
				else if (args[0].equalsIgnoreCase("3"))
				{
					if (Game.buyWithKarma(player, 40))
					{
						player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60 * 20, 1));
					}
				}
				// else if (args[0].equalsIgnoreCase("4"))
				// {
				// if(Game.buyWithKarma(player, 20))
				// {
				// player.getInventory().addItem(new ItemStack(Material.TNT,
				// 5));
				// }
				// }
				else if (args[0].equalsIgnoreCase("5"))
				{
					if (Game.buyWithKarma(player, 60))
					{
						Events.secondWind.add(player.getName());
					}
				}
				else if (args[0].equalsIgnoreCase("6"))
				{
					if (!MirrorIllusion.inProgress)
					{
						if (Game.buyWithKarma(player, 100))
						{
							MirrorIllusion.cast(player.getPlayer());
							player.sendMessage("Your illusion has been cast. all real players have RED names!");
						}
					}
					else
					{
						player.sendMessage("A mirror illusion is currently in progress...");
					}
				}
				else
				{
					player.sendMessage(ChatColor.GRAY + "Can't find that item!");
				}
			}
			else if (Game.isSaboteur(player))
			{
				if (args[0].equalsIgnoreCase("1"))
				{
					if (Game.buyWithKarma(player, 20))
					{
						player.getInventory().addItem(new ItemStack(Material.COMPASS));
					}
				}
				else if (args[0].equalsIgnoreCase("2"))
				{
					if (Game.buyWithKarma(player, 30))
					{
						player.getInventory().addItem(ItemUtil.setName(CraftItemStack.asCraftCopy(new ItemStack(Material.CHEST)), "Surprise Chest"));
					}
				}
				else if (args[0].equalsIgnoreCase("3"))
				{
					if (Game.buyWithKarma(player, 40))
					{
						player.getInventory().setItem(player.getInventory().firstEmpty(), FukkitUtil.makeStaticMap(player.getPlayer(), Game.map, 1, Game.map.getSpawnLocation(), (short) 1337));
					}
				}
				else if (args[0].equalsIgnoreCase("4"))
				{
					if (Game.buyWithKarma(player, 40))
					{
						player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60 * 20, 1));
					}
				}
				else if (args[0].equalsIgnoreCase("5"))
				{
					if (Game.buyWithKarma(player, 40))
					{
						Events.hackAbility.add(player.getName());
					}
				}
				else if (args[0].equalsIgnoreCase("6"))
				{
					if (Game.buyWithKarma(player, 50))
					{
						Events.martyr.add(player.getName());
					}
				}
				else if (args[0].equalsIgnoreCase("7"))
				{
					if (Game.buyWithKarma(player, 60))
					{
						player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 30 * 20, 1));
					}
				}
				else
				{
					player.sendMessage(ChatColor.GRAY + "Can't find that item!");
				}
			}
			return true;
		}
		return false;
	}
}
