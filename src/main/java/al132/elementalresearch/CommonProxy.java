package al132.elementalresearch;

import al132.elementalresearch.capabilities.ResearchCapability;
import al132.elementalresearch.event.EventHandler;
import al132.elementalresearch.network.PacketHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent e) {
        ElementalResearch.logger = e.getModLog();
        CapabilityManager.INSTANCE.register(ResearchCapability.class, new ResearchCapability.Storage(), ResearchCapability::new);
        PacketHandler.registerMessages(Reference.MODID);
    }

    public void init(FMLInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    public void postInit(FMLPostInitializationEvent e) {
        //ShopRegistry.init();
    }
}