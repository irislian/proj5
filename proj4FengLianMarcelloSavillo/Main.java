/*
File: Main.java
CS361 Project 4
Names: Yi Feng, Iris Lian, Christopher Marcello, and Evan Savillo
Date: 10/02/18
*/

package proj4FengLianMarcelloSavillo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * This class creates a stage, as specified in Main.fxml, that contains a
 * set of tabs, embedded in a tab pane, with each tab window containing a
 * text area, a menu bar containing File and Edit menu, and Hello and
 * Goodbye buttons that have features of creating an "input a number"
 * dialog, and changing the text of the button to "Yah, sure!"
 *
 * @author Liwei Jiang
 * @author Iris Lian
 * @author Tracy Quan
 */
public class Main extends Application {
    /**
     * Creates a stage, as specified in Main.fxml, that contains a set of tabs,
     * embedded in a tab pane, with each tab window containing a text area,
     * a menu bar containing File and Edit menu, and Hello and Goodbye
     * buttons that have features of creating an "input a number"
     * dialog, and changing the text of the button to "Yah, sure!"
     *
     * @param stage The stage that contains the window content
     */
    @Override public void start(Stage stage) throws Exception{
        // load the fxml file to create the stage and get the root
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/proj4FengLianMarcelloSavillo/Main.fxml"));
        Parent root = loader.load();

        // initialize a scene and add features specified in the css file to the scene
        Scene scene = new Scene(root, 640, 480);
        scene.getStylesheets().add("/proj4FengLianMarcelloSavillo/Main.css");
        scene.getStylesheets().add("/proj4FengLianMarcelloSavillo/java-keywords.css");
 
        // configure the stage
        stage.setTitle("Yi Feng, Iris Lian, Christopher Marcello, and Evan Savillo's Project 4");
        stage.sizeToScene();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * main function of Main class
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
