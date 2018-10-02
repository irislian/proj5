/*
File: Controller.java
CS361 Project 4
Names: Yi Feng, Iris Lian, Christopher Marcello, and Evan Savillo
Date: 10/02/18
*/

package proj4FengLianMarcelloSavillo;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;


public class Controller
{
    public ArrayList<Object> passFXMLElements()
    {
        ArrayList<Object> list = new ArrayList<>();
        list.add(this.tabPane);
        list.add(this.closeMenuItem);
        list.add(this.saveAsMenuItem);
        list.add(this.saveMenuItem);
        list.add(this.undoMenuItem);
        list.add(this.redoMenuItem);
        list.add(this.cutMenuItem);
        list.add(this.copyMenuItem);
        list.add(this.pasteMenuItem);
        list.add(this.selectAllMenuItem);
        list.add(this.primaryStage);

        return list;
    }
    @FXML
    TabPane tabPane;

    @FXML
    MenuItem closeMenuItem;
    @FXML
    MenuItem saveMenuItem;
    @FXML
    MenuItem saveAsMenuItem;

    @FXML
    MenuItem undoMenuItem;
    @FXML
    MenuItem redoMenuItem;
    @FXML
    MenuItem cutMenuItem;
    @FXML
    MenuItem copyMenuItem;
    @FXML
    MenuItem pasteMenuItem;
    @FXML
    MenuItem selectAllMenuItem;

    @FXML
    Stage primaryStage;

    FileMenuController fileMenuController = new FileMenuController();
    EditMenuController editMenuController = new EditMenuController();

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
    private void handleCloseMenuItemAction(Event event)
    {
        fileMenuController.handleCloseMenuItemAction(event);
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
    private void handleExitMenuItemAction()
    {
        fileMenuController.handleExitMenuItemAction();
    }


    /**
     * Updates the visual status (greyed or not) of items when user
     * click open the File menu
     */
    @FXML
    private void handleFileMenuShowing()
    {
        fileMenuController.handleFileMenuShowing();
    }

    /**
     * Resets the greying out of items when File menu closes
     */
    @FXML
    private void handleFileMenuHidden()
    {
        fileMenuController.handleFileMenuHidden();
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
     * Updates the visual status (greyed or not) of items when user
     * click open the Edit menu
     */
    @FXML
    private void handleEditMenuShowing()
    {
        editMenuController.handleEditMenuShowing();
    }

    /**
     * Resets the greying out of items when Edit menu closes
     */
    @FXML
    private void handleEditMenuHidden()
    {
        editMenuController.handleEditMenuHidden();
    }

    /**
     * This function is called after the FXML fields are populated.
     * Initializes the tab file map with the default tab.
     * and passes necessary items
     */
    public void initialize()
    {
        fileMenuController.recieveFXMLElements(this.passFXMLElements());
        editMenuController.recieveFXMLElements(this.passFXMLElements());

        this.handleNewMenuItemAction();
    }
}