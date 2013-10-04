/** 
 * Copyright (c) MewK, 2013
 * http://advancedbrewing.mewk.net
 * 
 * Advanced Brewing is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://advancedbrewing.mewk.net/MMPL-1.0.txt
 */

package advancedbrewing;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import advancedbrewing.gui.ContainerMachine;
import advancedbrewing.gui.ContainerVaporizer;
import advancedbrewing.tileentity.TileEntityMachine;
import advancedbrewing.tileentity.TileEntityVaporizer;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class HandlerPacket implements IPacketHandler {

	@Override
	@SuppressWarnings("rawtypes")
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data));
		try {
			int packetID = data.readInt();
			switch (packetID) {
				case 0:
					if (player instanceof EntityPlayerMP && ((EntityPlayerMP) player).openContainer instanceof ContainerMachine) {
						try {
							boolean redstoneActivated = data.readBoolean();
							ContainerMachine containerMachine = (ContainerMachine) (((EntityPlayerMP) player).openContainer);
							TileEntityMachine tileEntityMachine = containerMachine.getTileEntity();
							tileEntityMachine.setRedstoneActivated(redstoneActivated);
							tileEntityMachine.onInventoryChanged();
						}
						catch (Exception exception) {
							exception.printStackTrace();
						}
					}
					break;
				case 1:
					if (player instanceof EntityPlayerMP && ((EntityPlayerMP) player).openContainer instanceof ContainerVaporizer) {
						try {
							int radius = data.readInt();
							ContainerVaporizer containerVaporizer = (ContainerVaporizer) (((EntityPlayerMP) player).openContainer);
							TileEntityVaporizer tileEntityVaporizer = containerVaporizer.getTileEntity();
							tileEntityVaporizer.setRadius(radius);
							;
							tileEntityVaporizer.onInventoryChanged();
						}
						catch (Exception exception) {
							exception.printStackTrace();
						}
					}
					break;
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
