package sample;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.Arrays;
import java.util.List;

/*Assumptions:
 * 1. board is implemented as array of squares (Indexed 0)
 */

public class Board extends Pane {

    final static Color DARK_COLOR = Color.rgb(100, 100, 100);
    final static Color LIGHT_COLOR = Color.rgb(200, 200, 200);
    final static Color HINT_COLOR = Color.rgb(0, 255, 0);
    final static float squareDimension = 80f;
    Square[][] squares;
    final int N = 8;
    Pawn[][] pawnsMatrix = new Pawn[State.ROW][State.COL];
    float width;
    float height;
    State state;
    Player player = Player.RED;
    Player computer = Player.BLUE;
    final Runnable computerRunnable;

    public Board() {
        this.width = N * squareDimension;
        this.height = N * squareDimension;
        this.squares = new Square[State.ROW][State.COL];


        initSquares();
        resetBoard();

        computerRunnable = () -> {

            player = computer;

            int emptyCount = 0;
            int edgePiecesCount = 0;
            for (int i = 0; i < State.ROW; i++) {
                for (int j = 0; j < State.COL; j++) {

                    if (state.stateMatrix[i][j] == null)
                        emptyCount++;
                    else if (state.stateMatrix[i][j] != null && (i == 0 || i == State.ROW - 1 || j == 0 || j == State.COL - 1))
                        edgePiecesCount++;
                }
            }

            boolean isEndGame = (emptyCount <= 15);

            if (isEndGame || edgePiecesCount == 28) {
                Heuristic.MODE = Heuristic.Mode.COUNT;
            } else {
                Heuristic.MODE = Heuristic.Mode.WEIGHT;
            }


            float privilege = isEndGame ? (float) Math.pow(2, 29) : (float) Math.pow(2, 20);

            MAX_Player max_player = new MAX_Player(null);

            IntermediateState nexBest = max_player.solveForMax(new IntermediateState(state), privilege, computer, computer);

            Position newPiece = null;
            for (int i = 0; i < State.ROW; i++) {
                for (int j = 0; j < State.COL; j++) {
                    /*//todo
                    System.out.println(state);
                    System.out.println(nexBest);*/

                    if (this.state.stateMatrix[i][j] == null && nexBest.state.stateMatrix[i][j] != null) {
                        newPiece = new Position(i, j);
                        break;
                    }
                }

                if (newPiece != null) {
                    break;
                }
            }


            Position finalNewPiece = newPiece;
            Platform.runLater(() -> {
                Board.this.getToState(nexBest.state);

                if (finalNewPiece != null)
                    Board.this.highlightSquare(finalNewPiece);

                switchTurn();
            });

        };

    }


    void initSquares() {
        for (int i = 0; i < State.ROW; i++) {
            for (int j = 0; j < State.COL; j++) {
                squares[i][j] = new Square(i, j, (i + j) % 2 == 0 ? LIGHT_COLOR : DARK_COLOR);
                getChildren().add(squares[i][j]);
            }
        }
    }


    State initialiseState() {
        PieceType[][] stateMatrix = new PieceType[State.ROW][State.COL];

        for (PieceType[] row : stateMatrix)
            Arrays.fill(row, null);

        return new State(stateMatrix);
    }


    void resetBoard() {
        resetSquaresColors();

        for (int i = 0; i < State.ROW; i++)
            for (int j = 0; j < State.COL; j++)
                removePawnAt(i, j);

        state = initialiseState();

        setUpPawns();
    }

    void getToState(State state) {
        resetSquaresColors();

        for (int i = 0; i < State.ROW; i++)
            for (int j = 0; j < State.COL; j++)
                removePawnAt(i, j);

        this.state = state;

        int red_count = 0;
        int blue_count = 0;


        for (int i = 0; i < State.ROW; i++) {
            for (int j = 0; j < State.COL; j++) {
                if (state.stateMatrix[i][j] != null) {
                    Pawn pawn = new Pawn(state.stateMatrix[i][j], i, j);
                    this.getChildren().add(pawn); // add pawn to board
                    pawnsMatrix[pawn.posRow][pawn.posCol] = pawn;


                    if (state.stateMatrix[i][j] == PieceType.RED)
                        red_count++;
                    else blue_count++;
                }
            }
        }


        // if game over
        if (red_count + blue_count == State.ROW * State.COL) {
            PieceType winner;

            if (red_count > blue_count)
                winner = PieceType.RED;
            else if (blue_count > red_count)
                winner = PieceType.BLUE;
            else winner = null;

            String matchResult;
            if(winner != null)
                matchResult = "WINNER IS "+winner.name()+" !!!";
            else
                matchResult = "MATCH DRAWN";

            Alert gameOver = new Alert(Alert.AlertType.INFORMATION);
            gameOver.setHeaderText("Game Over");
            gameOver.setContentText("Red's Points = "+red_count+"\nBlue's Points = "+blue_count+"\n\n"+matchResult);
            gameOver.showAndWait();

        }


    }


    Pawn removePawnAt(int row, int col) {
        Pawn toBeRemoved = pawnsMatrix[row][col];

        if (toBeRemoved != null)
            this.getChildren().remove(toBeRemoved);

        pawnsMatrix[row][col] = null;

        return toBeRemoved;
    }


    void resetSquaresColors() {
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                squares[i][j].setColor((i + j) % 2 == 0 ? LIGHT_COLOR : DARK_COLOR);
    }


