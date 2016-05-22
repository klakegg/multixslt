package net.klakegg.xml.multixslt;

import org.testng.Assert;
import org.testng.annotations.Test;

public class StylesheetProcessorTest {

    @Test
    public void simpleConstructor() {
        Assert.assertNotNull(new StylesheetProcessor());
    }
}
