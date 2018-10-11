package proj5LianDurstCoyne;

import javafx.application.Platform;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CompilationThread extends Thread {
    Process process;
    int errCode;
    String filePath;
    StyleClassedTextArea consolePane;

    CompilationThread(StyleClassedTextArea consolePane, String filePath) {
        this.filePath = filePath;
        this.consolePane = consolePane;
    }

    public void run() {
        // creating the process
        ProcessBuilder pb = new ProcessBuilder("javac", filePath);
        // redirect error to error file
        File errorFile = new File("src/proj5LianDurstCoyne/ErrorLog.txt");
        pb.redirectError(errorFile);
        // start the process
        try {
            process = pb.start();
            errCode = process.waitFor();
            Platform.runLater(
                    () -> consolePane.appendText("Compilation executed, any errors? "
                            + (errCode == 0 ? "No\n" : "Yes\n"))
            );
            // if there is an error, print the error
            if (errCode != 0) {
                StringBuilder acc = new StringBuilder();
                FileReader fr = new FileReader(errorFile);
                BufferedReader br = new BufferedReader(fr);
                String line;
                while ((line = br.readLine()) != null) {
                    acc.append(line+"\n");
                }
                Platform.runLater(
                        () -> consolePane.appendText("Error:\n +" +acc.toString() + "\n")
                );
                br.close();
                fr.close();
            } else {
                Platform.runLater(
                        () -> consolePane.appendText("Success!\n")
                );
            }
        } catch (IOException e ) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("*********************************");
    }
}
