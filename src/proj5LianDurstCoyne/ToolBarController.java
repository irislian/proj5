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
    private Process currentProcess;

    private Button compileButton;
    private Button cpRunButton;
    private Button stopButton;

    // initialize to null if no running threads
    private Thread runningThread = null;

    /**
     *
     * @param filePath
     *
     * TODO: DO A TRY CATCH and this should return a boolean indicating whether it succeeds
     */
    private void doCompilation(String filePath) {
        CompilationThread thread = new CompilationThread(consolePane, filePath);
        thread.start();
        runningThread = thread;
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
        this.consolePane.appendText("Compiling: "+filePath+"\n");
        this.doCompilation(filePath);
        this.consolePane.appendText("Done compiling: "+filePath+"\n");
    }


    private void doRun(String classPath, String className, String filePath) {
        //        System.out.println("class path: "+classPath);

        CompileRunThread compileRunThread = new CompileRunThread(this.consolePane, filePath, classPath, className);
        compileRunThread.start();
        this.runningThread = compileRunThread;
    }


    public void handleCprunButton() {

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
        String[] splitBySep = pathNoJava.split("\\\\");
//        System.out.println(Paths.get(pathNoJava);
        String className = splitBySep[splitBySep.length-1];
        String classPath = pathNoJava.split("\\\\"+className)[0];
        doRun(classPath, className, pathToFile);
    }

    
    public void handleStopButton() {
        if (runningThread != null) {
            runningThread.interrupt();
            runningThread = null;
        }
    }

    public void bindToolBar() {
        BooleanBinding emptyBinding = Bindings.isEmpty(this.tabPane.getTabs());
        compileButton.disableProperty().bind(emptyBinding);
        cpRunButton.disableProperty().bind(emptyBinding);
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
        cpRunButton = (Button) list[6];
        stopButton = (Button) list[7];
        tabFileMap = (Map<Tab, File>) list[8];
        consolePane = (StyleClassedTextArea) list[9];
    }
}
