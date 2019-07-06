
package kookaburra.minecraft.pvp;

import java.io.File;

import kookaburra.minecraft.Settings;
import kookaburra.minecraft.kShared;
import kookaburra.minecraft.duel.Duel;
import kookaburra.minecraft.economy.Econ;
import kookaburra.minecraft.enderbattle.Enderbattle;
import kookaburra.minecraft.items.ChestTag;
import kookaburra.minecraft.log.blocks.BlockLogReader;
import kookaburra.minecraft.log.blocks.BlockLogger;
import kookaburra.minecraft.mcpvp.McpvpPlugin;
import kookaburra.minecraft.mcpvp.ServerType;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.Vector;

public class kElite extends kShared
{
	@Override
	public void onDisable()
	{
		super.onDisable();
		System.out.println("kElite is now disabled!");
		Econ.SINGLE_STACK_ITEMS.remove(Material.MUSHROOM_SOUP);
	}

	@Override
	public void onEnable()
	{
		super.onEnable();
		PluginDescriptionFile pdfFile = getDescription();
		System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
		Econ.SINGLE_STACK_ITEMS.add(Material.MUSHROOM_SOUP);
		// ServerStopper.monitor(this, 60, false);
		ChestTag.enable(this);
		Duel.enable(this);
		Enderbattle.enable(this);
	}

	@Override
	protected void LoadEventListeners()
	{
		super.LoadEventListeners();
		new BlockLogger(this);
		new BlockLogReader(this);
	}

	@Override
	protected void InitializeSettings()
	{
		Settings.AllowKick = true;
		Settings.DeathBanDuration = 60;
		Settings.AllowReservedSlot = true;
		Settings.AllowServerLock = true;
		Settings.BorderRadius = -10000;
		Settings.DisallowZombeMods = true;
		Settings.EnableEvents = true;
		Settings.EnableSpongeLauncher = true;
		Settings.EnableSpawnProtection = true;
		Settings.ForceDefaultGameMode = true;
		Settings.SpawnSize = 64;
		Settings.EnableInstantSoup = true;
		Settings.EnableSalvaging = true;
		Settings.EnablePacifism = true;
		Settings.EnableEconomy = true;
		Settings.EnableTeams = true;
		Settings.EnableTracking = true;
		Settings.Type = ServerType.Elite;
		Settings.Spawn = new Vector(0, 0, 0);
		Settings.AutoFixSpawn = true;
		Settings.PROWarps = 25;
		Settings.MVPWarps = 20;
		Settings.VIPWarps = 10;
		Settings.RegularWarps = 5;
		Settings.PRONetherWarps = 5;
		Settings.MVPNetherWarps = 3;
		Settings.VIPNetherWarps = 2;
		Settings.RegularNetherWarps = 0;
		Settings.DisableNetherWarps = true;
		Settings.WelcomeMessage.add(ChatColor.AQUA + "Welcome to the McPVP Elite server!");
		Settings.WelcomeMessage.add(ChatColor.AQUA + "Visit " + ChatColor.BLUE + "www.mcpvp.com" + ChatColor.AQUA + " for news and information.");
		Settings.WelcomeMessage.add(ChatColor.AQUA + "Use " + ChatColor.WHITE + "/help" + ChatColor.AQUA + " for a list of commands.");
		Settings.WelcomeMessage.add(ChatColor.GREEN + "VIP" + ChatColor.AQUA + ", " + ChatColor.BLUE + "MVP" + ChatColor.AQUA + " & " + ChatColor.GOLD + "PRO" + ChatColor.AQUA + " Packages now available. See website for details.");
		Settings.WelcomeMessage.add(ChatColor.RED + "NO CHEATING! FLYING/XRAY/ETC = BAN! YOU HAVE BEEN WARNED!");
		try
		{
			File dir = new File("plugins/elite/players/");
			if (!dir.exists())
				dir.mkdirs();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		new EventListener(this);
		McpvpPlugin.getInstance().setServerDomain("elite.mcpvp.com", ServerType.Elite);
	}
}