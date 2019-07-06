
package kookaburra.minecraft.sabotage;

import kookaburra.minecraft.kShared;
import kookaburra.minecraft.mcpvp.McpvpPlugin;
import kookaburra.minecraft.util.API;
import kookaburra.minecraft.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GameOver
{
	public static void Run()
	{
		Game.hasStarted = false;
		Game.canExplore = false;
		Game.hasEnded = true;
		Util.broadcast(ChatColor.GOLD + "The saboteurs were:");
		String saboteurs = "";
		for (String saboteur : Game.allSaboteurs)
		{
			if (saboteurs.equals(""))
			{
				saboteurs = saboteur;
			}
			else
			{
				saboteurs = saboteurs + ", " + saboteur;
			}
		}
		Util.broadcast(ChatColor.RED + saboteurs);
		if (Game.saboteurs.size() == 0)
		{
			if (Game.detectives.size() == 0)
			{
				Util.broadcast(ChatColor.GOLD + "All alive innocents got a karma bonus of 40 karma!");
			}
			else
			{
				Util.broadcast(ChatColor.DARK_GREEN + "All alive innocents and the detective got a karma bonus of 40 karma!");
			}
			for (String innocent : Game.innocents)
			{
				Player player = Bukkit.getPlayer(innocent);
				if (player == null || !player.isOnline())
					continue;
				PlayerInfo info = PlayerInfo.get(player.getName());
				info.addKarma(40);
				Game.updateKarma(player);
			}
			for (String detective : Game.detectives)
			{
				Player player = Bukkit.getPlayer(detective);
				if (player == null || !player.isOnline())
					continue;
				PlayerInfo info = PlayerInfo.get(player.getName());
				info.addKarma(80);
				Game.updateKarma(player);
				player.sendMessage(ChatColor.AQUA + "As the detective, you got 40 more karma for succesfully solving the case!");
			}
		}
		else if (Game.innocents.size() == 0 && Game.detectives.size() == 0)
		{
			Util.broadcast(ChatColor.DARK_RED + "All alive saboteurs got a karma bonus of 40 karma!");
			for (String saboteur : Game.saboteurs)
			{
				Player player = Bukkit.getPlayer(saboteur);
				if (player == null || !player.isOnline())
					continue;
				PlayerInfo info = PlayerInfo.get(player.getName());
				info.addKarma(40);
				Game.updateKarma(player);
			}
		}
		for (String name : Game.players)
		{
			try
			{
				API.callAsync(kShared.getInstance(), "https://www.minecraftpvp.com/api/sabotage/lost.cshtml/" + McpvpPlugin.getInstance().getHostName() + "/" + name);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		Bukkit.broadcastMessage(ChatColor.AQUA + "Did you like this map?");
		Bukkit.broadcastMessage(ChatColor.AQUA + "Give us your feedback with " + ChatColor.GREEN + "/y" + ChatColor.AQUA + " or " + ChatColor.RED + "/n" + ChatColor.AQUA + "!");
		Bukkit.getScheduler().runTaskLater(kShared.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				kSabotage.mapsVote.record();
			}
		}, 400);
		Bukkit.getScheduler().runTaskLater(kShared.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				Util.killServer(ChatColor.GOLD + "Server restarting, try reconnecting!");
			}
		}, 800);
		// String data = "";
		//
		// data += Game.startTime + "\n";
		//
		// //This is just a simple cipher to prove we are making the request
		// // some sort of hash encoding would be better - but eh - this is easy
		// for now
		// char[] k = UUID.randomUUID().toString().toCharArray();
		// k[(int) ((Game.startTime * (Game.startTime % 57)) % k.length)] = '0';
		//
		// data += new String(k) + "\n";
		//
		// for(String stats : Game.Stats.values())
		// {
		// data += stats + "\n";
		// }
		//
		// System.out.println(data);
		//
		// OutputStreamWriter wr = null;
		// BufferedReader rd = null;
		//
		// try
		// {
		//
		// URL url = new
		// URL("https://www.minecraftpvp.com/api/tournaments/hungergames/gameover");
		// URLConnection conn = url.openConnection();
		// conn.setDoOutput(true);
		// wr = new OutputStreamWriter(conn.getOutputStream());
		// wr.write(data);
		// wr.flush();
		//
		// // Get the response
		// rd = new BufferedReader(new
		// InputStreamReader(conn.getInputStream()));
		//
		// String line;
		// while ((line = rd.readLine()) != null) {
		// System.out.println(line);
		// }
		//
		// wr.close();
		// rd.close();
		// }
		// catch (Exception e)
		// {
		// e.printStackTrace();
		// System.out.println(e);
		// System.out.println(data);
		// }
		// finally
		// {
		// if (wr != null)
		// {
		// try
		// {
		// wr.close();
		// }
		// catch (Exception e) {
		// }
		// }
		//
		// if (rd != null)
		// {
		// try
		// {
		// rd.close();
		// }
		// catch (Exception e) {
		// }
		// }
		// }
		// Bukkit.shutdown();
	}
}
