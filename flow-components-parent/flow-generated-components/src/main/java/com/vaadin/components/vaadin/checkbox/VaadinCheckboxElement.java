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
package com.vaadin.components.vaadin.checkbox;

import com.vaadin.ui.Component;
import javax.annotation.Generated;
import com.vaadin.annotations.Tag;
import com.vaadin.annotations.HtmlImport;
import com.vaadin.annotations.DomEvent;
import com.vaadin.ui.ComponentEvent;
import com.vaadin.flow.event.ComponentEventListener;
import com.vaadin.shared.Registration;

/**
 * Description copied from corresponding location in WebComponent:
 * 
 * {@code <vaadin-checkbox>} is a Polymer element for customized checkboxes.
 * 
 * {@code }`html <vaadin-checkbox> Make my profile visible </vaadin-checkbox>
 * {@code }`
 * 
 * ### Styling
 * 
 * The following shadow DOM parts are exposed for styling:
 * 
 * Part name | Description ------------------|---------------- {@code wrapper} |
 * The {@code <label>} element which wrapps the checkbox and [part="label"]
 * {@code native-checkbox} | The {@code <input type="checkbox">} element
 * {@code checkbox} | The {@code <span>} element for a custom graphical check
 * {@code label} | The {@code <span>} element for slotted text/HTML label
 * 
 * The following attributes are exposed for styling:
 * 
 * Attribute | Description -------------|------------ {@code active} | Set when
 * the checkbox is pressed down, either with mouse, touch or the keyboard.
 * {@code disabled} | Set when the checkbox is disabled. {@code focus-ring} |
 * Set when the checkbox is focused using the keyboard. {@code focused} | Set
 * when the checkbox is focused.
 */
@Generated({
		"Generator: com.vaadin.generator.ComponentGenerator#0.1.12-SNAPSHOT",
		"WebComponent: Vaadin.CheckboxElement#null", "Flow#0.1.12-SNAPSHOT"})
