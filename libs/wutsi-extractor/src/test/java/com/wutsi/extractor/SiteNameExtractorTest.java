package com.wutsi.extractor;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SiteNameExtractorTest {
    private SiteNameExtractor extractor = new SiteNameExtractor();

    @Test
    public void ogSite() throws Exception {
        URL url = new URL("http://www.google.ca");
        String site = extractor.extract(url, load("/site/opengraph-site.html"));
        assertEquals("ZeSite", site);
    }

    @Test
    public void ogUrl() throws Exception {
        URL url = new URL("http://www.google.ca");
        String site = extractor.extract(url, load("/site/opengraph-url.html"));
        assertEquals("www.zesite.com", site);
    }

    @Test
    public void url() throws Exception {
        URL url = new URL("http://www.google.ca");
        String site = extractor.extract(url, load("/site/none.html"));
        assertEquals("www.google.ca", site);

    }

    private String load(String path) throws IOException {
        InputStream in = getClass().getResourceAsStream(path);
        return IOUtils.toString(in);
    }
}
