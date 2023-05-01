package com.wutsi.extractor;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RSSExtractorTest {
    private RSSExtractor extractor = new RSSExtractor();

    @Test
    public void extract() throws Exception {
        String html = load("/rss/page.html");
        String value = extractor.extract(html);

        assertEquals("http://www.jewanda-magazine.com/feed/", value);
    }

    @Test
    public void extractNone() throws Exception {
        String html = load("/rss/none.html");
        String value = extractor.extract(html);

        assertNull(value);
    }

    private String load(String path) throws IOException {
        InputStream in = getClass().getResourceAsStream(path);
        return IOUtils.toString(in);
    }
}
