package com.headfishindustries.undestructible;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldStructure {
	
	private BlockPos start, end;
	private Map<BlockPos, BlockData> blockMap = new HashMap<BlockPos, BlockData>();
	private World world;
	private int ticks, blocks;

	

	public WorldStructure(NBTTagCompound structTag, World worldIn){
		this.world = worldIn;
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
		
		NBTTagCompound blocks = struct.getCompoundTag("BLOCKS");
		BlockPos pos;
		
		for (int x = Math.min(start.getX(), end.getX()); x<=Math.max(start.getX(), end.getX()); x++){
			for (int y = Math.min(start.getY(), end.getY()); y<=Math.max(start.getY(), end.getY()); y++){
				for (int z = Math.min(start.getZ(), end.getZ()); z<=Math.max(start.getZ(), end.getZ()); z++){
					pos = new BlockPos(x, y, z);
					NBTTagCompound block = blocks.getCompoundTag(pos.toString());
					BlockData bd = BlockData.fromNBT(block);
					blockMap.put(pos, bd);
				}
			}
		}		
	}
	
	public BlockPos randomPos(){
				
		int x = randInt(start.getX(), end.getX());
		int y = randInt(start.getY(), end.getY());
		int z = randInt(start.getZ(), end.getZ());

		return new BlockPos(x, y, z);
	}
	
	private int randInt(int a, int b){
		int i = Math.max(a, b);
		int j = Math.min(a, b);
		
		int k = ThreadLocalRandom.current().nextInt(j, i + 1);
		
		//int k = (int) Math.floor(world.rand.nextDouble() * (i - j + 1) - j);
		
		return k;
	}
	
	public Integer blocksPerCycle(){
		return this.blocks;
	}
	
	public Integer ticksPerCycle(){
		return this.ticks;
	}
	
	public void buildPos(BlockPos pos){
		//Undestructible.LOGGER.info("Setting block at pos " + pos);
		BlockData bd = blockMap.get(pos);
		world.setBlockState(pos, bd.block, 2);
		if (bd.tile != null && !bd.tile.hasNoTags()){
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
			Integer id = Block.getStateId(this.block);
			tag.setInteger("id", id);
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
