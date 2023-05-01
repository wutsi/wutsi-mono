package com.wutsi.extractor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PublishedDateExtractor {
    private static final Logger LOGGER = LoggerFactory.getLogger(PublishedDateExtractor.class);
    public static final String[] DATE_FORMAT = new String[] {
            "yyyy-MM-dd'T'HH:mm.ss.SSSZ",
            "yyyy-MM-dd'T'HH:mm:ssXXX",
    };


    public Date extract(String html) {
        Document doc = Jsoup.parse(html);
        Elements tags = doc.select("meta[property=article:published_time]");
        if (!tags.isEmpty()){
            String timestamp = tags.first().attr("content");
            return toDate(timestamp);
        }
        return null;
    }

    private Date toDate(String timestamp) {
        for(String dateFormat: DATE_FORMAT){
            DateFormat fmt = new SimpleDateFormat(dateFormat);
            try {
                return fmt.parse(timestamp);
            } catch (Exception e) {

            }
        }

        System.err.println("Invalid published date: " + timestamp);
        LOGGER.warn("Invalid published date: " + timestamp);
        return null;
    }
}
