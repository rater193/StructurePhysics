package com.rater193.sp;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.EventHandler;


public class PhysicsManager implements Listener {
	public static ArrayList<WorldBlock> blocksToUpdate = new ArrayList<WorldBlock>();

	public static ArrayList<Material> ignoredMaterials = new ArrayList<Material>();
	public static ArrayList<Material> airMaterials = new ArrayList<Material>();
	
	public PhysicsManager(Plugin plugin) {

		Debug.log("Registering materials to check for updates");
		ignoredMaterials.add(Material.BEDROCK);
		
		airMaterials.add(Material.AIR);
		airMaterials.add(Material.WATER);
		airMaterials.add(Material.LAVA);
		airMaterials.add(Material.LONG_GRASS);
		airMaterials.add(Material.SNOW);
		
		Debug.log("Registering events");
		Bukkit.getPluginManager().registerEvents(this, plugin);
		
		Debug.log("Registering update task");
		new PhysicsTaskManager(plugin).runTaskTimer(plugin, 1L, 1L);
	}
	
	@EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
		Debug.log("Player, " + event.getPlayer().getDisplayName()+", has logged in, handling playerdata.");
    }
	
	@EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
		/*if(event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			Debug.log(event.getBlock().getType().toString());
			addBlockToUpdate(event.getBlock());
		}*/
		//event.setCancelled(true);
    }
	
	@EventHandler
	public void onEntityExploded(EntityExplodeEvent event) {
		for(int index = 0; index < event.blockList().size(); index++) {
			addBlockToUpdate(event.blockList().get(index));
			Location location = event.blockList().get(index).getLocation();
			addBlockToUpdate(location.getWorld(), (int)location.getX(), (int)location.getY()+1, (int)location.getZ());
			addBlockToUpdate(location.getWorld(), (int)location.getX(), (int)location.getY()-1, (int)location.getZ());
			addBlockToUpdate(location.getWorld(), (int)location.getX()-1, (int)location.getY(), (int)location.getZ());
			addBlockToUpdate(location.getWorld(), (int)location.getX()+1, (int)location.getY(), (int)location.getZ());
			addBlockToUpdate(location.getWorld(), (int)location.getX(), (int)location.getY(), (int)location.getZ()-1);
			addBlockToUpdate(location.getWorld(), (int)location.getX(), (int)location.getY(), (int)location.getZ()+1);
		}
	}
	
	@EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
    }
	
	@EventHandler
    public void onBlockDamaged(BlockDamageEvent event) {
		//addBlockToUpdate(event.getBlock());
		//event.setCancelled(true);
    }
	
	public static void TaskUpdate(PhysicsTaskManager physicsTaskManager, Plugin plugin) {
		//Here we are cloning the first 100 items in the list(or how many we configured we want per update)
		//and then we will process them ;)
		
		ArrayList<WorldBlock> blocksToUpdateClone = new ArrayList<WorldBlock>();
		for(int i = 0; i < Settings.maxUpdateQueue; i++) {
			if(i >= blocksToUpdate.toArray().length) {
				break;
			}else{
				if(blocksToUpdate.toArray().length > 0) {
					blocksToUpdateClone.add(blocksToUpdate.get(i));
				}else{
					break;
				}
			}
		}
		
		
		for(long i = 0; i < blocksToUpdateClone.size(); i++) {

			//If there is something in it, then we want to do an update ;)
			Block block = blocksToUpdate.get(0).block;
			//here we are removing the block from the update queue
			//Debug.log("Handling block, " + i);
			//Location location;
			
			//Block targetBlock;
			boolean deleteBlock = false;
			
			if(tryToPlaceBlock(block.getWorld(), block.getX(), block.getY()-1, block.getZ(), block.getType())) {
				deleteBlock = true;
				addBlockToUpdate(block.getWorld(), block.getX(), block.getY()-1, block.getZ());
			}else if(tryToPlaceBlock(block.getWorld(), block.getX(), block.getY()-1, block.getZ()-1, block.getType())) {
				deleteBlock = true;
				addBlockToUpdate(block.getWorld(), block.getX(), block.getY()-1, block.getZ()-1);
			}else if(tryToPlaceBlock(block.getWorld(), block.getX(), block.getY()-1, block.getZ()+1, block.getType())) {
				deleteBlock = true;
				addBlockToUpdate(block.getWorld(), block.getX(), block.getY()-1, block.getZ()+1);
			}else if(tryToPlaceBlock(block.getWorld(), block.getX()-1, block.getY()-1, block.getZ(), block.getType())) {
				deleteBlock = true;
				addBlockToUpdate(block.getWorld(), block.getX()-1, block.getY()-1, block.getZ());
			}else if(tryToPlaceBlock(block.getWorld(), block.getX()+1, block.getY()-1, block.getZ(), block.getType())) {
				deleteBlock = true;
				addBlockToUpdate(block.getWorld(), block.getX()+1, block.getY()-1, block.getZ());
			}
			
			if(deleteBlock==true) {
				addBlockToUpdate(block.getWorld(), block.getX(), block.getY()+1, block.getZ());
				block.setType(Material.AIR);
			}
			
			//Remove the first item in the list;
			blocksToUpdate.remove(0);
		}
	}
	
	public static boolean tryToPlaceBlock(World world, int x, int y, int z, Material type) {
		if(airMaterials.contains(world.getBlockAt(x, y, z).getType())) {
			world.getBlockAt(x, y, z).setType(type);
			return true;
		}
		return false;
	}
	
	public static void addBlockToUpdate(World world, int x, int y, int z) {
		addBlockToUpdate(world.getBlockAt(x, y, z));
	}
	
	public static void addBlockToUpdate(Block block) {
		/*
		if(allowedMaterials.contains(block.getType())) {
			blocksToUpdate.add(new WorldBlock(block));
		}*/
		if(!ignoredMaterials.contains(block.getType())) {
			if(!airMaterials.contains(block.getType())) {
				blocksToUpdate.add(new WorldBlock(block));
			}
		}
	}
}
