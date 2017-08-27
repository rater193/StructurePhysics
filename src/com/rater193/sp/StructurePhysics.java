package com.rater193.sp;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class StructurePhysics extends JavaPlugin {
	
	public static Plugin plugin;

	@Override
	public void onEnable() {
		// TODO Auto-generated method stub
		super.onEnable();
		plugin = this;
		new PhysicsManager(this);
	}
	
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		super.onDisable();
	}

}
