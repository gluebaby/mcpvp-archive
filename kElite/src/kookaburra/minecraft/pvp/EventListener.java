
package kookaburra.minecraft.pvp;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import kookaburra.minecraft.kPlayer;
import kookaburra.minecraft.kShared;
import kookaburra.minecraft.mcpvp.login.handshake.HandshakeEvent;
import kookaburra.minecraft.player.PlayerDamage;
import kookaburra.minecraft.player.PlayerManager;
import kookaburra.minecraft.player.SharedPlayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class EventListener implements Listener
{
	private JavaPlugin _plugin;

	public EventListener(JavaPlugin plugin)
	{
		_plugin = plugin;
		_plugin.getServer().getPluginManager().registerEvents(this, _plugin);
	}

	@EventHandler
	public void noMakingPotions(BrewEvent event)
	{
		event.setCancelled(true);
		// event.getBlock().getWorld().createExplosion(event.getBlock().getLocation(),
		// 2f);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onlyVIP(HandshakeEvent event)
	{
		kPlayer player = kPlayer.Get(event.getPlayer());
		if (!player.IsVIP())
		{
			event.setKick("You must be VIP to play here. " + ChatColor.AQUA + "www.mcpvp.com/order");
			return;
		}
		if (Bukkit.getOnlinePlayers().length >= Bukkit.getMaxPlayers() && !player.IsMVP())
		{
			event.setKick("FULL! Buy MVP to get in now. " + ChatColor.AQUA + "www.mcpvp.com/order");
			return;
		}
	}

	private static final Set<Material> NO_ENCHANT = new HashSet<>(Arrays.asList(//
	Material.DIAMOND_HELMET,//
			Material.DIAMOND_CHESTPLATE,//
			Material.DIAMOND_LEGGINGS,//
			Material.DIAMOND_BOOTS,//
			Material.IRON_HELMET,//
			Material.IRON_CHESTPLATE,//
			Material.IRON_LEGGINGS,//
			Material.IRON_BOOTS,//
			Material.CHAINMAIL_HELMET,//
			Material.CHAINMAIL_CHESTPLATE,//
			Material.CHAINMAIL_LEGGINGS,//
			Material.CHAINMAIL_BOOTS,//
			Material.GOLD_HELMET,//
			Material.GOLD_CHESTPLATE,//
			Material.GOLD_LEGGINGS,//
			Material.GOLD_BOOTS,//
			Material.LEATHER_HELMET,//
			Material.LEATHER_CHESTPLATE,//
			Material.LEATHER_LEGGINGS,//
			Material.LEATHER_BOOTS,//
			Material.DIAMOND_SWORD,//
			Material.IRON_SWORD,//
			Material.GOLD_SWORD,//
			Material.STONE_SWORD,//
			Material.WOOD_SWORD));

	@EventHandler(ignoreCancelled = true)
	public void onEnchantPrepare(PrepareItemEnchantEvent event)
	{
		if (NO_ENCHANT.contains(event.getItem().getType()))
			event.setCancelled(true);
	}

	// Economy is currently disabled... @EventHandler
	public void dropBalanceOnDeath(PlayerDeathEvent event)
	{
		SharedPlayer player = PlayerManager.GetPlayer(event.getEntity());
		int balance = (int) player.GetBalance();
		player.SetBalance(0);
		int ingots = balance % 9;
		int blocks = balance / 9;
		while (blocks > 64)
		{
			event.getDrops().add(new ItemStack(Material.GOLD_BLOCK, 64));
			blocks -= 64;
		}
		if (ingots > 0)
			event.getDrops().add(new ItemStack(Material.GOLD_BLOCK, blocks));
		if (blocks > 0)
			event.getDrops().add(new ItemStack(Material.GOLD_INGOT, ingots));
	}

	@EventHandler
	public void noRaining(WeatherChangeEvent event)
	{
		if (event.toWeatherState())
			event.setCancelled(true);
	}

	@EventHandler
	public void limitMooshroomBreeding(CreatureSpawnEvent event)
	{
		if (event.getSpawnReason() == SpawnReason.BREEDING && event.getEntityType() == EntityType.MUSHROOM_COW)
		{
			event.setCancelled(true);
			int nearByCount = 0;
			List<Entity> entities = event.getEntity().getNearbyEntities(32, 32, 32);
			for (Entity entity : entities)
			{
				if (entity.getType() == EntityType.MUSHROOM_COW)
				{
					nearByCount++;
				}
			}
			if (nearByCount > 5)
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void noVillagerTrading(InventoryOpenEvent event)
	{
		if (event.getInventory().getType() == InventoryType.MERCHANT)
			event.setCancelled(true);
	}

	@EventHandler
	public void noEnchantmentBooks(InventoryClickEvent event)
	{
		if (event.getInventory().getType() == InventoryType.ANVIL)
		{
			if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.ENCHANTED_BOOK)
				event.getInventory().remove(event.getCurrentItem());
			if (event.getCursor() != null && event.getCursor().getType() == Material.ENCHANTED_BOOK)
				event.getInventory().remove(event.getCursor());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void newArmorSystem(EntityDamageEvent event)
	{
		if (event.getDamage() == 0 || event.isCancelled())
			return;
		if (event.getEntityType() == EntityType.PLAYER)
		{
			Player player = (Player) event.getEntity();
			if (player.isDead() || player.getHealth() <= 0)
				return;
			SharedPlayer sp = PlayerManager.GetPlayer(player);
			if (kShared.getInstance().hasSpawnProtection(sp))
			{
				event.setCancelled(true);
				return;
			}
			if (sp.LastDamage != null && System.currentTimeMillis() - sp.LastDamage.When < 150)
			{
				return;
			}
			sp.LastDamage = new PlayerDamage(event);
			double dmg = event.getDamage();
			double armorPoints = 0;
			PlayerInventory i = player.getInventory();
			ItemStack helmet = i.getHelmet();
			ItemStack chest = i.getChestplate();
			ItemStack legs = i.getLeggings();
			ItemStack boots = i.getBoots();
			if (helmet != null)
			{
				armorPoints += 0.15 * ((helmet.getType().getMaxDurability() - helmet.getDurability()) / (double) helmet.getType().getMaxDurability());
			}
			if (chest != null)
			{
				armorPoints += 0.40 * ((chest.getType().getMaxDurability() - chest.getDurability()) / (double) chest.getType().getMaxDurability());
			}
			if (legs != null)
			{
				armorPoints += 0.30 * ((legs.getType().getMaxDurability() - legs.getDurability()) / (double) legs.getType().getMaxDurability());
			}
			if (boots != null)
			{
				armorPoints += 0.15 * ((boots.getType().getMaxDurability() - boots.getDurability()) / (double) boots.getType().getMaxDurability());
			}
			int armor = (int) (dmg * (0.7 * armorPoints));
			double health = dmg - armor;
			health = player.getHealth() - health;
			if (health < 0)
				health = 0;
			player.setHealth(health);
			while (armor > 0)
			{
				if (chest != null)
				{
					chest.setDurability((short) (chest.getDurability() + 1));
					if (--armor == 0)
						break;
				}
				if (legs != null)
				{
					legs.setDurability((short) (legs.getDurability() + 1));
					if (--armor == 0)
						break;
				}
				if (helmet != null)
				{
					helmet.setDurability((short) (helmet.getDurability() + 1));
					if (--armor == 0)
						break;
				}
				if (boots != null)
				{
					boots.setDurability((short) (boots.getDurability() + 1));
					if (--armor == 0)
						break;
				}
			}
			event.setCancelled(true);
			player.playEffect(EntityEffect.HURT);
			player.getWorld().playSound(player.getLocation(), Sound.HURT_FLESH, 1, 1);
			if (event instanceof EntityDamageByEntityEvent)
			{
				Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
				player.damage(0, damager);
				Location source = damager.getLocation().clone();
				Vector direction = player.getLocation().toVector().subtract(source.toVector());
				double multiply = 0.25 + (dmg - 1) / 12.0;
				if (damager instanceof Player)
				{
					if (((Player) damager).isSprinting())
						multiply *= 2.0;
				}
				if (player.isSneaking())
					multiply /= 2.0;
				player.setVelocity(direction.normalize().multiply(multiply));
			}
			else
			{
				player.damage(0);
			}
		}
	}

	/*
	 * This code from here on is used to fix *EXISTING* enchantment books and
	 * enchanted armor, if they came into existance somehow
	 */
	@EventHandler
	public void removeEnchantmentBooks(InventoryOpenEvent event)
	{
		HashSet<Material> allowedTypes = new HashSet<Material>();
		allowedTypes.add(Material.BOW);
		allowedTypes.add(Material.WOOD_PICKAXE);
		allowedTypes.add(Material.STONE_PICKAXE);
		allowedTypes.add(Material.IRON_PICKAXE);
		allowedTypes.add(Material.GOLD_PICKAXE);
		allowedTypes.add(Material.DIAMOND_PICKAXE);
		ListIterator<ItemStack> it = event.getInventory().iterator();
		while (it.hasNext())
		{
			ItemStack item = it.next();
			if (item == null)
				continue;
			if (allowedTypes.contains(item.getType()))
				continue;
			if (item.getType() == Material.ENCHANTED_BOOK)
				event.getInventory().remove(item);
			if (!item.getEnchantments().isEmpty())
			{
				for (Enchantment ench : item.getEnchantments().keySet())
				{
					item.removeEnchantment(ench);
				}
				ItemMeta meta = item.getItemMeta();
				String[] message = new String[]{ChatColor.RESET + "Fake IV"};
				meta.setLore(Arrays.asList(message));
				item.setItemMeta(meta);
				((Player) event.getPlayer()).updateInventory();
				((Player) event.getPlayer()).sendMessage(new String[]{ChatColor.RED + "Elite is harder than your average server.", ChatColor.RED + "No armor or sword enchants.", ChatColor.RED + "If you'd like to enchant your weapons and gear,", ChatColor.RED + "please play on Main or Badger",});
			}
		}
	}

	@EventHandler
	public void removeEnchantmentBooksClick(InventoryClickEvent event)
	{
		HashSet<Material> allowedTypes = new HashSet<Material>();
		allowedTypes.add(Material.BOW);
		allowedTypes.add(Material.WOOD_PICKAXE);
		allowedTypes.add(Material.STONE_PICKAXE);
		allowedTypes.add(Material.IRON_PICKAXE);
		allowedTypes.add(Material.GOLD_PICKAXE);
		allowedTypes.add(Material.DIAMOND_PICKAXE);
		// allowedTypes.add(Material.);
		if (event.getCurrentItem() != null)
		{
			if (!allowedTypes.contains(event.getCurrentItem().getType()))
			{
				if (event.getCurrentItem().getType().equals(Material.ENCHANTED_BOOK))
				{
					event.getInventory().remove(event.getCurrentItem());
					((Player) event.getWhoClicked()).updateInventory();
				}
				if (!event.getCurrentItem().getEnchantments().isEmpty())
				{
					for (Enchantment ench : event.getCurrentItem().getEnchantments().keySet())
					{
						event.getCurrentItem().removeEnchantment(ench);
					}
					ItemMeta meta = event.getCurrentItem().getItemMeta();
					String[] message = new String[]{ChatColor.RESET + "Fake IV"};
					meta.setLore(Arrays.asList(message));
					event.getCurrentItem().setItemMeta(meta);
					((Player) event.getWhoClicked()).updateInventory();
					((Player) event.getWhoClicked()).sendMessage(new String[]{ChatColor.RED + "Elite is harder than your average server.", ChatColor.RED + "No armor or sword enchants.", ChatColor.RED + "If you'd like to enchant your weapons and gear,", ChatColor.RED + "please play on Main or Badger",});
				}
			}
		}
	}
	/* End code */
}
