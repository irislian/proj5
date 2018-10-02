/*
File: Controller.java
CS361 Project 4
Names: Yi Feng, Iris Lian, Christopher Marcello, and Evan Savillo
Date: 10/02/18
*/

package proj4FengLianMarcelloSavillo;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;

import javafx.scene.control.ButtonBar.ButtonData;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;


/**
 * Main controller handles all actions evoked by the Main window.
 *
 * @author Liwei Jiang
 * @author Iris Lian
 * @author Tracy Quan
 */
public class Controller
{
    /**
     * Hello button defined in Main.fxml
     */
    @FXML
    private Button helloButton;
    /**
     * Goodbye button defined in Main.fxml
     */
    @FXML
    private Button goodbyeButton;
    /**
     * TabPane defined in Main.fxml
     */

    @FXML
    private TabPane tabPane;
    /**
     * the default untitled tab defined in Main.fxml
     */
    @FXML
    private Tab untitledTab;

    /**
     * a HashMap mapping the tabs and associated files
     */
    private Map<Tab, File> tabFileMap = new HashMap<Tab, File>();

    @FXML
    private MenuItem closeButton;
    @FXML
    private MenuItem saveButton;
    @FXML
    private MenuItem saveAsButton;

    @FXML
    private MenuItem undoButton;
    @FXML
    private MenuItem redoButton;
    @FXML
    private MenuItem cutButton;
    @FXML
    private MenuItem copyButton;
    @FXML
    private MenuItem pasteButton;
    @FXML
    private MenuItem selectButton;




    /**
     * This function is called after the FXML fields are populated.
     * Initializes the tab file map with the default tab.
     */
    @FXML
    public void initialize()
    {
        // put the default tab into the tab file map
        this.tabFileMap.put(this.untitledTab, null);
        // set up the code area
        untitledTab.setContent(new VirtualizedScrollPane<>(ColoredCodeArea.createCodeArea()));
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
            catch (IOException ex)
            {
                System.out.println("Error saving file.");
            }
        }
        else
        {
            System.out.println("There are no tabs!");
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
        String contentString = "";
        try
        {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            try
            {
                while ((line = bufferedReader.readLine()) != null)
                {
                    contentString += line;
                }
                bufferedReader.close();
            }
            catch (Exception ex)
            {
                System.out.println("Unable to read file '" + file.toString() + "'");
            }
        }
        catch (FileNotFoundException ex)
        {
            System.out.println("Unable to open file '" + file.toString() + "'");
        }
        return contentString;
    }


