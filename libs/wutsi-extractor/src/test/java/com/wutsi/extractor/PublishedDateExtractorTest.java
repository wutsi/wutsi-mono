package com.wutsi.extractor;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PublishedDateExtractorTest {
    private PublishedDateExtractor extractor = new PublishedDateExtractor();

    @Test
    public void testOpenGraph() throws Exception {
        String html = load("/published/opengraph.html");
        Date date = extractor.extract(html);
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

        assertNotNull(date);
        assertTrue(fmt.format(date).startsWith("2017-03-31"));
    }

    @Test
    public void testBadFormat() throws Exception {
        String html = load("/published/bad-format.html");
        assertNull(extractor.extract(html));
    }

    @Test
    public void testNone() throws Exception {
        String html = load("/published/none.html");
        assertNull(extractor.extract(html));
    }

    private String load(String path) throws IOException {
        InputStream in = getClass().getResourceAsStream(path);
        return IOUtils.toString(in);
    }
}
