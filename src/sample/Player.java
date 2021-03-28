package sample;

public enum Player {
    RED, BLUE;

    Player getOpponent()
    {
        switch (this){
            case RED:
                return BLUE;
            case BLUE:
                return RED;
            default:
                return null;
        }
    }
}
