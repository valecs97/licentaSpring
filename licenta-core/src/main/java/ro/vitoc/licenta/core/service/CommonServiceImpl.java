package ro.vitoc.licenta.core.service;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ro.vitoc.licenta.core.model.BaseProject;
import ro.vitoc.licenta.core.model.SimpleProject;
import ro.vitoc.licenta.core.model.WebMicroService;
import ro.vitoc.licenta.miscellaneous.service.ProcessService;

import java.io.*;
import java.util.Arrays;
import java.util.Objects;

@Service
public class CommonServiceImpl implements CommonService {
    private static final Logger log = LoggerFactory.getLogger(CommonServiceImpl.class);

    @Value("${docker.username}")
    private String dockerUser;

    @Value("${docker.password}")
    private String dockerPass;

    @Autowired
    private ProcessService processService;

    private static String installPipReqs = "pip install pipreqs";
    private static String createReqs = "pipreqs";

    @Override
    public Integer createDockerFile(BaseProject project, Boolean web) throws IOException {
        log.trace("createDockerFile: location={},lang={}", project.getLocation(), project.getLang());
        FileWriter fileWriter = new FileWriter(project.getLocation() + "\\Dockerfile");
        String language = "FROM ";

        if (project.getLang().contains("python")) {
            if (project.getLang().contains("2.7"))
                language += "python:2.7-slim\n";
            else
                language += "python:3\n";
            fileWriter.write(language);
            fileWriter.write("WORKDIR /app\nCOPY . /app\n");
            fileWriter.write("RUN pip install --trusted-host pypi.python.org -r requirements.txt\n");
            if (web)
                fileWriter.write("EXPOSE " + ((WebMicroService) project).getPortIn() + "\n");
            fileWriter.write("CMD python " + project.getMain());
        } else
            return 1;
        fileWriter.close();
        return 0;
    }

    @Override
    public Integer createDockerFile(SimpleProject project) throws IOException {
        log.trace("createDockerFile: location={},lang={},parameters={}", project.getLocation(), project.getLang(), project.getParameters());
        FileWriter fileWriter = new FileWriter(project.getLocation() + "\\Dockerfile");
        String language = "FROM ";

        if (project.getLang().contains("python")) {
            if (project.getLang().contains("2.7"))
                language += "python:2.7-slim\n";
            else
                language += "python:3\n";
            fileWriter.write(language);
            fileWriter.write("WORKDIR /app\nCOPY . /app\n");
            fileWriter.write("RUN pip install --trusted-host pypi.python.org -r requirements.txt\n");
            for (int i = 0; i < project.getParameters(); i++) {
                fileWriter.write("ENV ARG" + i + " Default" + i + "\n");
            }
            fileWriter.write("CMD python " + project.getMain());
            for (int i = 0; i < project.getParameters(); i++) {
                fileWriter.write(" $ARG" + i);
            }
        } else
            return 1;
        fileWriter.close();
        return 0;
    }

    @Override
    public void createRequirementsFile(BaseProject project) throws IOException {
        log.trace("createRequirementsFile: location={}, req={}", project.getLocation());
        try {
            log.trace(processService.executeCommand(installPipReqs));
            //.replace("\\","/").replace(" ","\\ ")
            log.trace(processService.executeCommand(createReqs + " \"" + project.getLocation() + "\""));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Boolean preCheckProject(BaseProject project) {
        log.trace("preCheckProject project={}", project.getName());
        if (!project.getLang().contains("python"))
            return false;
        return true;
    }

    @Override
    public Boolean postCheckProject(BaseProject project) {
        log.trace("postCheckProject project={}", project.getName());
        if (!new File(project.getLocation() + "\\" + project.getMain()).exists()) {
            deleteFolder(project.getLocation());
            return false;
        }
        return true;
    }

    private void deleteFolder(String folder) {
        try {
            FileUtils.deleteDirectory(new File(folder));
        } catch (IOException e) {
            log.trace("Could not delete folder !");
            e.printStackTrace();
        }
    }
}
