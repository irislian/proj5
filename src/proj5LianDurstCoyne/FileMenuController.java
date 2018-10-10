package proj5LianDurstCoyne;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * FileMenuController contains the handler methods for the MenuItems
 * found in the file menu of our IDE. It also contains a series of helper
 * methods for these handlers, dealing with saving and loading files
 * as well as closing/opening tabs. The FileMenuController has no
 * direct link to the FXML, and relies on the Controller to act as an
 * intermediary.
 *
 *  @author Yi Feng
 *  @author Iris Lian
 *  @author Chris Marcello
 *  @author Evan Savillo
 */
class FileMenuController
{
    private TabPane tabPane;

    private MenuItem closeMenuItem;
    private MenuItem saveMenuItem;
    private MenuItem saveAsMenuItem;

    private Stage primaryStage;

    /**
     * a HashMap mapping the tabs and associated files
     */
    private Map<Tab, File> tabFileMap;

    private int untitledCounter = 1;

    public void bindFileMenu() {
        BooleanBinding emptyBinding = Bindings.isEmpty(tabPane.getTabs());
        closeMenuItem.disableProperty().bind(emptyBinding);
        saveMenuItem.disableProperty().bind(emptyBinding);
        saveAsMenuItem.disableProperty().bind(emptyBinding);
    }
    /**
     * Handles the About button action.
     * Creates a dialog window that displays the authors' names.
     */
    public void handleAboutMenuItemAction()
    {
        // create a information dialog window displaying the About text
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);

        // enable to close the window by clicking on the x on the top left corner of
        // the window
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
    public void handleNewMenuItemAction()
    {
        Tab newTab = this.createNewTab();

        newTab.setText("untitled" + (untitledCounter++) + ".txt");
        this.tabFileMap.put(newTab, null);
    }

    /**
     * Handles the open button action.
     * Opens a dialog in which the user can select a file to open.
     * If the user chooses a valid file, a new tab is created and the file is loaded into the text area.
     * If the user cancels, the dialog disappears without doing anything.
     */
    public void handleOpenMenuItemAction()
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

            Tab newTab = this.createNewTab();

