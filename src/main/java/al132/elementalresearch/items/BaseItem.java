package al132.elementalresearch.items;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BaseItem extends Item {

    public BaseItem(String name) {
        super();
        setRegistryName(name);
        setTranslationKey(this.getRegistryName().toString());
        ModItems.items.add(this);
    }

    public void registerItem(RegistryEvent.Register<Item> e) {
        e.getRegistry().register(this);
    }

    @SideOnly(Side.CLIENT)
    public void registerModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0,
                new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
