
package kookaburra.minecraft.sabotage.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import kookaburra.minecraft.kPlayer;
import kookaburra.minecraft.kShared;
import kookaburra.minecraft.mcpvp.login.handshake.HandshakeEvent;
import kookaburra.minecraft.player.PlayerManager;
import kookaburra.minecraft.player.SharedPlayer;
import kookaburra.minecraft.plugins.hax.HaxUtil;
import kookaburra.minecraft.plugins.hax.forcefield.ForcefieldTest;
import kookaburra.minecraft.sabotage.Game;
import kookaburra.minecraft.sabotage.MapPreparer;
import kookaburra.minecraft.sabotage.Messages;
import kookaburra.minecraft.sabotage.PlayerInfo;
import kookaburra.minecraft.sabotage.VoteControl;
import kookaburra.minecraft.sabotage.kSabotage;
import kookaburra.minecraft.sabotage.combatlog.CombatLogHandler;
import kookaburra.minecraft.util.FukkitUtil;
import kookaburra.minecraft.util.InventoryUtil;
import kookaburra.minecraft.util.ItemUtil;
import kookaburra.minecraft.util.Util;
import kookaburra.util.WeightedRandomizer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftItemStack;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

public class Events implements Listener
{
	private VoteControl voteControl;

	public Events(VoteControl voteControl)
	{
		Bukkit.getPluginManager().registerEvents(this, kShared.getInstance());
		this.voteControl = voteControl;
	}

	@EventHandler
	public void onFoodChange(FoodLevelChangeEvent event)
	{
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		if (!(event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.CREATIVE))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onHangingBreak(HangingBreakEvent event)
	{
		event.setCancelled(true);
	}

	public static HashSet<Location> surpriseChest = new HashSet<Location>();

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if (!(event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.CREATIVE))
		{
			event.setCancelled(true);
		}
	}

