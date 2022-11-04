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
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class CallbackServlet extends AbstractAuthorizationServlet {
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final StringBuffer buf = req.getRequestURL();
        if (req.getQueryString() != null) {
            buf.append('?').append(req.getQueryString());
        }
        final AuthorizationCodeResponseUrl responseUrl = new AuthorizationCodeResponseUrl(buf.toString());
        final String code = responseUrl.getCode();
        if (responseUrl.getError() != null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, responseUrl.getError());
        } else if (code == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("Missing authorization code");
        } else {
            final AuthorizationCodeFlow flow = getAuthorizationCodeFlow();
            final String redirectUri = redirectUri(req, "callback"); // TODO
            final TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
            final HttpSession session = req.getSession();
            final String sessionId = session.getId();
            final Credential credential = flow.createAndStoreCredential(response, sessionId);
            System.out.println("accessToken = " + credential.getAccessToken());
            final Oauth2 oauth2 = new Oauth2.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), credential)
                    .setApplicationName("Oauth2")
                    .build();
            final Userinfo userinfo = oauth2.userinfo().get().execute();
            System.out.println(userinfo.toPrettyString());
            session.setAttribute("userinfo", userinfo); // is this smart?
//            onSuccess(req, resp, credential);
            resp.sendRedirect(redirectUri(req, ""));
        }
    }
}
