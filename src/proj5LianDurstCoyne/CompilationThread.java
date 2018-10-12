package proj5LianDurstCoyne;

import javafx.application.Platform;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.io.*;

public class CompilationThread extends Thread {
    private String filePath;
    private ConsolePane consolePane;
    private boolean compiled;

    public CompilationThread(ConsolePane consolePane, String filePath) {
        this.filePath = filePath;
        this.consolePane = consolePane;
        this.compiled = true;
    }

    public boolean getCompileState(){
        return compiled;
    }

    public void run() {
        // creating the process
        ProcessBuilder pb = new ProcessBuilder("javac", filePath);
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
                this.compiled = false;
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
            } else {
                Platform.runLater(
                        () -> this.consolePane.appendText("Done compiling: "+filePath+"\n")
                );
            }
        } catch (IOException e ) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
