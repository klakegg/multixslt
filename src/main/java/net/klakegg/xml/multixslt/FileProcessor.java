package net.klakegg.xml.multixslt;

import net.klakegg.xsd.xml.multixslt._1.Multixslt;
import net.klakegg.xsd.xml.multixslt._1.StylesheetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileProcessor {

    private static Logger logger = LoggerFactory.getLogger(FileProcessor.class);

    private static JAXBContext jaxbContext;

    static {
        try {
            jaxbContext = JAXBContext.newInstance(Multixslt.class);
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
            Multixslt multixslt = (Multixslt) unmarshaller.unmarshal(Files.newBufferedReader(path));

            for (StylesheetType stylesheet : multixslt.getStylesheet()) {
                new StylesheetProcessor(path.getParent(), stylesheet).perform();
            }
        } catch (JAXBException | IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
