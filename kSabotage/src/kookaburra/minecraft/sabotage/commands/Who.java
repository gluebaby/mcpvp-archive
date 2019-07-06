
package kookaburra.minecraft.sabotage.commands;

import java.util.Arrays;
import java.util.List;

import kookaburra.minecraft.command.CommandExecutionException;
import kookaburra.minecraft.command.CommonCommand;
import kookaburra.minecraft.command.CommonCommandExecutor;
import kookaburra.minecraft.player.PlayerManager;
import kookaburra.minecraft.sabotage.Game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Who implements CommonCommandExecutor
{
	public CommonCommandExecutor original;

	public Who()
	{
		CommonCommand who = CommonCommand.getByName("who");
		if (who != null)
		{
			original = who.getExecutor();
			who.setExecutor(this);
		}
	}

	@Override
	public boolean onExecute(CommandSender sender, String alias, String[] args) throws CommandExecutionException
	{
		if (Game.hasStarted)
		{
			sender.sendMessage(ChatColor.RED + "Players alive (" + PlayerManager.GetPlayerCount() + " of " + Bukkit.getMaxPlayers() + ")");
			sender.sendMessage(ChatColor.BLUE + "Detective " + ChatColor.YELLOW + "Innocent");
			String message = "";
			for (String otherName : Game.players)
			{
				Player other = Bukkit.getPlayer(otherName);
				if (other == null || !other.isOnline())
				{
					continue;
				}
				ChatColor color = ChatColor.YELLOW;
				if (Game.isDetective(other))
				{
					color = ChatColor.BLUE;
				}
				message = message + color + other.getName() + ChatColor.WHITE + ", ";
			}
			message = message.substring(0, message.length() - 2);
			sender.sendMessage(message);
			return true;
		}
		else
			return original.onExecute(sender, alias, args);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String alias, String[] args)
	{
		return Arrays.asList();
	}
}
