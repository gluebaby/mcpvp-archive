
package kookaburra.minecraft.sabotage;

import org.bukkit.ChatColor;

/**
 * Contains all the messages used
 * by Sabotage. Centralised to make
 * future changes much easier.
 * 
 * @author Alexander Mackenzie
 */
public class Messages
{
	public static final String DENIED = ChatColor.RED + "You can't do this!";
	public static final String DENIED_SPECTATOR = ChatColor.RED + "Spectators can't do this!";
	public static final String SPECTATING = ChatColor.GOLD + "You are now spectating!";
	public static final String SPECTATING_SKULL = ChatColor.GRAY + "Click with the head to teleport between players!";
	public static final String SPECTATING_STARTED = ChatColor.GOLD + "The game has already started!";
	public static final String SPECTATING_ARROW = ChatColor.AQUA + "You were in the way of an arrow!";
	public static final String SPECTATING_ARROW_ADVICE = ChatColor.GRAY + "It's recommended to stay 4-5 blocks above the ground.";
	public static final String EXPLORE = ChatColor.AQUA + "You can now explore the map and get items.";

	public static String parse(String message, String... args)
	{
		for (int i = 0; i != args.length; i++)
		{
			message = message.replaceAll("{%" + i + "}", args[i]);
		}
		return message;
	}
}