    void setUpPawns() {
        int midRow = (State.ROW - 1) / 2;
        int midCol = (State.COL - 1) / 2;

        Pawn b1 = new Pawn(PieceType.BLUE, midRow, midCol);
        Pawn b2 = new Pawn(PieceType.BLUE, midRow + 1, midCol + 1);

        Pawn r1 = new Pawn(PieceType.RED, midRow, midCol + 1);
        Pawn r2 = new Pawn(PieceType.RED, midRow + 1, midCol);

        List<Pawn> initPawns = Arrays.asList(b1, b2, r1, r2);

        for (Pawn pawn : initPawns) {
            this.getChildren().add(pawn); // add pawn to board
            pawnsMatrix[pawn.posRow][pawn.posCol] = pawn;
            state.stateMatrix[pawn.posRow][pawn.posCol] = pawn.pieceType;
        }
    }


    class Square extends Rectangle {
        int row;
        int col;
        Color color;


        public Square(int row, int col, Color color) {
            super(squareDimension, squareDimension);
            this.row = row;
            this.col = col;
            this.color = color;

            this.setFill(color);
            this.setOnMouseClicked(mouseEvent -> {
                clickAction();
            });

            this.relocate(col * squareDimension, row * squareDimension);
        }


        public void setColor(Color color) {
            this.color = color;
            this.setFill(color);
        }

        void clickAction() {
            Board.this.clickedSquare(row, col);
        }
    }


    class Pawn extends StackPane {
        PieceType pieceType;
        int posRow;
        int posCol;
        Pawn self;
        Circle circle;

        public Pawn(PieceType pieceType, int posRow, int posCol) {
            super();
            this.pieceType = pieceType;
            this.self = this;

            circle = new Circle();
            circle.setRadius(squareDimension / 2);
            circle.setFill((pieceType == PieceType.RED) ? Color.RED : Color.BLUE);
            circle.setEffect(new DropShadow());

            this.getChildren().addAll(circle);

            moveTo(posRow, posCol);
        }


        void moveTo(int row, int col) {

            this.posRow = row;
            this.posCol = col;


            this.relocate(posCol * squareDimension, posRow * squareDimension);
        }

    }


    void highlightSquare(Position position) {
        if (positionIsInRange(position))
            squares[position.row][position.col].setColor(HINT_COLOR);
    }


    void clickedSquare(int row, int col) {
        PieceType currPlayerPiece = player == Player.RED ? PieceType.RED : PieceType.BLUE;
        State newState = this.state.cloneAndMakeMove(currPlayerPiece, row, col);
        if (newState != null) {
            getToState(newState);
            switchTurn();
        }
    }


    void switchTurn() {
        this.player = this.player == Player.RED ? Player.BLUE : Player.RED;

        if (computer != null && computer == this.player) {
            Thread backgroundThread = new Thread(computerRunnable);
            backgroundThread.start();
        }

    }


    void showHint() {
        Runnable runnable = () -> {
            int emptyCount = 0;
            int edgePiecesCount = 0;
            for (int i = 0; i < State.ROW; i++) {
                for (int j = 0; j < State.COL; j++) {

                    if (state.stateMatrix[i][j] == null)
                        emptyCount++;
                    else if (state.stateMatrix[i][j] != null && (i == 0 || i == State.ROW - 1 || j == 0 || j == State.COL - 1))
                        edgePiecesCount++;
                }
            }

            boolean isEndGame = (emptyCount <= 15);

            if (isEndGame || edgePiecesCount == 28) {
                Heuristic.MODE = Heuristic.Mode.COUNT;
            } else {
                Heuristic.MODE = Heuristic.Mode.WEIGHT;
            }


            float privilege = isEndGame ? (float) Math.pow(2, 28) : (float) Math.pow(2, 20);


            MAX_Player max_player = new MAX_Player(null);
            IntermediateState nexBest = max_player.solveForMax(new IntermediateState(state), privilege, player, player);

            Position newPiece = null;
            for (int i = 0; i < State.ROW; i++) {
                for (int j = 0; j < State.COL; j++) {
                    if (this.state.stateMatrix[i][j] == null && nexBest.state.stateMatrix[i][j] != null) {
                        newPiece = new Position(i, j);
                        break;
                    }
                }

                if (newPiece != null) {
                    break;
                }
            }

            Position finalNewPiece = newPiece;
            Platform.runLater(() -> {
                Board.this.resetSquaresColors();
                if (finalNewPiece != null)
                    Board.this.highlightSquare(finalNewPiece);
                //draw hint
            });
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    void botFirst() {
        resetSquaresColors();

        for (int i = 0; i < State.ROW; i++)
            for (int j = 0; j < State.COL; j++)
                removePawnAt(i, j);

        int midRow = (State.ROW - 1) / 2;
        int midCol = (State.COL - 1) / 2;

        Pawn red1 = new Pawn(PieceType.RED, midRow, midCol);
        Pawn red2 = new Pawn(PieceType.RED, midRow + 1, midCol + 1);

        Pawn blue1 = new Pawn(PieceType.BLUE, midRow, midCol + 1);
        Pawn blue2 = new Pawn(PieceType.BLUE, midRow + 1, midCol);


        List<Pawn> initPawns = Arrays.asList(red1, red2, blue1, blue2);

        for (Pawn pawn : initPawns) {
            this.getChildren().add(pawn); // add pawn to board
            pawnsMatrix[pawn.posRow][pawn.posCol] = pawn;
            state.stateMatrix[pawn.posRow][pawn.posCol] = pawn.pieceType;
        }


        switchTurn();
    }


    void playBot() {
        new Thread(computerRunnable).start();
    }


    boolean positionIsInRange(Position position) {
        return position.row >= 0 && position.col >= 0 && position.row < State.ROW && position.col < State.COL;
    }

}
