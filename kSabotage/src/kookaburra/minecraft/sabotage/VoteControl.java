
package kookaburra.minecraft.sabotage;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import kookaburra.minecraft.mcpvp.map.ActiveMap;
import kookaburra.minecraft.mcpvp.map.ActiveMapSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class VoteControl
{
	private static final Random random = new SecureRandom();
	private final List<ActiveMap> mapList = new ArrayList<>();
	private final Map<String, Integer> votes = new HashMap<String, Integer>();

	public VoteControl(ActiveMapSet maps)
	{
		getMapList().addAll(maps.getMaps().values());
		Collections.shuffle(getMapList(), random);
	}

	public void registerVote(Player player, int map)
	{
		if (1 <= map && map <= 3)
		{
			Integer chosenVote = votes.get(player.getName());
			if (chosenVote == null)
				player.sendMessage(ChatColor.DARK_AQUA + "Thank you for voting!");
			else
				player.sendMessage(ChatColor.DARK_AQUA + "Changing your vote from " + chosenVote + " to " + map);
			votes.put(player.getName(), map);
		}
		else
			player.sendMessage(ChatColor.DARK_AQUA + "Invalid vote. Do /vote <number>");
	}

	public int getVoteCount(int map)
	{
		int count = 0;
		for (int vote : votes.values())
		{
			if (vote == map)
				count++;
		}
		return count;
	}

	public ActiveMap getWinningMap()
	{
		int votes1 = getVoteCount(1);
		int votes2 = getVoteCount(2);
		int votesRandom = getVoteCount(3);
		int largest = Math.max(Math.max(votes1, votes2), votesRandom);
		if (largest == votesRandom)
		{
			Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "Random wins! Choosing a random map.");
			return getMapList().get(random.nextInt(getMapList().size()));
		}
		else if (votes1 == votes2)
		{
			Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "It's a tie! Choosing a random map.");
			return getMapList().get(random.nextInt(getMapList().size()));
		}
		else if (largest == votes1)
		{
			Bukkit.broadcastMessage(ChatColor.DARK_AQUA + getMapList().get(0).getName() + " won the vote!");
			return getMapList().get(0);
		}
		else
		{
			Bukkit.broadcastMessage(ChatColor.DARK_AQUA + getMapList().get(1).getName() + " won the vote!");
			return getMapList().get(1);
		}
	}

	public List<ActiveMap> getMapList()
	{
		return mapList;
	}
}
