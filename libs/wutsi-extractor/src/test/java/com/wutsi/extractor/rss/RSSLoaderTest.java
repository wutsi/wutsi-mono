package com.wutsi.extractor.rss;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RSSLoaderTest {
    private RSSLoader rss = new RSSLoader();

    @Test
    public void loadInputStream() throws Exception {
        InputStream in = getClass().getResourceAsStream("/rss/feed.xml");
        List<Item> items = rss.load(in);

        assertEquals(10, items.size());
        assertEquals("https://mapanes.fsquarecorporation.com/2020/05/06/voici-venu-le-temps-des-mutations/", items.get(0).getLink());
        assertEquals("https://mapanes.fsquarecorporation.com/2020/05/06/foo.png", items.get(0).getImageUrls().get(0));
        assertEquals("https://mapanes.fsquarecorporation.com/2020/05/04/choisir-ses-influenceurs-sur-linkedin-avec-linkalyze/", items.get(1).getLink());
        assertEquals("https://mapanes.fsquarecorporation.com/2020/04/30/7-outils-gratuits-de-gestion-de-projets/", items.get(2).getLink());
        assertEquals("https://mapanes.fsquarecorporation.com/2020/04/10/rester-a-la-maison-mais-travailler-efficacement/", items.get(3).getLink());
        assertEquals("https://mapanes.fsquarecorporation.com/2020/04/10/covid19-cameroun-sunu-assurances-offre-un-don-materiel-de-30-millions-au-minsante/", items.get(4).getLink());
        assertEquals("https://mapanes.fsquarecorporation.com/2020/03/25/page-speed-insights-pour-analyser-la-vitesse-de-chargement-dune-page-web/", items.get(5).getLink());
        assertEquals("https://mapanes.fsquarecorporation.com/2020/03/13/non-le-senat-americain-na-pris-aucune-sanction-contre-le-cameroun/", items.get(6).getLink());
        assertEquals("https://mapanes.fsquarecorporation.com/2020/03/11/devenir-community-manager/", items.get(7).getLink());
        assertEquals("https://mapanes.fsquarecorporation.com/2020/03/10/laudit-est-la-base-de-tout/", items.get(8).getLink());
        assertEquals("https://mapanes.fsquarecorporation.com/2020/03/09/connaitre-les-fondamentaux-du-seo/", items.get(9).getLink());
        assertEquals("Willy christian", items.get(0).getAuthor());
        assertEquals("Hello world", items.get(1).getAuthor());
        assertEquals("Hello world", items.get(2).getAuthor());
        assertEquals("Batalong College", items.get(3).getAuthor());
        assertEquals("ken le survivan", items.get(4).getAuthor());
        assertEquals("Batalong College", items.get(5).getAuthor());
        assertEquals("TIFOS", items.get(6).getAuthor());
        assertEquals("TIFOS", items.get(7).getAuthor());
        assertEquals("herve tcheps", items.get(8).getAuthor());
        assertEquals(null, items.get(9).getAuthor());
    }

    @Test
    public void loadPeople237() throws Exception {
        List<Item> items = rss.load(new URL("https://www.people237.com/feed"));
        assertTrue(items.size() > 0);
    }

    @Test
    public void loadCamfoot() throws Exception {
        List<Item> items = rss.load(new URL("https://camfoot.com/feed"));
        assertTrue(items.size() > 0);
    }
}
