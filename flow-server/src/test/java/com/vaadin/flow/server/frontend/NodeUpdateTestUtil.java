/*
 * Copyright 2000-2019 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package com.vaadin.flow.server.frontend;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vaadin.flow.server.frontend.ClassFinder.DefaultClassFinder;
import elemental.json.JsonObject;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static com.vaadin.flow.server.Constants.PACKAGE_JSON;
import static com.vaadin.flow.server.DevModeHandler.WEBPACK_SERVER;
import static com.vaadin.flow.server.frontend.FrontendUtils.getBaseDir;
import static org.junit.Assert.assertNotNull;

public class NodeUpdateTestUtil {

    public static final String WEBPACK_TEST_OUT_FILE = "webpack-out.test";

    static ClassFinder getClassFinder()
            throws MalformedURLException {
        return new DefaultClassFinder(
                new URLClassLoader(getClassPath()),
                NodeTestComponents.class.getDeclaredClasses());
    }

    static URL[] getClassPath() throws MalformedURLException {
        // Add folder with test classes
        List<URL> classPaths = new ArrayList<>();

        classPaths.add(new File("target/test-classes").toURI().toURL());

        // Add this test jar which has some frontend resources used in tests
        URL jar = getTestResource("jar-with-frontend-resources.jar");
        classPaths.add(jar);

        // Add other paths already present in the system classpath
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        URL[] urls = ((URLClassLoader) classLoader).getURLs();
        for (URL url : urls) {
            classPaths.add(url);
        }

        return classPaths.toArray(new URL[0]);
    }

    // Creates stub versions of `node` and `npm` in the ./node folder as
    // frontend-maven-plugin does
    // Also creates a stub version of webpack-devmode-server
    public static void createStubNode(boolean stubNode, boolean stubNpm) throws IOException {

        if (stubNpm) {
            File npmCli = new File(getBaseDir(), "node/node_modules/npm/bin/npm-cli.js");
            FileUtils.forceMkdirParent(npmCli);
            npmCli.createNewFile();
        }
        if (stubNode) {
            File node = new File(getBaseDir(),
                    FrontendUtils.isWindows() ? "node/node.exe" : "node/node");
            node.createNewFile();
            node.setExecutable(true);
            if (FrontendUtils.isWindows()) {
                FileUtils.copyFile(new File(
                        getClassFinder().getClass().getClassLoader().getResource("test_node.exe").getFile()
                ), node);
            } else {
                FileUtils.write(node,
                        "#!/bin/sh\n[ \"$1\" = -v ] && echo 8.0.0 || sleep 1\n",
                        "UTF-8");
            }
        }
    }

    // Creates a stub webpack-dev-server able to output a ready string, sleep
    // for a while and output arguments passed to a file, so as tests can check it
    public static void createStubWebpackServer(String readyString, int milliSecondsToRun) throws IOException {
        File serverFile = new File(getBaseDir(), WEBPACK_SERVER);
        FileUtils.forceMkdirParent(serverFile);

        serverFile.createNewFile();
        serverFile.setExecutable(true);
        FileUtils.write(serverFile, (
            "#!/usr/bin/env node\n" +
            "const fs = require('fs');\n" +
            "const args = String(process.argv);\n" +
            "fs.writeFileSync('" + WEBPACK_TEST_OUT_FILE + "', args);\n" +
            "console.log(args + '\\n[wps]: "  + readyString + ".');\n" +
            "setTimeout(() => {}, " + milliSecondsToRun + ");\n"), "UTF-8");
    }


    // Creates a `NodeUpdatePackages` instance with a modified
    // `updateDependencies` method able to write `dependencies` and
    // `devDependencies` to the `package.json` file instead of calling
    // `npm` which to speed up unit testing
    @SuppressWarnings("unchecked")
    static NodeUpdatePackages createStubUpdater() throws MalformedURLException {
        File tmpRoot = new File(getBaseDir());
        File modules = new File(tmpRoot, "node_modules");
        File packageFile = new File(tmpRoot, PACKAGE_JSON);

        // Create a spy version of the updater instance
        NodeUpdatePackages spy = Mockito.spy(
                new NodeUpdatePackages(
                        getClassFinder(),
                            tmpRoot, modules, true));

        // Override the `updateDependencies` method
        Mockito.doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) throws Exception {
                // Read the actual package.json file and parse into a json object
                JsonObject json = ((NodeUpdatePackages)invocation.getMock()).getPackageJson();

                // Add all dependencies to the appropriate key
                String type = invocation.getArgumentAt(1, String.class);
                List<String> deps = invocation.getArgumentAt(0, List.class);
                JsonObject devs = json.getObject("--save".equals(type) ? "dependencies" : "devDependencies");
                for (String dep : deps) {
                    devs.put(dep, "latest");
                }

                // Write the file with the new content
                FileUtils.writeStringToFile(packageFile, json.toJson(), "UTF-8");
                return null;
            }})
        .when(spy).updateDependencies(Mockito.anyList(), Mockito.anyVararg());

        return spy;
    }

    static URL getTestResource(String resourceName) {
        URL resourceUrl = NodeUpdateTestUtil.class.getClassLoader()
                .getResource(resourceName);
        assertNotNull(String.format(
                "Expect the test resource to be present in test resource folder with name = '%s'",
                resourceName), resourceUrl);
        return resourceUrl;
    }

    void sleep(int ms) throws InterruptedException {
        Thread.sleep(ms); // NOSONAR
    }

    List<String> getExpectedImports() {
        return Arrays.asList("@polymer/iron-icon/iron-icon.js",
                "@vaadin/vaadin-lumo-styles/spacing.js",
                "@vaadin/vaadin-lumo-styles/icons.js",
                "@vaadin/vaadin-lumo-styles/style.js",
                "@vaadin/vaadin-lumo-styles/typography.js",
                "@vaadin/vaadin-lumo-styles/color.js",
                "@vaadin/vaadin-lumo-styles/sizing.js",
                "@vaadin/vaadin-date-picker/theme/lumo/vaadin-date-picker.js",
                "@vaadin/vaadin-date-picker/src/vaadin-month-calendar.js",
                "@vaadin/vaadin-element-mixin/vaadin-element-mixin.js",
                "@vaadin/vaadin-mixed-component/theme/lumo/vaadin-mixed-component.js",
                "@vaadin/vaadin-mixed-component/theme/lumo/vaadin-something-else.js",
                "@vaadin/flow-frontend/ExampleConnector.js",
                "./local-p3-template.js",
                "./foo.js",
                "./vaadin-mixed-component/theme/lumo/vaadin-mixed-component.js",
                "./local-p2-template.js",
                "./foo-dir/vaadin-npm-component.js");
    }

    void createExpectedImports(File directoryWithImportsJs,
                               File nodeModulesPath) throws IOException {
        for (String expectedImport : getExpectedImports()) {
            File newFile = resolveImportFile(directoryWithImportsJs,
                    nodeModulesPath, expectedImport);
            newFile.getParentFile().mkdirs();
            Assert.assertTrue(newFile.createNewFile());
        }
    }

    void deleteExpectedImports(File directoryWithImportsJs,
                               File nodeModulesPath) {
        for (String expectedImport : getExpectedImports()) {
            Assert.assertTrue(resolveImportFile(directoryWithImportsJs,
                    nodeModulesPath, expectedImport).delete());
        }
    }

    File resolveImportFile(File directoryWithImportsJs,
                           File nodeModulesPath, String jsImport) {
        File root = jsImport.startsWith("./") ? directoryWithImportsJs
                : nodeModulesPath;
        return new File(root, jsImport);
    }

}
