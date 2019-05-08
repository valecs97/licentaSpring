package ro.vitoc.licenta.miscellaneous.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ro.vitoc.licenta.core.facade.SimpleProjectFacadeImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

@Service
public class ProcessServiceImpl implements ProcessService {
    private static final Logger log = LoggerFactory.getLogger(ProcessServiceImpl.class);

    private String getVMsCommand = "docker-machine ls -f \"{{.Name}} {{.State}} {{.URL}}\"";

    private String executeCommand(String[] command, Boolean wait) throws IOException {
        Process process;
        try {
            if (command.length == 1)
                process = Runtime.getRuntime().exec(command[0]);
            else
                process = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            log.trace("executeCommand failed: Execute command failed with error : " + e.getMessage());
            return null;
        }
        if (!wait)
            return null;
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            log.trace("executeCommand failed: Wait for process failed with error : " + e.getMessage());
            return null;
        }
        InputStream inputStream = process.getInputStream();
        InputStream outputStream = process.getErrorStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        BufferedReader reader1 = new BufferedReader(new InputStreamReader(outputStream));
        String line = "";
        String res = "";
        while ((line = reader.readLine()) != null) {
            res += line + "\n";

        }
        while ((line = reader1.readLine()) != null) {
            res += line + "\n";
        }
        return res;
    }

    @Override
    public String executeCommand(String[] command) throws IOException {
        return executeCommand(command,true);
    }

    @Override
    public String executeCommand(String command) throws IOException {
        return executeCommand(new String[]{command});
    }

    @Override
    public int returnCode(String[] command) {
        Process process;
        try {
            process = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            return 2;
        }
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            return 3;
        }
        return process.exitValue();
    }

    @Override
    public void executeCommandWithoutWait(String command) throws IOException {
        executeCommand(new String[]{command},false);
    }

    @Override
    public String[] executeInVM(String VMname, String command) {
        return new String[] {"docker-machine","ssh",VMname,"\"" + command + "\""};
    }

    @Override
    public String getVMInfo(String VMName) {
        String[] vms = new String[0];
        try {
            vms = executeCommand(getVMsCommand).split("\n");
        } catch (IOException e) {
            log.trace("getDefaultVMInfo failed with error : " + e.getMessage());
        }
        return Arrays.stream(vms).filter(elem -> elem.split(" ")[0].equals(VMName)).findAny().orElse(null);
    }

    public static String getDefaultVMInfoGlobal(String VMName){
        ProcessService processService = new ProcessServiceImpl();
        return processService.getVMInfo(VMName);
    }
}
