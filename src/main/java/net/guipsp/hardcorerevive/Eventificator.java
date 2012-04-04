package net.guipsp.hardcorerevive;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.potion.*;

import com.avaje.ebean.EbeanServer;

public class Eventificator implements Listener {
	private EbeanServer database;

	public Eventificator(EbeanServer db) {
		this.database = db;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (isDead(event.getPlayer().getName())) {
			event.setCancelled(true);
		}
		if (!(event.getBlock().getType() == Material.SIGN_POST
				|| event.getBlock().getType() == Material.IRON_BLOCK || event
				.getBlock().getType() == Material.IRON_BLOCK)) {
			return;
		}
		if (event.getBlock().getType() == Material.SIGN_POST) {
			Sign sign = (Sign) event.getBlock().getState();
			if (sign.getLine(0).equalsIgnoreCase("revive")) {
				Databaseficator query = database.find(Databaseficator.class)
						.where().ieq("playerName", sign.getLine(1))
						.findUnique();
				int[] coords = new int[3];
				coords[0] = event.getBlock().getX();
				coords[1] = event.getBlock().getY();
				coords[2] = event.getBlock().getZ();
				query.removeSingleCoords(coords);
			}
		} else {

			Location loc = new Location(event.getBlock().getWorld(), event
					.getBlock().getX(), event.getBlock().getY() + 1, event
					.getBlock().getZ());
			if (loc.getBlock().getType() == Material.SIGN_POST) {
				Sign sign = (Sign) event.getBlock().getWorld().getBlockAt(loc);
				if (sign.getLine(0).equalsIgnoreCase("revive")) {
					Databaseficator query = database
							.find(Databaseficator.class).where()
							.ieq("playerName", sign.getLine(1)).findUnique();
					int[] coords = new int[3];
					coords[0] = loc.getBlockX();
					coords[1] = loc.getBlockY();
					coords[2] = loc.getBlockZ();
					query.removeSingleCoords(coords);
				}
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (isDead(event.getPlayer().getName())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamageByBlock(EntityDamageByBlockEvent event) {
		if (event.getEntity() instanceof Player) {
			if (isDead(((Player) event.getEntity()).getName())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			if (isDead(((Player) event.getDamager()).getName())) {
				event.setCancelled(true);
			}
		} else if (event.getEntity() instanceof Player) {
			if (isDead(((Player) event.getEntity()).getName())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityTarget(EntityTargetEvent event) {
		if (event.getTarget() instanceof Player) {
			if (isDead(((Player) event.getTarget()).getName())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		Databaseficator query = database.find(Databaseficator.class).where()
				.ieq("playerName", player.getName()).findUnique();
		if (query == null) {
			query = new Databaseficator();
			query.setPlayerName(player.getName());
		}
		query.setStatus("dead");
		database.save(query);
		Block block = player.getWorld().getBlockAt(player.getLocation());
		if (!block.isEmpty()
				&& !(block.getType().getId() == Material.SNOW.getId())) {
			return;
		}
		block.setTypeId(63);
		Sign s = (Sign) block.getState();
		s.setLine(0, "R.I.P");
		s.setLine(1, "");
		s.setLine(2, "Here Lies");
		s.setLine(3, player.getName());
		s.update();
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (isDead(event.getPlayer().getName())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (isDead(event.getPlayer().getName())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if (isDead(event.getPlayer().getName())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Databaseficator query = database.find(Databaseficator.class).where()
				.ieq("playerName", event.getPlayer().getName()).findUnique();
		if (query == null) {
			query = new Databaseficator();
			query.setPlayerName(event.getPlayer().getName());
			query.setStatus("alive");
		}
		database.save(query);
		onPlayerRespawnAndJoin(event.getPlayer());
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (isDead(event.getPlayer().getName())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		onPlayerRespawnAndJoin(event.getPlayer());
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		System.out.println(event.getLine(0));
		if (event.getLine(0).equalsIgnoreCase("revive")) {
			Block blockunder = (event.getBlock().getWorld().getBlockAt(event
					.getBlock().getX(), event.getBlock().getY() - 1, event
					.getBlock().getZ()));
			System.out
					.println((blockunder.getType() != Material.GOLD_BLOCK && blockunder
							.getType() != Material.IRON_BLOCK));
			if (blockunder.getType() != Material.GOLD_BLOCK
					&& blockunder.getType() != Material.IRON_BLOCK) {
				return;
			}
			if (database.find(Databaseficator.class).where()
					.ieq("playerName", event.getLine(1)).findUnique() == null) {
				event.getPlayer().sendMessage(
						ChatColor.RED + "Player not found");
				return;

			}
			int[] loc = new int[3];
			loc[0] = event.getBlock().getX();
			loc[1] = event.getBlock().getY();
			loc[2] = event.getBlock().getZ();

			Databaseficator dbficator = database.find(Databaseficator.class)
					.where().ieq("playerName", event.getLine(1)).findUnique();
			dbficator.setSingleCoords(loc);
			dbficator.setReviver(event.getPlayer().getName());
			database.save(dbficator);
			if (database.find(Databaseficator.class).where()
					.ieq("playerName", event.getLine(1)).ieq("status", "dead")
					.findUnique() == null) {

				event.getPlayer().sendMessage(
						ChatColor.RED + "Added pre-rez block!");
			} else {
				event.getPlayer().sendMessage(
						ChatColor.RED + "Player has been revived!");
			}
			revivePlayer(event.getLine(1));

		}
	}

	public void onPlayerRespawnAndJoin(Player player) {
		if (!isDead(player.getName())) {
			return;
		}
		if (revivePlayer(player.getName()))
			return;
		player.sendMessage(ChatColor.RED
				+ "You are dead, you must be revived by a friend!");
	}

	public boolean isDead(String player) {
		Databaseficator query = database.find(Databaseficator.class).where()
				.ieq("playerName", player).ieq("status", "dead").findUnique();
		if (query == null) {
			return false;
		}
		return true;
	}

	public boolean revivePlayer(String playername) {
		Databaseficator query = database.find(Databaseficator.class).where()
				.ieq("playerName", playername).findUnique();
		OfflinePlayer player = Bukkit.getOfflinePlayer(playername);
		if (!player.isOnline()) {
			query.setStatus("revive");
		} else if (query.getCoords().isEmpty()) {
			query.setStatus("dead");
		} else {
			int[] pos = query.getSingleCoords();
			query.setStatus("alive");
			Location location = new Location(player.getPlayer().getWorld(),
					pos[0], pos[1], pos[2]);
			Location location2 = new Location(player.getPlayer().getWorld(),
					pos[0], pos[1] - 1, pos[2]);
			Material material;
			boolean golden = false;
			if (player.getPlayer().getWorld().getBlockAt(location2).getType() == Material.GOLD_BLOCK) {
				golden = true;
			}
			if (golden) {
				material = Material.GOLD_BLOCK;
				player.getPlayer().addPotionEffect(
						new PotionEffect(PotionEffectType.CONFUSION, 60, 1));
			} else {
				material = Material.IRON_BLOCK;
			}
			if (player.getPlayer().getWorld().getBlockAt(location).getType() != Material.SIGN_POST) {
				return false;
			}
			if (player.getPlayer().getWorld().getBlockAt(location2).getType() != material) {
				return false;
			}
			player.getPlayer().teleport(location);
			player.getPlayer().getWorld().getBlockAt(location)
					.setType(Material.AIR);
			player.getPlayer().getWorld().getBlockAt(location2)
					.setType(Material.AIR);
			player.getPlayer().getWorld().strikeLightning(location2);
			player.getPlayer().getWorld().getBlockAt(location2)
					.setType(Material.AIR);
			return true;
		}
		return false;
	}
}
