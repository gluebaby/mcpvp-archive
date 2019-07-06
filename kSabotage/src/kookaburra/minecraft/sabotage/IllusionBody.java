
package kookaburra.minecraft.sabotage;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Achievement;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.InventoryView.Property;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

public class IllusionBody implements Player
{
	private String name;
	private GameMode gameMode;
	private LivingEntity entity;
	private InetSocketAddress address;
	private Map<String, Object> serialized;
	private boolean isOp;
	private long firstPlayed;
	private long lastPlayed;
	private boolean hasPlayedBefore;
	private Location bedSpawnLocation;
	private Location compassTarget;
	private String displayName;
	private int level;
	private int totalExperience;
	private float saturation;
	private String playerListName;

	public IllusionBody(String n, LivingEntity e)
	{
		name = n;
		entity = e;
		gameMode = GameMode.SURVIVAL;
		isOp = false;
		displayName = name;
		level = 0;
		totalExperience = 0;
		playerListName = "";
		entity.setHealth(20);
		entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 12 * 60 * 60 * 1000, 0));
	}

	@Override
	public GameMode getGameMode()
	{
		return gameMode;
	}

	@Override
	public PlayerInventory getInventory()
	{
		return null;
	}

	@Override
	public ItemStack getItemInHand()
	{
		return null;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public int getSleepTicks()
	{
		return 0;
	}

	@Override
	public boolean isSleeping()
	{
		return false;
	}

	@Override
	public void setGameMode(GameMode arg0)
	{
	}

	@Override
	public void setItemInHand(ItemStack arg0)
	{
	}

	@Override
	public void damage(double arg0)
	{
		entity.damage(arg0);
	}

	@Override
	public void damage(double arg0, Entity arg1)
	{
		entity.damage(arg0, arg1);
	}

	@Override
	public double getEyeHeight()
	{
		return entity.getEyeHeight();
	}

	@Override
	public double getEyeHeight(boolean arg0)
	{
		return entity.getEyeHeight();
	}

	@Override
	public Location getEyeLocation()
	{
		return entity.getEyeLocation();
	}

	@Override
	public double getHealth()
	{
		return entity.getHealth();
	}

	@Override
	public Player getKiller()
	{
		return entity.getKiller();
	}

	@Override
	public double getLastDamage()
	{
		return entity.getLastDamage();
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<Block> getLastTwoTargetBlocks(HashSet<Byte> arg0, int arg1)
	{
		return entity.getLastTwoTargetBlocks(arg0, arg1);
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<Block> getLineOfSight(HashSet<Byte> arg0, int arg1)
	{
		return entity.getLineOfSight(arg0, arg1);
	}

	@Override
	public double getMaxHealth()
	{
		return entity.getMaxHealth();
	}

	@Override
	public int getMaximumAir()
	{
		return entity.getMaximumAir();
	}

	@Override
	public int getMaximumNoDamageTicks()
	{
		return entity.getMaximumNoDamageTicks();
	}

	@Override
	public int getNoDamageTicks()
	{
		return entity.getNoDamageTicks();
	}

	@Override
	public int getRemainingAir()
	{
		return entity.getRemainingAir();
	}

	@SuppressWarnings("deprecation")
	@Override
	public Block getTargetBlock(HashSet<Byte> arg0, int arg1)
	{
		return entity.getTargetBlock(arg0, arg1);
	}

	@Override
	public Entity getVehicle()
	{
		return entity.getVehicle();
	}

	@Override
	public boolean isInsideVehicle()
	{
		return entity.isInsideVehicle();
	}

	@Override
	public boolean leaveVehicle()
	{
		return entity.leaveVehicle();
	}

	@Override
	public void setHealth(double arg0)
	{
		entity.setHealth(arg0);
	}

	@Override
	public void setLastDamage(double arg0)
	{
		entity.setLastDamage(arg0);
	}

	@Override
	public void setMaximumAir(int arg0)
	{
		entity.setMaximumAir(arg0);
	}

	@Override
	public void setMaximumNoDamageTicks(int arg0)
	{
		entity.setMaximumNoDamageTicks(arg0);
	}

	@Override
	public void setNoDamageTicks(int arg0)
	{
		entity.setNoDamageTicks(arg0);
	}

	@Override
	public void setRemainingAir(int arg0)
	{
		entity.setRemainingAir(arg0);
	}

	@Override
	public boolean eject()
	{
		return entity.eject();
	}

	@Override
	public int getEntityId()
	{
		return entity.getEntityId();
	}

	@Override
	public float getFallDistance()
	{
		return entity.getFallDistance();
	}

	@Override
	public int getFireTicks()
	{
		return entity.getFireTicks();
	}

	@Override
	public EntityDamageEvent getLastDamageCause()
	{
		return entity.getLastDamageCause();
	}

	@Override
	public Location getLocation()
	{
		return entity.getLocation();
	}

	@Override
	public int getMaxFireTicks()
	{
		return entity.getMaxFireTicks();
	}

	@Override
	public List<Entity> getNearbyEntities(double arg0, double arg1, double arg2)
	{
		return entity.getNearbyEntities(arg0, arg1, arg2);
	}

	@Override
	public Entity getPassenger()
	{
		return entity.getPassenger();
	}

	@Override
	public Server getServer()
	{
		return entity.getServer();
	}

	@Override
	public int getTicksLived()
	{
		return entity.getTicksLived();
	}

	@Override
	public UUID getUniqueId()
	{
		return entity.getUniqueId();
	}

	@Override
	public Vector getVelocity()
	{
		return entity.getVelocity();
	}

	@Override
	public World getWorld()
	{
		return entity.getWorld();
	}

	@Override
	public boolean isDead()
	{
		return false;
	}

	@Override
	public boolean isEmpty()
	{
		return entity.isEmpty();
	}

	@Override
	public void remove()
	{
		entity.remove();
	}

	@Override
	public void setFallDistance(float arg0)
	{
		entity.setFallDistance(arg0);
	}

	@Override
	public void setFireTicks(int arg0)
	{
		entity.setFireTicks(arg0);
	}

	@Override
	public void setLastDamageCause(EntityDamageEvent arg0)
	{
		entity.setLastDamageCause(arg0);
	}

	@Override
	public boolean setPassenger(Entity arg0)
	{
		return entity.setPassenger(arg0);
	}

	@Override
	public void setTicksLived(int arg0)
	{
		entity.setTicksLived(arg0);
	}

	@Override
	public void setVelocity(Vector arg0)
	{
		entity.setVelocity(arg0);
	}

	@Override
	public boolean teleport(Location arg0)
	{
		return entity.teleport(arg0);
	}

	@Override
	public boolean teleport(Entity arg0)
	{
		return entity.teleport(arg0);
	}

	@Override
	public boolean teleport(Location arg0, TeleportCause arg1)
	{
		return entity.teleport(arg0, arg1);
	}

	@Override
	public boolean teleport(Entity arg0, TeleportCause arg1)
	{
		return entity.teleport(arg0, arg1);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0)
	{
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, int arg1)
	{
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2)
	{
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2, int arg3)
	{
		return null;
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions()
	{
		return null;
	}

	@Override
	public boolean hasPermission(String arg0)
	{
		return false;
	}

	@Override
	public boolean hasPermission(Permission arg0)
	{
		return false;
	}

	@Override
	public boolean isPermissionSet(String arg0)
	{
		return false;
	}

	@Override
	public boolean isPermissionSet(Permission arg0)
	{
		return false;
	}

	@Override
	public void recalculatePermissions()
	{
	}

	@Override
	public void removeAttachment(PermissionAttachment arg0)
	{
	}

	@Override
	public boolean isOp()
	{
		return isOp;
	}

	@Override
	public void setOp(boolean arg0)
	{
	}

	@Override
	public void sendMessage(String arg0)
	{
	}

	@Override
	public long getFirstPlayed()
	{
		return firstPlayed;
	}

	@Override
	public long getLastPlayed()
	{
		return lastPlayed;
	}

	@Override
	public Player getPlayer()
	{
		return this;
	}

	@Override
	public boolean hasPlayedBefore()
	{
		return hasPlayedBefore;
	}

	@Override
	public boolean isBanned()
	{
		return false;
	}

	@Override
	public boolean isOnline()
	{
		return false;
	}

	@Override
	public boolean isWhitelisted()
	{
		return false;
	}

	@Override
	public void setBanned(boolean arg0)
	{
	}

	@Override
	public void setWhitelisted(boolean arg0)
	{
	}

	@Override
	public Map<String, Object> serialize()
	{
		return serialized;
	}

	@Override
	public void awardAchievement(Achievement arg0)
	{
	}

	@Override
	public void chat(String arg0)
	{
	}

	@Override
	public InetSocketAddress getAddress()
	{
		return address;
	}

	@Override
	public Location getBedSpawnLocation()
	{
		return bedSpawnLocation;
	}

	@Override
	public Location getCompassTarget()
	{
		return compassTarget;
	}

	@Override
	public String getDisplayName()
	{
		return displayName;
	}

	@Override
	public float getExhaustion()
	{
		return 0;
	}

	@Override
	public float getExp()
	{
		return 0;
	}

	@Override
	public int getFoodLevel()
	{
		return 0;
	}

	@Override
	public int getLevel()
	{
		return level;
	}

	@Override
	public String getPlayerListName()
	{
		return playerListName;
	}

	@Override
	public long getPlayerTime()
	{
		return 0;
	}

	@Override
	public long getPlayerTimeOffset()
	{
		return 0;
	}

	@Override
	public float getSaturation()
	{
		return saturation;
	}

	@Override
	public int getTotalExperience()
	{
		return totalExperience;
	}

	@Override
	public void giveExp(int arg0)
	{
	}

	@Override
	public void incrementStatistic(Statistic arg0)
	{
	}

	@Override
	public void incrementStatistic(Statistic arg0, int arg1)
	{
	}

	@Override
	public void incrementStatistic(Statistic arg0, Material arg1)
	{
	}

	@Override
	public void incrementStatistic(Statistic arg0, Material arg1, int arg2)
	{
	}

	@Override
	public boolean isPlayerTimeRelative()
	{
		return false;
	}

	@Override
	public boolean isSleepingIgnored()
	{
		return false;
	}

	@Override
	public boolean isSneaking()
	{
		return false;
	}

	@Override
	public boolean isSprinting()
	{
		return false;
	}

	@Override
	public void kickPlayer(String arg0)
	{
	}

	@Override
	public void loadData()
	{
	}

	@Override
	public boolean performCommand(String arg0)
	{
		return false;
	}

	@Override
	public void playEffect(Location arg0, Effect arg1, int arg2)
	{
	}

	@Override
	public void playNote(Location arg0, byte arg1, byte arg2)
	{
	}

	@Override
	public void playNote(Location arg0, Instrument arg1, Note arg2)
	{
	}

	@Override
	public void resetPlayerTime()
	{
	}

	@Override
	public void saveData()
	{
	}

	@Override
	public void sendBlockChange(Location arg0, Material arg1, byte arg2)
	{
	}

	@Override
	public void sendBlockChange(Location arg0, int arg1, byte arg2)
	{
	}

	@Override
	public boolean sendChunkChange(Location arg0, int arg1, int arg2, int arg3, byte[] arg4)
	{
		return false;
	}

	@Override
	public void sendMap(MapView arg0)
	{
	}

	@Override
	public void sendRawMessage(String arg0)
	{
	}

	@Override
	public void setCompassTarget(Location arg0)
	{
	}

	@Override
	public void setDisplayName(String arg0)
	{
	}

	@Override
	public void setExhaustion(float arg0)
	{
	}

	@Override
	public void setExp(float arg0)
	{
	}

	@Override
	public void setFoodLevel(int arg0)
	{
	}

	@Override
	public void setLevel(int arg0)
	{
	}

	@Override
	public void setPlayerListName(String arg0)
	{
	}

	@Override
	public void setPlayerTime(long arg0, boolean arg1)
	{
	}

	@Override
	public void setSaturation(float arg0)
	{
	}

	@Override
	public void setSleepingIgnored(boolean arg0)
	{
	}

	@Override
	public void setSneaking(boolean arg0)
	{
	}

	@Override
	public void setSprinting(boolean arg0)
	{
	}

	@Override
	public void setTotalExperience(int arg0)
	{
	}

	@Override
	public void updateInventory()
	{
	}

	@Override
	public void playEffect(EntityEffect arg0)
	{
	}

	@Override
	public Set<String> getListeningPluginChannels()
	{
		return null;
	}

	@Override
	public void sendPluginMessage(Plugin arg0, String arg1, byte[] arg2)
	{
	}

	@Override
	public boolean getAllowFlight()
	{
		return false;
	}

	@Override
	public void setAllowFlight(boolean arg0)
	{
	}

	@Override
	public void setBedSpawnLocation(Location arg0)
	{
	}

	@Override
	public void closeInventory()
	{
	}

	@Override
	public ItemStack getItemOnCursor()
	{
		return null;
	}

	@Override
	public InventoryView getOpenInventory()
	{
		return null;
	}

	@Override
	public InventoryView openEnchanting(Location arg0, boolean arg1)
	{
		return null;
	}

	@Override
	public InventoryView openInventory(Inventory arg0)
	{
		return null;
	}

	@Override
	public void openInventory(InventoryView arg0)
	{
	}

	@Override
	public InventoryView openWorkbench(Location arg0, boolean arg1)
	{
		return null;
	}

	@Override
	public void setItemOnCursor(ItemStack arg0)
	{
	}

	@Override
	public boolean setWindowProperty(Property arg0, int arg1)
	{
		return false;
	}

	@Override
	public boolean addPotionEffect(PotionEffect arg0)
	{
		return false;
	}

	@Override
	public boolean addPotionEffect(PotionEffect arg0, boolean arg1)
	{
		return false;
	}

	@Override
	public boolean addPotionEffects(Collection<PotionEffect> arg0)
	{
		return false;
	}

	@Override
	public Collection<PotionEffect> getActivePotionEffects()
	{
		return null;
	}

	@Override
	public boolean hasPotionEffect(PotionEffectType arg0)
	{
		return false;
	}

	@Override
	public <T extends Projectile> T launchProjectile(Class<? extends T> arg0)
	{
		return null;
	}

	@Override
	public void removePotionEffect(PotionEffectType arg0)
	{
	}

	@Override
	public EntityType getType()
	{
		return null;
	}

	@Override
	public List<MetadataValue> getMetadata(String arg0)
	{
		return null;
	}

	@Override
	public boolean hasMetadata(String arg0)
	{
		return false;
	}

	@Override
	public void removeMetadata(String arg0, Plugin arg1)
	{
	}

	@Override
	public void setMetadata(String arg0, MetadataValue arg1)
	{
	}

	@Override
	public void abandonConversation(Conversation arg0)
	{
	}

	@Override
	public void acceptConversationInput(String arg0)
	{
	}

	@Override
	public boolean beginConversation(Conversation arg0)
	{
		return false;
	}

	@Override
	public boolean isConversing()
	{
		return false;
	}

	@Override
	public void sendMessage(String[] arg0)
	{
	}

	@Override
	public boolean canSee(Player arg0)
	{
		return true;
	}

	@Override
	public void hidePlayer(Player arg0)
	{
	}

	@Override
	public <T> void playEffect(Location arg0, Effect arg1, T arg2)
	{
	}

	@Override
	public void showPlayer(Player arg0)
	{
	}

	@Override
	public Arrow shootArrow()
	{
		return null;
	}

	@Override
	public Egg throwEgg()
	{
		return null;
	}

	@Override
	public Snowball throwSnowball()
	{
		return null;
	}

	@Override
	public boolean isBlocking()
	{
		return false;
	}

	@Override
	public void abandonConversation(Conversation arg0, ConversationAbandonedEvent arg1)
	{
	}

	@Override
	public boolean isFlying()
	{
		return false;
	}

	@Override
	public void setFlying(boolean arg0)
	{
	}

	@Override
	public int getExpToLevel()
	{
		return 0;
	}

	@Override
	public boolean hasLineOfSight(org.bukkit.entity.Entity arg0)
	{
		return false;
	}

	@Override
	public boolean isValid()
	{
		return false;
	}

	@Override
	public float getFlySpeed()
	{
		return 0;
	}

	@Override
	public float getWalkSpeed()
	{
		return 0;
	}

	@Override
	public void setFlySpeed(float arg0) throws IllegalArgumentException
	{
	}

	@Override
	public void setWalkSpeed(float arg0) throws IllegalArgumentException
	{
	}

	@Override
	public Inventory getEnderChest()
	{
		return null;
	}

	@Override
	public void playSound(Location arg0, Sound arg1, float arg2, float arg3)
	{
	}

	@Override
	public void setBedSpawnLocation(Location arg0, boolean arg1)
	{
	}

	@Override
	public void giveExpLevels(int arg0)
	{
	}

	@Override
	public boolean getCanPickupItems()
	{
		return false;
	}

	@Override
	public EntityEquipment getEquipment()
	{
		return null;
	}

	@Override
	public boolean getRemoveWhenFarAway()
	{
		return false;
	}

	@Override
	public void setCanPickupItems(boolean arg0)
	{
	}

	@Override
	public void setRemoveWhenFarAway(boolean arg0)
	{
	}

	@Override
	public Location getLocation(Location arg0)
	{
		return null;
	}

	@Override
	public void setTexturePack(String arg0)
	{
	}

	@Override
	public void resetMaxHealth()
	{
	}

	@Override
	public void setMaxHealth(double arg0)
	{
	}

	@Override
	public String getCustomName()
	{
		return null;
	}

	@Override
	public boolean isCustomNameVisible()
	{
		return false;
	}

	@Override
	public void setCustomName(String arg0)
	{
	}

	@Override
	public void setCustomNameVisible(boolean arg0)
	{
	}

	@Override
	public WeatherType getPlayerWeather()
	{
		return null;
	}

	@Override
	public boolean isOnGround()
	{
		return false;
	}

	@Override
	public void resetPlayerWeather()
	{
	}

	@Override
	public void setPlayerWeather(WeatherType arg0)
	{
	}

	@Override
	public Scoreboard getScoreboard()
	{
		return null;
	}

	@Override
	public void setScoreboard(Scoreboard arg0)
	{
	}

	@Override
	@Deprecated
	public int _INVALID_getLastDamage()
	{
		return entity._INVALID_getLastDamage();
	}

	@Override
	@Deprecated
	public void _INVALID_setLastDamage(int arg0)
	{
		entity._INVALID_setLastDamage(arg0);
	}

	@Override
	@Deprecated
	public void _INVALID_damage(int arg0)
	{
		entity._INVALID_damage(arg0);
	}

	@Override
	@Deprecated
	public void _INVALID_damage(int arg0, org.bukkit.entity.Entity arg1)
	{
		entity._INVALID_damage(arg0, arg1);
	}

	@Override
	@Deprecated
	public int _INVALID_getHealth()
	{
		return entity._INVALID_getHealth();
	}

	@Override
	@Deprecated
	public int _INVALID_getMaxHealth()
	{
		return entity._INVALID_getMaxHealth();
	}

	@Override
	@Deprecated
	public void _INVALID_setHealth(int arg0)
	{
		entity._INVALID_setHealth(arg0);
	}

	@Override
	@Deprecated
	public void _INVALID_setMaxHealth(int arg0)
	{
		entity._INVALID_setMaxHealth(arg0);
	}

	@Override
	public org.bukkit.entity.Entity getLeashHolder() throws IllegalStateException
	{
		return entity.getLeashHolder();
	}

	@Override
	public boolean isLeashed()
	{
		return entity.isLeashed();
	}

	@Override
	public boolean setLeashHolder(org.bukkit.entity.Entity arg0)
	{
		return entity.setLeashHolder(arg0);
	}

	@Override
	public double getHealthScale()
	{
		return 0;
	}

	@Override
	public boolean isHealthScaled()
	{
		return false;
	}

	@Override
	public void playSound(Location arg0, String arg1, float arg2, float arg3)
	{
	}

	@Override
	public void setHealthScale(double arg0) throws IllegalArgumentException
	{
	}

	@Override
	public void setHealthScaled(boolean arg0)
	{
	}

	@Override
	public void setResourcePack(String arg0) {
		// TODO Auto-generated method stub
		
	}
}
