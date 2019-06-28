package al132.elementalresearch.items;

import al132.elementalresearch.event.ClientEventHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ModItems {

    public static List<BaseItem> items = new ArrayList<>();

    public static BaseItem researchBook = new BaseItem("research_book") {
        @Override
        public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
            ClientEventHandler.openShop();
            return super.onItemRightClick(worldIn, playerIn, handIn);
        }
    };
}
