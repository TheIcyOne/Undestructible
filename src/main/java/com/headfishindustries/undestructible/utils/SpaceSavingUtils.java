package com.headfishindustries.undestructible.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.headfishindustries.undestructible.Undestructible;
import com.headfishindustries.undestructible.WorldStructure;
import com.headfishindustries.undestructible.WorldStructure.BlockData;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpaceSavingUtils {
	/** Takes input of 2 corner positions, returns an NBT tag holding all blocks within the area.**/
	//TODO: Config for min/max on these settings.
	//TODO: Config for moving a structure to a new position
	public static NBTTagCompound areaToNBT(World world, BlockPos start, BlockPos end, int ticksPer, int blocksPer){
		NBTTagCompound tag = new NBTTagCompound();
		BlockPos pos;
		
		tag.setLong("START", start.toLong());
		tag.setLong("END", end.toLong());
		tag.setInteger("TICKS_CYCLE", ticksPer);
		tag.setInteger("BLOCKS_CYCLE", blocksPer);
		tag.setBoolean("ACTIVE", true);
		
		NBTTagCompound blockdata = new NBTTagCompound();
	
		for (int x = Math.min(start.getX(), end.getX()); x<=Math.max(start.getX(), end.getX()); x++){
			for (int y = Math.min(start.getY(), end.getY()); y<=Math.max(start.getY(), end.getY()); y++){
				for (int z = Math.min(start.getZ(), end.getZ()); z<=Math.max(start.getZ(), end.getZ()); z++){
					pos = new BlockPos(x, y, z);
					BlockData bd = new WorldStructure.BlockData(world.getBlockState(pos), new NBTTagCompound());
					if (world.getTileEntity(pos) != null){
						bd.tile = world.getTileEntity(pos).serializeNBT();
					}
					blockdata.setTag(pos.toString(), bd.toNBT());
				}
			}
		}
		
		tag.setTag("BLOCKS", blockdata);
		
/*		Undestructible.LOGGER.info(tag.getTag("BLOCKS"));
		Undestructible.LOGGER.info(tag.getTag("BLOCKS").getClass());*/
		
		return tag;
	}
	
	public static void writeToFile(Integer id, NBTTagCompound nbt, World world){
		if (world == null || world.getSaveHandler().getWorldDirectory() == null) return;
		File f = new File(world.getSaveHandler().getWorldDirectory(), "Undestructible/" + world.provider.getSaveFolder() + "/" + id + ".dat");
		try{
			f.getParentFile().mkdirs();
			f.setWritable(true);
			DataOutputStream s = new DataOutputStream(new FileOutputStream(f));
			CompressedStreamTools.write(nbt, s);
			s.close();
		}catch(Exception e){
			Undestructible.LOGGER.error("Couldn't save file because " + e.getMessage());
		}
		
	}
	
	public static void deleteFile(Integer id, World world){
		if (world == null || world.getSaveHandler().getWorldDirectory() == null) return;
		File f = new File(world.getSaveHandler().getWorldDirectory(), "Undestructible/" + world.provider.getSaveFolder() + "/" + id + ".dat");
		Undestructible.LOGGER.info("Deleting saved structure: " + f.getAbsolutePath());
		f.delete();
	}
	
	public static NBTTagCompound readFromFile(Integer id, World world){
		if (world == null || world.getSaveHandler().getWorldDirectory() == null) return null;
		File f = new File(world.getSaveHandler().getWorldDirectory(), "Undestructible/" + world.provider.getSaveFolder() + "/" + id + ".dat");
		if (!f.exists()){return null;}
		try{
			DataInputStream s = new DataInputStream(new FileInputStream(f));
			NBTTagCompound t = CompressedStreamTools.read(s);
			s.close();
			return t;
		}catch(Exception e){
			Undestructible.LOGGER.error("Couldn't read file because " + e.getMessage());
			return null;
		}		
	}
	
	public static int firstAvailableID(World world){
		int i = 0;
		while (true){
			File f = new File(world.getSaveHandler().getWorldDirectory(), "Undestructible/" + world.provider.getSaveFolder() + "/" + i + ".dat");
			if(!f.exists()) break;
			i++;			
		}
		return i;
	}
	
	public static boolean isActive(NBTTagCompound tag){
		return tag.getBoolean("ACTIVE");
	}
	
	public static List<NBTTagCompound> allActive(World world){
		List<NBTTagCompound> ls = new ArrayList<NBTTagCompound>();
		for (int i = 0; i <= firstAvailableID(world); i++){
			NBTTagCompound x = readFromFile(i, world);
			if (x != null && isActive(x)){
				ls.add(x);
			}
		}
		return ls;
	}
	
	public static List<WorldStructure> allActiveStruct(World world){
		List<WorldStructure> ls = new ArrayList<WorldStructure>();
		for (NBTTagCompound nbt : allActive(world)){
			WorldStructure s = new WorldStructure(nbt, world);
			if (nbt.getBoolean("ACTIVE")) ls.add(s);
		}
		return ls;
	}


}
