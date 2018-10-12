package proj5LianDurstCoyne;

import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.*;
import java.nio.file.Paths;

import java.util.Map;

public class ToolBarController {

    private TabPane tabPane;
    private Map<Tab, File> tabFileMap;
    private ConsolePane consolePane;

    private Button compileButton;
    private Button cpRunButton;
    private Button stopButton;
//    private ToolBar toolBar;

    // initialize to null if no running threads
    private Thread runningThread = null;


    /**
     *
     * @param filePath the path to the selected file
     *
     */
    private void doCompilation(String filePath) {
        CompilationThread thread = new CompilationThread(consolePane, filePath);
        thread.start();
        this.runningThread = thread;
        try{ this.runningThread.join();}
        catch (InterruptedException e){System.out.println("INTERRUPTED IN doCompilation!");}
    }

    /**
     *
     */
    public void handleCompileButton(){
        this.disableCpCpRunButtons(true);
        this.stopButton.setDisable(false);

        consolePane.clear();

        Tab selectedTab;
        if (this.tabPane.getTabs().size() > 1){
            // get the corresponding file of the selected tab from the tab pane
            selectedTab = this.tabPane.getSelectionModel().getSelectedItem();
        }
        else{
            selectedTab = (Tab)this.tabPane.getTabs().toArray()[0];
            this.tabPane.getSelectionModel().select(selectedTab);
        }

        File file = tabFileMap.get(selectedTab);

//        if(file == null){
//            consolePane.appendText("file not saved yet in the map\n");
//            return;
//        }
        String filePath = Paths.get(file.toURI()).toString();
        this.consolePane.appendText("Compiling: "+filePath+"\n");
        this.doCompilation(filePath);

        if (!this.runningThread.isAlive()){
            this.disableCpCpRunButtons(false); //enable compile and compile run
            this.stopButton.setDisable(true);//disable stop
        }
    }


    private void doRun(String classPath, String className, String filePath) {
        CompileRunThread compileRunThread = new CompileRunThread(this.consolePane, filePath, classPath, className);
        compileRunThread.start();
        this.runningThread = compileRunThread;
        try{ this.runningThread.join();}
        catch (InterruptedException e){System.out.println("INTERRUPTED IN doRun!");}
    }


    public void handleCprunButton() {
        this.disableCpCpRunButtons(true);
        this.stopButton.setDisable(false);
//        consolePane.clear();

        // get the corresponding file of the selected tab from the tab pane
        Tab selectedTab;
        if (this.tabPane.getTabs().size() > 1){
            // get the corresponding file of the selected tab from the tab pane
            selectedTab = this.tabPane.getSelectionModel().getSelectedItem();
        }
        else{
            selectedTab = (Tab)this.tabPane.getTabs().toArray()[0];
            this.tabPane.getSelectionModel().select(selectedTab);
        }
        File file = tabFileMap.get(selectedTab);

//        if(file == null){
//            System.out.println("file is not in the map");
//            return;
//        }

        String pathToFile = Paths.get(file.toURI()).toString();
        String[] splitByJava = pathToFile.split(".ja");

        String pathNoJava = splitByJava[0];
        String[] splitBySep = pathNoJava.split("\\\\");
        String className = splitBySep[splitBySep.length-1];
        String classPath = pathNoJava.split("\\\\"+className)[0];

        this.doRun(classPath, className, pathToFile);

        if (!this.runningThread.isAlive()){
            this.disableCpCpRunButtons(false); //enable compile and compile run
            this.stopButton.setDisable(true);//disable stop
        }
    }

    
    public void handleStopButton() {
        if (this.runningThread != null) {
            this.runningThread.interrupt();
            this.runningThread = null;
        }
        System.out.println("STOP IS PRESSED");
    }

    public void disableCpCpRunButtons(boolean disable){
        this.compileButton.setDisable(disable);
        this.cpRunButton.setDisable(disable);
//        this.stopButton.setDisable(!b);
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
        consolePane = (ConsolePane) list[9];
    }
}
