package com.wutsi.extractor;

public interface Filter<T> {
    T filter(T str);
}
