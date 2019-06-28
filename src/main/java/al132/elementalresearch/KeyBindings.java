package al132.elementalresearch;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KeyBindings {

    public static KeyBinding shopKey;

    public static void init() {
       // shopKey = new KeyBinding("key.elementalresearch.shopkey", Keyboard.KEY_L, "key.categories." + Reference.MODID);
       // ClientRegistry.registerKeyBinding(shopKey);
    }
}