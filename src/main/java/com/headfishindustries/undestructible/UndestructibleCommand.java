package com.headfishindustries.undestructible;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class UndestructibleCommand extends CommandBase{

	@Override
	public String getName() {
		return "undestructible";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "Use '/undestructible help' for more information.";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		World world = sender.getEntityWorld();
		if (sender.getEntityWorld().isRemote){
			return;
		}
		if (sender.canUseCommand(this.getRequiredPermissionLevel(), this.getName())){
			switch(args[0]){
			case "help":
				sender.sendMessage(new TextComponentString("You're an idiot. Anyhow, use /undestructible add <startX> <y> <z> <endX> <y> <z> [ticksEach] [blocksEach]."));
				break;
			case "add":
				NBTTagCompound tag = SpaceSavingUtils.areaToNBT(sender.getEntityWorld(), new BlockPos(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3])), new BlockPos(Integer.parseInt(args[4]), Integer.parseInt(args[5]), Integer.parseInt(args[6])), Integer.parseInt(args[7]), Integer.parseInt(args[8]));
				sender.sendMessage(new TextComponentString("ID: " + SpaceSavingUtils.firstAvailableID(sender.getEntityWorld())));
				SpaceSavingUtils.writeToFile(SpaceSavingUtils.firstAvailableID(world), tag, world);
				WorldStructure s = new WorldStructure(tag, sender.getEntityWorld());
				Undestructible.HANDLER.addStruct(s);
				break;
			case "remove":
				SpaceSavingUtils.deleteFile(Integer.parseInt(args[1]), world);
				break;
			case "pos":
				sender.sendMessage(new TextComponentString(sender.getPosition().toString()));
				break;
			default:
				
			}
			
		}else{
			sender.sendMessage(new TextComponentString("You do not have permission to use this command."));
		}
		
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

}
