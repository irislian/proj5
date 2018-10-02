/*
File: Controller.java
CS361 Project 4
Names: Yi Feng, Iris Lian, Christopher Marcello, and Evan Savillo
Date: 10/02/18
*/

package proj4FengLianMarcelloSavillo;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import javafx.scene.control.ButtonBar.ButtonData;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

import javafx.stage.Stage;

//TODO: update authors and jdocs


/**
 * Main controller handles all actions evoked by the Main window.
 *
 * @author Liwei Jiang
 * @author Iris Lian
 * @author Tracy Quan
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

    @FXML
    private MenuItem undoMenuItem;
    @FXML
    private MenuItem redoMenuItem;
    @FXML
    private MenuItem cutMenuItem;
    @FXML
    private MenuItem copyMenuItem;
    @FXML
    private MenuItem pasteMenuItem;
    @FXML
    private MenuItem selectAllMenuItem;

    @FXML
    private Stage primaryStage;

    /**
     * a HashMap mapping the tabs and associated files
     */
    private Map<Tab, File> tabFileMap = new HashMap<Tab, File>();

    private int untitledCounter = 1;

    /**
     * This function is called after the FXML fields are populated.
     * Initializes the tab file map with the default tab.
     */
    @FXML
    public void initialize()
    {
        this.handleNewMenuItemAction();
    }

    /**
     * Handles the About button action.
     * Creates a dialog window that displays the authors' names.
     */
    @FXML
    private void handleAboutMenuItemAction()
    {
        // create a information dialog window displaying the About text
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);

        // enable to close the window by clicking on the red cross on the top left corner of the window
        Window window = dialog.getDialogPane().getScene().getWindow();
        window.setOnCloseRequest(event -> window.hide());

        // set the title and the content of the About window
        dialog.setTitle("About");
        dialog.setHeaderText("Authors");
        dialog.setContentText("Yi Feng,\nIris Lian,\nChristopher Marcello,\nand Evan Savillo");

        // enable to resize the About window
        dialog.setResizable(true);
        dialog.showAndWait();
    }


    /**
     * Handles the New button action.
     * Opens a text area embedded in a new tab.
     * Sets the newly opened tab to the the topmost one.
     */
    @FXML
    private void handleNewMenuItemAction()
    {
        Tab newTab = new Tab();
        newTab.setText("untitled" + (untitledCounter++) + ".txt");

        newTab.setContent(new VirtualizedScrollPane<>(ColoredCodeArea.createCodeArea()));

        // set close action (clicking the 'x')
        newTab.setOnCloseRequest(this::handleCloseMenuItemAction);

        // add the new tab to the tab pane
        // set the newly opened tab to the the current (topmost) one
        this.tabPane.getTabs().add(newTab);
        this.tabPane.getSelectionModel().select(newTab);
        this.tabFileMap.put(newTab, null);
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
        // create a fileChooser
        FileChooser fileChooser = new FileChooser();
        File openFile = fileChooser.showOpenDialog(this.primaryStage);

        if (openFile != null)
        {
            // Case: file is already opened in another tab
            // Behavior: switch to that tab
            for (Map.Entry<Tab, File> entry : this.tabFileMap.entrySet())
            {
                if (entry.getValue() != null)
                {
                    if (entry.getValue().equals(openFile))
                    {
                        this.tabPane.getSelectionModel().select(entry.getKey());
                        return;
                    }
                }
            }

            String contentOpenedFile = this.getFileContent(openFile);

            // Case: current text area is in use and shouldn't be overwritten
            // Behavior: generate new tab and open the file there

            Tab newTab = new Tab();
            this.tabPane.getTabs().add(newTab);
            //current tab is now new tab, so getCurrentCodeArea() can be used below
            this.tabPane.getSelectionModel().select(newTab);

            newTab.setText(openFile.getName());
            newTab.setContent(
                    new VirtualizedScrollPane<>(ColoredCodeArea.createCodeArea()));
            this.getCurrentCodeArea().replaceText(contentOpenedFile);
            newTab.setOnCloseRequest(this::handleCloseMenuItemAction);

            this.tabFileMap.put(newTab, openFile);

        }
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
        event.consume();

        if (!this.isTabless())
        {
            this.closeTab(this.getCurrentTab());
        }
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
        // create a fileChooser and add file extension restrictions
        FileChooser fileChooser = new FileChooser();

        // file where the text content is to be saved
        File saveFile = fileChooser.showSaveDialog(this.primaryStage);
        if (saveFile != null)
        {
            // get the selected tab from the tab pane
            Tab selectedTab = this.getCurrentTab();

            // get the text area embedded in the selected tab window
            // save the content of the active text area to the selected file
            CodeArea activeCodeArea = this.getCurrentCodeArea();
            this.saveFile(activeCodeArea.getText(), saveFile);
            // set the title of the tab to the name of the saved file
            selectedTab.setText(saveFile.getName());

            // map the tab and the associated file
            this.tabFileMap.put(selectedTab, saveFile);
        }
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
        // get the selected tab from the tab pane
        Tab selectedTab = this.tabPane.getSelectionModel().getSelectedItem();

        // get the text area embedded in the selected tab window
        CodeArea activeCodeArea = this.getCurrentCodeArea();

        // if the tab content was not loaded from a file nor ever saved to a file
        // save the content of the active text area to the selected file path
        if (this.tabFileMap.get(selectedTab) == null)
        {
            this.handleSaveAsMenuItemAction();
        }
        // if the current text area was loaded from a file or previously saved to a file,
        // then the text area is saved to that file
        else
        {
            this.saveFile(activeCodeArea.getText(), this.tabFileMap.get(selectedTab));
        }
    }

    /**
     * Handles the Exit button action.
     * Exits the program when the Exit button is clicked.
     */
    @FXML
    private void handleExitMenuItemAction()
    {
        ArrayList<Tab> tablist = new ArrayList<>(this.tabFileMap.keySet());
        for (Tab tab : tablist)
        {
            this.tabPane.getSelectionModel().select(tab);
            if (!this.closeTab(tab))
            {
                return;
            }
        }

        Platform.exit();
    }

    /**
     * Handles the Undo button action.
     * Undo the actions in the text area.
     */
    @FXML
    private void handleUndoMenuItemAction()
    {
        this.getCurrentCodeArea().undo();
    }

    /**
     * Handles the Redo button action.
     * Redo the actions in the text area.
     */
    @FXML
    private void handleRedoMenuItemAction()
    {
        this.getCurrentCodeArea().redo();
    }

    /**
     * Handles the Cut button action.
     * Cuts the selected text.
     */
    @FXML
    private void handleCutMenuItemAction()
    {
        this.getCurrentCodeArea().cut();
    }

    /**
     * Handles the Copy button action.
     * Copies the selected text.
     */
    @FXML
    private void handleCopyMenuItemAction()
    {
        this.getCurrentCodeArea().copy();
    }

    /**
     * Handles the Paste button action.
     * Pastes the copied/cut text.
     */
    @FXML
    private void handlePasteMenuItemAction()
    {
        this.getCurrentCodeArea().paste();
    }

    /**
     * Handles the SelectAll button action.
     * Selects all texts in the text area.
     */
    @FXML
    private void handleSelectAllMenuItemAction()
    {
        this.getCurrentCodeArea().selectAll();
    }

    /**
     * Updates the visual status (greyed or not) of items when user
     * click open the File menu
     */
    @FXML
    private void handleFileMenuShowing()
    {
        // Case 1: No tabs
        if (isTabless())
        {
            this.closeMenuItem.setDisable(true);
            this.saveMenuItem.setDisable(true);
            this.saveAsMenuItem.setDisable(true);
        }
    }

    /**
     * Resets the greying out of items when File menu closes
     */
    @FXML
    private void handleFileMenuHidden()
    {
        this.closeMenuItem.setDisable(false);
        this.saveMenuItem.setDisable(false);
        this.saveAsMenuItem.setDisable(false);
    }

    /**
     * Updates the visual status (greyed or not) of items when user
     * click open the Edit menu
     */
    @FXML
    private void handleEditMenuShowing()
    {
        // Case 1: No tabs
        if (this.isTabless())
        {
            this.undoMenuItem.setDisable(true);
            this.redoMenuItem.setDisable(true);
            this.cutMenuItem.setDisable(true);
            this.copyMenuItem.setDisable(true);
            this.pasteMenuItem.setDisable(true);
            this.selectAllMenuItem.setDisable(true);
        }
        else
        {
            // Case 2: No undos
            if (!getCurrentCodeArea().isUndoAvailable())
            {
                this.undoMenuItem.setDisable(true);
            }

            // Case 3: No redos
            if (!getCurrentCodeArea().isRedoAvailable())
            {
                this.redoMenuItem.setDisable(true);
            }
        }
    }

    /**
     * Resets the greying out of items when Edit menu closes
     */
    @FXML
    private void handleEditMenuHidden()
    {
        this.undoMenuItem.setDisable(false);
        this.redoMenuItem.setDisable(false);
        this.cutMenuItem.setDisable(false);
        this.copyMenuItem.setDisable(false);
        this.pasteMenuItem.setDisable(false);
        this.selectAllMenuItem.setDisable(false);
    }

    /**
     * Helper function to save the input string to a specified file.
     *
     * @param content String that is saved to the specified file
     * @param file    File that the input string is saved to
     */
    private void saveFile(String content, File file)
    {
        if (!tabPane.getTabs().isEmpty())
        {
            try
            {
                // open a file, save the content to it, and close it
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(content);
                fileWriter.close();
            }
            catch (IOException e)
            {
                MyErrorDialog myErrorDialog = new MyErrorDialog(
                        MyErrorDialogType.SAVING_ERROR, file.getName());
                myErrorDialog.showAndWait();
            }
        }
    }

    /**
     * Helper function to get the text content of a specified file.
     *
     * @param file File to get the text content from
     * @return the text content of the specified file
     */
    private String getFileContent(File file)
    {
        String content = "";
        try{
            content = new String(Files.readAllBytes(Paths.get(file.toURI())));
        }
        catch (IOException e)
        {
            MyErrorDialog myErrorDialog = new MyErrorDialog(
                    MyErrorDialogType.READING_ERROR, file.getName());
            myErrorDialog.showAndWait();

        }
        return content;
    }


    /**
     * Helper function to check if the content of the specified TextArea
     * has changed from the specified File.
     *
     * @param codeArea TextArea to compare with the the specified File
     * @param file     File to compare with the the specified TextArea
     * @return Boolean indicating if the TextArea has changed from the File
     */
    private boolean ifFileChanged(CodeArea codeArea, File file)
    {
        String codeAreaContent = codeArea.getText();
        String fileContent = this.getFileContent((file));
        return !codeAreaContent.equals(fileContent);
    }


    /**
     * Helper function to handle closing tag action.
     * Removed the tab from the tab file mapping and from the TabPane.
     *
     * @param tab Tab to be closed
     */
    private void removeTab(Tab tab)
    {
        this.tabFileMap.remove(tab);
        this.tabPane.getSelectionModel().selectPrevious();
        this.tabPane.getTabs().remove(tab);
    }


    /**
     * Helper function to handle closing tab action.
     * Checks if the text content within the tab window should be saved.
     *
     * @param tab Tab to be closed
     * @return true if the tab content has not been saved to any file yet,
     * or have been changed since last save.
     */
    private boolean ifSaveFile(Tab tab)
    {
        // check whether the embedded text has been saved or not
        if (this.tabFileMap.get(tab) == null)
        {
            return true;
        }
        // check whether the saved file has been changed or not
        else
        {
            VirtualizedScrollPane vsp = (VirtualizedScrollPane) tab.getContent();
            return this.ifFileChanged((CodeArea) vsp.getContent(),
                    this.tabFileMap.get(tab));
        }
    }

    /**
     * Helper function to handle closing tab action.
     * <p>
     * If the text embedded in the tab window has not been saved yet,
     * or if a saved file has been changed, asks the user if to save
     * the file via a dialog window.
     *
     * @param tab Tab to be closed
     * @return true if the tab is closed successfully; false if the user clicks cancel.
     */
    private boolean closeTab(Tab tab)
    {
        //TODO: autoclose if unsavedfile is empty?
        // if the file has not been saved or has been changed
        // pop up a dialog window asking whether to save the file
        if (this.ifSaveFile(tab))
        {
            //TODO replace with custom dialog?
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Save Changes?");
            alert.setHeaderText("Do you want to save the changes you made?");
            alert.setContentText("Your changes will be lost if you don't save them.");

            ButtonType buttonYes = new ButtonType("Yes", ButtonData.YES);
            ButtonType buttonNo = new ButtonType("No", ButtonData.NO);
            ButtonType buttonCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(buttonYes, buttonNo, buttonCancel);

            Optional<ButtonType> result = alert.showAndWait();
            // if user presses Yes button, save the file and close the tab
            if (result.get() == buttonYes)
            {
                this.handleSaveMenuItemAction();
                this.removeTab(tab);
                return true;
            }
            // if user presses No button, close the tab without saving
            else if (result.get() == buttonNo)
            {
                this.removeTab(tab);
                return true;
            }
            else
            {
                return false;
            }
        }
        // if the file has not been changed, close the tab
        else
        {
            this.removeTab(tab);
            return true;
        }
    }

    /**
     * Simple helper method which returns the currently viewed tab
     *
     * @return currently viewed tab
     */
    private Tab getCurrentTab()
    {
        return this.tabPane.getSelectionModel().getSelectedItem();
    }

    /**
     * Simple helper method which returns the code area  within the currently viewed tab
     *
     * @return current viewed code area
     */
    private CodeArea getCurrentCodeArea()
    {
        Tab selectedTab = this.getCurrentTab();
        VirtualizedScrollPane vsp = (VirtualizedScrollPane) selectedTab.getContent();
        return (CodeArea) vsp.getContent();
    }

    /**
     * Simple helper method
     *
     * @return true if there aren't currently any tabs open, else false
     */
    private boolean isTabless()
    {
        return this.tabPane.getTabs().isEmpty();
    }

    /**
     * Reads in the application's main stage.
     * For use in Filechooser dialogs
     */
    public void setPrimaryStage(Stage primaryStage)
    {
        this.primaryStage = primaryStage;
    }
}