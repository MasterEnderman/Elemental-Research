package al132.elementalresearch.compat.ct;

import com.google.common.collect.Lists;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

public class StageGroup {

    public List<String> stages;
    public double costModifier = 1.0;
    public Type logicType;

    enum Type {
        AND,
        OR;
    }

    public StageGroup(String type, double costModifier, String... stages) {
        if (type.toUpperCase().equals("OR")) this.logicType = Type.OR;
        else this.logicType = Type.AND;
        this.stages = Lists.newArrayList(stages);
        this.costModifier = costModifier;
    }

    public StageGroup(String type, String... stages) {
        if (type.toUpperCase().equals("OR")) this.logicType = Type.OR;
        else this.logicType = Type.AND;
        this.stages = Lists.newArrayList(stages);
        this.costModifier = 1.0;
    }

    public boolean evaluateFor(EntityPlayer player) {
        if (this.logicType == Type.AND) return GameStageHelper.hasAllOf(player, this.stages);
        else return GameStageHelper.hasAnyOf(player, this.stages);
    }

    @Override
    public String toString() {
        return "[TYPE: " + logicType + "\tStages: " + stages + "]";
    }
}
