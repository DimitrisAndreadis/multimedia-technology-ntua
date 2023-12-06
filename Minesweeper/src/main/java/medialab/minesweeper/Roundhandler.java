package medialab.minesweeper;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import java.io.BufferedReader;
import java.io.FileReader;

public class Roundhandler {
    @FXML
    private GridPane roundGrid;
    @FXML
    private void initialize(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader("medialab\\rounds.txt"));
            String line;
            Text toAdd;
            int start = 1;
            while ((line=reader.readLine())!=null){
                String[] stuff=line.split(", ");
                int item=0;
                for (String s:stuff){
                    toAdd=new Text(s);
                    toAdd.setFill(Paint.valueOf("black"));
                    roundGrid.add(toAdd,item++,start);
                }
                start++;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
