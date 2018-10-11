package proj5LianDurstCoyne;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.*;
import java.nio.file.Paths;

import java.util.Map;

import org.fxmisc.richtext.StyleClassedTextArea;

public class ToolBarController {

    private TabPane tabPane;
    private Map<Tab, File> tabFileMap;
    private StyleClassedTextArea consolePane;

    private Button compileButton;
    private Button cprunButton;
    private Button stopButton;

    private void doCompilation(String filePath) {
        Thread thread = new Thread() {

            Process process;
            int errCode;
//            FileReader fr;

            public void run() {
//                consolePane.appendText("In Run\n");
                // creating the process
                ProcessBuilder pb = new ProcessBuilder("javac", filePath);
                // redirect error to error file
                File errorFile = new File("src/proj5LianDurstCoyne/ErrorLog.txt");
                pb.redirectError(errorFile);
                // start the process
                try {
                    process = pb.start();
                    errCode = process.waitFor();

                    System.out.println("Compilation executed, any errors? " + (errCode == 0 ? "No" : "Yes"));
                    // if there is an error, print the error
                    if (errCode != 0) {
                        consolePane.appendText("Error:\n");
                        FileReader fr = new FileReader(errorFile);
                        BufferedReader br = new BufferedReader(fr);
                        String line;
                        while ((line = br.readLine()) != null) {
                            consolePane.appendText(line+"\n");
                        }
                        br.close();
                        fr.close();
                    } else {
                        consolePane.appendText("Success!\n");
                    }
                } catch (IOException e ) {
                    System.out.println(e.getMessage());
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
                System.out.println("*********************************");
            }
        };
        thread.start();
    }

    public void handleCompileButton(){

        consolePane.clear();

        Tab selectedTab;
        System.out.print(this.tabPane.getTabs().size()+"\n");

        // TEMPORARILY SOLVES THE PROBLEM
        if (this.tabPane.getTabs().size() > 1){
            // get the corresponding file of the selected tab from the tab pane
            selectedTab = this.tabPane.getSelectionModel().getSelectedItem();
        }
        else{
            selectedTab = (Tab)this.tabPane.getTabs().toArray()[0];
            this.tabPane.getSelectionModel().select(selectedTab);
        }

        File file = tabFileMap.get(selectedTab);

        if(file == null){
            consolePane.appendText("file not saved yet in the map\n");
            return;
        }
        String filePath = Paths.get(file.toURI()).toString();
        consolePane.appendText("Compiling: "+filePath+"\n");
        this.doCompilation(filePath);
        consolePane.appendText("Done compiling: "+filePath+"\n");
    }

//    public void handleCompileButton()
//            throws InterruptedException, IOException {
//
////        consolePane.clear();
//
//        Tab selectedTab;
//        System.out.print(this.tabPane.getTabs().size()+"\n");
//
//        // TEMPORARILY SOLVES THE PROBLEM
//        if (this.tabPane.getTabs().size() > 1){
//        // get the corresponding file of the selected tab from the tab pane
//            selectedTab = this.tabPane.getSelectionModel().getSelectedItem();
//        }
//        else{
//            selectedTab = (Tab)this.tabPane.getTabs().toArray()[0];
//            this.tabPane.getSelectionModel().select(selectedTab);
//        }
//
//        File file = tabFileMap.get(selectedTab);
//
//        if(file == null){
//            consolePane.appendText("file not saved yet in the map\n");
//            return;
//        }
//
//        String filePath = Paths.get(file.toURI()).toString();
//
//        consolePane.appendText("Compiling: "+filePath+"\n");
//
//        // creating the process
//        ProcessBuilder pb = new ProcessBuilder("javac", filePath);
//
//        // redirect error to error file
//        File errorFile = new File("src/proj5LianDurstCoyne/ErrorLog.txt");
//        pb.redirectError(errorFile);
//
//        // start the process
//        Process process = pb.start();
//
//        // wait for the process to complete or throw an error
//        int errCode = process.waitFor();
////        System.out.println("Compilation executed, any errors? " + (errCode == 0 ? "No" : "Yes"));
//        // if there is an error, print the error
//        if (errCode != 0) {
//            consolePane.appendText("Error:\n");
//            FileReader fr = new FileReader(errorFile);
//            BufferedReader br = new BufferedReader(fr);
//            String line;
//            while ((line = br.readLine()) != null) {
//                consolePane.appendText(line+"\n");
//            }
//            br.close();
//            fr.close();
//        } else {
//            consolePane.appendText("Success!\n");
//        }
//    }


    public void handleCprunButton()
            throws InterruptedException, IOException {
        // compile first
        handleCompileButton();

//        consolePane.clear();

        // get the corresponding file of the selected tab from the tab pane
        Tab selectedTab;
        System.out.print(this.tabPane.getTabs().size()+"\n");
        // TEMPORARILY SOLVES THE PROBLEM
        if (this.tabPane.getTabs().size() > 1){
            // get the corresponding file of the selected tab from the tab pane
            selectedTab = this.tabPane.getSelectionModel().getSelectedItem();
        }
        else{
            selectedTab = (Tab)this.tabPane.getTabs().toArray()[0];
            this.tabPane.getSelectionModel().select(selectedTab);
        }
        System.out.println("In Toolbar CR: "+selectedTab.getText());
        File file = tabFileMap.get(selectedTab);

        if(file == null){
            System.out.println("file is not in the map");
            return;
        }



        String pathToFile = Paths.get(file.toURI()).toString();
        String[] splitByJava = pathToFile.split(".ja");

        String pathNoJava = splitByJava[0];
        System.out.println(Paths.get(pathNoJava));
        String[] splitBySep = pathNoJava.split("\\\\"); //File.separator for mac
//        System.out.println(Paths.get(pathNoJava);
        String className = splitBySep[splitBySep.length-1];
        String classPath = pathNoJava.split("\\\\"+className)[0];

//        System.out.println("class path: "+classPath);
        // creating the process
        ProcessBuilder pb = new ProcessBuilder("java", "-cp", classPath, className);

        // redirect error to error file
        File errorFile = new File("src/proj5LianDurstCoyne/ErrorLog.txt");
        pb.redirectError(errorFile);

        // start the process
        Process process = pb.start();

//        IOThreadHandler outputHandler = new IOThreadHandler(process.getInputStream());

        // wait for the process to complete or throw an error
        int errCode = process.waitFor();
        System.out.println("Run executed, any errors? " + (errCode == 0 ? "No" : "Yes"));
        // if there is an error, print the error
        if (errCode != 0) {
            FileReader fr = new FileReader(errorFile);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                consolePane.appendText(line);
                consolePane.appendText("\n");
            }
            br.close();
            fr.close(); }

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
        consolePane.appendText(sb.toString());
    }

    public void handleStopButton(String filename) throws IOException {
        // TODO: add some code
    }

    public void bindToolBar() {
        BooleanBinding emptyBinding = Bindings.isEmpty(this.tabPane.getTabs());
        compileButton.disableProperty().bind(emptyBinding);
        cprunButton.disableProperty().bind(emptyBinding);
        stopButton.disableProperty().bind(emptyBinding);
    }

    /**
     * Simple helper method that gets the FXML objects from the
     * main controller for use by other methods in the class.
     */
    public void receiveFXMLElements(Object[] list)
    {
        tabPane = (TabPane) list[0];
        compileButton = (Button) list[5];
        cprunButton = (Button) list[6];
        stopButton = (Button) list[7];
        tabFileMap = (Map<Tab, File>) list[8];
        consolePane = (StyleClassedTextArea) list[9];
    }
}
