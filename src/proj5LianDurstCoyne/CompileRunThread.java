/*
    File: CompileRunThread.java
    CS361 Project 5
    Names: Iris Lian, Robert Durst, and Michael Coyne
    Date: 10/12/18
*/

package proj5LianDurstCoyne;

import javafx.application.Platform;

import java.io.*;


/**
 * This class extends Thread, and is used to compile and then run a .java program
 * in its own thread. This is so the GUI does not
 * freeze while a program is being compiled. The run() method is the most important
 * method in the class; it is executed when the thread is started using the start()
 * method, which is inherited from the Thread class.
 */
public class CompileRunThread extends Thread {

    /**
     * the ConsolePane object where text is to be appended. The text in the consolePane
     * is seen by the user in the IO console.
     */
    private ConsolePane consolePane;

    /**
     * the path to the file which is to be compiled and run.
     */
    private String filePath;
    /**
     * the process which is to be executed by the thread. This process runs the java program with
     * the system command "java" and the "-cp" option to specify the classpath.
     */
    private ProcessBuilder runPB;

    /**
     * @param consolePane the ConsolePane object where text is to be appended, and displayed to
     *                    the user.
     * @param filePath    the path to file which is to be compiled and run.
     * @param classPath   the path to the directory where the .class file is stored.
     * @param className   the name of the class which is to be run.
     */
    public CompileRunThread(
            ConsolePane consolePane, String filePath,
            String classPath, String className) {
        this.consolePane = consolePane;
        this.filePath = filePath;
        // creating the process
        this.runPB = new ProcessBuilder("java", "-cp", classPath, className);
    }

    /**
     * a helper method which is used for printing errors encountered while attempting
     * to run the java program.
     * @param process the process which is executed by the thread.
     * @throws IOException an exception which may occur due to an error in input or output.
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
     * A helper method used for printing output to the consolePane so that the user
     * can view it.
     * @param process the process which is executed by the thread.
     * @throws IOException an exception which may occur due to an error in input or output.
     */
    private void printOutput(Process process) throws IOException{
        InputStreamReader isr = new InputStreamReader(process.getInputStream());
        BufferedReader br = new BufferedReader(isr);
        int line;
        while ((line = br.read()) != '\0') {
            char s =(char) line;
            Platform.runLater(
                    () -> consolePane.appendText((String.valueOf(s)))
            );
        }
        br.close();
        isr.close();
    }

    /**
     * the method which is run when the thread is started using the start() method inherited from
     * Thread. In this method, the .java program is compiled and then run. Any errors which
     * occur during compilation or execution are printed to the consolePane so that the user
     * can view them.
     */
    public void run() {
        CompilationThread compileThread = new CompilationThread(consolePane, filePath);
        try {
            // start compilation in a new thread
            compileThread.start();
            // wait for compilation to finish before moving on to execution
            compileThread.join();
            // if compilation failed, return
            if(!compileThread.getExeState()){
                return;
            }

            // start the process
            Process process = runPB.start();

            // print to console pane as new output is generated
            new Thread(() -> {
                try {
                    printOutput(process);
                } catch(IOException e) {}
            }).start();

            // wait for the process to complete or throw an error
            int errCode = process.waitFor();
            Platform.runLater(
                    () -> consolePane.appendText("Run executed, any errors? "
                            + (errCode == 0 ? "No\n" : "Yes\n"))
            );

            // if there is an error, print the error
            if (errCode != 0) {
                this.printError(process);
            }

        }catch(IOException e) {
            System.out.println("IOException in CompilationThread "+e.getMessage());
        }catch(InterruptedException e) {
            if (compileThread.isAlive()) {
                compileThread.interrupt();
            } else {
                Platform.runLater(
                    () -> this.consolePane.appendText("Execution interrupted.\n")
                );
            }
        }
    }
}
