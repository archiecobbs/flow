/*
 * Copyright 2000-2021 Vaadin Ltd.
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
 */
package com.vaadin.flow.webcomponent;

import com.vaadin.flow.component.WebComponentExporter;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.webcomponent.WebComponent;
import com.vaadin.flow.theme.Theme;

@Theme("reusable-theme")
@NpmPackage(value = "@fortawesome/fontawesome-free", version = "5.15.1")
public class ThemedComponentExporter
        extends WebComponentExporter<ThemedComponent> {
    public ThemedComponentExporter() {
        super("themed-component");
    }

    @Override
    public void configureInstance(WebComponent<ThemedComponent> webComponent,
            ThemedComponent component) {

    }
}