    /**
     * Helper function to check if the content of the specified TextArea
     * has changed from the specified File.
     *
     * @param codeArea TextArea to compare with the the specified File
     * @param file     File to compare with the the specified TextArea
     * @return Boolean indicating if the TextArea has changed from the File
     */
    private Boolean ifFileChanged(CodeArea codeArea, File file)
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
        this.tabPane.getTabs().remove(tab);
    }


    /**
     * Helper function to handle closing tag action.
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
            return this.ifFileChanged((CodeArea) tab.getContent(), this.tabFileMap.get(tab));
        }
    }


    /**
     * Helper function to handle closing tag action.
     * If the text embedded in the tab window has not been saved yet,
     * or if a saved file has been changed, asks the user if to save
     * the file via a dialog window.
     *
     * @param tab Tab to be closed
     * @return true if the tab is closed successfully; false if the user clicks cancel.
     */
    private boolean closeTab(Tab tab)
    {
        // if the file has not been saved or has been changed
        // pop up a dialog window asking whether to save the file
        if (this.ifSaveFile(tab))
        {
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
                this.handleSaveButtonAction();
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
     * Handles the Hello button action.
     * Creates a dialog that takes in an integer between 0 and 255 when Hello
     * button is clicked, and sets the Hello button text to the input number
     * when ok button inside the dialog is clicked.
     */
    @FXML
    private void handleHelloButtonAction()
    {
        // set up the number input dialog
        TextInputDialog dialog = new TextInputDialog("60");
        dialog.setTitle("Give me a number");
        dialog.setHeaderText("Give me an integer from 0 to 255:");

        // when ok button is clicked, set the text of the Hello button to the input number
        final Optional<String> enterValue = dialog.showAndWait();
        enterValue.ifPresent(s -> this.helloButton.setText(s));
    }


    /**
     * Handles the Goodbye button action.
     * Sets the text of Goodbye button to "Yah, sure!" when the Goodbye button is clicked.
     */
    @FXML
    private void handleGoodbyeButtonAction()
    {
        this.goodbyeButton.setText("Yah, sure!");
    }


    /**
     * Handles the About button action.
     * Creates a dialog window that displays the authors' names.
     */
    @FXML
    private void handleAboutButtonAction()
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
    private void handleNewButtonAction()
    {

        Tab newTab = new Tab();
        newTab.setText("untitled");
        newTab.setContent(new VirtualizedScrollPane<>(ColoredCodeArea.createCodeArea()));
        // set close action
        //TODO come back later, may not be necessary
        newTab.setOnClosed(event -> this.closeTab(newTab));

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
    private void handleOpenButtonAction()
    {
        // create a fileChooser and add file extension restrictions
        FileChooser fileChooser = new FileChooser();
        File openFile = fileChooser.showOpenDialog(null);

        if (openFile != null)
        {
            // if the selected file is already open, it cannot be opened twice
            // the tab containing this file becomes the current (topmost) one
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

            String contentString = this.getFileContent(openFile);
            CodeArea untitledCodeArea = (CodeArea) this.untitledTab.getContent();
            // if the default text area is empty, then fill that in with the file to be open
            // this tab becomes the topmost tab
            if (untitledCodeArea.getText().isEmpty())
            {
                untitledCodeArea.replaceText(contentString);
                this.untitledTab.setText(openFile.getName());
                this.tabPane.getSelectionModel().select(this.untitledTab);
                this.tabFileMap.put(this.untitledTab, openFile);
            }
            // if the default text area is not empty, open the file in a new tab window
            else
            {
                CodeArea newCodeArea = new CodeArea();
                newCodeArea.replaceText(contentString);

                Tab newTab = new Tab();
                newTab.setText(openFile.getName());
                newTab.setContent(newCodeArea);
                newTab.setOnClosed(event -> {
                    this.closeTab(newTab);
                });

                this.tabPane.getTabs().add(newTab);
                this.tabPane.getSelectionModel().select(newTab);
                this.tabFileMap.put(newTab, openFile);
            }
        }
    }


    /**
     * Handles the close button action.
     * If the current text area has already been saved to a file, then the current tab is closed.
     * If the current text area has been changed since it was last saved to a file, a dialog
     * appears asking whether you want to save the text before closing it.
     */
    @FXML
    private void handleCloseButtonAction(Event event)
    {
        event.consume();

        if (!tabPane.getTabs().isEmpty())
        {
            Tab selectedTab = this.tabPane.getSelectionModel().getSelectedItem();
            this.closeTab(selectedTab);
        }
        else
        {
            System.out.println("There are no tabs!");
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
    private void handleSaveAsButtonAction()
    {
        // create a fileChooser and add file extension restrictions
        FileChooser fileChooser = new FileChooser();

        // file where the text content is to be saved
        File saveFile = fileChooser.showSaveDialog(null);
        if (saveFile != null)
        {
            // get the selected tab from the tab pane
            Tab selectedTab = this.tabPane.getSelectionModel().getSelectedItem();

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
    private void handleSaveButtonAction()
    {
        // get the selected tab from the tab pane
        Tab selectedTab = this.tabPane.getSelectionModel().getSelectedItem();

        // get the text area embedded in the selected tab window
        CodeArea activeCodeArea = this.getCurrentCodeArea();
        ;

        // if the tab content was not loaded from a file nor ever saved to a file
        // save the content of the active text area to the selected file path
        if (this.tabFileMap.get(selectedTab) == null)
        {
            this.handleSaveAsButtonAction();
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
    private void handleExitButtonAction()
    {
        ArrayList<Tab> tablist = new ArrayList<Tab>(this.tabFileMap.keySet());
        for (Tab currentTab : tablist)
        {
            this.tabPane.getSelectionModel().select(currentTab);
            if (!this.closeTab(currentTab))
            {
                return;
            }
        }
        System.exit(0);
    }


    /**
     * Handles the Undo button action.
     * Undo the actions in the text area.
     */
    @FXML
    private void handleUndoButtonAction()
    {
        // get the text area embedded in the selected tab window
        CodeArea activeCodeArea = this.getCurrentCodeArea();
        // undo the actions in the text area
        activeCodeArea.undo();
    }


    /**
     * Handles the Redo button action.
     * Redo the actions in the text area.
     */
    @FXML
    private void handleRedoButtonAction()
    {
        // get the text area embedded in the selected tab window
        CodeArea activeCodeArea = this.getCurrentCodeArea();
        // redo the actions in the text area
        activeCodeArea.redo();
    }


    /**
     * Handles the Cut button action.
     * Cuts the selected text.
     */
    @FXML
    private void handleCutButtonAction()
    {
        // get the text area embedded in the selected tab window
        CodeArea activeCodeArea = this.getCurrentCodeArea();
        // cut the selected text
        activeCodeArea.cut();
    }


    /**
     * Handles the Copy button action.
     * Copies the selected text.
     */
    @FXML
    private void handleCopyButtonAction()
    {
        // get the text area embedded in the selected tab window
        CodeArea activeCodeArea = this.getCurrentCodeArea();
        // copy the selected text
        activeCodeArea.copy();
    }


    /**
     * Handles the Paste button action.
     * Pastes the copied/cut text.
     */
    @FXML
    private void handlePasteButtonAction()
    {
        // get the code area embedded in the selected tab window
        CodeArea activeCodeArea = this.getCurrentCodeArea();
        // paste the copied/cut text
        activeCodeArea.paste();
    }


    /**
     * Handles the SelectAll button action.
     * Selects all texts in the text area.
     */
    @FXML
    private void handleSelectAllButtonAction()
    {
        // get the code area embedded in the selected tab window
        CodeArea activeCodeArea = this.getCurrentCodeArea();
        // select all texts in the text area
        activeCodeArea.selectAll();
    }

    @FXML
    private void handleFileShowing()
    {
        // Case 1: No tabs
        if (tabPane.getTabs().isEmpty())
        {
            closeButton.setDisable(true);
            saveButton.setDisable(true);
            saveAsButton.setDisable(true);
        }
    }

    @FXML
    private void handleFileHidden()
    {
        closeButton.setDisable(false);
        saveButton.setDisable(false);
        saveAsButton.setDisable(false);
    }

    @FXML
    private void handleEditShowing()
    {
        // Case 1: No tabs
        if (tabPane.getTabs().isEmpty())
        {
            undoButton.setDisable(true);
            redoButton.setDisable(true);
            cutButton.setDisable(true);
            copyButton.setDisable(true);
            pasteButton.setDisable(true);
            selectButton.setDisable(true);
        }
        else
        {
            // Case 2: No undos
            if (!getCurrentCodeArea().isUndoAvailable())
            {
                undoButton.setDisable(true);
            }

            // Case 3: No redos
            if (!getCurrentCodeArea().isRedoAvailable())
            {
                redoButton.setDisable(true);
            }
        }
    }

    @FXML
    private void handleEditHidden()
    {
        undoButton.setDisable(false);
        redoButton.setDisable(false);
        cutButton.setDisable(false);
        copyButton.setDisable(false);
        pasteButton.setDisable(false);
        selectButton.setDisable(false);
    }

    private CodeArea getCurrentCodeArea()
    {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        VirtualizedScrollPane vsp = (VirtualizedScrollPane) selectedTab.getContent();
        return (CodeArea)vsp.getContent();
    }
}