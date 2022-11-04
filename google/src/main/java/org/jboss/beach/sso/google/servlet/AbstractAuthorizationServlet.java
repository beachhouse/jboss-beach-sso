/*
 * Copyright (c) 2022 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.beach.sso.google.servlet;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Set;

public abstract class AbstractAuthorizationServlet extends HttpServlet {
    public static final String GOOGLE_CLIENT_ID = "GOOGLE_CLIENT_ID";
    public static final String GOOGLE_CLIENT_SECRET = "GOOGLE_CLIENT_SECRET";

    private AuthorizationCodeFlow flow;

    protected AuthorizationCodeFlow getAuthorizationCodeFlow() {
        return flow;
    }

    @Override
    public void init(final ServletConfig config) throws ServletException {
        try {
            super.init(config);
            final String clientId = config.getInitParameter(GOOGLE_CLIENT_ID);
            final String clientSecret = config.getInitParameter(GOOGLE_CLIENT_SECRET);
            this.flow = new AuthorizationCodeFlow.Builder(BearerToken.authorizationHeaderAccessMethod(),
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    new GenericUrl("https://oauth2.googleapis.com/token"), // tokenServerUrl
                    new BasicAuthentication(clientId, clientSecret), // clientAuthentication
                    clientId, // clientId
                    "https://accounts.google.com/o/oauth2/auth") // authorizationServerEncodedUrl
                    .setScopes(Set.of("email", "profile"))
                    .setCredentialDataStore(MemoryDataStoreFactory.getDefaultInstance().getDataStore("GoogleSSOTest"))
                    .build();
        } catch (IOException e) {
            throw new ServletException(e);
        }
    }

    String redirectUri(final HttpServletRequest req, final String path) {
        final String scheme = req.getScheme();
        final String host = req.getServerName();
        final int port = req.getLocalPort();
        final String redirectUri = scheme + "://" + host + ":" + port + "/" + path;
        return redirectUri;
    }
}
