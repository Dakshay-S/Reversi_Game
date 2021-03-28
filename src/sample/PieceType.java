package sample;

public enum PieceType {
    RED, BLUE;

    PieceType getOppositePiece() {
        switch (this) {
            case RED:
                return BLUE;
            case BLUE:
                return RED;
            default:
                return null;
        }
    }
}


