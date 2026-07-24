package io.github.newhoo.restkit.common;

import io.github.newhoo.restkit.restful.RestClient;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Request
 *
 * @author newhoo
 * @since 2.0.0
 */
@NotProguard
@Data
public class Request {

    private String url;
    private String method;
    private Map<String, String> config;
    private List<KV> headers = Collections.emptyList();
    private Map<String, String> params;
    private String body;

    private RestClient client;
}