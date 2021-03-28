package sample;

//TODO RENAME
public enum Direction {

    UP(-1, 0), DOWN(1, 0), LEFT(0, -1), RIGHT(0, 1), LOWER_RIGHT(1, 1), LOWER_LEFT(1, -1), UPPER_RIGHT(-1, 1), UPPER_LEFT(-1, -1);
    int delRow;
    int delCol;

    Direction(int delRow, int delCol) {
        this.delRow = delRow;
        this.delCol = delCol;
    }
}
