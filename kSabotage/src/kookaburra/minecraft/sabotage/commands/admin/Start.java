
package kookaburra.minecraft.sabotage.commands.admin;

import kookaburra.minecraft.commands.admin.AdminBase;
import kookaburra.minecraft.player.SharedPlayer;
import kookaburra.minecraft.sabotage.Game;

public class Start extends AdminBase
{
	public Start()
	{
		super("start");
	}

	@Override
	public boolean ExecuteAdminCommand(SharedPlayer player, String[] args)
	{
		if (Game.hasStarted)
		{
			player.sendMessage("Tournament has already started!");
			return true;
		}
		if (Game.StartTimer == null)
			Game.createCountdown();
		if (args.length == 0)
			Game.StartTimer.setTimeLeft(0);
		else
		{
			// If you do /start 10s it starts in 10 seconds.
			if (args[0].endsWith("m"))
				Game.StartTimer.setTimeLeft(Integer.parseInt(args[0].replace("m", "")) * 60);
			else if (args[0].endsWith("s"))
				Game.StartTimer.setTimeLeft(Integer.parseInt(args[0].replace("s", "")));
			else
			{
				player.sendMessage("Wrong syntax, use /start (time)m|s - m = minutes, s = seconds.");
				player.sendMessage("ie. /start 10s will start in 10 seconds.");
			}
		}
		return true;
	}
}
