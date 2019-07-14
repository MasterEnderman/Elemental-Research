package al132.elementalresearch.data;

import net.minecraft.item.ItemStack;

public class EarthStack {

    public ItemStack stack;
    public int points;

    public EarthStack(ItemStack stack, int points) {
        this.stack = stack;
        this.points = points;
    }

    public boolean stackMatches(ItemStack other) {
        return ItemStack.areItemsEqual(this.stack, other) && this.stack.getMetadata() == other.getMetadata();
    }
}