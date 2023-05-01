package com.wutsi.extractor.filter;

import com.wutsi.extractor.Filter;
import com.wutsi.extractor.util.JsoupHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Arrays;
import java.util.List;

/**
 * Remove polluting tags like SCRIPT, IMAGES, etc.
 */
public class SanitizeFilter implements Filter<String> {
    private static final List<String> ID_CSS_BLACKLIST = Arrays.asList(
            "footer",
            "comments",
            "menu-ay-side-menu-mine",
            "mashsb-container",
            "top-nav",
            "related_posts",
            "share-post",
            "navbar",
            "nav",
            "addthis_tool",
            "embedly-card",
            "sidebar",
            "rrssb-buttons", // See https://github.com/AdamPS/rrssb-plus
            "the_champ_sharing_container", // https://github.com/wp-plugins/super-socializer
            "a2a_kit", //https://www.addtoany.com/

            // journalducameroon.com
            "post-infos",

            // camfoot.com
            "jeg_share_top_container",
            "jeg_share_bottom_container",
            "jeg_post_tags",
            "jp-relatedposts",
            "truncate-read-more",
            "jnews_author_box_container",
            "jnews_related_post_container",
            "jnews_prev_next_container",
            "jnews_inline_related_post_wrapper",
            "ads-wrapper",

            "td-post-sharing"
    );
    private static List<String> TAG_BLACKLIST = Arrays.asList(
            "head",
            "style",
            "script",
            "nav",
            "iframe",
            "noscript",
            "header",
            "footer",
            "aside",
            "form"
    );


    //-- TextFilter overrides
    @Override
    public String filter(final String html) {

        final Document doc = Jsoup.parse(html);

        JsoupHelper.removeComments(doc.body());
        JsoupHelper.remove(doc, (i) -> reject(i));
        JsoupHelper.visit(doc, (i) -> empty(i));
        JsoupHelper.visit(doc, (i) -> cleanup(i));


        return doc.html();
    }

    private boolean cleanup(Element elt) {
        elt.removeAttr("id");
        elt.removeAttr("class");
        elt.removeAttr("style");
        elt.removeAttr("onclick");

        return true;
    }

    private boolean reject(Element elt) {
        return TAG_BLACKLIST.contains(elt.tagName())
                || isSocialLink(elt)
                || isTagLink(elt)
                || isBlacklistedClassOrId(elt)
//                || isMenu(elt)
        ;
    }

    private boolean isSocialLink(final Element elt) {
        if (!"a".equals(elt.tagName())){
            return false;
        }

        String href = elt.attr("href");
        return href.contains("twitter.com/intent/tweet") ||
                href.contains("twitter.com/share") ||
                href.contains("facebook.com/share.php") ||
                href.contains("facebook.com/sharer.php") ||
                href.contains("plus.google.com/share") ||
                href.contains("linkedin.com/shareArticle") ||
                href.contains("linkedin.com/cws/share") ||
                href.contains("pinterest.com/pin/create/button")
        ;
    }

    private boolean isMenu(final Element elt) {
        if (isLink(elt) || elt.tag().isInline() || isImage(elt)){
            return false;
        }

        Element clone = elt.clone();
        JsoupHelper.remove(clone, (i) -> isLink(i));
        return clone.text().trim().isEmpty();
    }

    private boolean isImage(Element elt) {
        String tagName = elt.tagName();
        return "figure".equalsIgnoreCase(tagName) || "img".equalsIgnoreCase(tagName);
    }

    private boolean isTagLink(Element elt) {
        if (!isLink(elt)) {
            return false;
        }
        return elt.hasClass("tag") || hasRel(elt, "tag");
    }

    private boolean hasRel(Element elt, String value) {
        String rel = elt.attr("rel");
        if (rel.isEmpty()){
            return false;
        }

        String[] parts = rel.split("\\s");
        for (String part: parts) {
            if (part.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    private boolean isLink(Element elt) {
        return "a".equals(elt.tagName());
    }

    private boolean isBlacklistedClassOrId(Element elt) {
        for (String clazz : elt.classNames()){
            if (ID_CSS_BLACKLIST.contains(clazz.toLowerCase())){
                return true;
            }
        }
        return ID_CSS_BLACKLIST.contains(elt.attr("id").toLowerCase());
    }

    private boolean empty(final Element elt) {
        if (elt.tag().isBlock() && !elt.hasText() && elt.children().isEmpty()){
            elt.remove();
            return true;
        }
        return false;
    }
}
