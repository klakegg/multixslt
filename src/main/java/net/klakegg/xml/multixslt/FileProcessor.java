package net.klakegg.xml.multixslt;

import net.klakegg.xsd.xml.multixslt._1.Manifest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileProcessor {

    private static Logger logger = LoggerFactory.getLogger(FileProcessor.class);

    private static JAXBContext jaxbContext;

    static {
        try {
            jaxbContext = JAXBContext.newInstance(Manifest.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private Path path;

    public FileProcessor(Path path) {
        this.path = path;
    }

    public void perform() {
        try {
            logger.info("File: {}", path);

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Manifest manifest = (Manifest) unmarshaller.unmarshal(Files.newBufferedReader(path, StandardCharsets.UTF_8));

            manifest.getStylesheet().parallelStream().forEach(s ->
                new StylesheetProcessor(path.getParent(), s, manifest.getParameter()).perform());
        } catch (JAXBException | IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
