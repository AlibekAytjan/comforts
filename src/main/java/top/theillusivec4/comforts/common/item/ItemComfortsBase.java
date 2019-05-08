package top.theillusivec4.comforts.common.item;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBed;
import top.theillusivec4.comforts.Comforts;

public class ItemComfortsBase extends ItemBed {

    public ItemComfortsBase(Block block) {
        super(block, new Item.Properties().group(Comforts.CREATIVE_TAB));
        this.setRegistryName(block.getRegistryName());
    }
}
