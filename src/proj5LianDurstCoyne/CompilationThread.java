package proj5LianDurstCoyne;

import javafx.application.Platform;

import java.io.*;

public class CompilationThread extends Thread {
    private ConsolePane consolePane;
    private String filePath;
    private ProcessBuilder pb;
    private boolean succeed;

    public CompilationThread(ConsolePane consolePane, String filePath) {
        this.consolePane = consolePane;
        this.filePath = filePath;
        // creating the process
        this.pb = new ProcessBuilder("javac", filePath);
        this.succeed = true;
    }

    public boolean getExeState(){
        return succeed;
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
