
package kookaburra.minecraft.sabotage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import kookaburra.minecraft.CountDown;
import kookaburra.minecraft.kPlayer;
import kookaburra.minecraft.kShared;
import kookaburra.minecraft.mcpvp.McpvpPlayer;
import kookaburra.minecraft.mcpvp.McpvpPlugin;
import kookaburra.minecraft.mcpvp.command.map.VoteNoCommand;
import kookaburra.minecraft.mcpvp.command.map.VoteYesCommand;
import kookaburra.minecraft.mcpvp.map.ActiveMap;
import kookaburra.minecraft.mcpvp.map.ActiveMapHolder;
import kookaburra.minecraft.mcpvp.map.ActiveMapVoteSet;
import kookaburra.minecraft.mcpvp.map.ActiveMapVoteSetHolder;
import kookaburra.minecraft.player.PlayerDamage;
import kookaburra.minecraft.player.PlayerManager;
import kookaburra.minecraft.player.SharedPlayer;
import kookaburra.minecraft.plugins.hax.forcefield.ForcefieldTest;
import kookaburra.minecraft.util.API;
import kookaburra.minecraft.util.BroadcastCountdownTimer;
import kookaburra.minecraft.util.Duration;
import kookaburra.minecraft.util.FukkitUtil;
import kookaburra.minecraft.util.ItemUtil;
import kookaburra.minecraft.util.Util;
import kookaburra.minecraft.util.map.MapUtil;
import kookaburra.util.ReflectionUtil;
import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.util.com.mojang.authlib.GameProfile;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class Game implements ActiveMapHolder, ActiveMapVoteSetHolder
{
	public static HashSet<Location> chestLocations = new HashSet<Location>();
	public static HashSet<String> detectives;
	public static HashSet<String> innocents;
	public static HashSet<String> saboteurs;
	public static HashSet<String> allSaboteurs;
	public static HashSet<String> players;
	public static HashSet<String> spectators;
	public static Random random;
	public static HashSet<String> streamers;
	public static World map;
	public static ActiveMap mapActive;
	public static boolean hasStarted;
	public static boolean canExplore;
	public static boolean hasEnded;
	public static boolean mapCopied;
	public static boolean mapLoaded;
	public static boolean SILoaded;
	public static boolean chestsRandomized;
	public static CountDown countDown;
	public static long startTime;
	public static String winner;
	public static HashSet<String> losers;
	public static Hashtable<String, Long> logouts;
	public static BroadcastCountdownTimer StartTimer;
	public static long restart = System.currentTimeMillis() + (3600 * 1000);
	public static BukkitTask voteTimer;
	public static Hashtable<String, String> Stats = new Hashtable<String, String>();
	public static Hashtable<String, Long> LoginTime = new Hashtable<String, Long>();
	public static VoteControl voteControl;

	public static void Initialize()
	{
		voteTimer = Bukkit.getScheduler().runTaskTimer(kShared.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				Bukkit.broadcastMessage(ChatColor.STRIKETHROUGH + "                                               ");
				Bukkit.broadcastMessage(ChatColor.GOLD + "Currently voting for: ");
				Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "#1: " + voteControl.getMapList().get(0).getName() + " - Votes: " + voteControl.getVoteCount(1));
				Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "#2: " + voteControl.getMapList().get(1).getName() + " - Votes: " + voteControl.getVoteCount(2));
				Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "#3: Random - Votes: " + voteControl.getVoteCount(3));
				Bukkit.broadcastMessage(ChatColor.STRIKETHROUGH + "                                               ");
				Bukkit.broadcastMessage(ChatColor.GOLD + "Say " + ChatColor.DARK_AQUA + " /vote <number> " + ChatColor.GOLD + " to vote for one of the maps.");
				Bukkit.broadcastMessage(ChatColor.STRIKETHROUGH + "                                               ");
			}
		}, Duration.seconds(30).toTicks(), Duration.seconds(30).toTicks());
		logouts = new Hashtable<String, Long>();
		losers = new HashSet<String>();
		players = new HashSet<String>();
		spectators = new HashSet<String>();
		detectives = new HashSet<String>();
		innocents = new HashSet<String>();
		saboteurs = new HashSet<String>();
		allSaboteurs = new HashSet<String>();
		streamers = new HashSet<String>();
		random = new Random();
		hasStarted = false;
		canExplore = false;
		hasEnded = false;
		mapCopied = false;
		mapLoaded = false;
		SILoaded = false;
		chestsRandomized = false;
		startTime = 0;
		winner = null;
		/**
		 * Spawn the innocent and saboteur npc
		 */
		final ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		console.sendMessage(ChatColor.RED + "[SignReader] Reading all signs in the lobby.");
		final long start = System.currentTimeMillis();
		World lobby = Bukkit.getWorld("world");
		for (int x = -4; x < 4; x++)
		{
			for (int z = -4; z < 4; z++)
			{
				lobby.loadChunk(x, z);
				Chunk chunk = lobby.getChunkAt(x, z);
				for (Block block : MapUtil.findBlocks(chunk, Material.SIGN_POST, Material.WALL_SIGN))
				{
					Sign sign = (Sign) block.getState();
					try
					{
						if (sign.getLine(0).equalsIgnoreCase("[saboteur]"))
						{
							System.out.println("Tried to spawn saboteur at " + sign.getLocation());
							spawnNPC(sign.getLocation(), ChatColor.RED + "Saboteur");
							block.setType(Material.AIR);
						}
						else if (sign.getLine(0).equalsIgnoreCase("[innocent]"))
						{
							System.out.println("Tried to spawn innocent at " + sign.getLocation());
							spawnNPC(sign.getLocation(), ChatColor.GREEN + "Innocent");
							block.setType(Material.AIR);
						}
						else if (sign.getLine(0).equalsIgnoreCase("[detective]"))
						{
							System.out.println("Tried to spawn detective at " + sign.getLocation());
							spawnNPC(sign.getLocation().add(0, 0, 2), ChatColor.BLUE + "Detective");
							block.setType(Material.AIR);
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		console.sendMessage(ChatColor.RED + "[SignReader] Finished reading all signs. - " + (System.currentTimeMillis() - start) + "ms");
		Runnable task = null;
		task = new GameWatcher();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(kShared.getInstance(), task, 20 * 4, 20 * 4);
	}

	public static void updateCountdown()
	{
		System.out.println("Updating countdown...");
		if (hasStarted || hasEnded)
			return;
		long time = Long.MAX_VALUE;
		if (StartTimer != null)
			time = StartTimer.getTimeLeft();
		if (Bukkit.getOnlinePlayers().length < 6)
		{
			if (StartTimer != null)
			{
				System.out.println("Stopping current countdown...");
				StartTimer.stop();
				StartTimer = null;
			}
		}
		else if (time > (3L * 60L))
		{
			if (StartTimer != null)
				StartTimer.stop();
			createCountdown();
		}
	}

	public static void createCountdown()
	{
		System.out.println("Creating countdown...");
		StartTimer = new BroadcastCountdownTimer(kShared.getInstance(), 3 * 60, ChatColor.RED + "Sabotage will start", null, new Runnable()
		{
			@Override
			public void run()
			{
				Bukkit.getScheduler().scheduleSyncDelayedTask(kShared.getInstance(), new Runnable()
				{
					@Override
					public void run()
					{
						voteTimer.cancel();
						ActiveMap map = voteControl.getWinningMap();
						Bukkit.broadcastMessage(ChatColor.AQUA + "Loading the map!");
						MapPreparer.openMap(map);
						MapPreparer.runChestRandomizer();
						Bukkit.broadcastMessage(ChatColor.GOLD + "You now have 30 seconds to explore the map and get items.");
						for (Player player : Bukkit.getOnlinePlayers())
						{
							if (player.getGameMode() == GameMode.CREATIVE)
								continue;
							player.getInventory().clear();
							player.teleport(Game.map.getSpawnLocation());
							player.getInventory().clear();
						}
						Game.canExplore = true;
						Bukkit.getScheduler().runTaskLater(kShared.getInstance(), new Runnable()
						{
							@Override
							public void run()
							{
								StartGame();
							}
						}, Duration.seconds(30).toTicks());
					}
				});
			}
		});
		StartTimer.start();
	}

	public static boolean usedSaboteurPass(String playername)
	{
		for (McpvpPlayer player : PassSystem.selected.keySet())
		{
			if (player == null)
				continue;
			if (player.getName().equalsIgnoreCase(playername))
				return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private static void StartGame()
	{
		ArrayList<Player> playerArray = new ArrayList<Player>();
		try
		{
			Player[] players = Bukkit.getOnlinePlayers();
			if (players.length > 0)
			{
				for (Player player : players)
				{
					if (player.isOp() && player.getGameMode() == GameMode.CREATIVE)
						continue;
					Game.addPlayer(player);
					if (player != null && (player.isOp() || player.getGameMode() == GameMode.ADVENTURE))
						playerArray.add(player);
				}
			}
			else
			{
				Bukkit.getLogger().log(Level.SEVERE, "0 Players. Restarting.");
				Util.killServer();
				return;
			}
		}
		catch (Exception ex)
		{
			System.out.println("Could not start game!" + ex.getMessage());
			ex.printStackTrace();
			Util.killServer();
			return;
		}
		Game.hasStarted = true;
		Game.startTime = System.currentTimeMillis();
		Bukkit.broadcastMessage(ChatColor.GOLD + "Sabotage... Begins!");
		if (Game.players.size() == 1)
			Bukkit.broadcastMessage(ChatColor.GOLD + "There is " + ChatColor.DARK_AQUA + Game.players.size() + ChatColor.GOLD + " player in this game.");
		else
			Bukkit.broadcastMessage(ChatColor.GOLD + "There are " + ChatColor.DARK_AQUA + Game.players.size() + ChatColor.GOLD + " players in this game.");
		Bukkit.broadcastMessage(ChatColor.GOLD + "Good Luck!");
		Player[] players = playerArray.toArray(new Player[playerArray.size()]);
		int detective = (int) (Math.random() * players.length);
		int tries = 0;
		while (usedSaboteurPass(players[detective].getName()) && tries <= 100)
		{
			detective = (int) (Math.random() * players.length);
			tries++;
		}
		Game.addDetective(players[detective]);
		int amount = (players.length / 5);
		if (amount < 1)
			amount = 1;
		for (int i = 0; i <= amount; i++)
		{
			int saboteur = (int) (Math.random() * players.length);
			tries = 0;
			while ((Game.isDetective(players[saboteur]) || Game.isSaboteur(players[saboteur])) && tries < 100)
			{
				saboteur = (int) (Math.random() * players.length);
				tries++;
			}
			if (PassSystem.passQueue.size() > 0)
			{
				String name = PassSystem.getNext().getName();
				Player player = Bukkit.getPlayer(name);
				if (player != null && player.isOnline())
				{
					if (!Game.isDetective(player))
					{
						Game.addSaboteur(player);
					}
					else
					{
						Game.addSaboteur(players[saboteur]);
					}
				}
				else
					Game.addSaboteur(players[saboteur]);
			}
			else
			{
				Game.addSaboteur(players[saboteur]);
			}
		}
		for (String playerName : Game.players)
		{
			Player player = Bukkit.getPlayer(playerName);
			if (player == null || !player.isOnline())
				continue;
			if (Game.isDetective(player) || Game.isSaboteur(player))
				continue;
			Game.addInnocent(player);
		}
		updateAllNames();
		for (String playerName : Game.players)
		{
			Player player = Bukkit.getPlayer(playerName);
			if (player == null || !player.isOnline())
			{
				Game.players.remove(playerName);
				continue;
			}
			String detectives = "";
			for (String name : Game.detectives)
			{
				if (detectives.equals(""))
				{
					detectives = name;
				}
				else
				{
					detectives = detectives + ", " + name;
				}
			}
			if (Game.isDetective(player))
			{
				player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "You are the " + ChatColor.BLUE + ChatColor.UNDERLINE + ChatColor.BOLD + "Detective" + ChatColor.RESET + "" + ChatColor.GOLD + ChatColor.BOLD + " this game!");
				player.sendMessage(ChatColor.DARK_AQUA + "Your job is to examine bodies and find out who the traitor is!");
				player.sendMessage(ChatColor.GOLD + "Other players have yellow names.");
			}
			else if (Game.isSaboteur(player))
			{
				String teamMates = "";
				for (String name : Game.saboteurs)
				{
					if (name.equals(player.getName()))
						continue;
					if (teamMates.equals(""))
					{
						teamMates = name;
					}
					else
					{
						teamMates = teamMates + ", " + name;
					}
				}
				player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "You are a " + ChatColor.RED + ChatColor.UNDERLINE + ChatColor.BOLD + "Saboteur" + ChatColor.RESET + "" + ChatColor.GOLD + ChatColor.BOLD + " this game!");
				player.sendMessage(ChatColor.GOLD + "Your fellow saboteurs are: " + ChatColor.RED + teamMates);
				player.sendMessage(ChatColor.DARK_AQUA + "You will be able to recognize them by their " + ChatColor.RED + "red" + ChatColor.DARK_AQUA + " nametag.");
				player.sendMessage(ChatColor.GOLD + "Your job is to kill all the " + ChatColor.GREEN + "innocents" + ChatColor.GOLD + " who are marked with a green name, and kill the " + ChatColor.BLUE + "detective" + ChatColor.GOLD + ".");
			}
			else if (Game.isInnocent(player))
			{
				player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "You are " + ChatColor.GREEN + ChatColor.UNDERLINE + "innocent" + ChatColor.RESET + "" + ChatColor.GOLD + ChatColor.BOLD + " this game!");
				player.sendMessage(ChatColor.DARK_AQUA + "You can use your magnifier (glass bottle) to investigate *living* players!");
				player.sendMessage(ChatColor.GOLD + "Your job is to find and kill the Saboteurs.");
			}
			// Tell others who the detective is
			if (!Game.isDetective(player))
			{
				player.sendMessage(ChatColor.GOLD + "The detective is: " + ChatColor.BLUE + detectives);
				player.sendMessage(ChatColor.GOLD + "The detective has a blue name.");
			}
		}
		Game instance = new Game();
		new VoteNoCommand(instance, instance).registerCommand();
		new VoteYesCommand(instance, instance).registerCommand();
		allSaboteurs = (HashSet<String>) Game.saboteurs.clone();
		/**
		 * Regeneration timer, regenerates 30 chests every 2 minutes, also has a
		 * 10% chance to create 1 ender chest.
		 */
		Bukkit.getScheduler().runTaskTimer(kShared.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				int i = 1;
				for (Location loc : chestLocations)
				{
					if (loc == null || loc.getBlock() == null)
						continue;
					if (loc.getBlock().getType() == Material.CHEST || loc.getBlock().getType() == Material.ENDER_CHEST)
						continue;
					if (i < 30)
					{
						loc.getBlock().setType(Material.CHEST);
					}
					if (i >= 31)
					{
						if (Math.random() < 0.10)
						{
							loc.getBlock().setType(Material.ENDER_CHEST);
						}
						break;
					}
					i++;
				}
			}
		}, (20 * 60 * 2 + 20 * 30), 20 * 60 * 2);
	}

	public static boolean isSaboteur(Player player)
	{
		return saboteurs.contains(player.getName());
	}

	public static boolean isDetective(Player player)
	{
		return detectives.contains(player.getName());
	}

	public static boolean isInnocent(Player player)
	{
		return innocents.contains(player.getName());
	}

	public static boolean isSpectator(Player player)
	{
		return spectators.contains(player.getName());
	}

	public static boolean isStreamer(Player player)
	{
		return streamers.contains(player.getName());
	}

	public static void addDetective(Player player)
	{
		if (!detectives.contains(player.getName()))
		{
			detectives.add(player.getName());
			Game.Stats.put(player.getName(), player.getName() + ";Detective;");
			player.getInventory().addItem(ItemUtil.setName(CraftItemStack.asCraftCopy(new ItemStack(Material.SHEARS)), ChatColor.RED + "Forceps"));
		}
	}

	public static void addSaboteur(Player player)
	{
		if (!saboteurs.contains(player.getName()))
		{
			saboteurs.add(player.getName());
			Game.Stats.put(player.getName(), player.getName() + ";Saboteur;");
			player.getInventory().addItem(ItemUtil.setName(CraftItemStack.asCraftCopy(new ItemStack(Material.GLASS_BOTTLE)), ChatColor.RED + "Magnifier"));
		}
	}

	public static void addInnocent(Player player)
	{
		if (!innocents.contains(player.getName()))
		{
			innocents.add(player.getName());
			Game.Stats.put(player.getName(), player.getName() + ";Innocent;");
			player.getInventory().addItem(ItemUtil.setName(CraftItemStack.asCraftCopy(new ItemStack(Material.GLASS_BOTTLE)), ChatColor.RED + "Magnifier"));
		}
	}

	public static void addSpectator(final Player player, String message)
	{
		spectators.add(player.getName());
		player.sendMessage(message);
		final String name = player.getName();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(kShared.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				final Player thePlayer = Bukkit.getPlayerExact(name);
				if (thePlayer == null || !thePlayer.isOnline())
					return;
				for (Player other : Bukkit.getOnlinePlayers())
				{
					other.hidePlayer(thePlayer);
				}
			}
		}, 20, 20);
	}

	public static void addPlayer(final Player player)
	{
		if (player.getGameMode() == GameMode.CREATIVE)
			return;
		Bukkit.getScheduler().scheduleSyncDelayedTask(kShared.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				SharedPlayer sp = PlayerManager.GetPlayer(player);
				if (sp == null)
				{
					sp = new SharedPlayer(player);
				}
				if (sp.isPRO())
				{
					sp.getPlayer().getInventory().addItem(new ItemStack(Material.BOW));
					sp.getPlayer().getInventory().addItem(new ItemStack(Material.ARROW, 64));
				}
				else if (sp.isMVP())
				{
					sp.getPlayer().getInventory().addItem(new ItemStack(Material.STONE_SWORD));
				}
				else if (sp.isVIP())
				{
					sp.getPlayer().getInventory().addItem(new ItemStack(Material.WOOD_SWORD));
				}
			}
		}, 20L);
		player.setGameMode(GameMode.ADVENTURE);
		final ItemStack map = ItemUtil.addDescription(ItemUtil.setName(FukkitUtil.makeStaticMap(player, Game.map, 1, Game.map.getSpawnLocation(), (short) 1337), "Map Tracker"), new String[]{ChatColor.RED + "Unremovable", ChatColor.RED + "Unmoveable", ChatColor.RED + "Game Item"});
		player.getInventory().setItem(35, map);
		players.add(player.getName());
		kPlayer kp = kPlayer.Get(player);
		if (kp != null)
			kp.Invalidate(20 * 5);
		// Reset Health
		player.setHealth(20);
		player.setFoodLevel(20);
	}

	public static void removeSpectator(final Player P)
	{
		spectators.remove(P.getName());
	}

	public static boolean isPlaying(Player player)
	{
		if (player == null)
			return false;
		String name = player.getName();
		for (String other : players)
		{
			if (other.equalsIgnoreCase(name))
				return true;
		}
		return false;
	}

	public static void deathKick(Player player)
	{
		deathKick(player, 0);
	}

	private static void deathKick(Player player, int attempt)
	{
		reportLost(player.getName());
		if (!isPlaying(player))
			return;
		Player killer = player.getKiller();
		if (killer != null)
		{
			PlayerInfo info = PlayerInfo.get(killer.getName());
			Stats.put(killer.getName(), Stats.get(killer.getName()) + player.getName() + ",");
			if (Game.isDetective(killer) || Game.isInnocent(killer))
			{
				if (Game.isInnocent(player))
				{
					info.removeKarma(50);
					killer.sendMessage(ChatColor.AQUA + player.getName() + " was innocent. " + ChatColor.RED + "You lost 50 karma.");
				}
				else if (Game.isDetective(player))
				{
					info.removeKarma(75);
					killer.sendMessage(ChatColor.AQUA + player.getName() + " was the detective. " + ChatColor.RED + "You lost 75 karma.");
				}
				else if (Game.isSaboteur(player))
				{
					info.addKarma(40);
					killer.sendMessage(ChatColor.AQUA + player.getName() + " was a saboteur! " + ChatColor.GREEN + "You gained 40 karma!");
				}
			}
			if (Game.isSaboteur(killer))
			{
				if (Game.isInnocent(player))
				{
					info.addKarma(25);
					killer.sendMessage(ChatColor.AQUA + player.getName() + " was innocent! " + ChatColor.GREEN + "You gained 25 karma!");
				}
				else if (Game.isDetective(player))
				{
					info.addKarma(40);
					killer.sendMessage(ChatColor.AQUA + player.getName() + " was the detective! " + ChatColor.GREEN + "You gained 40 karma!");
				}
				else if (Game.isSaboteur(player))
				{
					info.removeKarma(75);
					killer.sendMessage(ChatColor.AQUA + player.getName() + " was a saboteur... " + ChatColor.RED + "You lost 75 karma!");
				}
			}
			if (killer.isOnline())
			{
				Game.updateKarma(killer);
			}
		}
		// String stats = Stats.get(player.getName());
		//
		// if(stats.charAt(stats.length() - 1) == ',')
		// stats = stats.substring(0, stats.length() - 1);
		//
		// stats += ";";
		// stats += System.currentTimeMillis() - startTime + ";";
		// stats += Game.players.size();
		// Stats.put(player.getName(), stats);
		String name = player.getName();
		PlayerDamage damage = new PlayerDamage(player.getLastDamageCause());
		if (!damage.IsValid)
			damage = null;
		String message = "You died!";
		if (killer != null)
			message = "You got killed by " + killer.getName();
		Game.losers.add(name);
		Game.players.remove(name);
		if (Game.saboteurs.size() > 1 && (Game.innocents.size() > 1 || Game.detectives.size() > 1))
		{
			Bukkit.broadcastMessage(ChatColor.AQUA + "A player just got murdered...");
			Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "" + Game.players.size() + " players remaining.");
		}
		else if (Game.saboteurs.size() == 1 && Game.isSaboteur(player))
		{
			Bukkit.broadcastMessage(ChatColor.AQUA + name + " just got murdered...");
			Bukkit.broadcastMessage(ChatColor.RED + name + " was the last saboteur!");
		}
		else if (Game.innocents.size() == 1 && Game.isInnocent(player))
		{
			Bukkit.broadcastMessage(ChatColor.AQUA + name + " just got murdered...");
			Bukkit.broadcastMessage(ChatColor.GREEN + "" + name + " was the last innocent!");
		}
		else if (Game.isDetective(player))
		{
			Bukkit.broadcastMessage(ChatColor.AQUA + name + " just got murdered...");
			Bukkit.broadcastMessage(ChatColor.BLUE + "" + name + " was the detective!");
		}
		// DeadBodyComponent.spawnBody(player);
		if (isSaboteur(player))
		{
			PlayerInfo info = PlayerInfo.get(player.getName());
			Game.saboteurs.remove(player.getName());
			player.sendMessage(ChatColor.AQUA + "You failed to be a succesful saboteur.");
			player.sendMessage(ChatColor.AQUA + "You lost " + ChatColor.DARK_AQUA + "20 Karma" + ChatColor.AQUA + ".");
			info.removeKarma(20);
		}
		if (isInnocent(player))
			Game.innocents.remove(player.getName());
		if (isDetective(player))
			Game.detectives.remove(player.getName());
		// if(PlayerManager.GetPlayer(player).isMVP())
		// {
		Game.addSpectator(player, message);
		// return;
		// }
		// player.kickPlayer(message);
		player.sendMessage(ChatColor.AQUA + "Did you like this map?");
		player.sendMessage(ChatColor.AQUA + "Give us your feedback with " + ChatColor.GREEN + "/y" + ChatColor.AQUA + " or " + ChatColor.RED + "/n" + ChatColor.AQUA + "!");
	}

	public static void updateKarma(Player killer)
	{
		PlayerInfo info = PlayerInfo.get(killer.getName());
		killer.setLevel(info.karma);
	}

	public static void updateName(Player updatee, Player updated)
	{
		if (updatee == null || !updatee.isOnline() || updated == null || !updated.isOnline())
			return;
		String updateeName = updatee.getName();
		String updatedName = updated.getName();
		ChatColor color = ChatColor.YELLOW;
		String newName = "";
		if (MirrorIllusion.currentCaster != null && MirrorIllusion.currentCaster.equals(updatee))
		{
			color = ChatColor.RED;
			newName = color + updated.getName();
		}
		else
		{
			if (Game.isDetective(updated))
			{
				color = ChatColor.BLUE;
			}
			else if (Game.isSaboteur(updated))
			{
				if (Game.isSaboteur(updatee) && !Game.isStreamer(updatee))
				{
					color = ChatColor.RED;
				}
			}
			else if (Game.isInnocent(updated))
			{
				if (Game.isSaboteur(updatee) && !Game.isStreamer(updatee))
				{
					color = ChatColor.GREEN;
				}
			}
			newName = color + updated.getName();
		}
		if (updatee.canSee(updated))
		{
			PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn(((CraftPlayer) updated).getHandle());
			if (newName.length() > 16)
				newName = newName.substring(0, 16);
			ReflectionUtil.getAndSetField("a", packet, updated.getEntityId());
			ReflectionUtil.getAndSetField("b", packet, new GameProfile(newName, newName)); // Set the name of the player to the name they want.
			
			try
			{
				((CraftPlayer) updatee).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(updated.getEntityId()));
				((CraftPlayer) updatee).getHandle().playerConnection.sendPacket(packet);
			}
			catch (Exception ex)
			{
			}
		}
		updatee.setDisplayName(updateeName);
		updated.setDisplayName(updatedName);
	}

	public static void updateAllNames()
	{
		if (!Game.hasStarted)
			return;
		for (Player player : Bukkit.getOnlinePlayers())
		{
			for (Player other : Bukkit.getOnlinePlayers())
			{
				if (other == player || other.isOp() && other.getGameMode() == GameMode.CREATIVE && !spectators.contains(other.getName()))
					continue;
				if (player == null || !player.isOnline() || spectators.contains(other.getName()))
					continue;
				Game.updateName(player, other);
			}
		}
	}

	public static void updateAllNamesFor(Player player)
	{
		if (!Game.hasStarted || player.isDead() || player == null)
		{
			return;
		}
		for (Player other : Bukkit.getOnlinePlayers())
		{
			if (other == player || other.isOp() && other.getGameMode() == GameMode.CREATIVE && !spectators.contains(other.getName()))
				continue;
			if (player == null || !player.isOnline() || spectators.contains(other.getName()))
				continue;
			Game.updateName(player, other);
		}
	}

	public static boolean buyWithKarma(Player player, int amount)
	{
		PlayerInfo info = PlayerInfo.get(player.getName());
		if (info.karma - amount < 200)
		{
			player.sendMessage(ChatColor.RED + "Your karma can't go below 200 by buying items.");
			return false;
		}
		else
		{
			player.sendMessage(ChatColor.GOLD + "Thank you for buying!");
			info.removeKarma(amount);
			updateKarma(player);
			return true;
		}
	}

	public static void SetCompassTarget(Player player)
	{
		Player target = null;
		double minDistance = Long.MAX_VALUE;
		for (Player other : Bukkit.getOnlinePlayers())
		{
			if (other == null || other == player || !other.isOnline())
				continue;
			if (!Game.isPlaying(other))
				continue;
			double distance = other.getLocation().distance(player.getLocation());
			if (distance < minDistance)
			{
				minDistance = distance;
				target = other;
			}
		}
		if (target == null || target == player || !target.isOnline())
		{
			player.setCompassTarget(player.getWorld().getSpawnLocation());
			player.sendMessage(ChatColor.YELLOW + "No targets found, pointing at spawn.");
			return;
		}
		player.setCompassTarget(target.getLocation());
		player.sendMessage(ChatColor.YELLOW + "Compass pointing at " + target.getName());
	}

	public static void reportLost(String playerName)
	{
		try
		{
			API.callAsync(kShared.getInstance(), "https://www.minecraftpvp.com/api/tournaments/sabotage/lost.cshtml/" + McpvpPlugin.getInstance().getHostName() + "/" + playerName);
			if (usedSaboteurPass(playerName) && allSaboteurs.contains(playerName))
			{
				API.callAsync(kShared.getInstance(), "https://www.minecraftpvp.com/api/knohax/usesaboteurpass.cshtml/" + playerName);
				PassSystem.selected.remove(playerName);
			}
			Iterator<McpvpPlayer> it = PassSystem.selected.keySet().iterator();
			while (it.hasNext())
			{
				McpvpPlayer player = it.next();
				if (player == null)
					continue;
				if (player.getName().equals(playerName))
				{
					it.remove();
					continue;
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public static HashMap<Entity, List<String>> updateList = new HashMap<Entity, List<String>>();

	public static void spawnNPC(final Location spawnLoc, final String name)
	{
		ForcefieldTest.ForceVillagerSpawn = true;
		final Entity entity = spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.VILLAGER);
		boolean entityWasSpawned = false;
		for (LivingEntity e : spawnLoc.getWorld().getLivingEntities())
		{
			if (e.getEntityId() == entity.getEntityId())
			{
				entityWasSpawned = true;
				break;
			}
		}
		ForcefieldTest.ForceVillagerSpawn = false;
		if (!entityWasSpawned)
		{
			System.out.println(name + " was not spawned.");
		}
		final PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(entity.getEntityId());
		for (Player players : Bukkit.getOnlinePlayers())
		{
			((CraftPlayer) players).getHandle().playerConnection.sendPacket(destroy);
		}
		final List<String> playerList = new ArrayList<String>();
		updateList.put(entity, playerList);
		// This keeps track of who unloaded the dummy, and resends the cloaking
		// packages when they come back into range.
		Bukkit.getScheduler().scheduleSyncRepeatingTask(kShared.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				EntityPlayer ep = null;
				Iterator<String> it = updateList.get(entity).iterator();
				while (it.hasNext())
				{
					Player updated = Bukkit.getPlayer(it.next());
					if (updated == null)
					{
						it.remove();
						continue;
					}
					if (!updated.getLocation().getWorld().equals(spawnLoc.getWorld()))
					{
						it.remove();
						continue;
					}
					if (!updated.isOnline() || updated.getLocation().distance(spawnLoc) >= 50 && updated.isOnline())
					{
						it.remove();
						continue;
					}
				}
				for (Player other : Bukkit.getOnlinePlayers())
				{
					if (other.getLocation().getWorld() != spawnLoc.getWorld())
						continue;
					if (other.getLocation().distance(spawnLoc) < 50)
					{
						ep = ((CraftPlayer) other).getHandle();
						// Sometimes they result in nullpointer errors, but
						// spawn the dummy anyways.
						if (!updateList.get(entity).contains(other.getName()))
						{
							updateList.get(entity).add(other.getName());
							try
							{
								ep.playerConnection.sendPacket(destroy);
								final PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(ep);
								ReflectionUtil.getAndSetField("a", spawn, entity.getEntityId());
								ReflectionUtil.getAndSetField("b", spawn, new GameProfile(name, name));
								ReflectionUtil.getAndSetField("c", spawn, (int) (entity.getLocation().getX() * 32));
								ReflectionUtil.getAndSetField("d", spawn, (int) (entity.getLocation().getY() * 32));
								ReflectionUtil.getAndSetField("e", spawn, (int) (entity.getLocation().getZ() * 32));
								ReflectionUtil.getAndSetField("f", spawn, (byte) 0);
								ReflectionUtil.getAndSetField("g", spawn, (byte) 0);
								ReflectionUtil.getAndSetField("h", spawn, (short) 0);
								ep.playerConnection.sendPacket(spawn);
							}
							catch (Exception e)
							{
							}
						}
					}
				}
			}
		}, 1, 20);
	}

	@Override
	public ActiveMapVoteSet getSet()
	{
		return kSabotage.mapsVote;
	}

	@Override
	public ActiveMap getMap()
	{
		return Game.mapActive;
	}
}
