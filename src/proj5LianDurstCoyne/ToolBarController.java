package proj5LianDurstCoyne;

import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ToolBarController {
    private Button compileButton;
    private Button cprunButton;
    private Button stopButton;


    public static void handleCompileButton(String filename) throws IOException {
        // creating list of commands
        List<String> commands = new ArrayList<String>();
        commands.add("javac"); // command
        commands.add("test.java");

        // creating the process
        ProcessBuilder pb = new ProcessBuilder(commands);

        // startinf the process
        Process process = pb.start();

        // for reading the ouput from stream
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(process.getInputStream()));
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }
    }



    public static void handleCprunButton(String filename) throws IOException {
        // creating list of commands
        List<String> commands = new ArrayList<String>();
        commands.add("java"); // command
        commands.add("test");

        // creating the process
        ProcessBuilder pb = new ProcessBuilder(commands);

        // startinf the process
        Process process = pb.start();

        // for reading the ouput from stream
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(process.getInputStream()));
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }
    }

    public static void handleStopButton(String filename) throws IOException {
        // TODO: add some code
    }


    /**
     * Simple helper method that gets the FXML objects from the
     * main controller for use by other methods in the class.
     */
    public void recieveFXMLElements(Object[] list)
    {
        compileButton = (Button) list[11];
        cprunButton = (Button) list[12];
        stopButton = (Button) list[13];
    }
}
