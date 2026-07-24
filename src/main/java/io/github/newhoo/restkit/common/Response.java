package io.github.newhoo.restkit.common;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * Response
 *
 * @author newhoo
 * @since 2.0.0
 */
@NotProguard
@Data
public class Response {

    /** response body */
    private String body;

    /** response headers (duplicate keys allowed) */
    private List<KV> headers = Collections.emptyList();
}