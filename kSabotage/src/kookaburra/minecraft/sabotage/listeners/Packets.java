
package kookaburra.minecraft.sabotage.listeners;

import java.lang.reflect.Field;

import kookaburra.minecraft.kShared;
import kookaburra.minecraft.util.event.custom.PacketSendEvent;
import kookaburra.util.ReflectionUtil;
import net.minecraft.server.v1_7_R1.Packet;
import net.minecraft.server.v1_7_R1.PacketPlayOutNamedSoundEffect;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Packets implements Listener
{
	private static final Field SOUND = ReflectionUtil.getField("a", PacketPlayOutNamedSoundEffect.class);

	public Packets()
	{
		Bukkit.getPluginManager().registerEvents(this, kShared.getInstance());
	}

	@EventHandler
	public void onPacketPlayOut(PacketSendEvent event)
	{
		final Packet packet = event.getPacket();
		if (packet instanceof PacketPlayOutNamedSoundEffect && SOUND != null)
		{
			try
			{
				final String NAME = (String) SOUND.get(packet);
				if (NAME.startsWith("mob.villager"))
				{
					event.setCancelled(true);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
