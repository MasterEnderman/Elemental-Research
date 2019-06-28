package al132.elementalresearch.compat.ct;

import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.elementalresearch.Util")
@ModOnly("elementalresearch")
@ZenRegister
public class CTUtil {

    @ZenMethod
    public static CTEntryBuilder createEntry(String displayName) {
        CTEntryBuilder temp = new CTEntryBuilder();
        temp.displayName = displayName;
        return temp;
    }

}
