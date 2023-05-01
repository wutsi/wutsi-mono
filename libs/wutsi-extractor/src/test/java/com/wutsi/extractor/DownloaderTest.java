package com.wutsi.extractor;


import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

public class DownloaderTest {
    private Downloader downloader = new Downloader();

    @Test
    public void testKamerKongossa() throws Exception {
        String url = "https://www.google.ca";
        String html = downloader.download(new URL(url));

        assertFalse(html.isEmpty());
    }

    @Test
    public void test404() throws Exception {
        String url = "https://kamerkongossa.cm/flfkldkf";
        try {
            downloader.download(new URL(url));
            fail("failed");
        } catch (IOException e) {
            // Ignore
        }
    }
}
