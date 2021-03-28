package sample;

import java.util.*;

public class IntermediateState{
    State state;

    public IntermediateState(State state) {
        this.state = state;
    }


    List<IntermediateState> getAllPossibleNextStatesForTurn(Player currPlayer){
        PieceType currPiece = (currPlayer == Player.RED ? PieceType.RED : PieceType.BLUE);

        List<IntermediateState> list = new ArrayList<>();
        State clone = this.state.getClone();

        for (int i = 0; i < State.ROW; i++) {
            for (int j = 0; j <State.COL ; j++) {

                if(clone.placePieceAt(currPiece,i,j)){
                    list.add(new IntermediateState(clone));
                    clone = this.state.getClone();
                }

            }

        }
        return list;
    }

}
