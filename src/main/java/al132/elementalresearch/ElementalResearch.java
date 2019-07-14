package al132.elementalresearch;

import al132.elementalresearch.command.GeneralCommand;
import al132.elementalresearch.items.BaseItem;
import al132.elementalresearch.items.ModItems;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION)
@Mod.EventBusSubscriber
public class ElementalResearch {

    public static Logger logger;

    @Mod.Instance
    public static ElementalResearch INSTANCE;

    @SidedProxy(clientSide = "al132.elementalresearch.ClientProxy", serverSide = "al132.elementalresearch.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        proxy.preInit(e);
    }

    @EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }

    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new GeneralCommand());
    }


    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> e) {
        ModItems.items.forEach(x -> x.registerItem(e));
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent e) {
        ModItems.items.forEach(BaseItem::registerModel);

    }
}
