package com.headfishindustries.undestructible;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = Undestructible.MODID, version = Undestructible.VERSION, acceptedMinecraftVersions="[1.12, 1.13]")
public class Undestructible {
	public static final String MODID = "undestructible";
	public static final String VERSION = "%gradle.version%";
	
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	
	public static final StructureHandler HANDLER = new StructureHandler();
	
	@EventHandler
	public void init(FMLInitializationEvent e){
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(HANDLER);
	}
	
	@EventHandler
	public void startServer(FMLServerStartingEvent e){
		e.registerServerCommand(new UndestructibleCommand());
	}
}
