package com.wutsi.extractor.util;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JsoupHelper {
    private JsoupHelper() {
    }

    public static void visit(final Element root, final Visitor<Element> visitor){
        final Elements children = root.children();
        for (final Element child : children) {
            visit(child, visitor);
        }
        visitor.visit(root);
    }

    public static void removeComments(Node node) {
        for (int i = 0; i < node.childNodeSize();) {
            Node child = node.childNode(i);
            if (child.nodeName().equals("#comment"))
                child.remove();
            else {
                removeComments(child);
                i++;
            }
        }
    }

    public static void remove(final Element root, Visitor<Element> predicate) {
        final List<Element> elts = new ArrayList<>();
        JsoupHelper.filter(root, elts, predicate);
        elts.forEach( it -> it.remove());
    }

    public static void filter(final Element root, final Collection<Element> result, final Visitor<Element> predicate) {
        if (predicate.visit(root)) {
            result.add(root);
        }

        final Elements children = root.children();
        for (final Element child : children) {
            filter(child, result, predicate);
        }
    }

    public static Element findFirst(final Element root, final Visitor<Element> predicate) {
        if (predicate.visit(root)) {
            return root;
        }

        final Elements children = root.children();
        for (final Element child : children) {
            Element elt = findFirst(child, predicate);
            if (elt != null) {
                return elt;
            }
        }
        return null;
    }

    public static String select(final Document doc, final String cssSelector) {
        final Elements elts = doc.select(cssSelector);
        return elts.isEmpty() ? null : elts.get(0).text();
    }

    public interface Visitor<T> {
        boolean visit(T obj);
    }
}