@Tag("vaadin-checkbox")
@HtmlImport("frontend://bower_components/vaadin-checkbox/vaadin-checkbox.html")
public class VaadinCheckboxElement<R extends VaadinCheckboxElement<R>>
		extends
			Component {

	/**
	 * Description copied from corresponding location in WebComponent:
	 * 
	 * Specify that this control should have input focus when the page loads.
	 */
	public boolean isAutofocus() {
		return getElement().getProperty("autofocus", false);
	}

	/**
	 * Description copied from corresponding location in WebComponent:
	 * 
	 * Specify that this control should have input focus when the page loads.
	 * 
	 * @param autofocus
	 * @return This instance, for method chaining.
	 */
	public R setAutofocus(boolean autofocus) {
		getElement().setProperty("autofocus", autofocus);
		return getSelf();
	}

	/**
	 * Description copied from corresponding location in WebComponent:
	 * 
	 * If true, the element currently has focus.
	 */
	public boolean isFocused() {
		return getElement().getProperty("focused", false);
	}

	/**
	 * Description copied from corresponding location in WebComponent:
	 * 
	 * If true, the element currently has focus.
	 * 
	 * @param focused
	 * @return This instance, for method chaining.
	 */
	public R setFocused(boolean focused) {
		getElement().setProperty("focused", focused);
		return getSelf();
	}

	/**
	 * Description copied from corresponding location in WebComponent:
	 * 
	 * If true, the user cannot interact with this element.
	 */
	public boolean isDisabled() {
		return getElement().getProperty("disabled", false);
	}

	/**
	 * Description copied from corresponding location in WebComponent:
	 * 
	 * If true, the user cannot interact with this element.
	 * 
	 * @param disabled
	 * @return This instance, for method chaining.
	 */
	public R setDisabled(boolean disabled) {
		getElement().setProperty("disabled", disabled);
		return getSelf();
	}

	/**
	 * Description copied from corresponding location in WebComponent:
	 * 
	 * True if the checkbox is checked.
	 */
	public boolean isChecked() {
		return getElement().getProperty("checked", false);
	}

	/**
	 * Description copied from corresponding location in WebComponent:
	 * 
	 * True if the checkbox is checked.
	 * 
	 * @param checked
	 * @return This instance, for method chaining.
	 */
	public R setChecked(boolean checked) {
		getElement().setProperty("checked", checked);
		return getSelf();
	}

	/**
	 * Description copied from corresponding location in WebComponent:
	 * 
	 * Indeterminate state of the checkbox when it's neither checked nor
	 * unchecked, but undetermined.
	 * https://developer.mozilla.org/en-US/docs/Web/
	 * HTML/Element/input/checkbox#Indeterminate_state_checkboxes
	 */
	public boolean isIndeterminate() {
		return getElement().getProperty("indeterminate", false);
	}

	/**
	 * Description copied from corresponding location in WebComponent:
	 * 
	 * Indeterminate state of the checkbox when it's neither checked nor
	 * unchecked, but undetermined.
	 * https://developer.mozilla.org/en-US/docs/Web/
	 * HTML/Element/input/checkbox#Indeterminate_state_checkboxes
	 * 
	 * @param indeterminate
	 * @return This instance, for method chaining.
	 */
	public R setIndeterminate(boolean indeterminate) {
		getElement().setProperty("indeterminate", indeterminate);
		return getSelf();
	}

	/**
	 * Description copied from corresponding location in WebComponent:
	 * 
	 * The name of the control, which is submitted with the form data.
	 */
	public String getName() {
		return getElement().getProperty("name");
	}

	/**
	 * Description copied from corresponding location in WebComponent:
	 * 
	 * The name of the control, which is submitted with the form data.
	 * 
	 * @param name
	 * @return This instance, for method chaining.
	 */
	public R setName(java.lang.String name) {
		getElement().setProperty("name", name == null ? "" : name);
		return getSelf();
	}

	/**
	 * Description copied from corresponding location in WebComponent:
	 * 
	 * The value given to the data submitted with the checkbox's name to the
	 * server when the control is inside a form.
	 */
	public String getValue() {
		return getElement().getProperty("value");
	}

	/**
	 * Description copied from corresponding location in WebComponent:
	 * 
	 * The value given to the data submitted with the checkbox's name to the
	 * server when the control is inside a form.
	 * 
	 * @param value
	 * @return This instance, for method chaining.
	 */
	public R setValue(java.lang.String value) {
		getElement().setProperty("value", value == null ? "" : value);
		return getSelf();
	}

	public void connectedCallback() {
		getElement().callFunction("connectedCallback");
	}

	public void disconnectedCallback() {
		getElement().callFunction("disconnectedCallback");
	}

	@DomEvent("checked-changed")
	public static class CheckedChangedEvent
			extends
				ComponentEvent<VaadinCheckboxElement> {
		public CheckedChangedEvent(VaadinCheckboxElement source,
				boolean fromClient) {
			super(source, fromClient);
		}
	}

	public Registration addCheckedChangedListener(
			ComponentEventListener<CheckedChangedEvent> listener) {
		return addListener(CheckedChangedEvent.class, listener);
	}

	@DomEvent("indeterminate-changed")
	public static class IndeterminateChangedEvent
			extends
				ComponentEvent<VaadinCheckboxElement> {
		public IndeterminateChangedEvent(VaadinCheckboxElement source,
				boolean fromClient) {
			super(source, fromClient);
		}
	}

	public Registration addIndeterminateChangedListener(
			ComponentEventListener<IndeterminateChangedEvent> listener) {
		return addListener(IndeterminateChangedEvent.class, listener);
	}

	/**
	 * Gets the narrow typed reference to this object. Subclasses should
	 * override this method to support method chaining using the inherited type.
	 * 
	 * @return This object casted to its type.
	 */
	protected R getSelf() {
		return (R) this;
	}
}