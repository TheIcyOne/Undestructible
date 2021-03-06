package com.headfishindustries.undestructible.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.headfishindustries.undestructible.WorldStructure;

import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class StructureHandler {


	private Set<WorldStructure> structs = new HashSet<WorldStructure>();
	private Map<WorldStructure, Integer> timeMap = new HashMap<WorldStructure, Integer>();
	
	public StructureHandler() {
	}
	
	public void init(World w){
		this.structs.addAll(SpaceSavingUtils.allActiveStruct(w));
	}
	
	public void addStruct(WorldStructure struct){
		this.structs.add(struct);
	}
	
	private void tickStruct(WorldStructure struct){
		this.timeMap.putIfAbsent(struct, 0);
		if (timeMap.get(struct) >= struct.ticksPerCycle()){
			timeMap.put(struct, 0);
			for (int i = 0; i < struct.blocksPerCycle(); i++){
				struct.buildPos(struct.randomPos());
			}
			return;
		}
		
		timeMap.put(struct, timeMap.get(struct) + 1);
	}
	
	@SubscribeEvent
	public void onTick(WorldTickEvent e){
		if (e.world.isRemote) return;
		for (WorldStructure struct : this.structs){
			tickStruct(struct);
		}
	}
	
	@SubscribeEvent
	public void worldLoad(WorldEvent.Load e){
		init(e.getWorld());
	}

}
