
package kookaburra.minecraft.sabotage;

import java.io.File;
import java.io.IOException;

import kookaburra.minecraft.Settings;
import kookaburra.minecraft.kShared;
import kookaburra.minecraft.map.RepairableMapComponent;
import kookaburra.minecraft.mcpvp.McpvpPlugin;
import kookaburra.minecraft.mcpvp.ServerType;
import kookaburra.minecraft.mcpvp.map.ActiveMap;
import kookaburra.minecraft.mcpvp.map.ActiveMapSet;
import kookaburra.minecraft.mcpvp.map.ActiveMapVoteSet;
import kookaburra.minecraft.mcpvp.map.MapManager;
import kookaburra.minecraft.sabotage.commands.KarmaCommand;
import kookaburra.minecraft.sabotage.commands.SabotageCommandHandler;
import kookaburra.minecraft.sabotage.listeners.Events;
import kookaburra.minecraft.sabotage.listeners.Packets;
import kookaburra.util.io.IO;
import kookaburra.util.io.Json;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.plugin.PluginDescriptionFile;

import com.google.common.io.Files;

/**
 * Sabotage, a server about a saboteur, innocent players and the detective. The
 * saboteurs must try to kill all innocents and detectives to win the game The
 * innocents and the detective need to try and find out who of them is a
 * saboteur, but nobody can be trusted.
 * 
 * @author ThaRedstoner
 */
public class kSabotage extends kShared
{
	public static int borderRadius;
	public static ActiveMapSet maps;
	public static ActiveMapVoteSet mapsVote;

	@Override
	public void onLoad()
	{
		super.onLoad();
		// This is temporary, I'll be updating all the Commands soon.
		// When this is done, this will be moved into it's own method.
		new KarmaCommand("karma", new String[]{"mykarma", "k"}).registerCommand();
		try
		{
			maps = MapManager.downloadAllMaps(ServerType.Sabotage);
			mapsVote = new ActiveMapVoteSet(ServerType.Sabotage);
			IO.save(maps, "maps.json", Json.prettySerializer());
		}
		catch (Exception e)
		{
			Bukkit.getLogger().severe(e.toString());
			try
			{
				maps = IO.load("maps", Json.deserializer(ActiveMapSet.class));
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
				System.exit(-1);
			}
		}
		IO.deleteDirectory(new File("world"));
		IO.deleteDirectory(new File("session"));
		try
		{
			ActiveMap lobby = maps.getLobbies().values().iterator().next();
			IO.unzipMap(MapManager.getZipFile(lobby), new File("world"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		Game.voteControl = new VoteControl(maps);
	}

	@Override
	public void onEnable()
	{
		super.onEnable();
		McpvpPlugin.getInstance();
		// EXAMPLE: Custom code, here we just output some info so we can check
		// all is well
		PluginDescriptionFile pdfFile = this.getDescription();
		borderRadius = 100;
		Game.Initialize();
		System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}

	public static void copyFolder(File src, File dest) throws IOException
	{
		if (src.isDirectory())
		{
			// if directory not exists, create it
			if (!dest.exists())
			{
				dest.mkdir();
			}
			// list all the directory contents
			String files[] = src.list();
			for (String file : files)
			{
				// construct the src and dest file structure
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				// recursive copy
				copyFolder(srcFile, destFile);
			}
		}
		else
		{
			Files.copy(src, dest);
		}
	}

	@Override
	public void onDisable()
	{
		super.onDisable();
		for (World world : Bukkit.getWorlds())
		{
			Bukkit.unloadWorld(world, false);
		}
	}

	@Override
	protected void InitializeSettings()
	{
		Settings.AllowKick = false;
		Settings.AllowReservedSlot = true;
		Settings.AllowServerLock = false;
		Settings.BorderRadius = 0;
		Settings.ColorChatMessages = true;
		Settings.DisallowZombeMods = true;
		Settings.EnableColoredChat = true;
		Settings.EnableEvents = true;
		Settings.EnableInstantSoup = true;
		Settings.EnableSpawnProtection = false;
		Settings.EnableSpongeLauncher = true;
		Settings.ForceDefaultGameMode = true;
		Settings.UseBorder = false;
		Settings.DeathBanDuration = 0;
		Settings.SpawnSize = 0;
		Settings.Spawn = Bukkit.getWorld("world").getSpawnLocation().toVector();
		Settings.ShowLagAsMotd = false;
		Settings.IsPersistentWorld = false;
		Settings.DataInMemory = true;
		Settings.Type = ServerType.Sabotage;
		Settings.WelcomeMessage.add(ChatColor.RED + "Welcome to Sabotage!");
		Settings.WelcomeMessage.add(ChatColor.RED + "Read the book for playing instructions!");
		Settings.WelcomeMessage.add(ChatColor.RED + "Last group standing, innocents or saboteurs, wins!");
		McpvpPlugin.getInstance().setServerDomain("mc-sabotage.com", ServerType.Sabotage);
		McpvpPlugin.getInstance().setDefaultGameMode(GameMode.ADVENTURE);
	}

	@Override
	protected void CreateCommandHandler()
	{
		new SabotageCommandHandler(this);
	}

	@Override
	protected void LoadEventListeners()
	{
		new Events(Game.voteControl);
		new Packets();
		new RepairableMapComponent(this).onEnable();
		// new DeadBodyComponent(this).onEnable();
		super.LoadEventListeners();
	}
}
