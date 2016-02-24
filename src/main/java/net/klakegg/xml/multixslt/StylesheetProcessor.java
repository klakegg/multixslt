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
import java.util.List;

public class StylesheetProcessor {

    private static Logger logger = LoggerFactory.getLogger(StylesheetProcessor.class);

    private Path path;
    private StylesheetType stylesheet;
    private List<ParameterType> fileParameters;

    public StylesheetProcessor(Path path, StylesheetType stylesheet, List<ParameterType> parameters) {
        this.path = path;
        this.stylesheet = stylesheet;
        this.fileParameters = parameters;
    }

    public void perform() {
        try {
            final Path src = path.resolve(stylesheet.getSrc());

            TransformerFactory transformerFactory = new TransformerFactoryImpl();
            transformerFactory.setURIResolver(new PathURIResolver(src.getParent()));

            logger.info("Loading {}", src);
            Transformer transformer = transformerFactory.newTransformer(new StreamSource(Files.newInputStream(src)));

            for (FileType file : stylesheet.getFile()) {
                transformer.clearParameters();
                for (ParameterType parameter : fileParameters)
                    transformer.setParameter(parameter.getKey(), parameter.getValue());
                for (ParameterType parameter : stylesheet.getParameter())
                    transformer.setParameter(parameter.getKey(), parameter.getValue());
                for (ParameterType parameter : file.getParameter())
                    transformer.setParameter(parameter.getKey(), parameter.getValue());

                Source source = new StreamSource(Files.newInputStream(path.resolve(file.getSrc())));
                Result result;
                if (file.getTarget() != null) {
                    result = new StreamResult(Files.newOutputStream(path.resolve(file.getTarget())));
                    logger.info("Process {} => {}", file.getSrc(), file.getTarget());
                } else {
                    String extension = file.getExtension() == null ? "result.xml" : file.getExtension();
                    String filename = file.getSrc().substring(0, file.getSrc().lastIndexOf(".") + 1) + extension;
                    result = new StreamResult(Files.newOutputStream(path.resolve(filename)));
                    logger.info("Process {} => {}", file.getSrc(), filename);
                }

                transformer.transform(source, result);
            }
        } catch (IOException | TransformerException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
