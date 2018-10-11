package proj5LianDurstCoyne;

import javafx.application.Platform;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.io.*;

public class RunThread extends Thread {

    StyleClassedTextArea consolePane;
    String filePath;
    ProcessBuilder pb;
    File errorFile;

    public RunThread(
            StyleClassedTextArea consolePane, String filePath,
            String classPath, String className) {
        this.consolePane = consolePane;
        this.filePath = filePath;
        // creating the process
        this.pb = new ProcessBuilder("java", "-cp", classPath, className);
        // redirect error to error file
        errorFile = new File("src/proj5LianDurstCoyne/ErrorLog.txt");
        pb.redirectError(errorFile);
    }

    public void run() {
        try {
            CompilationThread compileThread = new CompilationThread(consolePane, filePath);
            compileThread.start();
            try{
                compileThread.join();
            }catch(InterruptedException e) {
                System.out.println(e.getMessage());
            }
            // start the process
            Process process = pb.start();
            StringBuilder sb = new StringBuilder();
            // wait for the process to complete or throw an error
            int errCode = process.waitFor();
            Platform.runLater(
                    () -> consolePane.appendText("Run executed, any errors? "
                            + (errCode == 0 ? "No\n" : "Yes\n"))
            );

            // if there is an error, print the error
            if (errCode != 0) {
                FileReader fr = new FileReader(errorFile);
                BufferedReader br = new BufferedReader(fr);
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
                br.close();
                fr.close();
            }
            // print the output
            BufferedReader br = null;
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + System.getProperty("line.separator"));
            }
            Platform.runLater(
                    () -> consolePane.appendText(sb.toString())
            );
            br.close();
        }catch(IOException e) {
            System.out.println(e.getMessage());
        }catch(InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
