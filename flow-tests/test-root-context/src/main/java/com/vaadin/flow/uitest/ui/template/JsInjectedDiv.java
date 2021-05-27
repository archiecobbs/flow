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
package com.vaadin.flow.uitest.ui.template;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;

@JsModule("divConnector.js")
public class JsInjectedDiv extends Div {

    public JsInjectedDiv() {
        getElement().executeJs("window.divConnector.jsFunction(this)");
    }

    @ClientCallable
    private void handleClientCall(String value) {
        setText(value);
    }
}
