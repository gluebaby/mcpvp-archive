/*
 * package kookaburra.minecraft.sabotage;
 * 
 * import java.util.HashMap;
 * 
 * import kookaburra.minecraft.PluginComponent;
 * import kookaburra.minecraft.plugins.hax.forcefield.ForcefieldTest;
 * import kookaburra.minecraft.util.FukkitUtil;
 * import kookaburra.minecraft.util.event.custom.PacketSendEvent;
 * import net.minecraft.server.v1_7_R1.EntityVillager;
 * import net.minecraft.server.v1_6_R3.PacketPlayOutEntityLocationAction;
 * import net.minecraft.server.v1_6_R3.PacketPlayOutNamedEntitySpawn;
 * import net.minecraft.server.v1_6_R3.PacketPlayOutSpawnEntityLiving;
 * import net.minecraft.server.v1_7_R1.WorldServer;
 * 
 * import org.bukkit.Bukkit;
 * import org.bukkit.Location;
 * import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
 * import org.bukkit.craftbukkit.v1_7_R1.entity.CraftEntity;
 * import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
 * import org.bukkit.entity.LivingEntity;
 * import org.bukkit.entity.Player;
 * import org.bukkit.entity.Villager;
 * import org.bukkit.event.EventHandler;
 * import org.bukkit.event.Listener;
 * import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
 * import org.bukkit.plugin.Plugin;
 * 
 * public class DeadBodyComponent extends PluginComponent implements Listener
 * {
 * public static HashMap<Integer, DeadBody> bodies = new HashMap<Integer,
 * DeadBody>();
 * 
 * public DeadBodyComponent(Plugin plugin)
 * {
 * super(plugin);
 * }
 * 
 * @Override
 * public void onEnable()
 * {
 * Bukkit.getPluginManager().registerEvents(this, this.getPlugin());
 * }
 * 
 * public static void spawnBody(Player player)
 * {
 * ForcefieldTest.ForceVillagerSpawn = true;
 * 
 * final DeadBody body = new DeadBody(player.getLocation(), player);
 * 
 * for(DeadBody otherBody : bodies.values())
 * {
 * if(otherBody.deadPlayer.equals(body.deadPlayer))
 * return;
 * }
 * 
 * body.spawn();
 * 
 * ForcefieldTest.ForceVillagerSpawn = false;
 * }
 * 
 * @EventHandler
 * public void onPacketPlayOutSend(PacketSendEvent event)
 * {
 * if(event.getPacket() instanceof PacketPlayOutSpawnEntityLiving)
 * {
 * PacketPlayOutSpawnEntityLiving spawn = (PacketPlayOutSpawnEntityLiving)
 * event.getPacket();
 * 
 * if(bodies.containsKey(spawn.a))
 * {
 * DeadBody body = bodies.get(spawn.a);
 * 
 * PacketPlayOutNamedEntitySpawn namedSpawn = new
 * PacketPlayOutNamedEntitySpawn
 * (((CraftPlayer)body.deadPlayer).getHandle());
 * 
 * namedSpawn.a = body.villager.getEntityId();
 * namedSpawn.b = body.deadPlayer.getName();
 * namedSpawn.c = (int) (body.villager.getLocation().getX() * 32);
 * namedSpawn.d = (int) (body.villager.getLocation().getY() * 32);
 * namedSpawn.e = (int) (body.villager.getLocation().getZ() * 32);
 * namedSpawn.f = 0;
 * namedSpawn.g = 0;
 * namedSpawn.h = 0;
 * 
 * PacketPlayOutEntityLocationAction deadPosition = new
 * PacketEntityLocationAction
 * (((CraftEntity)body.villager).getHandle(), 0,
 * body.villager.getLocation().getBlockX(),
 * body.villager.getLocation().getBlockY(),
 * body.villager.getLocation().getBlockZ());
 * 
 * // deadPosition.a = body.villager.getEntityId();
 * // deadPosition.b = 0;
 * // deadPosition.c = (int) (body.villager.getLocation().getX());
 * // deadPosition.d = (int) (body.villager.getLocation().getY());
 * // deadPosition.e = (int) (body.villager.getLocation().getZ());
 * 
 * event.setCancelled(true);
 * 
 * ((CraftPlayer)event.getPlayer()).getHandle().playerConnection.sendPacket
 * (namedSpawn);
 * ((CraftPlayer)event.getPlayer()).getHandle().playerConnection.sendPacket
 * (deadPosition);
 * }
 * }
 * }
 * 
 * public static class DeadBody
 * {
 * public Location location;
 * public Player deadPlayer;
 * public DeadBodyInfo info;
 * 
 * public Villager villager;
 * 
 * public DeadBody(Location loc, Player dead)
 * {
 * this.location = loc;
 * this.deadPlayer = dead;
 * }
 * 
 * public void spawn()
 * {
 * WorldServer world = ((CraftWorld)location.getWorld()).getHandle();
 * 
 * EntityVillager ev = new EntityVillager(world, 0);
 * 
 * ev.locX = location.getX();
 * ev.locY = location.getY() + 0.4;
 * ev.locZ = location.getZ();
 * 
 * FukkitUtil.clearGoalAI(ev);
 * FukkitUtil.clearTargetAI(ev);
 * 
 * villager = (Villager) ev.getBukkitEntity();
 * 
 * bodies.put(villager.getEntityId(), this);
 * 
 * world.addEntity(ev, SpawnReason.CUSTOM);
 * 
 * ev.setHealth(1000000);
 * ((LivingEntity)ev.getBukkitEntity()).setNoDamageTicks(100);
 * 
 * villager.teleport(location);
 * 
 * villager.setNoDamageTicks(10000000); // Make them invincible.
 * 
 * info = new DeadBodyInfo(deadPlayer.getName(), Game.isInnocent(deadPlayer));
 * }
 * 
 * public void remove()
 * {
 * bodies.remove(villager.getEntityId());
 * villager.remove();
 * }
 * }
 * }
 */