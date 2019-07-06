
package kookaburra.minecraft.sabotage;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import kookaburra.minecraft.kShared;
import kookaburra.minecraft.mcpvp.map.ActiveMap;
import kookaburra.minecraft.mcpvp.map.MapManager;
import kookaburra.minecraft.util.generation.EmptyChunkGenerator;
import kookaburra.minecraft.util.map.MapUtil;
import kookaburra.util.io.IO;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

public class MapPreparer
{
	public static Block trigger;
	public static ConcurrentHashMap<Block, BlockFace> changeBlocks = new ConcurrentHashMap<Block, BlockFace>();
	public static HashSet<Block> lamps = new HashSet<Block>();
	public static HashSet<Block> infoSigns = new HashSet<Block>();
	public static HashSet<String> checkingPlayers = new HashSet<String>();
	public static int blocksPerTwoTicks = (int) (1 * Math.pow(10, 8)); // 100
																		// million

	/**
	 * Open up the map on the server
	 */
	public static void openMap(ActiveMap map)
	{
		try
		{
			IO.unzipMap(MapManager.getZipFile(map), new File("session"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		Game.map = Bukkit.createWorld(new WorldCreator("session").generator(new EmptyChunkGenerator()));
		Game.mapActive = map;
		MapPreparer.loadMap();
		MapPreparer.readSigns();
	}

	/**
	 * Load the map
	 */
	public static void loadMap()
	{
		ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		int chunkCount = (kSabotage.borderRadius) / 16 + 1;
		console.sendMessage(ChatColor.RED + "[MapLoader] Loading the map. (Radius: " + ((kSabotage.borderRadius)) + " blocks)");
		long start = System.currentTimeMillis();
		int spawnX = Game.map.getSpawnLocation().getChunk().getX();
		int spawnZ = Game.map.getSpawnLocation().getChunk().getZ();
		Chunk chunk = kShared.getInstance().getWorld().getSpawnLocation().getChunk();
		chunk.load();
		for (int x = -chunkCount; x < chunkCount; x++)
		{
			for (int z = -chunkCount; z < chunkCount; z++)
				Game.map.getChunkAt(x + spawnX, z + spawnZ).load();
		}
		console.sendMessage(ChatColor.RED + "[MapLoader] Done Loading Chunks... - " + (System.currentTimeMillis() - start) + "ms");
		Game.mapLoaded = true;
	}

	/**
	 * Load the saboteur inspector and replace signs.
	 */
	@SuppressWarnings("deprecation")
	public static void readSigns()
	{
		final ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		console.sendMessage(ChatColor.RED + "[SignReader] Reading all signs on the map and applying changes.");
		final long start = System.currentTimeMillis();
		for (Chunk chunk : Game.map.getLoadedChunks())
		{
			List<Block> signs = MapUtil.findBlocks(chunk, Material.SIGN_POST, Material.WALL_SIGN);
			for (Block block : signs)
			{
				Sign sign = (Sign) block.getState();
				// System.out.println(sign.getLine(0));
				// System.out.println(sign.getLine(1));
				// System.out.println(sign.getLine(2));
				// System.out.println(sign.getLine(3));
				try
				{
					if (sign.getLine(0).equalsIgnoreCase("[si trigger]"))
					{
						console.sendMessage(ChatColor.RED + "[SI Loader] Trigger block is located at " + sign.getLocation() + ".");
						// block.setType(Material.matchMaterial(sign.getLine(1)));
						sign.setLine(0, ChatColor.RED + "" + ChatColor.BOLD + "Click this");
						sign.setLine(1, ChatColor.RED + "" + ChatColor.BOLD + "sign to");
						sign.setLine(2, ChatColor.RED + "" + ChatColor.BOLD + "start the");
						sign.setLine(3, ChatColor.RED + "" + ChatColor.BOLD + "test");
						sign.update();
						trigger = block;
					}
					else if (sign.getLine(0).equalsIgnoreCase("[si down]"))
					{
						console.sendMessage(ChatColor.RED + "[SI Loader] Registered " + sign.getLine(0) + " at " + sign.getLocation() + ".");
						block.setType(Material.matchMaterial(sign.getLine(1)));
						changeBlocks.put(block, BlockFace.DOWN);
					}
					else if (sign.getLine(0).equalsIgnoreCase("[si up]"))
					{
						console.sendMessage(ChatColor.RED + "[SI Loader] Registered " + sign.getLine(0) + " at " + sign.getLocation() + ".");
						block.setType(Material.matchMaterial(sign.getLine(1)));
						changeBlocks.put(block, BlockFace.UP);
					}
					else if (sign.getLine(0).equalsIgnoreCase("[si north]"))
					{
						console.sendMessage(ChatColor.RED + "[SI Loader] Registered " + sign.getLine(0) + " at " + sign.getLocation() + ".");
						block.setType(Material.matchMaterial(sign.getLine(1)));
						changeBlocks.put(block, BlockFace.NORTH);
					}
					else if (sign.getLine(0).equalsIgnoreCase("[si east]"))
					{
						console.sendMessage(ChatColor.RED + "[SI Loader] Registered " + sign.getLine(0) + " at " + sign.getLocation() + ".");
						block.setType(Material.matchMaterial(sign.getLine(1)));
						changeBlocks.put(block, BlockFace.EAST);
					}
					else if (sign.getLine(0).equalsIgnoreCase("[si south]"))
					{
						console.sendMessage(ChatColor.RED + "[SI Loader] Registered " + sign.getLine(0) + " at " + sign.getLocation() + ".");
						block.setType(Material.matchMaterial(sign.getLine(1)));
						changeBlocks.put(block, BlockFace.SOUTH);
					}
					else if (sign.getLine(0).equalsIgnoreCase("[si west]"))
					{
						console.sendMessage(ChatColor.RED + "[SI Loader] Registered " + sign.getLine(0) + " at " + sign.getLocation() + ".");
						block.setType(Material.matchMaterial(sign.getLine(1)));
						changeBlocks.put(block, BlockFace.WEST);
					}
					else if (sign.getLine(0).equalsIgnoreCase("[si air]"))
					{
						console.sendMessage(ChatColor.RED + "[SI Loader] Registered air at " + sign.getLocation() + ".");
						block.setType(Material.AIR);
					}
					else if (sign.getLine(0).equalsIgnoreCase("[si lamp]"))
					{
						console.sendMessage(ChatColor.RED + "[SI Loader] Registered lamp at " + sign.getLocation() + ".");
						block.setType(Material.WOOL);
						block.setData((byte) 0);
						lamps.add(block);
					}
					else if (sign.getLine(0).equalsIgnoreCase("[si info]"))
					{
						console.sendMessage(ChatColor.RED + "[SI Loader] Registered info sign at " + sign.getLocation() + ".");
						sign.setLine(0, ChatColor.BLUE + "Click here to");
						sign.setLine(1, ChatColor.BLUE + "get info");
						sign.setLine(2, ChatColor.BLUE + "on this");
						sign.setLine(3, ChatColor.BLUE + "machine!");
						sign.update();
						infoSigns.add(block);
					}
					else if (sign.getLine(0).equalsIgnoreCase("[sabotage]"))
					{
						for (String line : sign.getLines())
						{
							if (line.toLowerCase().contains("chestcount:"))
							{
								try
								{
									chestsOnMap = Integer.parseInt(line.replace("chestcount:", ""));
								}
								catch (Exception e)
								{
									e.printStackTrace();
									chestsOnMap = 300;
								}
							}
						}
						block.setType(Material.AIR);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		Game.SILoaded = true;
		console.sendMessage(ChatColor.RED + "[SignReader] Finished reading all signs. - " + (System.currentTimeMillis() - start) + "ms");
	}

	public static double chestsOnMap = 300;
	public static int foundChests = 0;
	public static int destroyedChests = 0;
	public static int filledChests = 0;

	/**
	 * Randomize all the chests on the map, the map NEEDS to be loaded before
	 * calling this method, else chest filling will fail.
	 * Returns: Thread id of the loading task.
	 */
	public static void runChestRandomizer()
	{
		final ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		console.sendMessage(ChatColor.RED + "[ChestRandomizer] Randomize ALL the chests! (Radius: " + ((kSabotage.borderRadius)) + " blocks)");
		final long start = System.currentTimeMillis();
		final double destroyChance = (80 / chestsOnMap);
		final double enderChance = (destroyChance / (chestsOnMap / 10));
		System.out.println("Chests count: " + chestsOnMap);
		System.out.println("Destroy ratio: " + (1 - destroyChance));
		System.out.println("Ender chance: " + enderChance);
		for (Chunk chunk : Game.map.getLoadedChunks())
		{
			for (Block block : MapUtil.findBlocks(chunk, Material.CHEST))
			{
				foundChests++;
				Game.chestLocations.add(block.getLocation());
				double chance = Math.random();
				if (chance < enderChance)
				{
					block.breakNaturally();
					block.setType(Material.ENDER_CHEST);
				}
				else if (chance < destroyChance)
				{
				}
				else
				{
					block.breakNaturally();
					destroyedChests++;
				}
				block.getLocation().getWorld().refreshChunk(block.getLocation().getChunk().getX(), block.getLocation().getChunk().getZ());
			}
		}
		for (Entity entity : Game.map.getEntities())
		{
			if (entity instanceof Item)
				entity.remove();
		}
		Game.chestsRandomized = true;
		console.sendMessage(ChatColor.RED + "[ChestRandomizer] Done randomizing chests... - " + (System.currentTimeMillis() - start) + "ms");
		console.sendMessage(ChatColor.RED + "[ChestRandomizer] Found " + foundChests + " chests in the process.");
		console.sendMessage(ChatColor.RED + "[ChestRandomizer] Destroyed " + destroyedChests + " chests in the process.");
	}
}
