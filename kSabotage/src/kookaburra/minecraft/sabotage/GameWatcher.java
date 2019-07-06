
package kookaburra.minecraft.sabotage;

import java.util.ArrayList;
import java.util.Hashtable;

import kookaburra.minecraft.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GameWatcher implements Runnable
{
	public static Hashtable<String, Integer> misses = new Hashtable<String, Integer>();
	public static boolean HasWon = false;

	@Override
	public void run()
	{
		Game.updateAllNames();
		if (Game.hasStarted)
		{
			ArrayList<String> playersToRemove = new ArrayList<String>();
			if (Game.innocents.size() == 0 && Game.detectives.size() == 0 || Game.saboteurs.size() == 0)
			{
				if (Game.saboteurs.size() == 0)
					Util.broadcast(ChatColor.RED + "The innocents wins!");
				else if (Game.innocents.size() == 0 && Game.detectives.size() == 0)
					Util.broadcast(ChatColor.RED + "The saboteurs wins!");
				if (!HasWon)
				{
					// String stats = Game.Stats.get(Game.players.get(0));
					//
					// if(stats.charAt(stats.length() - 1) == ',')
					// stats = stats.substring(0, stats.length() - 1);
					//
					// stats += ";";
					// stats += System.currentTimeMillis() - Game.startTime +
					// ";";
					// stats += Game.players.size();
					//
					// Game.Stats.put(Game.players.get(0), stats);
					//
					GameOver.Run();
					HasWon = true;
				}
				return;
			}
			for (String playerName : Game.players)
			{
				if (!misses.containsKey(playerName))
				{
					misses.put(playerName, 0);
				}
				Player player = Bukkit.getPlayer(playerName);
				if (misses.get(playerName) >= 15)
				{
					playersToRemove.add(playerName);
				}
				if (player == null || !player.isOnline())
				{
					misses.put(playerName, misses.get(playerName) + 1);
				}
			}
			for (String playerName : playersToRemove)
			{
				if (Game.players.size() > 1)
				{
					Game.losers.add(playerName);
					Game.players.remove(playerName);
					if (Game.saboteurs.contains(playerName))
					{
						Game.saboteurs.remove(playerName);
					}
					else if (Game.detectives.contains(playerName))
					{
						Game.detectives.remove(playerName);
					}
					else if (Game.innocents.contains(playerName))
					{
						Game.innocents.remove(playerName);
					}
					// String stats = Game.Stats.get(playerName);
					//
					// if(stats.charAt(stats.length() - 1) == ',')
					// stats = stats.substring(0, stats.length() - 1);
					//
					// stats += ";";
					// stats += System.currentTimeMillis() - Game.startTime +
					// ";";
					// stats += Game.players.size();
					Util.broadcast(ChatColor.AQUA + playerName + " was disconnected for too long, and has forfeit!");
				}
				else
				{
					Player player = Util.MatchPlayer((String) Game.players.toArray()[0]);
					if (player != null)
						player.setHealth(20);
				}
			}
		}
		else
		{
			if (System.currentTimeMillis() >= Game.restart && Game.StartTimer == null)
			{
				Util.killServer("Not enough Players!");
			}
		}
	}
}
