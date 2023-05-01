package com.wutsi.extractor.filter;

import com.wutsi.extractor.DescriptionExtractor;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DescriptionExtractorTest {
    private DescriptionExtractor extractor = new DescriptionExtractor();

    @Test
    public void testOpenGraph() throws Exception {
        String html = load("/description/opengraph.html");
        String url = extractor.extract(html);
        assertEquals("Wanda People, 2016 s'est écoulée et c'est l'heure du bilan. Sur le plan musical, plusieurs artistes urbains nous ont fait vibrer au rythme de leurs tubes.", url);
    }

    @Test
    public void testMeta() throws Exception {
        String html = load("/description/meta.html");
        String url = extractor.extract(html);
        assertEquals("Baylor's Rico Gathers a Man Among Boys in Basketball, May Face NFL Dilemma", url);
    }


    @Test
    public void testNone() throws Exception {
        String html = load("/description/none.html");
        String url = extractor.extract(html);
        assertNull(url);
    }

    private String load(String path) throws IOException {
        InputStream in = getClass().getResourceAsStream(path);
        return IOUtils.toString(in);
    }
}