	public static HashSet<String> secondWind = new HashSet<String>();
	public static HashMap<String, HashMap<String, Long>> damagers = new HashMap<String, HashMap<String, Long>>();

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event)
	{
		if (!Game.hasStarted)
		{
			event.setCancelled(true);
			return;
		}
		if (event instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent entityDmgEvent = (EntityDamageByEntityEvent) event;
			if (entityDmgEvent.getDamager() instanceof Player)
			{
				Player player = (Player) entityDmgEvent.getDamager();
				if (Game.isSpectator((Player) entityDmgEvent.getDamager()))
				{
					event.setCancelled(true);
					return;
				}
				if (event.getEntity() instanceof Player && !event.isCancelled())
				{
					if (!damagers.containsKey(player.getName()))
					{
						damagers.put(player.getName(), new HashMap<String, Long>());
					}
					damagers.get(player.getName()).put(((Player) event.getEntity()).getName(), System.currentTimeMillis());
				}
			}
		}
		if (event.getEntity() instanceof Player)
		{
			if (Game.isSpectator((Player) event.getEntity()))
			{
				event.setCancelled(true);
				return;
			}
			if (!damagers.containsKey(((Player) event.getEntity()).getName()))
			{
				damagers.put(((Player) event.getEntity()).getName(), new HashMap<String, Long>());
			}
			damagers.get(((Player) event.getEntity()).getName()).put(((Player) event.getEntity()).getName(), System.currentTimeMillis());
			if (secondWind.contains(((Player) event.getEntity()).getName()))
			{
				// if(((Player)event.getEntity()).getHealth() -
				// event.getDamage() <= 2)
				// {
				// if((((Player)event.getEntity()).getHealth() + (12 + (int)
				// (Math.random() * 4))) <= 20)
				// ((Player)event.getEntity()).setHealth((((Player)event.getEntity()).getHealth()
				// + (12 + (int) (Math.random() * 4))));
				// else
				// ((Player)event.getEntity()).setHealth(20);
				//
				// secondWind.remove(((Player)event.getEntity()).getName());
				// }
			}
		}
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event)
	{
		if (ForcefieldTest.ForceVillagerSpawn)
		{
			if (event.getEntityType() != EntityType.VILLAGER)
			{
				event.setCancelled(true);
			}
		}
		else
		{
			event.setCancelled(true);
		}
	}

	public static HashSet<String> martyr = new HashSet<String>();

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event)
	{
		event.setDroppedExp(0);
		if (event instanceof PlayerDeathEvent)
		{
			PlayerDeathEvent e = (PlayerDeathEvent) event;
			e.setDeathMessage(null);
			Player player = (Player) event.getEntity();
			if (CombatLogHandler.combatLogDamaged.contains(player.getName()))
			{
				Util.broadcast(ChatColor.AQUA + player.getName() + " combat logged to death!");
				CombatLogHandler.combatLogKilled.add(player.getName());
				CombatLogHandler.combatLogDamaged.remove(player.getName());
				return;
			}
			if (Game.hasStarted)
			{
				Game.deathKick((Player) event.getEntity());
			}
			if (martyr.contains(((Player) event.getEntity()).getName()))
			{
				TNTPrimed tnt;
				tnt = ((Player) event.getEntity()).getWorld().spawn(((Player) event.getEntity()).getLocation(), TNTPrimed.class);
				tnt.setFuseTicks(40);
				tnt.setYield(12);
				tnt.setIsIncendiary(true);
				martyr.remove(((Player) event.getEntity()).getName());
			}
			if (e.getEntity().getKiller() != null)
			{
				if (!killings.containsKey(e.getEntity().getKiller().getName()))
				{
					killings.put(e.getEntity().getKiller().getName(), new HashMap<String, Long>());
				}
				killings.get(e.getEntity().getKiller().getName()).put(e.getEntity().getName(), System.currentTimeMillis());
			}
		}
	}

	public static HashMap<String, HashMap<String, Long>> killings = new HashMap<String, HashMap<String, Long>>();

	@EventHandler
	public void onPortalEnter(PlayerPortalEvent event)
	{
		event.setCancelled(true);
	}

	@EventHandler
	public void onExplosion(EntityExplodeEvent event)
	{
		if (event.getLocation().getWorld().equals(Game.map))
		{
			for (Block block : MapPreparer.changeBlocks.keySet())
			{
				if (event.getLocation().distance(block.getLocation()) <= 7)
				{
					block.setType(block.getRelative(MapPreparer.changeBlocks.get(block)).getType());
					block.getRelative(MapPreparer.changeBlocks.get(block)).setType(Material.AIR);
					MapPreparer.changeBlocks.remove(block);
				}
			}
			for (Block block : MapPreparer.infoSigns)
			{
				if (event.getLocation().distance(block.getLocation()) <= 7)
				{
					MapPreparer.changeBlocks.remove(block);
				}
			}
			for (Block block : MapPreparer.lamps)
			{
				if (event.getLocation().distance(block.getLocation()) <= 7)
				{
					MapPreparer.changeBlocks.remove(block);
				}
			}
			if (MapPreparer.trigger.getLocation().distance(event.getLocation()) <= 7)
			{
				MapPreparer.trigger.setType(Material.AIR);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerLogin(final HandshakeEvent event)
	{
		Player player = event.getPlayer();
		PlayerInfo info = PlayerInfo.get(player.getName());
		if (info.karma <= 0)
		{
			event.setKick(ChatColor.RED + "Your karma is zero.\n You can't join anymore for the day.\n");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Game.updateCountdown();
		final Player player = event.getPlayer();
		if (Game.mapLoaded && Game.SILoaded && Game.chestsRandomized)
		{
			if (!player.getWorld().equals(Game.map))
			{
				player.getInventory().clear();
				Bukkit.getScheduler().scheduleSyncDelayedTask(kShared.getInstance(), new Runnable()
				{
					@Override
					public void run()
					{
						player.getInventory().clear();
						player.teleport(Game.map.getSpawnLocation());
					}
				}, 40);
			}
		}
		if (Game.hasStarted && !Game.isPlaying(player) && !Game.isSpectator(player))
		{
			Game.addSpectator(player.getPlayer(), Messages.SPECTATING_STARTED);
			player.setGameMode(GameMode.CREATIVE);
			player.sendMessage(Messages.SPECTATING);
			player.sendMessage(Messages.SPECTATING_SKULL);
			player.getInventory().addItem(new ItemStack(Material.SKULL_ITEM, 1));
			player.setDisplayName(player.getName());
		}
		else if (Game.hasStarted && !Game.isPlaying(player) && Game.isSpectator(player))
		{
			player.setGameMode(GameMode.CREATIVE);
			player.sendMessage(Messages.SPECTATING);
		}
		else if (!Game.hasStarted && Game.canExplore)
		{
			player.sendMessage(Messages.EXPLORE);
		}
		else if (!Game.mapLoaded && !Game.SILoaded && !Game.chestsRandomized)
		{
			if (Game.StartTimer != null)
			{
				player.sendMessage(ChatColor.RED + "The game is starting in " + Util.GetTimespanString(Game.StartTimer.getTimeLeft() * 1000) + "!");
			}
			else
			{
				player.sendMessage(ChatColor.RED + "Waiting for more Players...");
			}
			player.sendMessage(" ");
			player.sendMessage(ChatColor.STRIKETHROUGH + "                                               ");
			player.sendMessage(ChatColor.GOLD + "Currently voting for: ");
			player.sendMessage(ChatColor.DARK_AQUA + "#1: " + voteControl.getMapList().get(0).getName() + " - Votes: " + voteControl.getVoteCount(1));
			player.sendMessage(ChatColor.DARK_AQUA + "#2: " + voteControl.getMapList().get(1).getName() + " - Votes: " + voteControl.getVoteCount(2));
			player.sendMessage(ChatColor.DARK_AQUA + "#3: Random - Votes: " + voteControl.getVoteCount(3));
			player.sendMessage(ChatColor.STRIKETHROUGH + "                                               ");
			player.sendMessage(ChatColor.GOLD + "Say " + ChatColor.DARK_AQUA + "/vote <number>" + ChatColor.GOLD + " to vote for one of the maps.");
			player.sendMessage(ChatColor.STRIKETHROUGH + "                                               ");
			String[] pages = new String[]{"Welcome to sabotage!\nSabotage is a game which consists of Saboteurs, Innocents and Detectives.\nThe aim of the game is for the Innocents and Detectives to kill all the Saboteurs, and for the Saboteurs to kill the Innocents and detectives.", "This is the warm-up period for the game, or more commonly known as the lobby.\nWhen the game starts, you will have 30 seconds to explore the map and gather items from chests.", "After the immunity-time is over, your rank will be chosen.\nWhen you've been chosen, you can access the /shop command which allows you to purchase extra addons to help archieve your goal. Simply do /shop ingame to find out more.", "Not everyone gets the same things to purchase -- so beware! Purchasing stuff revolves around Karma. You can buy Karma with credits, or earn Karma by killing correct people.", "However, be aware of who you kill.\nIf you kill the wrong person, you will lose Karma, which can often be devastating.", "For more info, see\n" + ChatColor.AQUA + "http://www.mcpvp.com/sabotage",};
			ItemStack book = FukkitUtil.makeBook("A saboteur's guide to sabotage", "Sir. Abotage", pages);
			player.getInventory().addItem(book);
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(kShared.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				Game.updateAllNames();
			}
		}, 10);
		PlayerInfo info = PlayerInfo.get(player.getName());
		player.setLevel(info.karma);
		if (info.karma < 100 && info.karma >= 50)
			player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 24 * 60 * 60 * 20, 0));
		else if (info.karma < 50)
			player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 24 * 60 * 60 * 20, 1));
		event.setJoinMessage(null);
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		final Player player = event.getPlayer();
		if (Game.isPlaying(player))
		{
			Game.deathKick(player);
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(kShared.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				Game.updateAllNamesFor(player);
			}
		}, 40);
		if (Game.isSpectator(player))
		{
			if (player.isOp())
			{
				Game.removeSpectator(player);
			}
			player.setGameMode(GameMode.CREATIVE);
			player.sendMessage(Messages.SPECTATING);
			player.sendMessage(Messages.SPECTATING_SKULL);
			ItemStack head = new ItemStack(Material.SKULL_ITEM);
			head = ItemUtil.setName(head, ChatColor.GRAY + "The head of " + player.getName());
			head = FukkitUtil.createPlayerSkull(CraftItemStack.asCraftCopy(head), player.getName());
			player.getInventory().addItem(head);
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000 * 1000 * 1000, 2));
			event.setRespawnLocation(Game.map.getSpawnLocation());
			player.setDisplayName(player.getName());
			return;
		}
		if (Game.map != null)
			event.setRespawnLocation(Game.map.getSpawnLocation());
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event)
	{
		if (Game.mapLoaded && event.getPlayer().getWorld() == Game.map)
		{
			Location to = event.getTo();
			int x = Math.abs(Math.abs(Game.map.getSpawnLocation().getBlockX()) - Math.abs(to.getBlockX()));
			int z = Math.abs(Math.abs(Game.map.getSpawnLocation().getBlockZ()) - Math.abs(to.getBlockZ()));
			if (x >= kSabotage.borderRadius || z >= kSabotage.borderRadius)
			{
				event.getPlayer().teleport(event.getFrom());
				kPlayer kp = kPlayer.Get(event.getPlayer());
				if (kp == null)
					return;
				kp.Invalidate(5 * 20);
			}
		}
	}

	// Handle arrows and make sure spectators can't block them.
	public static HashMap<Arrow, Integer> arrows = new HashMap<Arrow, Integer>();
	public static HashMap<Arrow, HashMap<String, Location>> arrowMovings = new HashMap<Arrow, HashMap<String, Location>>();

	@EventHandler
	public void onArrowShot(EntityShootBowEvent event)
	{
		final Arrow arrow = (Arrow) event.getProjectile();
		arrowMovings.put(arrow, (new HashMap<String, Location>()));
		final int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(kShared.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				for (String spectatorName : Game.spectators)
				{
					Player spectator = Bukkit.getPlayer(spectatorName);
					HashMap<String, Location> map = arrowMovings.get(arrow);
					if (spectator == null || !spectator.isOnline() || arrow == null)
						continue;
					if (spectator.getWorld().equals(arrow.getWorld()))
					{
						if (spectator.getLocation().distance(arrow.getLocation()) <= 4 && !map.containsKey(spectator.getName()))
						{
							map.put(spectator.getName(), spectator.getLocation());
							if (spectator.getLocation().distance(arrow.getLocation()) <= 4)
							{
								spectator.teleport(new Location(spectator.getWorld(), spectator.getLocation().getX(), spectator.getLocation().getY() + 50, spectator.getLocation().getZ()));
							}
						}
					}
				}
				final Vector velocity = arrow.getVelocity();
				Bukkit.getScheduler().scheduleSyncDelayedTask(kShared.getInstance(), new Runnable()
				{
					@Override
					public void run()
					{
						if (velocity.equals(arrow.getVelocity()))
						{
							if (arrows.containsKey(arrow))
							{
								Bukkit.getScheduler().cancelTask(arrows.get(arrow));
								for (String spectatorName : Game.spectators)
								{
									Player spectator = Bukkit.getPlayer(spectatorName);
									HashMap<String, Location> map = arrowMovings.get(arrow);
									if (spectator == null || !spectator.isOnline() || arrow == null || !map.containsKey(spectator.getName()))
										continue;
									spectator.teleport(map.get(spectator.getName()));
									map.remove(spectator.getName());
									spectator.sendMessage(Messages.SPECTATING_ARROW);
									spectator.sendMessage(Messages.SPECTATING_ARROW_ADVICE);
								}
								arrowMovings.remove(arrow);
								arrows.remove(arrow);
							}
						}
					}
				}, 5);
			}
		}, 0, 2);
		arrows.put(arrow, id);
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event)
	{
		if (Game.spectators.contains(event.getPlayer().getName()))
		{
			event.setCancelled(true);
			return;
		}
		// SharedPlayer player = PlayerManager.GetPlayer(event.getPlayer());
		if (event.getItem().getItemStack().getType() == Material.SHEARS)
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
		if (Game.spectators.contains(event.getPlayer().getName()))
		{
			event.getPlayer().sendMessage(Messages.DENIED_SPECTATOR);
			event.setCancelled(true);
			return;
		}
		if (event.getItemDrop().getItemStack().getType() == Material.SHEARS || event.getItemDrop().getItemStack().getType() == Material.MAP)
		{
			event.setCancelled(true);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event)
	{
		if (event.getPlayer() instanceof Player && Game.spectators.contains(event.getPlayer().getName()))
		{
			((Player) event.getPlayer()).sendMessage(Messages.DENIED_SPECTATOR);
			event.setCancelled(true);
			return;
		}
		if (event.getPlayer() instanceof Player && (event.getInventory().getHolder() instanceof Chest || event.getInventory().getHolder() instanceof DoubleChest))
		{
			if (!Game.canExplore)
			{
				event.setCancelled(true);
				return;
			}
			for (ItemStack i : event.getInventory().getContents())
			{
				if (i == null || i.getType() == Material.AIR)
					continue;
				event.getPlayer().getInventory().addItem(i);
			}
			((Player) event.getPlayer()).updateInventory();
			event.getInventory().clear();
			if (event.getInventory().getHolder() instanceof Chest)
			{
				((Chest) event.getInventory().getHolder()).getBlock().setType(Material.AIR);
			}
			else if (event.getInventory().getHolder() instanceof DoubleChest)
			{
				((DoubleChest) event.getInventory().getHolder()).getLocation().getBlock().setType(Material.AIR);
			}
			((Player) event.getPlayer()).updateInventory();
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPreCommand(PlayerCommandPreprocessEvent event)
	{
		String[] blockedForSpectators = new String[]{"/who", "/msg", "/r", "/me", "/tell", "/pm", "/w"};
		if (event.getMessage().toLowerCase().startsWith("/kill"))
		{
			event.getPlayer().sendMessage(ChatColor.RED + "This command is disabled.");
			event.setCancelled(true);
		}
		else if (event.getMessage().toLowerCase().startsWith("/vote"))
		{
			try
			{
				voteControl.registerVote(event.getPlayer(), Integer.parseInt(event.getMessage().split(" ")[1]));
			}
			catch (Exception e)
			{
				event.getPlayer().sendMessage(ChatColor.RED + e.getMessage());
			}
			event.setCancelled(true);
		}
		else if (Game.isSpectator(event.getPlayer()))
		{
			for (String blocked : blockedForSpectators)
			{
				if (event.getMessage().toLowerCase().contains(blocked))
				{
					event.getPlayer().sendMessage(Messages.DENIED_SPECTATOR);
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		SharedPlayer sp = PlayerManager.GetPlayer(event.getPlayer());
		if (sp == null)
			return;
		String color = HaxUtil.getChatColor(sp.getHaxPlayer(), false);
		Player player = sp.getPlayer();
		event.setFormat(ChatColor.WHITE + "<" + color + "%1$s" + ChatColor.WHITE + "> %2$s");
		event.setMessage(ChatColor.stripColor(event.getMessage()));
		if (Game.hasStarted)
		{
			for (Player other : Bukkit.getOnlinePlayers())
			{
				if (Game.isSaboteur(other) && Game.isSaboteur(player) && !Game.isStreamer(other))
				{
					if (other != null && other.isOnline())
						other.sendMessage("<" + color + player.getName() + ChatColor.WHITE + "> <" + ChatColor.RED + "S" + ChatColor.WHITE + "> " + event.getMessage());
				}
				else if (Game.spectators.size() <= 1 && Game.isSpectator(player))
				{
					player.sendMessage(ChatColor.WHITE + "(" + ChatColor.DARK_GRAY + "DEAD" + ChatColor.WHITE + ") " + player.getName() + " > " + event.getMessage());
					player.sendMessage("It seems you are the first to enter the land of the dead...");
				}
				else if (Game.isSpectator(player) && (Game.isSpectator(other) || other.isOp() && other.getGameMode() == GameMode.CREATIVE))
				{
					if (other != null && other.isOnline())
						other.sendMessage(ChatColor.WHITE + "(" + ChatColor.DARK_GRAY + "DEAD" + ChatColor.WHITE + ") " + player.getName() + " > " + event.getMessage());
				}
				else if (other != null && other.isOnline() && !Game.isSpectator(player))
					other.sendMessage("<" + color + player.getName() + ChatColor.WHITE + "> " + event.getMessage());
			}
		}
		if (!Game.hasStarted)
			return;
		event.setCancelled(true);
	}

	public static HashSet<String> insight = new HashSet<String>();

	@EventHandler
	public void onPlayerInteractWithPlayer(final PlayerInteractEntityEvent event)
	{
		if (event.getRightClicked() instanceof Player)
		{
			Player player = (Player) event.getRightClicked();
			if (insight.contains(event.getPlayer().getName()) && event.getPlayer().getItemInHand().getType() == Material.SHEARS)
			{
				if (Game.isInnocent(player))
				{
					event.getPlayer().sendMessage(ChatColor.GRAY + "He seems to be innocent...");
				}
				else if (Game.isSaboteur(player))
				{
					event.getPlayer().sendMessage(ChatColor.GRAY + "This... Evil... He's a saboteur!");
				}
				else if (Game.isDetective(player))
				{
					event.getPlayer().sendMessage(ChatColor.GRAY + "... That's a detective, yes.");
				}
				insight.remove(event.getPlayer().getName());
			}
			else if (event.getPlayer().getItemInHand().getType() == Material.GLASS_BOTTLE)
			{
				if (killings.containsKey(player.getName()))
				{
					boolean dried = true;
					for (Long time : killings.get(player.getName()).values())
					{
						if (time >= (System.currentTimeMillis() - (20 * 1000)))
						{
							dried = false;
						}
					}
					if (dried)
						event.getPlayer().sendMessage(ChatColor.GRAY + "So much blood... it's dried though, I bet they killed someone...");
					else
						event.getPlayer().sendMessage(ChatColor.GRAY + "Wow, they're covered in blood, did they kill someone just now..!?");
				}
				else if (damagers.containsKey(player.getName()))
				{
					boolean dried = true;
					for (Long time : damagers.get(player.getName()).values())
					{
						if (time >= (System.currentTimeMillis() - (60 * 1000)))
						{
							dried = false;
						}
					}
					if (dried)
						event.getPlayer().sendMessage(ChatColor.GRAY + "Hmm, there's a few dry blood stains on their body...");
					else
						event.getPlayer().sendMessage(ChatColor.GRAY + "Ah! there's a few fresh blood stains on their body..!");
				}
				else
				{
					event.getPlayer().sendMessage(ChatColor.GRAY + "Seems like their clothes have no blood or cuts on them...");
				}
			}
		}
		/*
		 * if(DeadBodyComponent.bodies.containsKey(event.getRightClicked().
		 * getEntityId()) && Game.isDetective(event.getPlayer()))
		 * {
		 * final DeadBody body =
		 * DeadBodyComponent.bodies.get(event.getRightClicked().getEntityId());
		 * 
		 * if(body == null)
		 * return;
		 * 
		 * final DeadBodyInfo info = body.info;
		 * final Player player = event.getPlayer();
		 * 
		 * player.sendMessage(ChatColor.AQUA +
		 * "Don't move! Stay close to the body while investigating it.");
		 * event.setCancelled(true);
		 * 
		 * final int checkingTask;
		 * 
		 * final Toggle close = new Toggle(true);
		 * 
		 * checkingTask =
		 * Bukkit.getScheduler().scheduleSyncRepeatingTask(kShared
		 * .getInstance(), new Runnable() {
		 * 
		 * @Override
		 * public void run()
		 * {
		 * if(body.location.distance(player.getLocation()) > 4 &&
		 * close.isTrue())
		 * {
		 * close.setState(false);
		 * player.sendMessage(ChatColor.AQUA +
		 * "You moved too far away from the body...");
		 * }
		 * }
		 * }, 0, 20);
		 * 
		 * Bukkit.getScheduler().scheduleSyncDelayedTask(kShared.getInstance(),
		 * new Runnable() {
		 * 
		 * @SuppressWarnings("deprecation")
		 * 
		 * @Override
		 * public void run()
		 * {
		 * Bukkit.getScheduler().cancelTask(checkingTask);
		 * 
		 * if(!close.isTrue())
		 * {
		 * return;
		 * }
		 * 
		 * Util.broadcast(ChatColor.AQUA + "The body of " + info.name +
		 * " has been found.");
		 * 
		 * String innocence = "";
		 * 
		 * if(info.isInnocent)
		 * {
		 * Util.broadcast(ChatColor.GREEN + "" + info.name +
		 * " was innocent...");
		 * innocence = ChatColor.GREEN + "innocent.";
		 * }
		 * 
		 * else if(!info.isInnocent)
		 * {
		 * Util.broadcast(ChatColor.RED + "" + info.name + " was a saboteur!");
		 * innocence = ChatColor.RED + "a saboteur.";
		 * }
		 * 
		 * String[] pages = new String[] { "Victim name:\n" + info.name +
		 * "\n----------------\nThis player was " + innocence };
		 * 
		 * ItemStack book = FukkitUtil.makeBook("Autopsy report of " +
		 * info.name, event.getPlayer().getName(), pages);
		 * 
		 * event.getPlayer().getInventory().addItem(book);
		 * event.getPlayer().updateInventory();
		 * }
		 * }, 200);
		 * }
		 */
	}

	public static HashSet<String> hackAbility = new HashSet<String>();

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteract(final PlayerInteractEvent event)
	{
		if (Game.spectators.contains(event.getPlayer().getName()))
		{
			if (event.hasItem() && event.getItem().getType() == Material.SKULL_ITEM && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK))
			{
				Player random = Bukkit.getOnlinePlayers()[(int) (Math.random() * Bukkit.getOnlinePlayers().length)];
				int tries = 0;
				while ((random == null || !random.isOnline() || random == event.getPlayer()) && tries <= 100)
				{
					random = Bukkit.getOnlinePlayers()[(int) (Math.random() * Bukkit.getOnlinePlayers().length)];
					tries++;
				}
				event.getPlayer().teleport(random);
				event.getPlayer().sendMessage("You got teleported to " + random.getName());
				event.setCancelled(true);
				return;
			}
			event.getPlayer().sendMessage(ChatColor.RED + "Spectators can't do this.");
			event.setCancelled(true);
			return;
		}
		if (event.hasBlock() && surpriseChest.contains(event.getClickedBlock().getLocation()))
		{
			event.getClickedBlock().setType(Material.AIR);
			TNTPrimed tnt;
			tnt = event.getClickedBlock().getWorld().spawn(event.getClickedBlock().getLocation(), TNTPrimed.class);
			tnt.setFuseTicks(30);
			tnt.setYield(3);
			surpriseChest.remove(event.getClickedBlock().getLocation());
			return;
		}
		if (event.hasBlock() && event.hasItem() && event.getAction() == Action.RIGHT_CLICK_BLOCK && ItemUtil.getName(event.getItem()).equalsIgnoreCase("Surprise Chest"))
		{
			Block block = event.getClickedBlock().getRelative(event.getBlockFace());
			if (Util.IsAir(event.getClickedBlock().getType()))
			{
				block = event.getClickedBlock();
			}
			surpriseChest.add(block.getLocation());
			block.setType(Material.CHEST);
			if (event.getPlayer().getItemInHand().getAmount() >= 2)
			{
				event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1);
			}
			else
			{
				InventoryUtil.removeItemFromInventory(event.getPlayer().getItemInHand(), event.getPlayer().getInventory());
				event.getPlayer().updateInventory();
			}
			return;
		}
		if (event.hasBlock() && event.hasItem() && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem().getType() == Material.TNT)
		{
			Block block = event.getClickedBlock().getRelative(event.getBlockFace());
			if (Util.IsAir(event.getClickedBlock().getType()))
			{
				block = event.getClickedBlock();
			}
			TNTPrimed tnt = block.getWorld().spawn(block.getLocation(), TNTPrimed.class);
			tnt.setYield(2);
			tnt.setFuseTicks(50);
			tnt.setIsIncendiary(true);
			if (event.getPlayer().getItemInHand().getAmount() >= 2)
			{
				event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1);
			}
			else
			{
				InventoryUtil.removeItemFromInventory(event.getPlayer().getItemInHand(), event.getPlayer().getInventory());
				event.getPlayer().updateInventory();
			}
			return;
		}
		// Chest handler
		else if (event.hasBlock() && event.getClickedBlock().getType() == Material.CHEST)
		{
			if (!Game.canExplore)
			{
				event.setCancelled(true);
				return;
			}
			event.getClickedBlock().setType(Material.AIR);
			final WeightedRandomizer<ItemStack[]> chestFiller = new WeightedRandomizer<ItemStack[]>(new Random());
			chestFiller.setWeight(new ItemStack[]{new ItemStack(Material.WOOD_SWORD)}, 200);
			chestFiller.setWeight(new ItemStack[]{new ItemStack(Material.STONE_SWORD)}, 100);
			chestFiller.setWeight(new ItemStack[]{new ItemStack(Material.BOW), new ItemStack(Material.ARROW, 32)}, 70);
			chestFiller.setWeight(new ItemStack[]{new ItemStack(Material.IRON_SWORD)}, 30);
			chestFiller.setWeight(new ItemStack[]{new ItemStack(Material.LEATHER_BOOTS)}, 40);
			chestFiller.setWeight(new ItemStack[]{new ItemStack(Material.LEATHER_HELMET)}, 35);
			chestFiller.setWeight(new ItemStack[]{new ItemStack(Material.LEATHER_LEGGINGS)}, 35);
			chestFiller.setWeight(new ItemStack[]{new ItemStack(Material.LEATHER_CHESTPLATE)}, 35);
			chestFiller.setWeight(new ItemStack[]{new ItemStack(Material.FLINT_AND_STEEL)}, 10);
			chestFiller.setWeight(new ItemStack[]{new Potion(PotionType.REGEN).toItemStack(1)}, 30);
			chestFiller.setWeight(new ItemStack[]{new Potion(PotionType.FIRE_RESISTANCE).toItemStack(1)}, 30);
			chestFiller.setWeight(new ItemStack[]{new Potion(PotionType.INSTANT_HEAL).toItemStack(1)}, 30);
			chestFiller.setWeight(new ItemStack[]{new Potion(PotionType.SPEED).toItemStack(1)}, 30);
			chestFiller.setWeight(new ItemStack[]{new ItemStack(Material.ENDER_PEARL, 4)}, 4);
			event.getPlayer().getInventory().addItem(chestFiller.select());
			event.getPlayer().updateInventory();
			event.setCancelled(true);
		}
		// Ender Chest handler
		else if (event.hasBlock() && event.getClickedBlock().getType() == Material.ENDER_CHEST)
		{
			if (!Game.canExplore)
			{
				event.setCancelled(true);
				return;
			}
			event.getClickedBlock().setType(Material.AIR);
			final WeightedRandomizer<ItemStack[]> chestFiller = new WeightedRandomizer<ItemStack[]>(new Random());
			chestFiller.setWeight(new ItemStack[]{new ItemStack(Material.BOW), new ItemStack(Material.ARROW, 32)}, 40);
			chestFiller.setWeight(new ItemStack[]{new ItemStack(Material.IRON_SWORD)}, 30);
			chestFiller.setWeight(new ItemStack[]{new ItemStack(Material.TNT, 5)}, 30);
			chestFiller.setWeight(new ItemStack[]{new ItemStack(Material.GOLD_BOOTS)}, 30);
			chestFiller.setWeight(new ItemStack[]{new ItemStack(Material.GOLD_HELMET)}, 25);
			chestFiller.setWeight(new ItemStack[]{new ItemStack(Material.GOLD_LEGGINGS)}, 20);
			chestFiller.setWeight(new ItemStack[]{new ItemStack(Material.GOLD_CHESTPLATE)}, 20);
			chestFiller.setWeight(new ItemStack[]{new ItemStack(Material.ENDER_PEARL, 4)}, 20);
			event.getPlayer().getInventory().addItem(chestFiller.select());
			event.getPlayer().updateInventory();
			event.setCancelled(true);
		}
		if (Game.hasStarted && event.hasBlock() && MapPreparer.trigger.equals(event.getClickedBlock()) && !MapPreparer.checkingPlayers.contains(event.getPlayer().getName()) && MapPreparer.checkingPlayers.size() == 0)
		{
			final Player player = event.getPlayer();
			MapPreparer.checkingPlayers.add(player.getName());
			player.teleport(MapPreparer.trigger.getLocation().clone().subtract(0, 1, 0));
			for (Block block : MapPreparer.changeBlocks.keySet())
			{
				block.getRelative(MapPreparer.changeBlocks.get(block)).setType(block.getType());
				block.setType(block.getType());
			}
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 15 * 20, 255));
			Util.broadcast(ChatColor.AQUA + player.getName() + " is being analyzed in the Saboteur Inspecter for another " + ChatColor.DARK_AQUA + "15 seconds" + ChatColor.AQUA + ", watch for the red light.");
			Bukkit.getScheduler().scheduleSyncDelayedTask(kShared.getInstance(), new Runnable()
			{
				@Override
				public void run()
				{
					Util.broadcast(ChatColor.AQUA + player.getName() + " is being analyzed in the Saboteur Inspecter for another " + ChatColor.DARK_AQUA + "5 seconds" + ChatColor.AQUA + ", watch for the red light.");
				}
			}, 200);
			Bukkit.getScheduler().scheduleSyncDelayedTask(kShared.getInstance(), new Runnable()
			{
				@Override
				public void run()
				{
					player.removePotionEffect(PotionEffectType.SLOW);
					Iterator<Block> it = MapPreparer.lamps.iterator();
					while (it.hasNext())
					{
						final Block lamp = it.next();
						if (hackAbility.contains(player.getName()))
						{
							// Red
							lamp.setData((byte) 5);
						}
						else if (!Game.isSaboteur(player))
						{
							// Red
							lamp.setData((byte) 5);
						}
						else if (Game.isSaboteur(player))
						{
							// Green
							lamp.setData((byte) 14);
						}
						Bukkit.getScheduler().scheduleSyncDelayedTask(kShared.getInstance(), new Runnable()
						{
							@Override
							public void run()
							{
								MapPreparer.checkingPlayers.remove(player.getName());
								lamp.setData((byte) 0);
							}
						}, 60);
					}
					hackAbility.remove(player.getName());
					for (Block block : MapPreparer.changeBlocks.keySet())
					{
						block.setType(block.getRelative(MapPreparer.changeBlocks.get(block)).getType());
						block.getRelative(MapPreparer.changeBlocks.get(block)).setType(Material.AIR);
					}
				}
			}, 300);
		}
		if (event.hasItem() && event.getItem().getType() == Material.COMPASS)
		{
			Game.SetCompassTarget(event.getPlayer());
		}
		if (event.hasBlock() && MapPreparer.infoSigns.contains(event.getClickedBlock()))
		{
			event.getPlayer().sendMessage(new String[]{ChatColor.AQUA + "The Saboteur Inspecter will show you who the saboteurs are", ChatColor.AQUA + "and who the good guys are...", ChatColor.AQUA + "All you need to do is go in and right click the button.", ChatColor.AQUA + "Then the lamps will turn either green or red.", ChatColor.RED + "Red = Saboteur" + ChatColor.RESET + " and " + ChatColor.GREEN + " Green = Innocent",});
		}
		/*
		 * if(event.hasItem() && event.hasBlock() && event.getItem().getType()
		 * == Material.SHEARS &&
		 * !Game.spectators.contains(event.getPlayer().getName()) &&
		 * Game.detectives.contains(event.getPlayer().getName()))
		 * {
		 * Iterator<DeadBody> it = DeadBodyComponent.bodies.values().iterator();
		 * 
		 * while(it.hasNext())
		 * {
		 * final DeadBody body = it.next();
		 * 
		 * if(body == null)
		 * continue;
		 * 
		 * if(body.location.distance(event.getClickedBlock().getLocation()) > 4)
		 * continue;
		 * 
		 * final DeadBodyInfo info = body.info;
		 * final Player player = event.getPlayer();
		 * 
		 * player.sendMessage(ChatColor.AQUA +
		 * "Don't move! Stay close to the body while investigating it.");
		 * 
		 * final int checkingTask;
		 * 
		 * final Toggle close = new Toggle(true);
		 * 
		 * checkingTask =
		 * Bukkit.getScheduler().scheduleSyncRepeatingTask(kShared
		 * .getInstance(), new Runnable() {
		 * 
		 * @Override
		 * public void run()
		 * {
		 * if(body.location.distance(player.getLocation()) > 4 &&
		 * close.isTrue())
		 * {
		 * close.setState(false);
		 * player.sendMessage(ChatColor.AQUA +
		 * "You moved too far away from the body...");
		 * }
		 * }
		 * }, 0, 20);
		 * 
		 * Bukkit.getScheduler().scheduleSyncDelayedTask(kShared.getInstance(),
		 * new Runnable() {
		 * 
		 * @Override
		 * public void run()
		 * {
		 * Bukkit.getScheduler().cancelTask(checkingTask);
		 * 
		 * if(!close.isTrue())
		 * {
		 * return;
		 * }
		 * 
		 * Util.broadcast(ChatColor.AQUA + "The body of " + info.name +
		 * " has been found.");
		 * 
		 * String innocence = "";
		 * 
		 * if(info.isInnocent)
		 * {
		 * Util.broadcast(ChatColor.GREEN + "" + info.name +
		 * " was innocent...");
		 * innocence = ChatColor.GREEN + "innocent.";
		 * }
		 * 
		 * else if(!info.isInnocent)
		 * {
		 * Util.broadcast(ChatColor.RED + "" + info.name + " was a saboteur!");
		 * innocence = ChatColor.RED + "a saboteur.";
		 * }
		 * 
		 * String[] pages = new String[] { "Victim name:\n" + info.name +
		 * "\n----------------\nThis player was " + innocence };
		 * 
		 * ItemStack book = FukkitUtil.makeBook("Autopsy report of " +
		 * info.name, event.getPlayer().getName(), pages);
		 * 
		 * event.getPlayer().getInventory().addItem(book);
		 * event.getPlayer().updateInventory();
		 * }
		 * }, 200);
		 * }
		 * }
		 */
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onServerListPing(ServerListPingEvent event)
	{
		if (Game.hasStarted || Game.mapLoaded)
		{
			event.setMotd(ChatColor.GRAY + "Game in progress.");
		}
		else if (Game.hasEnded)
		{
			event.setMotd(ChatColor.GRAY + "Game finished.");
		}
		else
		{
			if (Game.StartTimer == null)
			{
				event.setMotd(ChatColor.GREEN + "Waiting for players...");
			}
			else
			{
				event.setMotd(ChatColor.GREEN + "Starts in " + Util.GetTimespanString(Game.StartTimer.getTimeLeft() * 1000) + ".");
			}
		}
	}

	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event)
	{
		event.setCancelled(true);
	}

	@EventHandler
	public void onInventoryChange(InventoryClickEvent event)
	{
		if (ItemUtil.getName(event.getCurrentItem()).equals("Map Tracker") || ItemUtil.getName(event.getCursor()).equals("Map Tracker"))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onCrafting(CraftItemEvent event)
	{
		event.setCancelled(true);
		event.setResult(CraftItemEvent.Result.DENY);
	}

	@EventHandler
	public void onBucketFill(PlayerBucketFillEvent event)
	{
		event.setCancelled(true);
	}

	@EventHandler
	public void onBucketEmpty(PlayerBucketEmptyEvent event)
	{
		event.setCancelled(true);
	}

	@EventHandler
	public void onEnderPearl(PlayerTeleportEvent event)
	{
		if (event.getCause() == TeleportCause.ENDER_PEARL)
		{
			event.setCancelled(true);
			event.getPlayer().teleport(event.getTo());
		}
	}

	public void onPlayerQuit(PlayerQuitEvent event)
	{
		Game.updateCountdown();
	}
}
