package sample;


import java.util.function.Predicate;

public class Heuristic {

    public static Mode MODE = Mode.COUNT;   //default


    static int getEvaluation(State state, Player player) {
        switch (MODE){
            case COUNT:
                return countHeuristic(state, player);
            case WEIGHT:
                return weightHeuristic(state, player);
            default:
                return 0;
        }
    }

    static int countHeuristic(State state, Player maxPlayer) {
        PieceType maxType = (maxPlayer == Player.RED ? PieceType.RED : PieceType.BLUE);
        int count = 0;

        for (int i = 0; i < State.ROW; i++) {
            for (int j = 0; j < State.COL; j++) {

                if (state.stateMatrix[i][j] == maxType)
                    count++;
                else if (state.stateMatrix[i][j] == maxType.getOppositePiece())
                    count--;

            }
        }

        return count;
    }


    static int weightHeuristic(State state, Player maxPlayer) {

        int NORMAL_POINT = 1;
        int EDGE_POINT = 10;
        int CORNER_POINT = 50;


        PieceType maxType = (maxPlayer == Player.RED ? PieceType.RED : PieceType.BLUE);
        int weight = 0;
        int point;

        for (int i = 0; i < State.ROW; i++) {
            for (int j = 0; j < State.COL; j++) {

                if ((i == 0 || i == State.ROW - 1) && (j == 0 || j == State.COL - 1))
                    point = CORNER_POINT;
                else if ((i == 0 || i == State.ROW - 1) || (j == 0 || j == State.COL - 1))
                    point = EDGE_POINT;
                else point = NORMAL_POINT;


                if (state.stateMatrix[i][j] == maxType)
                    weight += point;
                else if (state.stateMatrix[i][j] == maxType.getOppositePiece())
                    weight -= point;
            }
        }

        return weight;
    }

    enum Mode {
        COUNT, WEIGHT
    }
}
