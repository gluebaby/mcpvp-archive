
package kookaburra.minecraft.sabotage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Level;

import kookaburra.minecraft.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerInfo
{
	public long lastLogin;
	public int karma;
	public String name;

	public PlayerInfo(String playerName)
	{
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		long startOfDay = c.getTimeInMillis();
		lastLogin = startOfDay;
		karma = 300;
		name = playerName;
		load();
	}

	public static PlayerInfo get(String playerName)
	{
		PlayerInfo info = new PlayerInfo(playerName.toLowerCase());
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		long startOfDay = c.getTimeInMillis();
		if (info.lastLogin < startOfDay)
		{
			info.karma += 25;
			info.lastLogin = startOfDay;
			info.save();
		}
		return info;
	}

	public void addKarma(int karma)
	{
		this.karma += karma;
		save();
	}

	public void removeKarma(int karma)
	{
		this.karma -= karma;
		if (this.karma <= 0)
		{
			Player player = Bukkit.getPlayer(name);
			if (player != null && player.isOnline())
			{
				Util.broadcast(ChatColor.GOLD + player.getName() + " is out of karma!");
				player.kickPlayer("You lost because you're out of karma. \nTomorrow you'll be able to join again.");
			}
			this.karma = 0;
		}
		save();
	}

	public void load()
	{
		File save = new File("../common/players/" + name);
		
		File folder = new File("../common/players/");
		
		if(!folder.exists())
			folder.mkdirs();
		
		if (!save.exists())
		{
			try
			{
				save.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			save();
			return;
		}
		else
		{
			try
			{
				BufferedReader fin = new BufferedReader(new FileReader(save));
				lastLogin = Long.parseLong(fin.readLine());
				karma = Integer.parseInt(fin.readLine());
				// saboteurPasses = Integer.parseInt(fin.readLine());
				fin.close();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public void save()
	{
		File save = new File("../common/players/" + name);
		
		File folder = new File("../common/players/");
		
		if(!folder.exists())
			folder.mkdirs();
		
		if (!save.exists())
		{
			try
			{
				save.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			return;
		}
		try
		{
			BufferedWriter fout = new BufferedWriter(new FileWriter(save));
			fout.write(String.valueOf(lastLogin));
			fout.newLine();
			fout.write(String.valueOf(karma));
			fout.close();
		}
		catch (Exception ex)
		{
			Bukkit.getLogger().log(Level.WARNING, "Couldn't save player data of " + name + "!");
		}
	}
}
