package com.rater193.sp;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PhysicsTaskManager extends BukkitRunnable {
	
	Plugin plugin;

	public PhysicsTaskManager(Plugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		PhysicsManager.TaskUpdate(this, plugin);
	}

}
