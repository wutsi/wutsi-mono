package com.wutsi.extractor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class DescriptionExtractor {
    public String extract(String html) {
        Document doc = Jsoup.parse(html);
        String description = extractFromOpenGraph(doc);
        return description == null ? extractFromMeta(doc) : description;
    }

    private String extractFromOpenGraph(Document doc) {
        Elements nodes = doc.select("meta[property=og:description]");
        return nodes.isEmpty() ? null : nodes.attr("content");
    }

    private String extractFromMeta(Document doc) {
        Elements nodes = doc.select("meta[name=description]");
        return nodes.isEmpty() ? null : nodes.attr("content");
    }

}
