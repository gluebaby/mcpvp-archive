
package kookaburra.minecraft.sabotage.commands;

import kookaburra.minecraft.commands.CommandBase;
import kookaburra.minecraft.mcpvp.Mcpvp;
import kookaburra.minecraft.mcpvp.McpvpPlayer;
import kookaburra.minecraft.mcpvp.Rank;
import kookaburra.minecraft.player.SharedPlayer;
import kookaburra.minecraft.sabotage.Game;
import kookaburra.minecraft.sabotage.PassSystem;

import org.bukkit.ChatColor;

public class Saboteur extends CommandBase
{
	public Saboteur()
	{
		super("saboteur");
		super.Aliases().add("sab");
		super.Aliases().add("terrorist");
	}

	@Override
	public boolean Execute(SharedPlayer player, String[] args)
	{
		if (Game.hasStarted)
		{
			player.sendMessage(ChatColor.GOLD + "You can only use this before the game...");
			return true;
		}
		McpvpPlayer mc = Mcpvp.players.get(player.getPlayer());
		if (mc.hasPasses() || player.isOp() || mc.getRank() == Rank.PRO)
		{
			PassSystem.addPlayer(mc);
			player.sendMessage(ChatColor.GOLD + "You have been added to the priority list");
			player.sendMessage(ChatColor.GOLD + "A max of 3 players can become saboteur with passes.");
		}
		else
		{
			player.sendMessage(ChatColor.GOLD + "You need a saboteur pass for this! Get one at http://www.mcpvp.com");
		}
		return true;
	}
}
