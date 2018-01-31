package com.headfishindustries.undestructible;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class WorldStructure {
	
	private BlockPos start, end;
	private Map<BlockPos, BlockData> blockMap = new HashMap<BlockPos, BlockData>();
	private World world;
	private int ticks, blocks;

	

	public WorldStructure(NBTTagCompound structTag){
		this.parseNBT(structTag);
	}
	
	
	/** I'll probably use this later if I want to build things in different places. **/
	public WorldStructure(BlockPos startPos, BlockPos endPos, NBTTagCompound structTag, World worldIn, int ticksCycle, int blocksCycle) {
		this.parseNBT(structTag);
		this.start = startPos;
		this.end = endPos;
		this.world = worldIn;
		this.ticks = ticksCycle;
		this.blocks = blocksCycle;
	}
	
	private void parseNBT(NBTTagCompound struct){
		this.start = BlockPos.fromLong(struct.getLong("START"));
		this.end = BlockPos.fromLong(struct.getLong("END"));
		this.ticks = struct.getInteger("TICKS_CYCLE");
		this.blocks = struct.getInteger("BLOCKS_CYCLE");
		BlockPos pos;
		
		for (int x = Math.min(start.getX(), end.getX()); x<=Math.max(start.getX(), end.getX()); x++){
			for (int y = Math.min(start.getY(), end.getY()); y<=Math.max(start.getY(), end.getY()); y++){
				for (int z = Math.min(start.getZ(), end.getZ()); z<=Math.max(start.getZ(), end.getZ()); z++){
					pos = new BlockPos(x, y, z);
					blockMap.put(pos, BlockData.fromNBT((NBTTagCompound) struct.getTag("" + pos.toLong())));
				}
			}
		}		
	}
	
	public BlockPos randomPos(){
		int x = world.rand.nextInt(this.start.getX() - this.end.getX()) + end.getX();
		int y = world.rand.nextInt(this.start.getY() - this.end.getY()) + end.getY();
		int z = world.rand.nextInt(this.start.getZ() - this.end.getZ()) + end.getZ();

		return new BlockPos(x, y, z);
	}
	
	public Integer blocksPerCycle(){
		return this.blocks;
	}
	
	public Integer ticksPerCycle(){
		return this.ticks;
	}
	
	public void buildPos(BlockPos pos){
		BlockData bd = blockMap.get(pos);
		world.setBlockState(pos, bd.block, 2);
		if (!bd.tile.hasNoTags()){
			world.getTileEntity(pos).readFromNBT(bd.tile);
		}
	}
	
	public static class BlockData{
		public IBlockState block;
		public int meta;
		public NBTTagCompound tile;
		
		public BlockData(IBlockState blockIn, int metaIn, NBTTagCompound tag){
			this.block = blockIn;
//			this.meta = metaIn;
			this.tile = tag;
		}
		
		public BlockData(IBlockState blockIn, NBTTagCompound tag){
			this.block = blockIn;
			this.tile = tag;
		}
		
		private BlockData() {
			
		}

		public NBTTagCompound toNBT(){
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("id", Block.getStateId(this.block));
//			tag.setInteger("meta", meta);
			
			if(tile != null){
				tag.setTag("tile", tile);
			}
			
			return tag;
		}
		
		public static BlockData fromNBT(NBTTagCompound tag){
			BlockData bd = new BlockData();
			bd.block = Block.getStateById(tag.getInteger("id"));
//			bd.meta = tag.getInteger("meta");
			
			if (tag.hasKey("tile")){
				bd.tile = tag.getCompoundTag("tile");
			}	
			return bd;
		}
	}
}
