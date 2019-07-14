package al132.elementalresearch;

import al132.elementalresearch.capabilities.ResearchCapability;
import al132.elementalresearch.data.EarthStack;
import al132.elementalresearch.event.EventHandler;
import al132.elementalresearch.network.PacketHandler;
import com.google.common.collect.Lists;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.ArrayList;
import java.util.List;

public class CommonProxy {

    public static List<EarthStack> earthCustomStacks = new ArrayList<>();


    public void preInit(FMLPreInitializationEvent e) {
        ElementalResearch.logger = e.getModLog();
        CapabilityManager.INSTANCE.register(ResearchCapability.class, new ResearchCapability.Storage(), ResearchCapability::new);
        PacketHandler.registerMessages(Reference.MODID);
    }

    public void init(FMLInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        parseItemStacks();
    }

    public void postInit(FMLPostInitializationEvent e) {
        //ShopRegistry.init();
    }


    public void parseItemStacks() {
        for (String entry : ConfigHandler.general.earthCustomValues) {
            boolean failed = false;
            List<String> splits = Lists.newArrayList(entry.split(":"));
            if (splits.size() <= 2 || splits.size() >= 5) failed = true;
            else if (splits.size() == 3) {
                ResourceLocation loc = new ResourceLocation(splits.get(0), splits.get(1));
                Item item = Item.REGISTRY.getObject(loc);
                int points = Integer.parseInt(splits.get(2));
                if (item != null) {
                    earthCustomStacks.add(new EarthStack(new ItemStack(item), points));
                } else failed = true;
            } else if (splits.size() == 4) {
                ResourceLocation loc = new ResourceLocation(splits.get(0), splits.get(1));
                Item item = Item.REGISTRY.getObject(loc);
                int meta = Integer.parseInt(splits.get(2));
                int points = Integer.parseInt(splits.get(3));
                if (item != null) {
                    earthCustomStacks.add(new EarthStack(new ItemStack(item, 1, meta), points));
                }
            }
            if (failed) ElementalResearch.logger.warn("Unable to parse custom earth value for [" + entry + "]");
        }
    }
}