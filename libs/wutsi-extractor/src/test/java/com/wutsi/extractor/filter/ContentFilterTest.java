package com.wutsi.extractor.filter;

import org.junit.jupiter.api.Test;

public class ContentFilterTest extends AbstractFilterTest {
    private ContentFilter filter = new ContentFilter(20);

    @Test
    public void simple() throws Exception {
        validateText("/content/simple", filter);
    }

    @Test
    public void style() throws Exception {
        validateText("/content/style", filter);
    }

    @Test
    public void quote() throws Exception {
        validateText("/content/quote", filter);
    }

    @Test
    public void figure() throws Exception {
        validateText("/content/figure", filter);
    }

    @Test
    public void jounalducameroon() throws Exception {
        filter = new ContentFilter(100);
        validateText("/content/journal_du_cameroon", filter);
    }

    @Test
    public void investiraucameroun() throws Exception {
        filter = new ContentFilter(100);
        validateText("/content/investir_au_cameroun", filter);
    }

    @Test
    public void camfoot() throws Exception {
        filter = new ContentFilter(100);
        validateText("/content/camfoot", filter);
    }

    @Test
    public void jewanda() throws Exception {
        filter = new ContentFilter(100);
        validateText("/content/jewanda", filter);
    }

    @Test
    public void arolketch() throws Exception {
        filter = new ContentFilter(100);
        validateText("/content/arol_ketch", filter);
    }

    @Test
    public void people237() throws Exception {
        filter = new ContentFilter(100);
        validateText("/content/people237", filter);
    }
}
