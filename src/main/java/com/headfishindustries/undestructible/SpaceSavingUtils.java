package com.headfishindustries.undestructible;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpaceSavingUtils {
	
	//TODO: Save blocks to an NBT format
	/** Takes input of 2 corner positions, returns an NBT tag holding all blocks within the area.**/
	public static NBTTagCompound areaToNBT(BlockPos start, BlockPos end, World world){
		//TODO: Optionally save entities
		//TODO: Save tiles + contents
		NBTTagCompound tag = new NBTTagCompound();
		BlockPos pos;
		
		tag.setLong("START", start.toLong());
		tag.setLong("END", end.toLong());
		
		NBTTagCompound blocks = new NBTTagCompound();
		NBTTagList tiles = new NBTTagList();
	
		for (int x = Math.min(start.getX(), end.getX()); x<=Math.max(start.getX(), end.getX()); x++){
			for (int y = Math.min(start.getY(), end.getY()); y<=Math.max(start.getY(), end.getY()); y++){
				for (int z = Math.min(start.getZ(), end.getZ()); z<=Math.max(start.getZ(), end.getZ()); z++){
					pos = new BlockPos(x, y, z);
					world.getBlockState(pos);
					blocks.setString("" + pos.toLong(), world.getBlockState(pos).toString().split("@")[0]);
					
					if (world.getTileEntity(pos) != null){
						tiles.set(tiles.tagCount(), world.getTileEntity(pos).serializeNBT());
					}
				}
			}
		}
		
		tag.setTag("BLOCKS", blocks);
		tag.setTag("TILES", tiles);
		
		return tag;
	}
	

	



}
