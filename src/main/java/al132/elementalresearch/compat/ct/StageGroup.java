package al132.elementalresearch.compat.ct;

import com.google.common.collect.Lists;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

public class StageGroup {

    public List<String> stages;
    private double costModifier = 1.0;
    private Type logicType;

    enum Type {
        AND,
        OR,
        FOREACH_AND,
        FOREACH_OR;
    }

    public StageGroup(String type, double costModifier, String... stages) {
        this.logicType = parseType(type);
        this.stages = Lists.newArrayList(stages);
        this.costModifier = costModifier;
    }

    public StageGroup(String type, String... stages) {
        this(type, 1.0, stages);
    }

    private Type parseType(String _type) {
        String type = _type.toUpperCase();
        switch (type) {
            case "OR":
                return Type.OR;
            case "FOREACH_OR":
                return Type.FOREACH_OR;
            case "FOREACH_AND":
                return Type.FOREACH_AND;
            default:
                return Type.AND;
        }
    }

    public EvaluationResult evaluateFor(EntityPlayer player) {
        if (this.logicType == Type.AND) {
            return new EvaluationResult(GameStageHelper.hasAllOf(player, this.stages), this.costModifier);
        } else if (this.logicType == Type.OR) {
            return new EvaluationResult(GameStageHelper.hasAnyOf(player, this.stages), this.costModifier);
        } else if (this.logicType == Type.FOREACH_AND) {
            return new EvaluationResult(GameStageHelper.hasAllOf(player, this.stages), this.costModifier * stages.size());
        } else { //FOREACH_OR
            int stagesUnlocked = (int) this.stages.stream().filter(x -> GameStageHelper.hasStage(player, x)).count();
            return new EvaluationResult(stagesUnlocked > 0, this.costModifier * stagesUnlocked);
        }
    }

    public static class EvaluationResult {
        public boolean result;
        public double modifier;

        public EvaluationResult(boolean result, double modifier) {
            this.result = result;
            this.modifier = modifier;
        }
    }


    @Override
    public String toString() {
        return "[TYPE: " + logicType + "\tStages: " + stages + "]";
    }
}
