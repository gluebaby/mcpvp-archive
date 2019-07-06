
package kookaburra.minecraft.sabotage.commands;

import kookaburra.minecraft.command.CommonCommand;
import kookaburra.minecraft.sabotage.PlayerInfo;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class KarmaCommand extends CommonCommand
{
	public KarmaCommand(String name, String[] aliases)
	{
		super(name, aliases);
	}

	@Override
	public boolean onExecute(CommandSender sender, String alias, String[] args)
	{
		sender.sendMessage(ChatColor.GREEN + "You have " + ChatColor.DARK_GREEN + PlayerInfo.get(asPlayer(sender).getName()).karma + ChatColor.GREEN + " Karma.");
		return true;
	}
}
