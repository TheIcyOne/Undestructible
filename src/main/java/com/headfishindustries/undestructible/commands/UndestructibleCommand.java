package com.headfishindustries.undestructible.commands;

import com.headfishindustries.undestructible.Undestructible;
import com.headfishindustries.undestructible.WorldStructure;
import com.headfishindustries.undestructible.utils.SpaceSavingUtils;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
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
			switch(args[0].toLowerCase()){
			case "help":
				sender.sendMessage(new TextComponentString("Options available: help, add, remove, pos, nextid, modify, visualise"));
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
			case "nextid":
				sender.sendMessage(new TextComponentString("Next available ID :" + SpaceSavingUtils.firstAvailableID(sender.getEntityWorld())));
			case "modify":
				sender.sendMessage(new TextComponentString("Sorry, this functionality is not yet implemented."));
				//TODO: Allow editing of structures. Should probably use a GUI at some point.
			case "visualise":
				sender.sendMessage(new TextComponentString("Sorry, this functionality is not yet implemented."));
				//TODO: Shadow renders of structures.
			default:
				
			}
			
		}else{
			sender.sendMessage(new TextComponentString("You do not have permission to use this command."));
		}
		
	}

	
	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender){
		return sender.canUseCommand(2, "undestructible");
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

}
