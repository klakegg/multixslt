package net.klakegg.xml.multixslt;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ManifestProcessorTest {

    @Test
    public void simpleConstructor() {
        Assert.assertNotNull(new ManifestProcessor());
    }
}
