package proj5LianDurstCoyne;

import javafx.scene.control.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;


/**
 * This class controls the toolbar; the toolbar consists of buttons used for compiling  a java program,
 * compiling and running java program, and stopping the compilation/execution of a java program.
 */
public class ToolBarController {

    /**
     * the TabPane contains Tab objects. Tab objects contain CodeAreas, and CodeAreas
     * contain the text which the user has typed or loaded.
     */
    private TabPane tabPane;

    /**
     * a Map which maps from Tab objects to File objects. Given a Tab,
     * this can be used to find the associated File.
     */
    private Map<Tab, File> tabFileMap;
    /**
     * the consolePane is the "console" area where program output and error messages are
     * displayed, and where the user can enter input.
     */
    private ConsolePane consolePane;
    private Button compileButton;
    private Button cpRunButton;
    private Button stopButton;

    // initialize to null if no running threads
    private Thread runningThread = null;


    /**
     * a helper method which compiles a java program.
     * @param filePath the path to the file that is to be compiled.
     *
     */
    private void doCompilation(String filePath) {
        CompilationThread thread = new CompilationThread(consolePane, filePath);
        thread.start();
        this.runningThread = thread;
    }

    /**
     * this method is called when the compile button is pressed. When this method is called,
     * it performs several steps:
     *  1. The compile and compileAndRun buttons are disabled, and the stop button is enabled.
     *  2. The user is asked whether they wish to save the file which they are to compile,
     *     if it has been edited since the last save. If the user selects "cancel", the
     *     method returns and no further action is taken.
     *  3. If the user selected yes, the file is saved.
     *  4. The file is compiled.
     * @param fmCtrl the FileMenuController which is instantiated within the Controller class.
     */
    public void handleCompileButton(FileMenuController fmCtrl){
        this.disableEnableButtons(true);

        boolean userCancelled = maybeSave(
                this.tabPane.getSelectionModel().getSelectedItem(), fmCtrl);
        if (userCancelled) {
            this.disableEnableButtons(false);
            return;
        }

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

    /**
     * a helper method which is used to run a java program in its own thread.
     * @param classPath the path to the directory where the .class files are stored.
     * @param className the name of the class to be compiled and run.
     * @param filePath  the path to the file which is to be compiled and run.
     */
    private void doRun(String classPath, String className, String filePath) {
        CompileRunThread compileRunThread = new CompileRunThread(this.consolePane, filePath, classPath, className);
        compileRunThread.start();
        this.runningThread = compileRunThread;
    }

    /**
     * this method is called when the Compile&Run button is pressed. When this method is called,
     * it performs several steps:
     *   1. The compile and compileAndRun buttons are disabled, and the stop button is enabled.
     *   2. The user is asked whether they wish to save the file which they are to compile,
     *   3. if it has been edited since the last save. If the user selects "cancel", the
     *      method returns and no further action is taken.
     *   4. If the user selected yes, the file is saved.
     *   5. The file is compiled, and then run.
     * @param fmCtrl
     */
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

    /**
     * this method asks the user if they wish to save a file, and then acts accordingly.
     * @param tab the tab which may or may not be saved, depending on the user's selection.
     * @param fmCtrl the FileMenuController that is instantiated within the Controller class.
     * @return a boolean value that indicates whether the user wishes to cancel. true indicates
     *         that they do wish to cancel, false indicates that the user does not.
     */
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

    /**
     * this method creates an Alert which asks the user if they wish to save the file before compiling.
     * @return an integer which represents the choice that the user selected. 1 = yes, 0 = no, -1 = cancel.
     */
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

    /**
     * this method is called when the "stop" button is pressed. If a thread currently exists,
     * it is interrupted. Also, the compile buttons are enabled, and the stop button is disabled.
     */
    public void handleStopButton() {
        if (this.runningThread != null) {
            this.runningThread.interrupt();
            this.runningThread = null;
            disableEnableButtons(false);
        }
    }

    /**
     * a method which enables or disables the compile and stop buttons. given a true value,
     * the compile buttons are enabled and stop button is disabled. given a false value,
     * the inverse occurs.
     * @param disable a boolean value which indicates if the compile buttons are to be enabled
     *                or disabled. true means they are to be enabled. 
     */
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
