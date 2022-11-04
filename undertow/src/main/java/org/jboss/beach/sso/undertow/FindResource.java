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

import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceManager;

import java.io.IOException;
import java.util.Arrays;

public class FindResource {
    public static void main(final String[] args) throws IOException {
        final String cp = System.getProperty("java.class.path");
        Arrays.stream(cp.split(":")).forEach(p -> System.out.println(p));
        System.out.println();
        final ResourceManager resourceManager = new ClassPathResourceManager(Thread.currentThread().getContextClassLoader());
        System.out.println(resourceManager.getResource("/index.jsp").getFilePath());
    }
}
