package com.wutsi.extractor.rss;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Item {
    //-- Attributes
    private String link;
    private String title;
    private String description;
    private List<String> categories = new ArrayList<>();
    private String language;
    private String country;
    private String content;
    private Date publishedDate;
    private String author;
    private List<String> imageUrls = new ArrayList<>();

    //-- Getter/Setter
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void addCategory(String category) {
        categories.add(category);
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(final List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public void addImageUrl(String url){
        imageUrls.add(url);
    }
}
