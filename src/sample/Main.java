package sample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.TOP_CENTER);
        Board board = new Board();

        Button bot_first = new Button("Bot First");
        bot_first.setOnAction(actionEvent -> {board.botFirst(); bot_first.setDisable(true);});

        Button playBot =  new Button("Play Bot");
        playBot.setOnAction(actionEvent -> board.playBot());


        Button resetBoard = new Button("Reset Board");
        resetBoard.setOnAction(actionEvent -> {board.resetBoard(); bot_first.setDisable(false);});


        Button showHint = new Button("Show Hint");
        showHint.setOnAction(actionEvent -> board.showHint());


        HBox controls = new HBox();
        controls.setAlignment(Pos.BASELINE_CENTER);
        controls.setSpacing(40);
        controls.setPadding(new Insets(10));

        controls.getChildren().add(resetBoard);
        controls.getChildren().add(bot_first);
        controls.getChildren().add(showHint);
        controls.getChildren().add(playBot);



        vBox.getChildren().add(board);
        vBox.getChildren().add(controls);

        primaryStage.setTitle("IIT2018011: Blue player is Computer. Red is human player");
        primaryStage.setOnCloseRequest(windowEvent -> System.exit(0));

        primaryStage.setScene(new Scene(vBox, board.width , board.height + 50));
        primaryStage.setResizable(false);

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
