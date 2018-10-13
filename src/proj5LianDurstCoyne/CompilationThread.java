/*
    File: CompilationThread.java
    CS361 Project 5
    Names: Iris Lian, Robert Durst, and Michael Coyne
    Date: 10/12/18
*/

package proj5LianDurstCoyne;

import javafx.application.Platform;

import java.io.*;

/**
 * This class extends Thread, and is a class meant exclusively for handling the
 * compilation of .java files within a thread. This is so the GUI does not
 * freeze while a program is being compiled. The run() method is the most important
 * method in the class; it is executed when the thread is started using the start()
 * method, which is inherited from the Thread class.
 */

public class CompilationThread extends Thread {

    /**
     * a ConsolePane object which output text is appended to, so that the text may be
     * displayed in the GUI.
     */
    private ConsolePane consolePane;
    /**
     * the path to the file which is to be compiled.
     */
    private String filePath;

    /**
     * a ProcessBuilder object which executes the operating system command "javac", which
     * compiles a .java file.
     */
    private ProcessBuilder pb;

    /**
     * a boolean value that indicates whether the thread successfully executed.
     */
    private boolean succeed;

    /**
     * When the class is instantiated, a ProcessBuilder is initialized, which
     * is the process to be run by the thread.
     * @param consolePane the consolePane is the output area where text is to be
     *                    displayed
     * @param filePath the filePath is the path to the file which is to be
     *                 compiled.
     */
    public CompilationThread(ConsolePane consolePane, String filePath) {
        this.consolePane = consolePane;
        this.filePath = filePath;
        this.pb = new ProcessBuilder("javac", filePath);
        this.succeed = true;
    }

    /**
     *
     * @return a boolean value which indicates whether the thread successfully executed.
     */
    public boolean getExeState(){
        return succeed;
    }

    /**
     * a helper method for printing errors to the consolePane.
     * @param process the process which is being executed by the thread.
     * @throws IOException an exception that indicates that there was an error
     *                     with input.
     */
    private void printError(Process process)throws IOException{
        InputStreamReader isr = new InputStreamReader(process.getErrorStream());
        BufferedReader br = new BufferedReader(isr);
        StringBuilder acc = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            acc.append(line+"\n");}
        Platform.runLater(
                () -> consolePane.appendText("Error:\n" + acc.toString() + "\n")
        );
        isr.close();
        br.close();
    }

    /**
     * This method is executed when the thread is started using the start() method.
     * Within this method:
     * 1. The .java file is compiled.
     * 2. If any errors occurred during compilation, they are printed to the consolePane
     *    so that the can see them.
     */
    public void run() {
        // start the process
        try {
            Process process = pb.start();
            int errCode = process.waitFor();
            Platform.runLater(
                    () -> consolePane.appendText("Compilation executed, any errors? "
                            + (errCode == 0 ? "No\n" : "Yes\n"))
            );
            // if there is an error, print the error
            if (errCode != 0) {
                this.succeed = false;
                this.printError(process);
            } else {
                Platform.runLater(
                        () -> this.consolePane.appendText("Done compiling: "+filePath+"\n")
                );
            }
        } catch (IOException e ) {
            System.out.println("IOException in CompilationThread "+e.getMessage());
        } catch (InterruptedException e) {
            Platform.runLater(
                        () -> this.consolePane.appendText("Compilation interrupted.\n")
                );
        }
    }
}
