package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpRequestExtractor;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static com.google.common.collect.FluentIterable.from;

public class HeaderRequestExtractor extends HttpRequestExtractor<String[]> {
    private final String name;

    public HeaderRequestExtractor(final String name) {
        this.name = name;
    }

    @Override
    protected Optional<String[]> doExtract(final HttpRequest request) {
        final ImmutableMap<String,String> headers = request.getHeaders();
        String[] extractedValues = from(headers.entrySet()).filter(isForName(name)).transform(toValue()).toArray(String.class);
        if (extractedValues.length > 0) {
            return of(extractedValues);
        }

        return absent();
    }

    private Function<Map.Entry<String, String>, String> toValue() {
        return new Function<Map.Entry<String,String>, String>() {
            @Override
            public String apply(Map.Entry<String, String> input) {
                return input.getValue();
            }
        };
    }

    private Predicate<Map.Entry<String, String>> isForName(final String key) {
        return new Predicate<Map.Entry<String, String>>() {
            @Override
            public boolean apply(Map.Entry<String, String> input) {
                return key.equalsIgnoreCase(input.getKey());
            }
        };
    }
}
