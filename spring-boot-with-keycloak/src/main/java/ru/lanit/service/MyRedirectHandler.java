package ru.lanit.service;

import java.net.URI;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.protocol.HttpContext;

public class MyRedirectHandler extends DefaultRedirectStrategy {

    public URI lastRedirectedUri;

    @Override
    public boolean isRedirected(HttpRequest request, HttpResponse response,
                                HttpContext context) {
        try {
            return super.isRedirected(request, response, context);
        } catch (ProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public URI getLocationURI(HttpRequest request, HttpResponse response, HttpContext context)
            throws ProtocolException {

        lastRedirectedUri = super.getLocationURI(request, response, context);
        return lastRedirectedUri;
    }
}