package com.wutsi.extractor.rss;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class RSSFeedSAXHandler extends DefaultHandler {
    //-- Attributes
    private static final Logger LOGGER = LoggerFactory.getLogger(RSSFeedSAXHandler.class);

    private List<Item> items = new ArrayList<>();
    private Item item = null;
    private StringBuilder text = null;
    private DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

    //-- Public
    public List<Item> getItems (){
        return items;
    }

    //-- DefaultHandler overrides
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if ("item".equalsIgnoreCase(qName)) {
            item = new Item();
        } else if ("enclosure".equals(qName)){
            handleEnclosure(attributes);
        }

        text = new StringBuilder();
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if(text != null) {
            text.append(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if ("item".equalsIgnoreCase(qName)) {
            items.add(item);
            item = null;
        } else if (item == null) {
            return;
        }

        switch (qName.toLowerCase()){
            case "title":
                item.setTitle(trim(text.toString()));
                break;

            case "description":
                item.setDescription(text.toString());
                break;

            case "content:encoded":
                item.setContent(text.toString());
                break;

            case "language":
                item.setLanguage(text.toString());
                break;

            case "link":
                item.setLink(trim(text.toString()));
                break;

            case "category":
                item.addCategory(text.toString());
                break;

            case "author":
                item.setAuthor(text.toString());
                break;

            case "pubdate":
                try {
                    item.setPublishedDate(dateFormat.parse(text.toString()));
                } catch (ParseException e) {
                    LOGGER.warn("Invalid publish date", e);
                }
                break;
        }
    }

    private String trim (String str) {
        if (str == null){
            return null;
        }
        return str.trim().replaceAll("\\n", "");
    }

    private void handleEnclosure(Attributes attributes) {
        String url = null;
        String type = null;
        for (int i=0, len=attributes.getLength() ; i<len ; i++){
            String name = attributes.getLocalName(i);
            if ("url".equalsIgnoreCase(name)){
                url = attributes.getValue(i);
            } else if ("type".equalsIgnoreCase(name)){
                type = attributes.getValue(i);
            }
        }

        if (type != null && type.startsWith("image/")){
            item.addImageUrl(url);
        }
    }
}
