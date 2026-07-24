package io.github.newhoo.restkit.common;

import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * rest client info
 *
 * @author newhoo
 * @since 2.0.0
 */
@NotProguard
@Data
public class RestClientData {

    private String project;

    private String url;
    private String method;
    private Map<String, String> config;
    private List<KV> headers = Collections.emptyList();
    private Map<String, String> params;
    private String body;
}