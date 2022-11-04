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
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.oauth2.model.Userinfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class SSOServlet extends AbstractAuthorizationServlet {
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final HttpSession session = req.getSession();
        final String sessionId = session.getId();
        final AuthorizationCodeFlow flow = getAuthorizationCodeFlow();
        final Credential credential = flow.loadCredential(sessionId);
        System.out.println("TestServlet: credential = " + credential);
        final Userinfo userinfo = Userinfo.class.cast(session.getAttribute("userinfo"));
        if (credential != null && credential.getAccessToken() != null && userinfo != null) {
            // do stuff
            System.out.println("TestServlet: accessToken = " + credential.getAccessToken());
            req.setAttribute("userinfo", userinfo);
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
        } else {
            final AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl();
            authorizationUrl.setRedirectUri(redirectUri(req, "callback"));
            resp.sendRedirect(authorizationUrl.build());
        }
    }
}