            newTab.setText(openFile.getName());
            this.tabFileMap.put(newTab, openFile);
            this.getCurrentCodeArea().replaceText(contentOpenedFile);
            this.tabPane.getSelectionModel().select(newTab);
        }
    }

    /**
     * Handles the close button action.
     * If the current text area has already been saved to a file, then the current tab is closed.
     * If the current text area has been changed since it was last saved to a file, a dialog
     * appears asking whether you want to save the text before closing it.
     */
    public void handleCloseMenuItemAction() {

        Tab selectedTab = this.tabPane.getSelectionModel().getSelectedItem();
        System.out.println(selectedTab.getText());
        this.closeTab(selectedTab);
        System.out.println(this.tabPane.getSelectionModel().getSelectedItem().getText());
    }

    /**
     * Handles the Save As button action.
     * Shows a dialog in which the user is asked for the name of the file into
     * which the contents of the current text area are to be saved.
     * If the user enters any legal name for a file and presses the OK button in the dialog,
     * then creates a new text file by that name and write to that file all the current
     * contents of the text area so that those contents can later be reloaded.
     * If the user presses the Cancel button in the dialog, then the dialog closes and no saving occurs.
     *
     * @return true if the user chooses to save the file (i.e., clicks OK), false if canceled
     */
    public boolean handleSaveAsMenuItemAction()
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
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Handles the save button action.
     * If a text area was not loaded from a file nor ever saved to a file,
     * behaves the same as the save as button.
     * If the current text area was loaded from a file or previously saved
     * to a file, then the text area is saved to that file.
     *
     * @return true if the tab is saved, false if the user clicks CANCEL in the saveDialog
     */
    public boolean handleSaveMenuItemAction()
    {
        // get the selected tab from the tab pane
        Tab selectedTab = this.tabPane.getSelectionModel().getSelectedItem();

        // get the text area embedded in the selected tab window
        CodeArea activeCodeArea = this.getCurrentCodeArea();

        // if the tab content was not loaded from a file nor ever saved to a file
        // save the content of the active text area to the selected file path
        if (this.tabFileMap.get(selectedTab) == null)
        {
            return this.handleSaveAsMenuItemAction();
        }
        // if the current text area was loaded from a file or previously saved to a file,
        // then the text area is saved to that file
        else
        {
            this.saveFile(activeCodeArea.getText(), this.tabFileMap.get(selectedTab));
            return true;
        }
    }

    /**
     * Handles the Exit button action.
     * Exits the program when the Exit button is clicked.
     */
    public void handleExitMenuItemAction()
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
     * Helper function to save the input string to a specified file.
     *
     * @param content String that is saved to the specified file
     * @param file    File that the input string is saved to
     */
    private boolean saveFile(String content, File file)
    {
        if (!tabPane.getTabs().isEmpty())
        {
            try
            {
                // open a file, save the content to it, and close it
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(content);
                fileWriter.close();
                return true;
            }
            catch (IOException e)
            {
                UserErrorDialog userErrorDialog = new UserErrorDialog(
                        UserErrorDialog.ErrorType.SAVING_ERROR, file.getName());
                userErrorDialog.showAndWait();
                return false;
            }
        }
        return false;
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
        try
        {
            content = new String(Files.readAllBytes(Paths.get(file.toURI())));
        }
        catch (IOException e)
        {
            UserErrorDialog userErrorDialog = new UserErrorDialog(
                    UserErrorDialog.ErrorType.READING_ERROR, file.getName());
            userErrorDialog.showAndWait();

        }
        return content;
    }


    /**
     * Helper function to check if the content of the specified CodeArea
     * has changed from the specified File.
     *
     * @param codeArea CodeArea to compare with the the specified File
     * @param file     File to compare with the the specified CodeArea
     * @return boolean indicating if the CodeArea has changed from the File
     */
    private boolean contentsMatch(CodeArea codeArea, File file)
    {
        String codeAreaContent = codeArea.getText();
        String fileContent = this.getFileContent((file));
        return codeAreaContent.equals(fileContent);
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
     * Helper function to handle closing tab action.
     * Checks if the text content within the tab window should be saved.
     *
     * @param tab Tab to be closed
     * @return true if the tab content has not been saved to any file yet,
     * or have been changed since last save.
     */
    private boolean tabNeedsSaving(Tab tab)
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
            return !this.contentsMatch((CodeArea) vsp.getContent(),
                    this.tabFileMap.get(tab));
        }
    }

    /**
     * Helper function to handle closing tab action.
     *
     * If the text embedded in the tab window has not been saved yet,
     * or if a saved file has been changed, asks the user if to save
     * the file via a dialog window.
     *
     * @param tab Tab to be closed
     * @return true if the tab is closed successfully; false if the user clicks cancel.
     */
    private boolean closeTab(Tab tab)
    {

        if (tab == null) {
            return true;
        }
        // if the file has not been saved or has been changed
        // pop up a dialog window asking whether to save the file
        if (this.tabNeedsSaving(tab))
        {
            Alert alert = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Want to save before exit?",
                    ButtonType.YES,
                    ButtonType.NO,
                    ButtonType.CANCEL
            );
            alert.setTitle("Alert");


            Optional<ButtonType> result = alert.showAndWait();
            // if user presses Yes button, save the file and close the tab
            if (result.get() == ButtonType.YES)
            {
                if(this.handleSaveMenuItemAction()) {
                    this.removeTab(tab);
                    return true;
                }
                else{
                    return false;
                }
            }
            // if user presses No button, close the tab without saving
            else if (result.get() == ButtonType.NO)
            {
                this.removeTab(tab);
                return true;
            }
            else if (result.get() == ButtonType.CANCEL)
            {
                return false;
            }
            return true;
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
     * Simple helper method which creates a new tab and adds it to the tab pane
     * TODO: Modify javadoc header
     * @return a new tab
     */
    private Tab createNewTab(){
        Tab newTab = new Tab();
        newTab.setContent(new VirtualizedScrollPane<>(new ColoredCodeArea()));

        // set close action (clicking the 'x')
        newTab.setOnCloseRequest(event -> {
            event.consume();
//            System.out.println("In Filemenu: "+this.tabPane.getSelectionModel().getSelectedItem().getText());
            closeTab(newTab);
//            System.out.println("In Filemenu: "+this.tabPane.getSelectionModel().getSelectedItem().getText());
        });

        // add the new tab to the tab pane
        // set the newly opened tab to the the current (topmost) one
        this.tabPane.getTabs().add(newTab);
        this.tabPane.getSelectionModel().select(newTab);

        return newTab;
    }

    /**
     * Reads in the application's main stage.
     * For use in Filechooser dialogs
     */
    public void setPrimaryStage(Stage primaryStage)
    {
        this.primaryStage = primaryStage;
    }

//    /**
//     * Simple helper method which gets the file mapped with the given tab
//     * TODO: Modify javadoc header
//     * @param tab Tab which the corresponding file is desired
//     * @return a file
//     */
//    public File getFile(Tab tab){
//        return this.tabFileMap.get(tab);
//    }

    /** 
     * Simple helper method that gets the FXML objects from the
     * main controller for use by other methods in the class.
     */
    public void receiveFXMLElements(Object[] list)
    {
        tabPane = (TabPane) list[0];
        closeMenuItem = (MenuItem) list[1];
        saveMenuItem = (MenuItem) list[2];
        saveAsMenuItem = (MenuItem) list[3];
        tabFileMap = (Map<Tab, File>) list[8];
    }
}
