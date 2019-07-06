
package kookaburra.minecraft.sabotage.combatlog;

import java.util.ArrayList;
import java.util.List;

import kookaburra.minecraft.kPlayer;
import kookaburra.minecraft.kShared;
import kookaburra.minecraft.plugins.hax.movement.PlayerDamage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

public class CombatLogHandler
{
	public static List<String> combatLogKilled = new ArrayList<String>();
	public static List<String> combatLogDamaged = new ArrayList<String>();

	@SuppressWarnings("deprecation")
	public static void Fire(PlayerQuitEvent event)
	{
		final Player player = event.getPlayer();
		kPlayer kp = kPlayer.Get(player);
		double currentFall = 0;
		double extraDamage = 0;
		double fallDamage = 0;
		fallDamage = currentFall;
		for (PlayerDamage damage : kp.damage)
		{
			if (damage == null || damage.Tick == 0 || damage.Amount == 0)
				continue;
			if (damage.Player.CurrentTick().Tick - damage.PlayerTick.Tick > (10 * 20))
				continue;
			if (damage.Player.CurrentTick().Tick - damage.PlayerTick.Tick < (10 * 20) && damage.Amount != 0)
			{
				extraDamage += damage.Amount;
			}
		}
		if (fallDamage + extraDamage > 0)
		{
			combatLogDamaged.add(player.getName());
			player.damage((int) ((fallDamage + extraDamage) * 2));
			Bukkit.getScheduler().scheduleAsyncDelayedTask(kShared.getInstance(), new Runnable()
			{
				@Override
				public void run()
				{
					combatLogDamaged.remove(player.getName());
				}
			}, 10L);
		}
	}

	@SuppressWarnings("deprecation")
	public static boolean gotKilledByCombatLog(final PlayerLoginEvent event)
	{
		if (event.getPlayer().getName() != null)
		{
			if (combatLogKilled.contains(event.getPlayer().getName()) && !event.getPlayer().isOp())
			{
				event.disallow(Result.KICK_OTHER, ChatColor.AQUA + "You lost! " + ChatColor.RESET + "We implemented a new anti-combat log system, don't combat log!");
				combatLogKilled.remove(event.getPlayer());
				return true;
			}
			else if (combatLogKilled.contains(event.getPlayer().getName()) && event.getPlayer().isOp())
			{
				event.allow();
				Bukkit.getScheduler().scheduleAsyncDelayedTask(kShared.getInstance(), new Runnable()
				{
					@Override
					public void run()
					{
						event.getPlayer().sendMessage(ChatColor.RED + "You got killed by the anti-combat log.");
						event.getPlayer().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Do NOT continue to play.");
					}
				}, 10L);
				combatLogKilled.remove(event.getPlayer());
				return false;
			}
			else
			{
				event.allow();
				return false;
			}
		}
		return false;
	}
}
