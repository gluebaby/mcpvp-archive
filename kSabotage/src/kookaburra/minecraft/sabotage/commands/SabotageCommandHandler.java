
package kookaburra.minecraft.sabotage.commands;

import kookaburra.minecraft.commands.CommandHandlerBase;
import kookaburra.minecraft.commands.Position;
import kookaburra.minecraft.commands.admin.CopyPlayerInventory;
import kookaburra.minecraft.commands.admin.Countdown;
import kookaburra.minecraft.commands.admin.DrawSpawn;
import kookaburra.minecraft.commands.admin.Enchant;
import kookaburra.minecraft.commands.admin.FixSpawn;
import kookaburra.minecraft.commands.admin.GoAs;
import kookaburra.minecraft.commands.admin.God;
import kookaburra.minecraft.commands.admin.Lightning;
import kookaburra.minecraft.commands.admin.MobSpawn;
import kookaburra.minecraft.commands.admin.Record;
import kookaburra.minecraft.commands.admin.RegenSpawn;
import kookaburra.minecraft.commands.admin.SpawnFight;
import kookaburra.minecraft.commands.admin.Storm;
import kookaburra.minecraft.commands.team.Kick;
import kookaburra.minecraft.sabotage.commands.admin.Start;

import org.bukkit.plugin.java.JavaPlugin;

public class SabotageCommandHandler extends CommandHandlerBase
{
	public SabotageCommandHandler(JavaPlugin plugin)
	{
		super(plugin);
		new Who();
		AddCommand(new Shop());
		AddCommand(new Help());
		AddCommand(new Stream());
		// Admin Commands
		AddCommand(new Saboteur());
		AddCommand(new Start());
		AddCommand(new MobSpawn());
		AddCommand(new CopyPlayerInventory());
		// kShared Commands (trimmed)
		// Admin Commands
		AddCommand(new CopyPlayerInventory());
		AddCommand(new Enchant());
		AddCommand(new Lightning());
		AddCommand(new MobSpawn());
		AddCommand(new Storm());
		AddCommand(new Countdown());
		AddCommand(new DrawSpawn());
		AddCommand(new FixSpawn());
		AddCommand(new RegenSpawn());
		AddCommand(new God());
		AddCommand(new GoAs());
		AddCommand(new Record());
		AddCommand(new SpawnFight());
		// Mod Commands
		AddCommand(new Kick());
		// General Commands
		AddCommand(new Help());
		AddCommand(new Position());
	}
}
