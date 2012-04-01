package net.guipsp.hardcorerevive;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import com.avaje.ebean.*;

public class Commandificator implements CommandExecutor {

	private EbeanServer database;

	public Commandificator(EbeanServer db) {
		this.database = db;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender.getName().equalsIgnoreCase("console")) {
			sender.sendMessage("because i'm lazy this can only be ran ingame");
			return false;
		}
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
			player.setHealth(0);
			database.save(query);
		}

		if (label.equalsIgnoreCase("rdebug")) {
			Databaseficator query = database.find(Databaseficator.class)
					.where().ieq("playerName", args[0]).findUnique();
			if (query == null) {
				query = new Databaseficator();
				query.setPlayerName(args[0]);
				query.setStatus("derp");
			}
			database.save(query);
			System.out.println(database.find(Databaseficator.class)
					.findRowCount());
		}
		return false;
	}
}
