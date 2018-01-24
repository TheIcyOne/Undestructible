package com.headfishindustries.undestructible;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;

@Mod(modid = Undestructible.MODID, version = Undestructible.VERSION, acceptedMinecraftVersions="[1.12, 1.13]")
public class Undestructible {
	public static final String MODID = "undestructible";
	public static final String VERSION = "%gradle.version%";
	
	public static final Logger LOGGER = LogManager.getLogger(MODID);
}
