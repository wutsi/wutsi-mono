package com.wutsi.extractor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class ImageExtractor {
    public String extract(String html) {
        Document doc = Jsoup.parse(html);
        return extractImage(doc);
    }

    private String extractImage(Document doc) {
        Elements nodes = doc.select("meta[property=og:image]");
        return nodes.isEmpty() ? null : nodes.attr("content");
    }

}
