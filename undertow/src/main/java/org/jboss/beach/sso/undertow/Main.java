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

package org.jboss.beach.sso.undertow;

import io.undertow.Undertow;
import io.undertow.jsp.HackInstanceManager;
import io.undertow.jsp.JspServletBuilder;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.servlet.api.ServletInfo;
import org.apache.jasper.deploy.JspPropertyGroup;
import org.apache.jasper.deploy.TagLibraryInfo;
import org.jboss.beach.sso.google.servlet.CallbackServlet;
import org.jboss.beach.sso.google.servlet.SSOServlet;

import java.nio.file.Path;
import java.util.HashMap;

import static io.undertow.servlet.Servlets.servlet;
import static org.jboss.beach.sso.google.servlet.AbstractAuthorizationServlet.GOOGLE_CLIENT_ID;
import static org.jboss.beach.sso.google.servlet.AbstractAuthorizationServlet.GOOGLE_CLIENT_SECRET;

public class Main {
    public static void main(final String[] args) throws Exception {
        final String clientId = mandatorySystemProperty(GOOGLE_CLIENT_ID);
        final String clientSecret = mandatorySystemProperty(GOOGLE_CLIENT_SECRET);
        ResourceManager resourceManager = new ClassPathResourceManager(Thread.currentThread().getContextClassLoader());
        final Path indexPath = resourceManager.getResource("/index.jsp").getFilePath();
        if (indexPath != null)
            resourceManager = new PathResourceManager(indexPath.getParent());
        final ServletContainer container = ServletContainer.Factory.newInstance();
        final DeploymentInfo deployment = Servlets.deployment()
                .addServlets(
                        withInitParams(servlet(CallbackServlet.class).addMapping("/callback"), clientId, clientSecret),
                        withInitParams(servlet(SSOServlet.class).addMapping("/"), clientId, clientSecret),
                        JspServletBuilder.createServlet("Default JSP Servlet", "*.jsp"))
                .setClassLoader(Thread.currentThread().getContextClassLoader())
                .setContextPath("/")
                .setDeploymentName("google-sso.war")
                .setResourceManager(resourceManager);
        JspServletBuilder.setupDeployment(deployment, new HashMap<String, JspPropertyGroup>(), new HashMap<String, TagLibraryInfo>(), new HackInstanceManager());
        final DeploymentManager deploymentManager = container.addDeployment(deployment);
        deploymentManager.deploy();
        final HttpHandler handler = deploymentManager.start();
        final Undertow server = Undertow.builder()
                .addHttpListener(9090, "localhost")
                .setHandler(handler)
                .build();
        server.start();
    }

    private static String mandatorySystemProperty(final String key) {
        final String value = System.getProperty(key);
        if (value == null) throw new IllegalStateException(key + " system property not set");
        return value;
    }

    private static ServletInfo withInitParams(final ServletInfo servletInfo, final String clientId, final String clientSecret) {
        return servletInfo
                .addInitParam(GOOGLE_CLIENT_ID, clientId)
                .addInitParam(GOOGLE_CLIENT_SECRET, clientSecret);
    }
}
