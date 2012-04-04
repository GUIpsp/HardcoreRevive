package net.guipsp.hardcorerevive;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import com.avaje.ebean.EbeanServer;

public class Commandificator implements CommandExecutor {

	private EbeanServer database;

	public Commandificator(EbeanServer db) {
		this.database = db;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (label.equalsIgnoreCase("revive")) {

			Player player;
			if (args.length == 0) {
				player = Bukkit.getPlayer(sender.getName());
			} else {
				player = Bukkit.getPlayer(args[0]);
			}
			if (player == null) {
				sender.sendMessage(ChatColor.RED + "Player not found");
				return false;
			}
			Databaseficator query = database.find(Databaseficator.class)
					.where().ieq("playerName", player.getName()).findUnique();
			query.setStatus("alive");
			database.save(query);
			return true;
		}
		if (label.equalsIgnoreCase("rkill")) {
			Player player;
			if (args.length == 0) {
				player = Bukkit.getPlayer(sender.getName());
			} else {
				player = Bukkit.getPlayer(args[0]);
			}
			if (player == null) {
				sender.sendMessage(ChatColor.RED + "Player not found");
				return false;
			}
			Databaseficator query = database.find(Databaseficator.class)
					.where().ieq("playerName", player.getName()).findUnique();
			query.setStatus("dead");
			database.save(query);
			return true;
		}
		if (label.equalsIgnoreCase("haunt")) {
			Player player;

			try {
				player = Bukkit.getPlayer(args[0]);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + "Usage: /haunt <player>");
				return false;
			}

			Databaseficator query = database.find(Databaseficator.class)
					.where().ieq("playerName", sender.getName())
					.ieq("status", "dead").findUnique();
			if (query == null) {
				sender.sendMessage(ChatColor.RED + "You aren't dead!");
				return false;
			}
			if (player == null) {
				sender.sendMessage(ChatColor.RED + "Player not found");
				return false;
			}
		}
		return false;
	}
}
