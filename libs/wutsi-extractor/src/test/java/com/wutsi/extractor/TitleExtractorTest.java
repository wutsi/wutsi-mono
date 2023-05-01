package com.wutsi.extractor;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TitleExtractorTest {
    private TitleExtractor extractor = new TitleExtractor();

    @Test
    public void testOpenGraph() throws Exception {
        String html = load("/title/opengraph.html");
        String title = extractor.extract(html);
        assertEquals("Voici comment savoir si vous êtes manipulé dans votre relation", title);
    }

    @Test
    public void testDefault() throws Exception {
        String html = load("/title/default.html");
        String title = extractor.extract(html);
        assertEquals("Baylor's Rico Gathers a Man Among Boys in Basketball", title);
    }

    @Test
    public void testNone() throws Exception {
        String html = load("/title/none.html");
        String title = extractor.extract(html);
        assertEquals("", title);
    }

    private String load(String path) throws IOException {
        InputStream in = getClass().getResourceAsStream(path);
        return IOUtils.toString(in);
    }
}
