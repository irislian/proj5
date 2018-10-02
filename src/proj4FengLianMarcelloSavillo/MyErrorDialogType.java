/*
File: MyErrorDialogType.java
CS361 Project 4
Names: Yi Feng, Iris Lian, Christopher Marcello, and Evan Savillo
Date: 10/02/18
*/

package proj4FengLianMarcelloSavillo;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * enumeration which defines the contents of error alerts of certain types
 *
 * @author Evan Savillo
 */
public enum MyErrorDialogType
{
    SAVING_ERROR("Error",
            "Saving Error",
            "File %s could not be saved!"),
    READING_ERROR("Error",
            "Reading Error",
            "File %s could not be read!"),
    FNF_ERROR("Error",
            "File Not Found Error",
            "File %s could not be found!");

    final String TITLE;
    final String HEADER;
    final String CONTENT;
    MyErrorDialogType(final String title, final String header, final String content)
    {
        TITLE = title;
        HEADER = header;
        CONTENT = content;
    }
}

/**
 * Specialization of Alert class which makes use of the above enum
 * for ease of use.
 *
 * @author Evan Savillo
 * @see javafx.scene.control.Alert
 */
class MyErrorDialog extends Alert
{
    /**
     * Constructor which requires a predefined MyErrorDialogType in order to
     * construct itself with predefined contents.
     *
     * @param type enum of variety MyErrorDialogType
     * @param filename optionally pass the name of the file with which the error
     *                 occurred.
     */
    MyErrorDialog(MyErrorDialogType type, String filename)
    {
        super(AlertType.NONE);

        this.getButtonTypes().add(ButtonType.OK);

        this.setTitle(type.TITLE);
        this.setHeaderText(type.HEADER);
        this.setContentText(String.format(type.CONTENT, filename));
    }

    /**
     * Backup constructor
     */
    MyErrorDialog(MyErrorDialogType type)
    {
        this(type, "in question");
    }
}


///**
// * Convenience class for save decision Dialogs. These pop up when the user
// * attempts to close a file with unsaved changes.
// *
// * @see javafx.scene.control.Alert
// */
//class SaveDecisionDialog extends Alert
//{
//    private static final String SAVE_DECISION_HEADER = "Trying to close %s";
//    private static final String SAVE_DECISION_CONTENT = "You have unsaved changes to this file. " +
//            "Would you like to save them before closing?";
//
//    /**
//     * @param filenameText a String which is intended to be the name of the unsaved file in question,
//     *                     so it can be displayed on the Dialog
//     */
//    SaveDecisionDialog(String filenameText)
//    {
//        super(Alert.AlertType.NONE);
//
//        this.getButtonTypes().add(ButtonType.YES);
//        this.getButtonTypes().add(ButtonType.NO);
//        this.getButtonTypes().add(ButtonType.CANCEL);
//
//        this.setHeaderText(String.format(SAVE_DECISION_HEADER, filenameText));
//        this.setContentText(SAVE_DECISION_CONTENT);
//    }
//}
//
//class MyInteractiveDialog extends Alert
//{
//    public enum Type
//    {
//        SAVE_DECISION
//    }
//
//    public MyInteractiveDialog(Type type)
//    {
//        super(AlertType.NONE);
//    }
//}
