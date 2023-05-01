package com.wutsi.extractor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class RSSExtractor {
    public String extract(String html) {
        Document doc = Jsoup.parse(html);
        Elements nodes = doc.select("link[type=application/rss+xml]");
        return nodes.isEmpty() ? null : nodes.attr("href");
    }
}
