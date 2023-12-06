package medialab.minesweeper;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static medialab.minesweeper.Scenario.*;

public class LoadHandler {
    private Scenario loadedScenario;
    private String scenarioName;
    @FXML
    private TextField scenarioField;
    @FXML
    private Text messageText;
    public Scenario getLoadedScenario () {
        return loadedScenario;
    }
    public String getScenarioName () {
        return scenarioName;
    }
    @FXML
    private void findScenario()  {
        String filename=scenarioField.getText();
        if (filename.length()==0){
            messageText.setText("The filename cannot be empty!");
            return;
        }
        if (!filename.endsWith(".txt")) filename+=".txt";
        Scenario toLoad;

        try{
            File file = new File("medialab\\"+filename);
            Scanner sc = new Scanner(file);
            byte d;
            int m,t,u;
            try {
                d = sc.nextByte();
                m = sc.nextInt();
                t = sc.nextInt();
                u = sc.nextInt();
            } catch (NoSuchElementException e) {
                throw new InvalidDescriptionException("Invalid gameLogic.scenario description in file \"" + filename + "\"", e);
            }
                validValueScenario(d,m,t,u==1);
                toLoad =  new Scenario(d,m,t,u==1);
                scenarioName = filename;
        } catch (FileNotFoundException e) {
            messageText.setText("The file was not found,\nis the name correct?");
            return;
        } catch (InvalidDescriptionException ide){
            messageText.setText("The file's format is incorrect.");
            return;
        } catch (InvalidValueException ive){
            messageText.setText("The file's values are incorrect.");
            return;
        }
        loadedScenario=toLoad;
        messageText.setText("The file "+filename+" was successfully loaded.");
        Stage stage = (Stage) scenarioField.getScene().getWindow();
        PauseTransition delay = new PauseTransition(Duration.seconds(1));
        delay.setOnFinished( event -> stage.close() );
        delay.play();

    }
}
