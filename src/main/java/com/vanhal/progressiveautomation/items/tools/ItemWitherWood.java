package com.vanhal.progressiveautomation.items.tools;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.vanhal.progressiveautomation.items.BaseItem;
import com.vanhal.progressiveautomation.ref.Ref;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemWitherWood extends BaseItem {
	public ItemWitherWood() {
		super("WitherWood");
		setTextureName(Ref.MODID+":WitherWood");
	}
	
	@SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack itemStack) {
        return true;
    }
	
    protected void addNormalRecipe() {
    	ShapedOreRecipe recipe = new ShapedOreRecipe(new ItemStack(this, 8), new Object[]{
			"ppp", "pnp", "ppp", 'p', Blocks.log, 'n', Items.nether_star});
		GameRegistry.addRecipe(recipe);
	}

	protected void addUpgradeRecipe() {
		addNormalRecipe();
	}
	
	public boolean onItemUse(ItemStack p_77648_1_, EntityPlayer p_77648_2_, World p_77648_3_, int p_77648_4_, int p_77648_5_, int p_77648_6_, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_) {
		return false;
	}
	
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par) {
    	list.add(EnumChatFormatting.GRAY + "Can be used to craft tools");

    }
}
