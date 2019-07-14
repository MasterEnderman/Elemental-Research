package al132.elementalresearch;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Reference.MODID)
@Config(modid = Reference.MODID, category = "elemental_research_config")
public class ConfigHandler {

    public static General general = new General();

    public static class General {
        @Config.Comment({"Enable fire points from damage [default: true]"})
        public boolean enableFirePoints = true;

        @Config.Comment({"Enable water points from damage [default: true]"})
        public boolean enableWaterPoints = true;

        @Config.Comment({"Enable earth points from harvesting [default: true]"})
        public boolean enableEarthPoints = true;

        @Config.Comment({"Enable air points from jumping [default: true]"})
        public boolean enableAirPoints = true;

        @Config.Comment({"Jumps required for 1 air point [default: 3, min: 1, max: 2,147,483,647]"})
        @Config.RangeInt(min = 1, max = Integer.MAX_VALUE)
        public int jumpsPerAirPoint = 3;

        @Config.Comment({"Water points per drowning damage [default: 1, min: 1, max: 2,147,483,647]"})
        @Config.RangeInt(min = 1, max = Integer.MAX_VALUE)
        public int pointsPerDrownDamage = 1;

        @Config.Comment({"Fire points per burning damage [default: 1, min: 1, max: 2,147,483,647]"})
        @Config.RangeInt(min = 1, max = Integer.MAX_VALUE)
        public int pointsPerFireDamage = 1;

        @Config.Comment({"Earth points per block broken by default (set exceptions below) [default: 1, min: 0, max: 2,147,483,647]"})
        @Config.RangeInt(min = 0, max = Integer.MAX_VALUE)
        public int pointsPerHarvest = 1;

        @Config.Comment({"If earth points are enabled, the following will set custom point values for specific blocks, use the format <modid>:<name>:<meta>:<points>"})
        public String[] earthCustomValues = new String[]{};


    }


    @SubscribeEvent
    public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent e) {
        if (e.getModID().equals(Reference.MODID)) {
            ConfigManager.sync(Reference.MODID, Config.Type.INSTANCE);
        }
    }
}
