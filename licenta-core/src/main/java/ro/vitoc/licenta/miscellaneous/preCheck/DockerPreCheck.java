package ro.vitoc.licenta.miscellaneous.preCheck;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface DockerPreCheck {
    void checkDefaultVM() throws IOException, TimeoutException, InterruptedException;

}
