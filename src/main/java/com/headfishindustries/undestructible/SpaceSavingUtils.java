package com.headfishindustries.undestructible;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.headfishindustries.undestructible.WorldStructure.BlockData;

import net.minecraft.block.Block;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpaceSavingUtils {
	/** Takes input of 2 corner positions, returns an NBT tag holding all blocks within the area.**/
	public static NBTTagCompound areaToNBT(BlockPos start, BlockPos end, World world){
		NBTTagCompound tag = new NBTTagCompound();
		BlockPos pos;
		
		tag.setLong("START", start.toLong());
		tag.setLong("END", end.toLong());
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
					blockdata.setTag("" + pos.toLong(), bd.toNBT());
				}
			}
		}
		tag.setTag("BLOCKS", blockdata);
		
		return tag;
	}
	
	public static void writeToFile(Integer id, NBTTagCompound nbt, World world){
		if (world == null || world.getSaveHandler().getWorldDirectory() == null) return;
		File f = new File(world.getSaveHandler().getWorldDirectory(), "Undestructible/" + id + ".dat");
		try{
			f.getParentFile().mkdirs();
			DataOutputStream s = new DataOutputStream(new FileOutputStream(f));
			CompressedStreamTools.write(nbt, s);
			s.close();
		}catch(Exception e){
			Undestructible.LOGGER.error("Couldn't save file because " + e.getMessage());
		}
		
	}
	
	public static NBTTagCompound readFromFile(Integer id, World world){
		if (world == null || world.getSaveHandler().getWorldDirectory() == null) return null;
		File f = new File(world.getSaveHandler().getWorldDirectory(), "Undestructible/" + id + ".dat");
		if (!f.exists()){Undestructible.LOGGER.error("Tried to read a nonexistent file. Halp."); return null;}
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
			File f = new File(world.getSaveHandler().getWorldDirectory(), "Undestructible/" + i + ".dat");
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
			if (x != null || !isActive(x)){
				ls.add(x);
			}
		}
		return ls;
	}


}
