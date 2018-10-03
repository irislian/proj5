package proj4FengLianMarcelloSavillo;

import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;


class EditMenuController
{
    private TabPane tabPane;

    private MenuItem undoMenuItem;
    private MenuItem redoMenuItem;
    private MenuItem cutMenuItem;
    private MenuItem copyMenuItem;
    private MenuItem pasteMenuItem;
    private MenuItem selectAllMenuItem;

    /**
     * Handles the Undo button action.
     * Undo the actions in the text area.
     */
    void handleUndoMenuItemAction()
    {
        this.getCurrentCodeArea().undo();
    }

    /**
     * Handles the Redo button action.
     * Redo the actions in the text area.
     */
    void handleRedoMenuItemAction()
    {
        this.getCurrentCodeArea().redo();
    }

    /**
     * Handles the Cut button action.
     * Cuts the selected text.
     */
    void handleCutMenuItemAction()
    {
        this.getCurrentCodeArea().cut();
    }

    /**
     * Handles the Copy button action.
     * Copies the selected text.
     */
    void handleCopyMenuItemAction()
    {
        this.getCurrentCodeArea().copy();
    }

    /**
     * Handles the Paste button action.
     * Pastes the copied/cut text.
     */
    void handlePasteMenuItemAction()
    {
        this.getCurrentCodeArea().paste();
    }

    /**
     * Handles the SelectAll button action.
     * Selects all texts in the text area.
     */
    void handleSelectAllMenuItemAction()
    {
        this.getCurrentCodeArea().selectAll();
    }

    /**
     * Updates the visual status (greyed or not) of items when user
     * click open the Edit menu
     */
    void handleEditMenuShowing()
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
    void handleEditMenuHidden()
    {
        this.undoMenuItem.setDisable(false);
        this.redoMenuItem.setDisable(false);
        this.cutMenuItem.setDisable(false);
        this.copyMenuItem.setDisable(false);
        this.pasteMenuItem.setDisable(false);
        this.selectAllMenuItem.setDisable(false);
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

    void recieveFXMLElements(Object[] list)
    {
        tabPane = (TabPane) list[0];
        undoMenuItem = (MenuItem) list[4];
        redoMenuItem = (MenuItem) list[5];
        cutMenuItem = (MenuItem) list[6];
        copyMenuItem = (MenuItem) list[7];
        pasteMenuItem = (MenuItem) list[8];
        selectAllMenuItem = (MenuItem) list[9];
    }
}
