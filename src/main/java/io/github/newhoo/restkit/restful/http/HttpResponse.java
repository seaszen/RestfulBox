package io.github.newhoo.restkit.restful.http;

import io.github.newhoo.restkit.common.KV;
import io.github.newhoo.restkit.common.NotProguard;
import io.github.newhoo.restkit.common.Response;
import lombok.EqualsAndHashCode;
import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * http response
 *
 * @author newhoo
 * @date 2022/3/12 3:10 PM
 * @since 2.0.3
 */
@EqualsAndHashCode(callSuper = true)
public class HttpResponse extends Response {

    /** original http response */
    @NotProguard
    private org.apache.http.HttpResponse original;

    /** response original body */
    @NotProguard
    private String body0;

    public HttpResponse(org.apache.http.HttpResponse original, String body) {
        this.original = original;
        this.body0 = body;

        setBody(body);
        setHeaders(extractHeaders(original));
    }

    private static List<KV> extractHeaders(org.apache.http.HttpResponse original) {
        if (original == null) {
            return Collections.emptyList();
        }
        Header[] allHeaders = original.getAllHeaders();
        if (allHeaders == null || allHeaders.length == 0) {
            return Collections.emptyList();
        }
        List<KV> list = new ArrayList<>(allHeaders.length);
        for (Header header : allHeaders) {
            if (header != null) {
                list.add(new KV(header.getName(), header.getValue()));
            }
        }
        return list;
    }

    @NotProguard
    public org.apache.http.HttpResponse getOriginal() {
        return original;
    }

    @NotProguard
    public void setOriginal(org.apache.http.HttpResponse original) {
        this.original = original;
    }

    @NotProguard
    public String getBody0() {
        return body0;
    }

    @NotProguard
    public void setBody0(String body0) {
        this.body0 = body0;
    }
}
