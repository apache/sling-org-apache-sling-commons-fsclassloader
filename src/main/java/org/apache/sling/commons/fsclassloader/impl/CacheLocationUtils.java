/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.commons.fsclassloader.impl;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.BundleContext;

/**
 * Utils to acquire file system cache root directory based on configuration
 */
public class CacheLocationUtils {

    private CacheLocationUtils() {}

    public static File getRootDir(final BundleContext context, final FSClassLoaderComponentConfig config) {
        String cacheLocation = config.fsclassloader_fileSystemCompiledScriptsCacheLocation();
        if (StringUtils.isBlank(cacheLocation)) return new File(context.getDataFile(""), "classes");
        else return new File(cacheLocation);
    }
}
