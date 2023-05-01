package com.wutsi.extractor.filter;

import org.junit.jupiter.api.Test;

public class SanitizeFilterTest extends AbstractFilterTest {
    private final SanitizeFilter filter = new SanitizeFilter();

    @Test
    public void document() throws Exception {
        validateHtml("/sanitizer/document", filter);
    }

    @Test
    public void social() throws Exception {
        validateHtml("/sanitizer/social", filter);
    }

    @Test
    public void menu() throws Exception {
        validateHtml("/sanitizer/menu", filter);
    }

    @Test
    public void jounalducameroon() throws Exception {
        validateText("/sanitizer/journal_du_cameroon", filter);
    }

    @Test
    public void investiraucameroun() throws Exception {
        validateText("/sanitizer/investir_au_cameroun", filter);
    }

    @Test
    public void camfoot() throws Exception {
        validateText("/sanitizer/camfoot", filter);
    }

    @Test
    public void jewanda() throws Exception {
        validateText("/sanitizer/jewanda", filter);
    }

    @Test
    public void arolketch() throws Exception {
        validateText("/sanitizer/arol_ketch", filter);
    }

    @Test
    public void people237() throws Exception {
        validateText("/sanitizer/people237", filter);
    }
}
