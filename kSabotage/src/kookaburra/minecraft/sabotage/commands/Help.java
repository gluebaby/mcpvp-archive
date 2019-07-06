
package kookaburra.minecraft.sabotage.commands;

import kookaburra.minecraft.player.SharedPlayer;
import kookaburra.minecraft.sabotage.Game;
import kookaburra.minecraft.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Help extends kookaburra.minecraft.commands.Help
{
	@Override
	public boolean Execute(SharedPlayer player, String[] args)
	{
		player.sendMessage(ChatColor.GRAY + "/saboteur - Use a saboteur pass to become saboteur this game");
		player.sendMessage(ChatColor.GRAY + "/shop - See the shop for your rank");
		player.sendMessage(ChatColor.GRAY + "/stream - Enable streaming mode, useful for streamers!");
		player.sendMessage(ChatColor.GRAY + "/who - See all the players and the detective");
		player.sendMessage(ChatColor.GRAY + "Server : " + Bukkit.getServer().getIp());
		if (Game.hasStarted)
		{
			long elapsed = System.currentTimeMillis() - Game.startTime;
			player.sendMessage(ChatColor.GRAY + "Sabotage started " + Util.GetTimespanString(elapsed) + " ago.");
			player.sendMessage(ChatColor.GRAY + "" + Game.players.size() + " players remain.");
		}
		return true;
	}
}
