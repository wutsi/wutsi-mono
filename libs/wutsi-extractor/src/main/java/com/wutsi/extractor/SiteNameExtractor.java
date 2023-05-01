package com.wutsi.extractor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class SiteNameExtractor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SiteNameExtractor.class);

    public String extract(URL url, String html) {
        Document doc = Jsoup.parse(html);
        String site = extractSiteName(doc);
        if (site == null){
            site = extractURL(doc);
        }
        if (site == null){
            site = extractURL(url);
        }
        return site;
    }

    private String extractSiteName(Document doc) {
        Elements nodes = doc.select("meta[property=og:site_name]");
        return nodes.isEmpty() ? null : nodes.attr("content");
    }

    private String extractURL(Document doc) {
        Elements nodes = doc.select("meta[property=og:url]");
        if (!nodes.isEmpty()){
            String url = nodes.attr("content");
            try {
                return extractURL(new URL(url));
            } catch (Exception e) {
                LOGGER.warn("Invalid URL: " + url, e);
            }
        }
        return null;
    }

    private String extractURL(URL url) {
        return url.getHost();
    }

}
