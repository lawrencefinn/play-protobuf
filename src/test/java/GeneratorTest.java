import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.caboose.protobuf.utils.ServiceCodeGeneratorUtil;
import org.apache.commons.io.FileUtils;
import org.junit.*;

import protos.com.example.tutorial.Greeter;

import static org.junit.Assert.*;


public class GeneratorTest {


    @Test
    public void generateService() throws IOException, URISyntaxException {
        ServiceCodeGeneratorUtil gen = new ServiceCodeGeneratorUtil();
        Path tempDirectory = Files.createTempDirectory(Paths.get("/tmp"), null);
        gen.generateClassFiles(getClass().getClassLoader().getResource("service.proto").getFile(), tempDirectory.toString(), true);
        String outputController = String.join("\n",
                        Files.readAllLines(Paths.get(tempDirectory + "/io/caboose/protobuftest/controllers/Greeter.java"))
        );
        String expectedController = String.join("\n",
                Files.readAllLines(Paths.get(getClass().getClassLoader().getResource("Greeter.java").toURI()))
        );
        assertEquals(expectedController, outputController);

        String outputPhone = String.join("\n",
                Files.readAllLines(Paths.get(tempDirectory + "/io/caboose/protobuftest/parsers/PhoneNumberParser.java"))
        );
        String expectedPhone = String.join("\n",
                Files.readAllLines(Paths.get(getClass().getClassLoader().getResource("PhoneNumberParser.java").toURI()))
        );
        assertEquals(expectedPhone, outputPhone);

        FileUtils.deleteDirectory(tempDirectory.toFile());
    }

    @Test
    public void generateProto() throws IOException, URISyntaxException {
        ServiceCodeGeneratorUtil gen = new ServiceCodeGeneratorUtil();
        Path tempDirectory = Files.createTempDirectory(Paths.get("/tmp"), null);
        gen.generateProtoFiles(Greeter.class, tempDirectory.toString());
        String outputProto = String.join("\n",
                Files.readAllLines(Paths.get(tempDirectory + "/Greeter.proto"))
        );
        String expectedProto = String.join("\n",
                Files.readAllLines(Paths.get(getClass().getClassLoader().getResource("Greeter.proto").toURI()))
        );
        assertEquals(expectedProto, outputProto);
        FileUtils.deleteDirectory(tempDirectory.toFile());
    }

    @Test
    public void generateRoutes() throws IOException, URISyntaxException {
        ServiceCodeGeneratorUtil gen = new ServiceCodeGeneratorUtil();
        Path tempDirectory = Files.createTempDirectory(Paths.get("/tmp"), null);
        gen.generateRoutesFiles(getClass().getClassLoader().getResource("service.proto").getFile(), tempDirectory.toString() + "/routes");
        String outputRoutes = String.join("\n",
                Files.readAllLines(Paths.get(tempDirectory + "/routes"))
        );
        String expectedRoutes = String.join("\n",
                Files.readAllLines(Paths.get(getClass().getClassLoader().getResource("routes").toURI()))
        );
        assertEquals(expectedRoutes, outputRoutes);
        FileUtils.deleteDirectory(tempDirectory.toFile());
    }




}
