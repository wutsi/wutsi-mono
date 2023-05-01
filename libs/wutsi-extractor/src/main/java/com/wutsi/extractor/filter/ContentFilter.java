package com.wutsi.extractor.filter;

import com.wutsi.extractor.Filter;
import com.wutsi.extractor.util.JsoupHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Extract main content.
 * Implementation of https://rodricios.github.io/eatiht/#the-original-algorithm
 */
public class ContentFilter implements Filter<String> {
    //-- Attributes
    private static final List<String> INLINE_TAGS = Arrays.asList("i", "b", "font", "em", "small", "mark", "del", "ins", "q", "cite", "sub", "sup", "strong", "span", "a");

    private final int blocMinLen;

    public ContentFilter() {
        this(20);
    }

    public ContentFilter(final int blocMinLen) {
        this.blocMinLen = blocMinLen;
    }

    public String filter(final String html) {
        final List<Element> parts = select(html);
        final Map<Element, Long> histogram = partition(parts);
        final Element max = argmax(histogram);
        final List<Element> xparts = merge(max, parts);
        return toHtml(xparts);
    }

    //-- Private
    private List<Element> select(final String html) {
        final Element body = Jsoup.parse(html).body();
        final List<Element> blocs = new ArrayList<>();

        final JsoupHelper.Visitor<Element> visitor = (f) -> accept(body, f);
        JsoupHelper.filter(body, blocs, visitor);

        return blocs.stream()
                .map( it -> it.parent() )
                .distinct()
                .collect(Collectors.toList());
    }

    private Map<Element, Long> partition(final List<Element> parts) {
        final Map<Element, Long> result = new LinkedHashMap<>();
        for (final Element part : parts) {
            long value = part.children().stream()
                    .filter( it -> it.text().length() > blocMinLen )
                    .map( it -> sentenceCount(it.text()) )
                    .reduce(0, Integer::sum);
            result.put(part, value);
        }

        return result;
    }

    private Element argmax(final Map<Element, Long> parts) {
        final List<Element> sorted = parts.keySet().stream()
                .sorted((u,v) -> (int)(parts.get(u) - parts.get(v)))
                .collect(Collectors.toList());

        return sorted.isEmpty() ? null : sorted.get(sorted.size()-1);
    }

    private List<Element> merge(final Element max, final List<Element> parts) {
        List<Element> all = new ArrayList();
        if (max != null) {
            for (Element part : parts) {
                if (inPath(max, part)) {
                    all.add(part);
                }
            }
        }
        return all;
    }

    private String toHtml(List<Element> nodes) {
        Document doc = Jsoup.parse("");
        List<Element> elts = nodes;

        if (nodes.size() == 1) {
            Element node = nodes.get(0);
            if (node.children().size() > 0) {
                elts = node.children();
            }
        }

        elts.stream().forEach(it -> doc.body().appendChild(it));
        return doc.body().html();
    }

    private int sentenceCount(final String str) {
        String[] parts = str.split("(?<=[a-z])\\.\\s+");
        return parts.length;
    }

    private boolean accept(final Element root, final Element elt) {
        return elt != root &&
                isLeaf(elt) &&
                !INLINE_TAGS.contains(elt.tagName()) &&
                elt.text().trim().length()>blocMinLen;
    }

    private boolean isLeaf(final Element elt) {
        for (Element child : elt.children()) {
            if (!INLINE_TAGS.contains(child.tagName())){
                return false;
            }
        }
        return true;
    }

    private boolean inPath(Element max, Element part) {
        JsoupHelper.Visitor visitor = (it) -> it == max;
        Element elt = JsoupHelper.findFirst(part, visitor);
        return elt != null;
    }
}
