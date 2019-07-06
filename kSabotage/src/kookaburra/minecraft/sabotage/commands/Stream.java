
package kookaburra.minecraft.sabotage.commands;

import kookaburra.minecraft.commands.CommandBase;
import kookaburra.minecraft.player.SharedPlayer;
import kookaburra.minecraft.sabotage.Game;

import org.bukkit.ChatColor;

public class Stream extends CommandBase
{
	public Stream()
	{
		super("stream");
	}

	@Override
	public boolean Execute(SharedPlayer player, String[] args)
	{
		if (Game.streamers.contains(player.getName()))
		{
			player.sendMessage(ChatColor.GOLD + "You will see everything normal again");
			Game.streamers.remove(player.getName());
		}
		else
		{
			Game.streamers.add(player.getName());
			player.sendMessage(new String[]{ChatColor.GOLD + "You are now in streamer mode, even if you're a saboteur, nametags will not be colored", ChatColor.GOLD + "for saboteurs and other saboteurs chat won't include the <" + ChatColor.RED + "S" + ChatColor.GOLD + ">.", ChatColor.GOLD + "You will also not receive any death reward messages.", ChatColor.GOLD + "Be careful with commands like /shop though."});
		}
		return true;
	}
}
