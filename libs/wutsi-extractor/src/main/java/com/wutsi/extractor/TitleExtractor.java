package com.wutsi.extractor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class TitleExtractor {
    public String extract(String html) {
        Document doc = Jsoup.parse(html);
        Elements tags = doc.select("meta[property=og:title]");
        if (tags.isEmpty()){
            tags = doc.select("title");
            if (!tags.isEmpty()){
                return tags.text();
            }
        } else {
            return tags.first().attr("content");
        }
        return "";
    }

}
