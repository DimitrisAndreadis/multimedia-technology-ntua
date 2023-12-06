package medialab.minesweeper;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileWriter;
import java.io.IOException;

import static medialab.minesweeper.Scenario.validValueScenario;

public class CreateHandler {
    @FXML
    private TextField timeField, nameField,mineField;
    @FXML
    private Text messageText;
    @FXML
    private RadioButton diffButton1,diffButton2;
    @FXML
    private ToggleGroup diff;
    @FXML
    private CheckBox superBox;

    @FXML
    private void setPrompts(ActionEvent actionEvent) {
        if (actionEvent.getSource()==diffButton1){
            timeField.setPromptText("120-180");
            mineField.setPromptText("9-11");
            superBox.setSelected(false);
        }
        if (actionEvent.getSource()==diffButton2) {
            timeField.setPromptText("240-360");
            mineField.setPromptText("35-45");
            superBox.setSelected(true);
        }
    }
    @FXML
    private void resetFields(){
        nameField.setText("SCENARIO-01");
        timeField.setText("");
        mineField.setText("");
    }
    @FXML
    private void createScenario(){
        byte d;
        int m, t;
        try {
            m = Integer.parseInt(mineField.getText());
            t = Integer.parseInt(timeField.getText());
            d =  (byte) (diffButton1.isSelected() ? 1:2);
            String filename = nameField.getText();

            if (filename.length()==0){
                messageText.setText("The filename cannot be empty!");
                return;
            }
            if (!filename.endsWith(".txt")) filename +=".txt";

            boolean s=false;
            if (d==2) s = superBox.isSelected();
            validValueScenario( d,m,t,s);
            FileWriter outputFile =new FileWriter("medialab\\"+filename);
            messageText.setText("The file " + filename + " was successfully created!");
            StringBuilder output = new StringBuilder();
            output.append(d).append('\n');
            output.append(m).append('\n');
            output.append(t).append('\n');
            output.append((s)?1:0);
            outputFile.write(output.toString());
            outputFile.close();
            Stage stage = (Stage) timeField.getScene().getWindow();
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished( event -> stage.close() );
            delay.play();

        }catch (NumberFormatException|InvalidValueException e){
            messageText.setText("The file's values are incorrect.");
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
