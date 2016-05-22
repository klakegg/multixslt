package net.klakegg.xml.multixslt;

import net.klakegg.xml.multixslt.util.PathURIResolver;
import net.klakegg.xsd.xml.multixslt._1.FileType;
import net.klakegg.xsd.xml.multixslt._1.ParameterType;
import net.klakegg.xsd.xml.multixslt._1.StylesheetType;
import net.sf.saxon.TransformerFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class StylesheetProcessor {

    private static Logger logger = LoggerFactory.getLogger(StylesheetProcessor.class);

    public static void perform(Path path, StylesheetType stylesheet, List<ParameterType> manifestParameters) {
        try {
            Path src = path.resolve(stylesheet.getSrc());

            // Creating transformer factory
            TransformerFactory transformerFactory = new TransformerFactoryImpl();
            transformerFactory.setURIResolver(new PathURIResolver(src.getParent()));

            // Creating transformer
            Transformer transformer = transformerFactory.newTransformer(new StreamSource(Files.newInputStream(src)));

            for (FileType file : stylesheet.getFile()) {
                // Clear parameters
                transformer.clearParameters();

                // Set parameters
                Arrays.asList(manifestParameters, stylesheet.getParameter(), file.getParameter()).stream()
                        .flatMap(Collection::stream)
                        .forEach(p -> transformer.setParameter(p.getKey(), p.getValue()));

                // Calculate filename of result
                String filename = Optional.ofNullable(file.getTarget()).orElseGet(() -> {
                    String extension = Optional.ofNullable(file.getExtension()).orElse("result.xml");
                    return file.getSrc().substring(0, file.getSrc().lastIndexOf(".") + 1) + extension;
                });

                // Sources
                Source source = new StreamSource(Files.newInputStream(path.resolve(file.getSrc())));
                Result result = new StreamResult(Files.newOutputStream(path.resolve(filename)));

                // Transform
                logger.info("{} + {} => {}", src, file.getSrc(), filename);
                transformer.transform(source, result);
            }
        } catch (IOException | TransformerException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
