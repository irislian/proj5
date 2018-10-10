/*
File: Controller.java
CS361 Project 5
Names: Iris Lian, Robert Durst, and Michael Coyne
Date: 10/09/18
*/

package proj5LianDurstCoyne;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.lang.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller is the main controller for the application.
 * It itself doesn't handle much. What it does is delegate
 * tasks to either of the sub controllers, FileMenuController or
 * EditMenuController.
 *
 *  @author Yi Feng
 *  @author Iris Lian
 *  @author Chris Marcello
 *  @author Evan Savillo
 */
public class Controller
{
    @FXML
    private TabPane tabPane;

    @FXML
    private MenuItem closeMenuItem;
    @FXML
    private MenuItem saveMenuItem;
    @FXML
    private MenuItem saveAsMenuItem;

    /**
     * Compile button defined in Main.fxml
     */
    @FXML private Button compileButton;
    /**
     * Compile and Run button defined in Main.fxml
     */
    @FXML private Button cprunButton;
    /**
     * Stop button defined in Main.fxml
     */
    @FXML private Button stopButton;

    @FXML private Menu editMenu;

    private Map<Tab, File> tabFileMap = new HashMap<>();

    FileMenuController fileMenuController = new FileMenuController();
    private EditMenuController editMenuController = new EditMenuController();
    private ToolBarController toolBarController = new ToolBarController();

    /**
     * Handles the About button action.
     * Creates a dialog window that displays the authors' names.
     */
    @FXML
    private void handleAboutMenuItemAction()
    {
        fileMenuController.handleAboutMenuItemAction();
    }

    /**
     * Handles the New button action.
     * Opens a text area embedded in a new tab.
     * Sets the newly opened tab to the the topmost one.
     */
    @FXML
    private void handleNewMenuItemAction()
    {
        fileMenuController.handleNewMenuItemAction();
    }

    /**
     * Handles the open button action.
     * Opens a dialog in which the user can select a file to open.
     * If the user chooses a valid file, a new tab is created and the file is loaded into the text area.
     * If the user cancels, the dialog disappears without doing anything.
     */
    @FXML
    private void handleOpenMenuItemAction()
    {
        fileMenuController.handleOpenMenuItemAction();
    }

    /**
     * Handles the close button action.
     * If the current text area has already been saved to a file, then the current tab is closed.
     * If the current text area has been changed since it was last saved to a file, a dialog
     * appears asking whether you want to save the text before closing it.
     */
    @FXML
    private void handleCloseMenuItemAction()
    {
        fileMenuController.handleCloseMenuItemAction();
    }

    /**
     * Handles the Save As button action.
     * Shows a dialog in which the user is asked for the name of the file into
     * which the contents of the current text area are to be saved.
     * If the user enters any legal name for a file and presses the OK button in the dialog,
     * then creates a new text file by that name and write to that file all the current
     * contents of the text area so that those contents can later be reloaded.
     * If the user presses the Cancel button in the dialog, then the dialog closes and no saving occurs.
     */
    @FXML
    private void handleSaveAsMenuItemAction()
    {
        fileMenuController.handleSaveAsMenuItemAction();
    }

    /**
     * Handles the save button action.
     * If a text area was not loaded from a file nor ever saved to a file,
     * behaves the same as the save as button.
     * If the current text area was loaded from a file or previously saved
     * to a file, then the text area is saved to that file.
     */
    @FXML
    private void handleSaveMenuItemAction()
    {
        fileMenuController.handleSaveMenuItemAction();
    }

    /**
     * Handles the Exit button action.
     * Exits the program when the Exit button is clicked.
     */
    @FXML
    public void handleExitMenuItemAction()
    {
        fileMenuController.handleExitMenuItemAction();
    }

    /**
     * Handles the Undo button action.
     * Undo the actions in the text area.
     */
    @FXML
    private void handleUndoMenuItemAction()
    {
        editMenuController.handleUndoMenuItemAction();
    }

    /**
     * Handles the Redo button action.
     * Redo the actions in the text area.
     */
    @FXML
    private void handleRedoMenuItemAction()
    {
        editMenuController.handleRedoMenuItemAction();
    }

    /**
     * Handles the Cut button action.
     * Cuts the selected text.
     */
    @FXML
    private void handleCutMenuItemAction()
    {
        editMenuController.handleCutMenuItemAction();
    }

    /**
     * Handles the Copy button action.
     * Copies the selected text.
     */
    @FXML
    private void handleCopyMenuItemAction()
    {
        editMenuController.handleCopyMenuItemAction();
    }

    /**
     * Handles the Paste button action.
     * Pastes the copied/cut text.
     */
    @FXML
    private void handlePasteMenuItemAction()
    {
        editMenuController.handlePasteMenuItemAction();
    }

    /**
     * Handles the SelectAll button action.
     * Selects all texts in the text area.
     */
    @FXML
    private void handleSelectAllMenuItemAction()
    {
        editMenuController.handleSelectAllMenuItemAction();
    }

    /**
     * This function is called after the FXML fields are populated.
     * Initializes the tab file map with the default tab.
     * and passes necessary items
     */
    public void initialize()
    {
        fileMenuController.receiveFXMLElements(this.passFXMLElements());
        editMenuController.receiveFXMLElements(this.passFXMLElements());
        toolBarController.receiveFXMLElements(this.passFXMLElements());

        this.handleNewMenuItemAction();
        fileMenuController.bindFileMenu();
        editMenuController.bindEditMenu();
    }

    /**
     * Method which creates an array of necessary elements needed by
     * the subcontrollers, which is passed in initialize().
     *
     * @return list containing necessary elements
     */
    private Object[] passFXMLElements()
    {
        return new Object[]{
                this.tabPane,
                this.closeMenuItem,
                this.saveAsMenuItem,
                this.saveMenuItem,
                this.editMenu,
                this.compileButton,
                this.cprunButton,
                this.stopButton,
                this.tabFileMap
        };
    }


    /**
     * Handles the Hello button action.
     * Creates a dialog that takes in an integer between 0 and 255 when Hello
     * button is clicked, and sets the Hello button text to the input number
     * when ok button inside the dialog is clicked.
     */
    @FXML private void handleCompileButtonAction() {
        try {
            toolBarController.handleCompileButton();
        } catch (IOException e1) {

        } catch (InterruptedException e2){

        }
    }

    /**
     * Handles the Goodbye button action.
     * Sets the text of Goodbye button to "Yah, sure!" when the Goodbye button is clicked.
     */
    @FXML private void handleCpRunButtonAction() {
        try {
            toolBarController.handleCprunButton();
        } catch (IOException e) {

        }catch (InterruptedException e2){

        }
    }

    /**
     * Handles the Goodbye button action.
     * Sets the text of Goodbye button to "Yah, sure!" when the Goodbye button is clicked.
     */
    @FXML private void handleStopButtonAction() {
        this.stopButton.setText("Stop!");
    }

//    /**
//     * Simple helper method which gets the file mapped with the given tab
//     * TODO: Modify javadoc header
//     * @param tab Tab which the corresponding file is desired
//     * @return a file
//     */
//    public File getFile(Tab tab){
//        return fileMenuController.getFile(tab);
//    }
}