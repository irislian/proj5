package proj5LianDurstCoyne;

import javafx.application.Platform;

import java.io.*;

public class CompileRunThread extends Thread {
    private ConsolePane consolePane;
    private String filePath;
    private ProcessBuilder pb;

    public CompileRunThread(
            ConsolePane consolePane, String filePath,
            String classPath, String className) {
        this.consolePane = consolePane;
        this.filePath = filePath;
        // creating the process
        this.pb = new ProcessBuilder("java", "-cp", classPath, className);
    }

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

    private void printOutput(Process process) throws IOException{
        StringBuilder sb = new StringBuilder();
        InputStreamReader isr = new InputStreamReader(process.getInputStream());
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
            String s = line + System.getProperty("line.separator");
            Platform.runLater(
                    () -> consolePane.appendText(s)
            );
        }
        br.close();
        isr.close();
    }

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
            Process process = pb.start();

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
            System.out.println(e.getMessage());
        }catch(InterruptedException e) {
            if (compileThread.isAlive()) {
                compileThread.interrupt();
            } else {
                Platform.runLater(
                    () -> this.consolePane.appendText("Execution interrupted.")
                );
            }
        }
    }
}
