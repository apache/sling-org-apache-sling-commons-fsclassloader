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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.sling.commons.classloader.ClassLoaderWriter;
import org.apache.sling.commons.fsclassloader.FSClassLoaderMBean;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the FSClassLoaderMBean interface
 */
@Component(
        configurationPid = FSClassLoaderProvider.SHARED_CONFIGURATION_PID,
        property = {
            Constants.SERVICE_DESCRIPTION + "=Apache Sling FSClassLoader Controller Service",
            Constants.SERVICE_VENDOR + "=The Apache Software Foundation",
            "jmx.objectname=org.apache.sling.classloader:name=FSClassLoader,type=ClassLoader"
        })
public class FSClassLoaderMBeanImpl implements FSClassLoaderMBean {
    private volatile File root;
    private static final Logger log = LoggerFactory.getLogger(FSClassLoaderMBeanImpl.class);

    @Reference(target = "(component.name=org.apache.sling.commons.fsclassloader.impl.FSClassLoaderProvider)")
    private ClassLoaderWriter classLoaderWriter;

    /**
     * Activate this component. Create the root directory.
     *
     * @param componentContext
     *            the component context
     */
    @Activate
    protected void activate(final ComponentContext componentContext, final FSClassLoaderComponentConfig config) {
        // get the file root
        this.root = CacheLocationUtils.getRootDir(componentContext.getBundleContext(), config);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.sling.commons.fsclassloader.FSClassLoaderMBean#
     * getCachedScriptCount()
     */
    @Override
    public int getCachedScriptCount() throws IOException {
        return getScripts().size();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.sling.commons.fsclassloader.FSClassLoaderMBean#
     * getCachedScripts()
     */
    @Override
    public List<String> getCachedScripts() {
        List<String> scripts = new ArrayList<String>();
        scripts.addAll(getScripts());
        Collections.sort(scripts);
        return scripts;
    }

    private Collection<String> getScripts() {
        Collection<String> scripts = new HashSet<String>();
        try {
            Map<String, ScriptFiles> s = new LinkedHashMap<String, ScriptFiles>();
            if (root != null) {
                FSClassLoaderWebConsole.readFiles(root, root, s);
            }
            scripts = s.keySet();
        } catch (Exception e) {
            log.warn("Exception retrieving scripts from FSClassLoader", e);
        }
        return scripts;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.apache.sling.commons.fsclassloader.FSClassLoaderMBean#clearCache()
     */
    @Override
    public void clearCache() {
        classLoaderWriter.delete("");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.sling.commons.fsclassloader.FSClassLoaderMBean#
     * getFSClassLoaderRoot()
     */
    @Override
    public String getFSClassLoaderRoot() {
        return root.getAbsolutePath();
    }
}
