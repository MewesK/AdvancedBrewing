package advancedbrewing.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;

public abstract class TileEntitySynchronized extends TileEntity {

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
		this.readCustomFromNBT(par1NBTTagCompound);
	}

	protected abstract void readCustomFromNBT(NBTTagCompound par1NBTTagCompound);

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
		this.writeCustomToNBT(par1NBTTagCompound);
	}

	protected abstract void writeCustomToNBT(NBTTagCompound par1NBTTagCompound);

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		this.writeCustomToNBT(tag);
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData packet) {
		this.readCustomFromNBT(packet.data);
		this.worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}
}