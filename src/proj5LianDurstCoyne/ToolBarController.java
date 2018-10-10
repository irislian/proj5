package proj5LianDurstCoyne;

import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Button;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ToolBarController {

    private TabPane tabPane;
    private Map<Tab, File> tabFileMap;

//    private Button compileButton;
//    private Button cprunButton;
//    private Button stopButton;

    public void handleCompileButton()
            throws InterruptedException, IOException {

        // get the corresponding file of the selected tab from the tab pane
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        File file = tabFileMap.get(selectedTab);

        if(file == null){
            System.out.println("file is not in the map");
            return;
        }

        System.out.println(Paths.get(file.toURI()).toString());
        // creating list of commands
        List<String> commands = new ArrayList<String>();
        commands.add("javac");
        commands.add(Paths.get(file.toURI()).toString());

        // creating the process
        ProcessBuilder pb = new ProcessBuilder(commands);

        // redirect error to error file
        File errorFile = new File("src/proj5LianDurstCoyne/ErrorLog.txt");
        pb.redirectError(errorFile);

        // start the process
        Process process = pb.start();

        // wait for the process to complete or throw an error
        int errCode = process.waitFor();
        System.out.println("Compiling executed, any errors? " + (errCode == 0 ? "No" : "Yes"));
        // if there is an error, print the error
        if (errCode != 0) {
            System.out.println("\nPrint Error:");
            System.out.println("*********************************");
            FileReader fr = new FileReader(errorFile);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            br.close();
            fr.close();
            System.out.println("*********************************");
        }

    }


    public void handleCprunButton()
            throws InterruptedException, IOException {
        // compile first
//        handleCompileButton(filename);

//        // creating list of commands
//        List<String> commands = new ArrayList<String>();
//        commands.add("java"); // command
//        commands.add("test.Main");
        // get the corresponding file of the selected tab from the tab pane
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        File file = tabFileMap.get(selectedTab);

        if(file == null){
            System.out.println("file is not in the map");
            return;
        }
        String pathToFile = Paths.get(file.toURI()).toString();
        System.out.println("PTF: "+pathToFile);

//        String classPath = System.getProperty("java.class.path");
//        System.out.println("CP: "+classPath);
        String[] parts = pathToFile.split(".");
        String className = parts[-2];
        // creating the process
        ProcessBuilder pb = new ProcessBuilder("java", "-cp", pathToFile, className);

        // redirect error to error file
        File errorFile = new File("src/proj5LianDurstCoyne/ErrorLog.txt");
        pb.redirectError(errorFile);

        // start the process
        Process process = pb.start();

        // wait for the process to complete or throw an error
        int errCode = process.waitFor();
        System.out.println("Run executed, any errors? " + (errCode == 0 ? "No" : "Yes"));
        // if there is an error, print the error
        if (errCode != 0) {
            System.out.println("\nPrint Error:");
            System.out.println("*********************************");
            FileReader fr = new FileReader(errorFile);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            br.close();
            fr.close();
            System.out.println("*********************************");
        }

        // print the output
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + System.getProperty("line.separator"));
            }
        } finally {
            br.close();
        }
        System.out.println("Output:\n" + sb.toString());
    }

    public void handleStopButton(String filename) throws IOException {
        // TODO: add some code
    }


    /**
     * Simple helper method that gets the FXML objects from the
     * main controller for use by other methods in the class.
     */
    public void receiveFXMLElements(Object[] list)
    {
        tabPane = (TabPane) list[0];
//        compileButton = (Button) list[5];
//        cprunButton = (Button) list[6];
//        stopButton = (Button) list[7];
        tabFileMap = (Map<Tab, File>) list[8];
    }
}
