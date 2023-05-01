package com.wutsi.extractor;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class URLExtractorTest {
    private URLExtractor extractor = new URLExtractor();

    @Test
    public void ogSite() throws Exception {
        URL url = new URL("http://www.google.ca");
        String site = extractor.extract(url, load("/url/opengraph.html"));
        assertEquals("https://www.zesite.com/34090/4309/oieoir", site);
    }

    @Test
    public void none() throws Exception {
        URL url = new URL("http://www.google.ca");
        String site = extractor.extract(url, load("/url/none.html"));
        assertEquals("http://www.google.ca", site);
    }

    private String load(String path) throws IOException {
        InputStream in = getClass().getResourceAsStream(path);
        return IOUtils.toString(in);
    }
}
