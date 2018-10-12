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

    private CompilationThread compile(){
        CompilationThread compileThread = new CompilationThread(consolePane, filePath);
        compileThread.start();
        try{
            compileThread.join();
        }catch(InterruptedException e) {
            System.out.println(e.getMessage());
        }
        return compileThread;
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
            sb.append(line + System.getProperty("line.separator"));
        }
        Platform.runLater(
                () -> consolePane.appendText(sb.toString())
        );
        br.close();
        isr.close();
    }

    public void run() {
        try {
            CompilationThread compileThread = this.compile();
            // if compilation failed, return
            if(!compileThread.getExeState()){
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
                this.printError(process);
            }
            // print the output
            this.printOutput(process);

        }catch(IOException e) {
            System.out.println(e.getMessage());
        }catch(InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
