package com.wutsi.extractor.filter;

import com.wutsi.extractor.Filter;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractFilterTest {
    protected void validateText(final String name, Filter<String> filter) throws Exception {
        // Given
        final String html = IOUtils.toString(getClass().getResourceAsStream(name + ".html"));
        final String expected = IOUtils.toString(getClass().getResourceAsStream(name + suffix()));

        // When
        final String result = filter.filter(html);
        System.out.println(getClass() + " - " + name);
        System.out.println(result);

        // Then
        final String resultText = Jsoup.parse(result).text();
        final String expectedText = Jsoup.parse(expected).text();
        assertThat(resultText).isEqualTo(expectedText);
    }

    protected void validateHtml(final String name, Filter<String> filter) throws Exception {
        // Given
        final String html = IOUtils.toString(getClass().getResourceAsStream(name + ".html"));
        final String expected = IOUtils.toString(getClass().getResourceAsStream(name + suffix()));

        // When
        final String result = filter.filter(html);
        System.out.println(getClass() + " - " + name);
        System.out.println(result);

        // Then
        final String resultHtml = Jsoup.parse(result).body().html().trim();
        final String expectedHtml = Jsoup.parse(expected).body().html().trim();
        assertThat(resultHtml).isEqualTo(expectedHtml);
    }

    protected String suffix() {
        return "_output.html";
    }
}
