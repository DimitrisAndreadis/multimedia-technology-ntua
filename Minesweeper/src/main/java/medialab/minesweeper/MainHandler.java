package medialab.minesweeper;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.*;


public class MainHandler {
    private StackPane[][] panes;
    @FXML
    private Pane minePane;
    @FXML
    private Text timeDisplay,flagDisplay,mineDisplay;
    private Scenario loadedScenario;
    private Timeline timeline =null;
    @FXML
    private Menu scenarioSelection;
    private Game game = null;


    @FXML
    private void onClickSolution(){
        Alert alert;
        if (game==null){
            alert = new Alert(Alert.AlertType.NONE, "No active game to solve!", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        if (!game.getStatus().equals("running")){
            alert = new Alert(Alert.AlertType.NONE, "The game is already over!", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        timeline.stop();
        game.endGame(false, Integer.parseInt(timeDisplay.getText()));;
        refreshBoardTiles(game.getBoardSize());
        alert = new Alert(Alert.AlertType.NONE, "You can figure it out next time!", ButtonType.OK);
        alert.showAndWait();
    }
    @FXML
    private void onClickRounds() throws IOException{
        Stage scenarioStage = new Stage();
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/rounds.fxml")));
        scenarioStage.setTitle("Previously on Minesweeper:");
        scenarioStage.setScene(scene);
        scenarioStage.showAndWait();
    }
    @FXML
    private void onClickExit(){
       Stage stage = (Stage) timeDisplay.getScene().getWindow();
       stage.close();
    }
    @FXML
    private void onClickCreate() throws IOException {
        Stage scenarioStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/create.fxml"));
        Scene scene = new Scene(loader.load(),350,450);
        scenarioStage.setTitle("Create a scenario");
        scenarioStage.setScene(scene);
        scenarioStage.showAndWait();
    }
    @FXML
    private void onClickLoad() throws IOException {
        Stage scenarioStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/load.fxml"));
        Scene scene = new Scene(loader.load(),350,350);
        LoadHandler LDC = loader.getController();
        scenarioStage.setTitle("Load a scenario");
        scenarioStage.setScene(scene);
        scenarioStage.showAndWait();
        Scenario sc = LDC.getLoadedScenario();
        if (sc != null){
            loadedScenario=sc;
            scenarioSelection.setText(LDC.getScenarioName());
        }
    }
    @FXML
    private void onClickStart(){
        if (loadedScenario==null){
            Alert alert = new Alert(Alert.AlertType.NONE, "No scenario selected!!", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        game= new Game(loadedScenario);
        int size = game.getBoardSize();
        panes = new StackPane[size][size];
        GridPane field = new GridPane();
        for (int i=0;i<size;i++){
            for (int j=0;j<size;j++){
                int tempi = i;
                int tempj = j;
                StackPane T = new StackPane();
                T.setBackground(new Background(new BackgroundFill(Color.valueOf("grey"), CornerRadii.EMPTY, Insets.EMPTY )));
                T.setMinSize(500.0/size,500.0/size);
                T.setMaxSize(500.0/size,500.0/size);
                T.setOnMouseClicked(event -> onSquareClick(event, tempi, tempj,size));
                Text content = new Text(" ");
                T.getChildren().add(content);
                panes[i][j]=T;
                field.add(T,j,i);
            }
        }
        field.setGridLinesVisible(true);
        minePane.getChildren().add(field);
        mineDisplay.setText(Integer.toString(game.getFlagsLeft()));
        flagDisplay.setText(Integer.toString(game.getFlagsLeft()));
        timeDisplay.setText((Integer.toString(game.getTimeLeft())));
        timeDisplay.setFill(Paint.valueOf("black"));
        timer();
    }

    private void timer() {
        if (timeline ==null){
            timeline = new Timeline();
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(1),
                            actionEvent -> {
                                int current = Integer.parseInt(timeDisplay.getText());
                                if (current<=0){
                                    timeline.stop();
                                    game.endGame(false, Integer.parseInt(timeDisplay.getText()));
                                    refreshBoardTiles(game.getBoardSize());
                                    Alert alert = new Alert(Alert.AlertType.NONE, "You lost! Better luck next time...", ButtonType.OK);
                                    alert.showAndWait();
                                }else{
                                    timeDisplay.setText(Integer.toString(--current));
                                }
                            }));
        }
        timeline.playFromStart();
    }
    private void updateRounds(String winner){
        StringBuilder entry = new StringBuilder();
        entry.append(game.getBoardSize()).append(", ");
        entry.append(game.getTries()).append(", ");
        int seconds = game.getTimeLeft()-game.remainingSeconds;
        entry.append(seconds).append(", ");
        entry.append(winner);
        FileWriter fw = null;
        FileReader fr = null;
        BufferedReader buffer = null;
        File temp = new File("medialab\\temprounds.txt");
        File rounds = new File("medialab\\rounds.txt");
        try {
            if (temp.exists()){
                temp.delete();
            }
            temp.createNewFile();
            if (!rounds.exists()){
                rounds.createNewFile();
            }
            fw = new FileWriter(temp, false);
            fr = new FileReader(rounds);
            buffer = new BufferedReader(fr);
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            fw.write(entry.toString()+'\n');
            String line;
            int lines=0;
            while ((line = buffer.readLine()) != null) {
                if (lines<4){
                    fw.write(line+'\n');
                }
                lines+=1;
            }
            buffer.close();
            fw.close();
            fr.close();
            rounds.delete();
            temp.renameTo(rounds);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void onSquareClick(MouseEvent click, int x, int y, int size) {
        String status = game.getStatus();
        if (status.equals("running")) {
            if (click.getButton() == MouseButton.PRIMARY) {
                game.checkClickedBox(x, y);
            } else {
                game.mark(x, y);
            }
            flagDisplay.setText(Integer.toString(game.getFlagsLeft()));
            status = game.getStatus();
           if (!status.equals("running") ){
               timeline.stop();
               if (status.equals("loss")) {
                   game.endGame(false, Integer.parseInt(timeDisplay.getText()));
                   Alert alert = new Alert(Alert.AlertType.NONE, "You lost! Better luck next time...", ButtonType.OK);
                   alert.showAndWait();
                   updateRounds("PC");
                }
               else {
                   game.endGame(true, Integer.parseInt(timeDisplay.getText()));
                   Alert alert = new Alert(Alert.AlertType.NONE, "You won", ButtonType.OK);
                   alert.showAndWait();
                   updateRounds("Human");
               }
            }
            refreshBoardTiles(size);
        }
    }

    private void refreshBoardTiles(int size){
        for (int i=0;i<size;i++){
            for (int j=0;j<size;j++){
                Text text = (Text) panes[i][j].getChildren().get(0);
                char mark = game.getRevealedBoardAt(i,j);
                if (mark!='E'){
                    text.setText(""+mark);
                } else{
                    text.setText("");
                }
                if (mark!='\u0000'&& mark!='F'&& mark!='M'){
                    panes[i][j].setBackground(new Background(new BackgroundFill(Color.valueOf("white"), CornerRadii.EMPTY, Insets.EMPTY)));
                }
            }
        }
    }
}