package com.teamcqr.chocolatequestrepoured.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ServerProxy implements IProxy {

	public void preInit() {

	}

	public void init() {

	}

	public void postInit() {

	}

	@Override
	public EntityPlayer getPlayer(MessageContext context) {
		return context.getServerHandler().player;
	}

	@Override
	public World getWorld(MessageContext context) {
		return context.getServerHandler().player.world;
	}

}
