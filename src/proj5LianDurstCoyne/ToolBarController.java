package proj5LianDurstCoyne;

import com.sun.java.swing.action.FileMenu;
import javafx.scene.control.*;

import java.io.*;
import java.nio.file.Paths;

import java.util.Map;
import java.util.Optional;

public class ToolBarController {

    private TabPane tabPane;
    private Map<Tab, File> tabFileMap;
    private ConsolePane consolePane;

    private Button compileButton;
    private Button cpRunButton;
    private Button stopButton;

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
    }

    /**
     *
     */
    public void handleCompileButton(FileMenuController fmCtrl){
        this.disableEnableButtons(true);

        boolean userCancelled = maybeSave(
                this.tabPane.getSelectionModel().getSelectedItem(), fmCtrl);
        if (userCancelled) {
            this.disableEnableButtons(false);
            return;
        }

        //consolePane.clear();

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
        String filePath = Paths.get(file.toURI()).toString();
        this.consolePane.appendText("Compiling: "+filePath+"\n");
        this.doCompilation(filePath);

        new Thread(() -> {
            while (runningThread != null && runningThread.isAlive()) {}
            disableEnableButtons(false);
        }).start();
    }


    private void doRun(String classPath, String className, String filePath) {
        CompileRunThread compileRunThread = new CompileRunThread(this.consolePane, filePath, classPath, className);
        compileRunThread.start();
        this.runningThread = compileRunThread;
    }


    public void handleCprunButton(FileMenuController fmCtrl) {
        this.disableEnableButtons(true);

        boolean userCancelled = maybeSave(
                this.tabPane.getSelectionModel().getSelectedItem(), fmCtrl);
        if (userCancelled) {
            this.disableEnableButtons(false);
            return;
        }

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

        String pathToFile = Paths.get(file.toURI()).toString();
        String[] splitByJava = pathToFile.split(".ja");

        String pathNoJava = splitByJava[0];
        String[] splitBySep = pathNoJava.split(File.separator);
        String className = splitBySep[splitBySep.length-1];
        String classPath = pathNoJava.split(File.separator+className)[0];

        this.doRun(classPath, className, pathToFile);

        new Thread(() -> {
            while (runningThread != null && runningThread.isAlive()) {}
            disableEnableButtons(false);
        }).start();

    }

    // returns a boolean indicating whether the user canceled the dialog.
    // yes means the user canceled, no means the user did not cancel
    // also, saves the file if the user indicated that they wish to.
    private boolean maybeSave(Tab tab, FileMenuController fmCtrl) {
        if (!fmCtrl.tabNeedsSaving(tab)) { return false;}
        int userChoice = askIfUserWantsSave();
        // user did not cancel
        if(userChoice == 1 | userChoice == 0) {
            this.tabPane.getSelectionModel().select(tab);
            if (userChoice == 1) { fmCtrl.handleSaveMenuItemAction(); }
            return false;
        }
        //user cancelled
        return true;
    }

    private int askIfUserWantsSave() {
        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Want to save this file before compiling?",
                ButtonType.YES,
                ButtonType.NO,
                ButtonType.CANCEL
        );
        alert.setTitle("Alert");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.YES) {
            return 1;
        }else if(result.get() == ButtonType.NO) {
            return 0;
        }else{
            return -1;
        }
    }

    
    public void handleStopButton() {
        if (this.runningThread != null) {
            this.runningThread.interrupt();
            this.runningThread = null;
            disableEnableButtons(false);
        }
    }

    public void disableEnableButtons(boolean disable){
        this.compileButton.setDisable(disable);
        this.cpRunButton.setDisable(disable);
        this.stopButton.setDisable(!disable);
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
