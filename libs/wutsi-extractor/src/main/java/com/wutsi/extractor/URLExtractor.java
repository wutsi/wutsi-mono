package com.wutsi.extractor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.URL;

public class URLExtractor {
    public String extract(URL url, String html) {
        Document doc = Jsoup.parse(html);
        String result = extractOpengraph(doc);
        if (result == null){
            result = url.toString();
        }
        return result;
    }

    private String extractOpengraph(Document doc) {
        Elements nodes = doc.select("meta[property=og:url]");
        return nodes.isEmpty() ? null : nodes.attr("content");
    }
}
