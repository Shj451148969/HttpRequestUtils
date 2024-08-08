package com.shj.steam_gui.utils.http.handler;

import com.shj.steam_gui.utils.http.entity.RequestEntity;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.protocol.HttpContext;

public class AppDefaultRedirectStrategy extends DefaultRedirectStrategy {

    private final RequestEntity requestEntity;

    public AppDefaultRedirectStrategy(RequestEntity requestEntity) {
        super();
        this.requestEntity = requestEntity;
    }

    @Override
    public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
        boolean result = super.isRedirected(request, response, context);
        if (result) {
            Header locationHeader = response.getFirstHeader("location");
            requestEntity.getRedirectURL().add(locationHeader.getValue());
        }
        return result;
    }
}
