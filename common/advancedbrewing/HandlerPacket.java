package advancedbrewing;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import advancedbrewing.gui.ContainerMachine;
import advancedbrewing.tileentity.TileEntityMachine;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
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
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
