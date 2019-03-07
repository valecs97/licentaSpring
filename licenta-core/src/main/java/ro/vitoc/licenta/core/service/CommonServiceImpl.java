package ro.vitoc.licenta.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ro.vitoc.licenta.core.model.BaseProject;

import java.io.*;

@Service
public class CommonServiceImpl implements CommonService {
    private static final Logger log = LoggerFactory.getLogger(CommonServiceImpl.class);

    @Override
    public Integer createDockerFile(BaseProject project) throws IOException {
        log.trace("createDockerFile: location={},lang={},parameters={}",project.getLocation(),project.getLang(),project.getParameters());
        FileWriter fileWriter = new FileWriter(project.getLocation() + "\\Dockerfile");
        String language = "FROM ";

        if (project.getLang().contains("python")){
            if (project.getLang().contains("2.7"))
                language += "python:2.7-slim\n";
            else
                language += "python:3\n";
            fileWriter.write(language);
            fileWriter.write("WORKDIR /app\nCOPY . /app\n");
            fileWriter.write("RUN pip install --trusted-host pypi.python.org -r requirements.txt\n");
            for (int i=0;i<project.getParameters();i++){
                fileWriter.write("ENV ARG" + i + " Default" + i +"\n");
            }
            fileWriter.write("CMD python " + project.getMain());
            for (int i=0;i<project.getParameters();i++){
                fileWriter.write(" $ARG"+i);
            }
        }
        else
            return 1;
        fileWriter.close();
        return 0;
    }

    @Override
    public void createRequirementsFile(BaseProject project) throws IOException {
        log.trace("createImage: location={}, req={}",project.getLocation(),project.getReq().stream().reduce((a,b) -> a + " " + b));
        FileWriter fileWriter = new FileWriter(project.getLocation() + "\\requirements.txt");
        for (int i=0;i<project.getReq().size();i++)
            fileWriter.write(project.getReq().get(i) + "\n");
        fileWriter.close();
    }


}
