package net.klakegg.xml.multixslt;

import net.klakegg.xsd.xml.multixslt._1.Manifest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ManifestProcessor {

    private static Logger logger = LoggerFactory.getLogger(ManifestProcessor.class);

    private static JAXBContext jaxbContext;

    static {
        try {
            jaxbContext = JAXBContext.newInstance(Manifest.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static void perform(Path path) {
        try {
            Manifest manifest = jaxbContext.createUnmarshaller()
                    .unmarshal(new StreamSource(Files.newBufferedReader(path, StandardCharsets.UTF_8)), Manifest.class)
                    .getValue();

            manifest.getStylesheet().parallelStream()
                    .forEach(s -> StylesheetProcessor.perform(path.getParent(), s, manifest.getParameter()));
        } catch (JAXBException | IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
