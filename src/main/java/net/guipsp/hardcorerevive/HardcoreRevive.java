package net.guipsp.hardcorerevive;

import java.util.*;

import javax.persistence.PersistenceException;

import org.bukkit.plugin.java.JavaPlugin;

import com.avaje.ebean.EbeanServer;

public class HardcoreRevive extends JavaPlugin {
	private Eventificator Eventificator;
	private Commandificator Commandificator;
	EbeanServer database;

	@Override
	public void onDisable() {
		System.out.println("Beep Bop Boop disabling HardcoreRevive");
	}

	@Override
	public void onEnable() {
		System.out.println("Beep Bop Boop enabling HardcoreRevive");
		database = getDatabase();
		Eventificator = new Eventificator(database);
		Commandificator = new Commandificator(database);
		getServer().getPluginManager().registerEvents(Eventificator, this);
		getCommand("revive").setExecutor(Commandificator);
		getCommand("rkill").setExecutor(Commandificator);
		setupDatabase();
	}

	private void setupDatabase() {
		try {
			database.find(Databaseficator.class).findRowCount();
		} catch (PersistenceException ex) {
			// ex.printStackTrace();
			System.out
					.println("Installing database.find(Databaseficator.class) for "
							+ getDescription().getName()
							+ " due to first time use.");
			installDDL();
		}
	}

	@Override
	public List<Class<?>> getDatabaseClasses() {
		List<Class<?>> list = new ArrayList<Class<?>>();
		list.add(Databaseficator.class);
		return list;
	}
}
