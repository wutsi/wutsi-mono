package com.wutsi.extractor;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ImageExtractorTest {
    private ImageExtractor extractor = new ImageExtractor();

    public void testOpenGraph() throws Exception {
        String html = load("/image/opengraph.html");
        String url = extractor.extract(html);
        assertEquals("http://www.jewanda-magazine.com/wp-content/uploads/2016/12/Musique-2016-en-16-clips-Cameroun.png", url);
    }


    @Test
    public void testNone() throws Exception {
        String html = load("/image/none.html");
        String url = extractor.extract(html);
        assertNull(url);
    }

    private String load(String path) throws IOException {
        InputStream in = getClass().getResourceAsStream(path);
        return IOUtils.toString(in);
    }
}
