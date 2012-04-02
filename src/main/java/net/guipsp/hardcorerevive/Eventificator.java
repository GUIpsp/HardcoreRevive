package net.guipsp.hardcorerevive;

import org.bukkit.ChatColor;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;

import com.avaje.ebean.EbeanServer;

public class Eventificator implements Listener {
	private EbeanServer database;

	public Eventificator(EbeanServer db) {
		this.database = db;
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
		if (block.isEmpty()) {
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
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		onPlayerRespawnAndJoin(event.getPlayer());
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (isDead(event.getPlayer().getName())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
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
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (isDead(event.getPlayer().getName())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
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
	public void onSignChange(SignChangeEvent event) {
		if (event.getLine(0).equalsIgnoreCase("revive")) {
			if (database.find(Databaseficator.class).where()
					.ieq("playerName", event.getLine(1)).ieq("status", "dead")
					.findUnique() == null) {
				event.getPlayer().sendMessage(
						ChatColor.RED + "Player not found or alive");
				return;
			}

		}
	}

	public void onPlayerRespawnAndJoin(Player player) {
		if (!isDead(player.getName())) {
			return;
		}
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
}
