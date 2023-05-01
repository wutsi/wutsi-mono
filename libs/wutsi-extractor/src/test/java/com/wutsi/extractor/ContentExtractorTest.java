package com.wutsi.extractor;

import com.wutsi.extractor.filter.AbstractFilterTest;
import org.junit.jupiter.api.Test;

public class ContentExtractorTest extends AbstractFilterTest {
    ContentExtractor extractor = ContentExtractor.create(20);

    @Test
    public void camfoot() throws Exception {
        test("/extractor/camfoot");
    }

    private void test(String path) throws Exception {
        super.validateHtml(path, (s) -> extractor.extract(s));
    }
}
