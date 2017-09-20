/*
 * Copyright 2000-2017 Vaadin Ltd.
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
package com.vaadin.flow.uitest.ui;

import com.vaadin.ui.html.NativeButton;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

public class UIElementUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        getPage().executeJavaScript(getJs(), getElement());

        NativeButton attachElement = new NativeButton("Attach Element via JS",
                event -> attachElement());
        add(attachElement);
    }

    private void attachElement() {
        getPage().executeJavaScript(getJs(), getElement());
    }

    private String getJs() {
        return "var newElement = document.createElement('div');"
                + "newElement.className='body-child';"
                + "$0.appendChild(newElement);";
    }
}
