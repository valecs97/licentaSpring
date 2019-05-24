package ro.vitoc.licenta.miscellaneous.service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface ProcessService {
    String executeCommand(String[] command) throws IOException;
    String executeCommand(String command) throws IOException;
    void executeCommandFIX(String[] command) throws InterruptedException, TimeoutException, IOException;
    void executeCommandFIX(String command) throws InterruptedException, TimeoutException, IOException;
    int returnCode(String[] command);
    void executeCommandWithoutWait(String command) throws IOException;
    String[] executeInVM(String VMname, String command);
    String getVMInfo(String VMName);
    String getRequest(String urlToRead) throws Exception;
}
