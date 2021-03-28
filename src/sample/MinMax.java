package sample;

import java.util.List;

public class MinMax {

    int alpha = Integer.MAX_VALUE;
    int beta = Integer.MIN_VALUE;
    MinMax parent ;

    public MinMax(MinMax parent) {
        this.parent = parent;
    }


    public int getParentAlpha() {
        return parent == null ? Integer.MAX_VALUE : parent.alpha;
    }

    public int getParentBeta() {
        return parent == null ? Integer.MIN_VALUE : parent.beta;
    }

}

class MAX_Player extends MinMax {

    public MAX_Player(MinMax parent) {
        super(parent);
    }

    IntermediateState solveForMax(IntermediateState given, float privilege, Player currPlayer , Player max_player) {

        if (Math.round(privilege) <= 0) {
            this.beta = Heuristic.getEvaluation(given.state, max_player);
            return given;
        }

        List<IntermediateState> nextStates = given.getAllPossibleNextStatesForTurn(currPlayer);
        if(nextStates.isEmpty())
            return given;

        IntermediateState nextBestState = given;

        float nextLevelPrivilege = privilege / nextStates.size();

        for (int i = 0, nextStatesSize = nextStates.size(); i < nextStatesSize; i++) {
            IntermediateState state = nextStates.get(i);
            // BETA PRUNING
            if (this.beta >= this.getParentAlpha())
                break;


            MIN_Player MIN = new MIN_Player(this);
            MIN.solveForMin(state, nextLevelPrivilege, currPlayer == Player.RED ? Player.BLUE : Player.RED, max_player);

            if (MIN.alpha >= this.beta) {
                nextBestState = state;
                this.beta = MIN.alpha;
            }
        }

        return nextBestState;
    }

}


class MIN_Player extends MinMax
{
    public MIN_Player(MinMax parent) {
        super(parent);
    }

    IntermediateState solveForMin(IntermediateState given, float privilege, Player currPlayer , Player max_player)
    {
        if (Math.round(privilege) <= 0) {
            this.alpha = Heuristic.getEvaluation(given.state, max_player);
            return given;
        }

        List<IntermediateState> nextStates = given.getAllPossibleNextStatesForTurn(currPlayer);
        if(nextStates.isEmpty())
            return given;

        IntermediateState nextBestState = given;
        float nextLevelPrivilege = privilege / nextStates.size();

        for (int i = 0, nextStatesSize = nextStates.size(); i < nextStatesSize; i++) {
            IntermediateState state = nextStates.get(i);
            // ALPHA PRUNING
            if (this.alpha <= this.getParentBeta())
                break;


            MAX_Player MAX = new MAX_Player(this);
            MAX.solveForMax(state, nextLevelPrivilege, currPlayer == Player.RED ? Player.BLUE : Player.RED, max_player);

            if (MAX.beta <= this.alpha) {
                nextBestState = state;
                this.alpha = MAX.beta;
            }
        }

        return nextBestState;
    }
}
