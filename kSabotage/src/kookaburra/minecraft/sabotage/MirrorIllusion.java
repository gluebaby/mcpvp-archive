
package kookaburra.minecraft.sabotage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import kookaburra.minecraft.kShared;
import kookaburra.minecraft.plugins.hax.forcefield.ForcefieldTest;
import kookaburra.minecraft.util.Util;
import kookaburra.util.ReflectionUtil;
import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R1.PacketPlayOutNamedEntitySpawn;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class MirrorIllusion
{
	public static HashMap<Entity, List<String>> bodyList = new HashMap<Entity, List<String>>();
	public static HashMap<Entity, Integer> bodyTask = new HashMap<Entity, Integer>();
	public static HashSet<Entity> mirrorImages = new HashSet<Entity>();
	public static boolean inProgress = false;
	public static Player currentCaster = null;

	public static void cast(final Player player)
	{
		inProgress = true;
		currentCaster = player;
		ForcefieldTest.ForceVillagerSpawn = true;
		Util.broadcast(ChatColor.DARK_AQUA + "A dark shadow falls over the map, and a mirror illusion was cast.");
		for (Chunk chunk : Game.map.getLoadedChunks())
		{
			boolean dontSpawn = true;
			for (Player others : Bukkit.getOnlinePlayers())
			{
				if (others.getWorld() != chunk.getWorld())
					continue;
				if (others.getLocation().distance(chunk.getBlock(0, Game.map.getHighestBlockYAt(chunk.getBlock(8, 0, 8).getLocation()), 0).getLocation()) <= 16 * 4)
				{
					dontSpawn = false;
					break;
				}
			}
			if (dontSpawn)
				continue;
			for (int i = 0; i < 2; i++)
			{
				final Location spawnLoc = chunk.getBlock((int) (Math.random() * 15), Game.map.getHighestBlockYAt(chunk.getBlock(8, 0, 8).getLocation()), (int) (Math.random() * 15)).getLocation();
				final Entity entity = Game.map.spawnEntity(spawnLoc, EntityType.VILLAGER);
				boolean entityWasSpawned = false;
				for (LivingEntity e : player.getLocation().getWorld().getLivingEntities())
				{
					if (e.getEntityId() == entity.getEntityId())
					{
						entityWasSpawned = true;
						break;
					}
				}
				if (!entityWasSpawned)
				{
					System.out.println("An entity was not spawned.");
				}
				final PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(entity.getEntityId());
				for (Player players : Bukkit.getOnlinePlayers())
				{
					((CraftPlayer) players).getHandle().playerConnection.sendPacket(destroy);
				}
				String name = (String) Game.players.toArray()[(int) (Game.players.size() * Math.random())];
				Player mirror = Bukkit.getPlayer(name);
				int tries = 0;
				while ((mirror == null || !mirror.isOnline()) && tries <= 100)
				{
					name = (String) Game.players.toArray()[(int) (Game.players.size() * Math.random())];
					mirror = Bukkit.getPlayer(name);
					tries++;
				}
				final Player mirrorPlayer = mirror;
				final PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(((CraftPlayer) mirror).getHandle());
				final String previousName = name;
				if (name.length() > 14)
					name = name.substring(0, 14);
				final String newName = name;
				ReflectionUtil.getAndSetField("a", spawn, entity.getEntityId());
				ReflectionUtil.getAndSetField("c", spawn, (entity.getLocation().getBlockX() * 32));
				ReflectionUtil.getAndSetField("d", spawn, (entity.getLocation().getBlockY() * 32));
				ReflectionUtil.getAndSetField("e", spawn, (entity.getLocation().getBlockZ() * 32));
				final List<String> playerList = new ArrayList<String>();
				bodyList.put(entity, playerList);
				// This keeps track of who unloaded the dummy, and resends the
				// cloaking packages when they come back into range.
				int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(kShared.getInstance(), new Runnable()
				{
					@Override
					public void run()
					{
						EntityPlayer ep = null;
						Iterator<String> it = bodyList.get(entity).iterator();
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
								// Sometimes they result in nullpointer errors,
								// but spawn the dummy anyways.
								if (!bodyList.get(entity).contains(other.getName()))
								{
									bodyList.get(entity).add(other.getName());
									try
									{
										PacketPlayOutNamedEntitySpawn spawnUpdated = spawn;
										ReflectionUtil.getAndSetField("b", spawn, ChatColor.GREEN + newName);
										ep.playerConnection.sendPacket(spawnUpdated);
										mirrorPlayer.setDisplayName(previousName);
									}
									catch (Exception e)
									{
									}
								}
							}
						}
					}
				}, 1, 20);
				bodyTask.put(entity, taskId);
				mirrorImages.add(entity);
			}
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(kShared.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				for (Entity e : bodyTask.keySet())
				{
					Bukkit.getScheduler().cancelTask(bodyTask.get(e));
					final PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(e.getEntityId());
					for (Player other : Bukkit.getOnlinePlayers())
					{
						EntityPlayer ep = ((CraftPlayer) other).getHandle();
						try
						{
							ep.playerConnection.sendPacket(destroy);
						}
						catch (Exception ex)
						{
						}
					}
				}
				bodyTask.clear();
				bodyList.clear();
				for (Entity ent : mirrorImages)
				{
					ent.remove();
				}
				currentCaster = null;
				inProgress = false;
				Game.updateAllNames();
			}
		}, 40 * 20);
		Game.updateAllNames();
		ForcefieldTest.ForceVillagerSpawn = false;
	}
}
