package com.vanhal.progressiveautomation.entities;

import org.lwjgl.util.Point;
import org.lwjgl.util.vector.Vector2f;

import com.vanhal.progressiveautomation.ProgressiveAutomation;
import com.vanhal.progressiveautomation.ref.ToolInfo;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;

public class TileMiner extends BaseTileEntity {
	protected int totalMineBlocks = -1;
	protected int currentMineBlocks = 0;
	protected boolean invFull = false;

	public TileMiner() {
		super(13);
	}
	
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("MineBlocks", totalMineBlocks);
		nbt.setInteger("MinedBlocks", currentMineBlocks);
		nbt.setBoolean("InvFull", invFull);
	}
	
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		totalMineBlocks = nbt.getInteger("MineBlocks");
		currentMineBlocks = nbt.getInteger("MinedBlocks");
		invFull = nbt.getBoolean("InvFull");
	}
	
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) {
			checkForChanges();
			checkInventory();
			
			if ( (isBurning()) && (!invFull) ) {
				//mine!
				//mine();
			}
		}
	}
	
	public void scanBlocks() {
		ProgressiveAutomation.logger.info("Updating Blocks");
		totalMineBlocks = currentMineBlocks = 0;
		for (int i = 1; i <= getRange(); i++) {
			Point currentPoint = spiral(i, xCoord, zCoord);
			boolean bedrock = false;
			int newY = this.yCoord - 1;
			while (!bedrock) {
				Block tryBlock = worldObj.getBlock(currentPoint.getX(), newY, currentPoint.getY());
				if (tryBlock != null) {
					if (
						(tryBlock.getBlockHardness(worldObj, currentPoint.getX(), newY, currentPoint.getY())>=0) &&
						(tryBlock.getHarvestLevel(0)>=0)
						) {
						boolean mine = false;
						if (tryBlock == Blocks.cobblestone) {
							currentMineBlocks++;
						} if (tryBlock.getHarvestTool(0)=="pickaxe") {
							if (getToolMineLevel(2)>=tryBlock.getHarvestLevel(0)) {
								totalMineBlocks++;
								mine = true;
							}
						} else if (tryBlock.getHarvestTool(0)=="shovel") {
							if (getToolMineLevel(3)>=tryBlock.getHarvestLevel(0)) {
								totalMineBlocks++;
								mine = true;
							}
						} else {
							totalMineBlocks++;
							mine = true;
						}
						/*ProgressiveAutomation.logger.info("Block: "+currentPoint.getX()+","+newY+","+currentPoint.getY()+" Harvest Tool: "+
								tryBlock.getHarvestTool(0)+", Harvest Level: "+tryBlock.getHarvestLevel(0)+
								". Mine: "+mine);*/
					}
				}
				newY--;
				if (newY<0) bedrock = true;
			}
		}
		ProgressiveAutomation.logger.info("Update Finished: "+currentMineBlocks+"/"+totalMineBlocks);
	}

	public int getRange() {
		if (this.getStackInSlot(4)==null) {
			return 1;
		} else {
			return this.getStackInSlot(4).stackSize + 1;
		}
	}
	
	public int getToolMineLevel(int slot) {
		if (getStackInSlot(slot) != null) {
			if (getStackInSlot(slot).getItem() instanceof ItemTool) {
				ItemTool tool = (ItemTool) getStackInSlot(slot).getItem();
				return ToolInfo.getHarvestLevel(tool);
			}
		}
		return -1;
	}
	
	public int getMinedBlocks() {
		return currentMineBlocks;
	}
	
	public void setMinedBlocks(int value) {
		currentMineBlocks = value;
	}
	
	public int getMineBlocks() {
		return totalMineBlocks;
	}
	
	public void setMineBlocks(int value) {
		totalMineBlocks = value;
	}
	
	public boolean isInventoryFull() {
		return invFull;
	}

	
	/* Check for changes to tools and upgrades */
	protected int lastPick = -1;
	protected int lastShovel = -1;
	protected int lastUpgrades = 0;
	
	public void checkForChanges() {
		boolean update = false;
		//check pickaxe
		if ( (slots[2] == null) && (lastPick>=0) ) {
			lastPick = -1;
			update = true;
		} else if (slots[2] != null) {
			if (ToolInfo.getLevel(slots[2].getItem()) != lastPick) {
				lastPick = ToolInfo.getLevel(slots[2].getItem());
				update = true;
			}
		}
		
		//check shovel
		if ( (slots[3] == null) && (lastShovel>=0) ) {
			lastShovel = -1;
			update = true;
		} else if (slots[3] != null) {
			if (ToolInfo.getLevel(slots[3].getItem()) != lastShovel) {
				lastShovel = ToolInfo.getLevel(slots[3].getItem());
				update = true;
			}
		}
		
		//check upgrades
		if (getRange() != lastUpgrades) {
			lastUpgrades = getRange();
			update = true;
		}
		
		//update
		if (update) {
			scanBlocks();
		}
	}

	public static Point spiral(int n, int x, int y) {
		int dx, dy;
		
		int k = (int)Math.ceil( (Math.sqrt(n)-1)/2);
		int t = 2*k + 1;
		int m = t*t;
		t = t-1;
		
		if (n>=(m-t)) {
			dx = k-(m-n);
			dy = -k;
		} else {
			m = m-t;
			if (n>=(m-t)) {
				dx = -k;
				dy = -k + (m-n);
			} else {
				m = m-t;
				if (n>=(m-t)) {
					dx = -k + (m-n);
					dy = k;
				} else {
					dx = k;
					dy = k - (m-n-t);
				}
			}
		}
		
		return new Point(x + dx, y + dy);
	}

	/* Check if we are ready to go */
	public boolean readyToBurn() {
		if ( (totalMineBlocks>0) && (currentMineBlocks < totalMineBlocks) ) {
			if ( (slots[1]!=null) && (slots[2]!=null) && (slots[3]!=null) ) {
				return true;
			}
		}
		return false;
	}
	
	
	/* Check the inventory, move any useful items to their correct slots */
	public void checkInventory() {
		for (int i = 5; i <= 13; i++) {
			if (slots[i]!=null) {
				ProgressiveAutomation.logger.info("Slot: "+i);
				if (slots[i].isItemEqual(new ItemStack(Blocks.cobblestone))) {
					ProgressiveAutomation.logger.info("Is Cobble");
					setInventorySlotContents(1, slots[i]);
				}
			}
		}
	}

}
