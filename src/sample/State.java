package sample;


public class State {
    final static int ROW = 8;
    final static int COL = 8;
    PieceType[][] stateMatrix;


    public State(PieceType[][] stateMatrix) {
        this.stateMatrix = stateMatrix;
    }


    State getClone() {
        PieceType[][] clonedMatrix = new PieceType[ROW][];

        for (int i = 0; i < ROW; i++)
            clonedMatrix[i] = stateMatrix[i].clone();

        return new State(clonedMatrix);
    }

    State cloneAndMakeMove(PieceType piece, int row, int col) {
        State clone = this.getClone();
        if(clone.placePieceAt(piece, row, col)){
            return clone;
        }
        else return null;
    }


    void invertPiece(int row, int col) {
        if (isNotValidPosition(row, col))
            return;

        if (stateMatrix[row][col] == null)
            return;

        stateMatrix[row][col] = stateMatrix[row][col].getOppositePiece();
    }


    boolean placePieceAt(PieceType atkPiece, int row, int col) {
        if (isNotValidPosition(row, col) || !(stateMatrix[row][col] == null))
            return false;

        int invertedCount = 0;

        Direction[] values = Direction.values();
        for (int i = 0, valuesLength = values.length; i < valuesLength; i++) {
            Direction dir = values[i];
            int dirInvCount = invertPieceIfPossible(atkPiece, row + dir.delRow, col + dir.delCol, dir);

            if (dirInvCount > 0)
                invertedCount += dirInvCount;
        }

        if (invertedCount <= 0)
            return false;
        else {
            stateMatrix[row][col] = atkPiece;
            return true;
        }
    }



    private int invertPieceIfPossible(PieceType attackingPiece, int row, int col, Direction dir) {
        if (isNotValidPosition(row, col) || stateMatrix[row][col] == null)
            return -1;
        if (stateMatrix[row][col] == attackingPiece)
            return 0;

        int invertedCount = invertPieceIfPossible(attackingPiece, row + dir.delRow, col + dir.delCol, dir);

        if (invertedCount >= 0) {
            invertPiece(row, col);
            return invertedCount + 1;
        } else return invertedCount; // invertedCount = -1
    }

    boolean isNotValidPosition(int row, int col) {
        return row < 0 || col < 0 || row >= ROW || col >= COL;
    }

}
