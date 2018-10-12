package proj5LianDurstCoyne;

import javafx.application.Platform;
import org.fxmisc.richtext.StyleClassedTextArea;

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

    public void run() {
        try {
            CompilationThread compileThread = new CompilationThread(consolePane, filePath);
            System.out.println("In CR thread, before starting compileThread");
            compileThread.start();
            System.out.println("In CR thread, after starting compileThread");
            try{
                compileThread.join();
                System.out.println("In CR thread, after joining compileThread");
            }catch(InterruptedException e) {
                System.out.println(e.getMessage());
            }
            // if compilation failed, return
            if(!compileThread.getCompileState()){
                return;
            }

            // start the process
            Process process = pb.start();
            // wait for the process to complete or throw an error
            int errCode = process.waitFor();
            Platform.runLater(
                    () -> consolePane.appendText("Run executed, any errors? "
                            + (errCode == 0 ? "No\n" : "Yes\n"))
            );

            // if there is an error, print the error
            if (errCode != 0) {
                InputStreamReader isr = new InputStreamReader(process.getErrorStream());
                BufferedReader br = new BufferedReader(isr);
                StringBuilder acc = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    acc.append(line+"\n");}
                Platform.runLater(
                        () -> consolePane.appendText("Error:\n" + acc.toString() + "\n")
                );
                br.close();
                isr.close();
            }
            // print the output
            StringBuilder sb = new StringBuilder();
            InputStreamReader isr = new InputStreamReader(process.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + System.getProperty("line.separator"));
            }
            Platform.runLater(
                    () -> consolePane.appendText(sb.toString())
            );
            br.close();
            isr.close();

        }catch(IOException e) {
            System.out.println(e.getMessage());
        }catch(InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
