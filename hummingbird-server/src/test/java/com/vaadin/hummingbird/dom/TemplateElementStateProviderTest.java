/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.hummingbird.dom;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.hummingbird.StateNode;
import com.vaadin.hummingbird.dom.impl.BasicElementStateProvider;
import com.vaadin.hummingbird.dom.impl.TemplateElementStateProvider;
import com.vaadin.hummingbird.nodefeature.ComponentMapping;
import com.vaadin.hummingbird.nodefeature.ModelMap;
import com.vaadin.hummingbird.nodefeature.NodeFeature;
import com.vaadin.hummingbird.nodefeature.NodeFeatureRegistry;
import com.vaadin.hummingbird.nodefeature.ParentGeneratorHolder;
import com.vaadin.hummingbird.nodefeature.TemplateEventHandlerNames;
import com.vaadin.hummingbird.nodefeature.TemplateMap;
import com.vaadin.hummingbird.nodefeature.TemplateOverridesMap;
import com.vaadin.hummingbird.template.ElementTemplateBuilder;
import com.vaadin.hummingbird.template.ModelValueBindingProvider;
import com.vaadin.hummingbird.template.StaticBindingValueProvider;
import com.vaadin.hummingbird.template.TemplateNode;
import com.vaadin.hummingbird.template.TemplateNodeBuilder;
import com.vaadin.hummingbird.template.TextTemplateBuilder;
import com.vaadin.hummingbird.template.parser.TemplateParser;
import com.vaadin.hummingbird.template.parser.TemplateResolver;

import elemental.json.Json;
import elemental.json.JsonObject;

public class TemplateElementStateProviderTest {

    public static class NullTemplateResolver implements TemplateResolver {
        @Override
        public InputStream resolve(String relativeFilename) throws IOException {
            throw new IOException("Null resolver is used");
        }
    }

    @Test
    public void testEmptyElement() {
        ElementTemplateBuilder builder = new ElementTemplateBuilder("div");

        Element element = createElement(builder);

        Assert.assertEquals("div", element.getTag());
        Assert.assertFalse(element.isTextNode());

        Assert.assertNull(element.getParent());
        Assert.assertEquals(0, element.getChildCount());

        Assert.assertEquals(0, element.getPropertyNames().count());
        Assert.assertEquals(0, element.getAttributeNames().count());
    }

    @Test
    public void testElementStringProperties() {
        ElementTemplateBuilder builder = new ElementTemplateBuilder("div")
                .setProperty("a1", new StaticBindingValueProvider("v1"))
                .setProperty("a2", new StaticBindingValueProvider("v2"));

        Element element = createElement(builder);

        Assert.assertEquals("v1", element.getProperty("a1"));
        Assert.assertEquals("v2", element.getProperty("a2"));

        Assert.assertEquals(new HashSet<>(Arrays.asList("a1", "a2")),
                element.getPropertyNames().collect(Collectors.toSet()));
    }

    @Test
    public void testElementBooleanProperties() {
        ElementTemplateBuilder builder = new ElementTemplateBuilder("div")
                .setProperty("a", new ModelValueBindingProvider("key"));

        Element element = createElement(builder);

        StateNode stateNode = element.getNode();
        stateNode.getFeature(ModelMap.class).setValue("key", Boolean.TRUE);

        Assert.assertEquals(Boolean.TRUE, element.getPropertyRaw("a"));

        Assert.assertEquals(new HashSet<>(Arrays.asList("a")),
                element.getPropertyNames().collect(Collectors.toSet()));
    }

    @Test
    public void testElementDoubleProperties() {
        ElementTemplateBuilder builder = new ElementTemplateBuilder("div")
                .setProperty("a", new ModelValueBindingProvider("key"));

        Element element = createElement(builder);

        StateNode stateNode = element.getNode();
        stateNode.getFeature(ModelMap.class).setValue("key", 1.1d);

        Assert.assertEquals(1.1d, element.getPropertyRaw("a"));

        Assert.assertEquals(new HashSet<>(Arrays.asList("a")),
                element.getPropertyNames().collect(Collectors.toSet()));
    }

    @Test
    public void testElementJsonProperties() {
        ElementTemplateBuilder builder = new ElementTemplateBuilder("div")
                .setProperty("a", new ModelValueBindingProvider("key"));

        Element element = createElement(builder);

        StateNode stateNode = element.getNode();
        JsonObject json = Json.createObject();
        json.put("foo", "bar");
        stateNode.getFeature(ModelMap.class).setValue("key", json);

        Assert.assertEquals(json, element.getPropertyRaw("a"));

        Assert.assertEquals(new HashSet<>(Arrays.asList("a")),
                element.getPropertyNames().collect(Collectors.toSet()));
    }

    @Test
    public void testElementAttributes() {
        ElementTemplateBuilder builder = new ElementTemplateBuilder("div")
                .setAttribute("a1", new StaticBindingValueProvider("v1"))
                .setAttribute("a2", new StaticBindingValueProvider("v2"));

        Element element = createElement(builder);

        Assert.assertEquals("v1", element.getAttribute("a1"));
        Assert.assertEquals("v2", element.getAttribute("a2"));

        Assert.assertEquals(new HashSet<>(Arrays.asList("a1", "a2")),
                element.getAttributeNames().collect(Collectors.toSet()));
    }

    @Test
    public void testTemplateInBasicElement() {
        Element templateElement = createElement(
                new ElementTemplateBuilder("template"));
        Element basicElement = new Element("basic");

        basicElement.appendChild(templateElement);

        Element child = basicElement.getChild(0);
        Assert.assertEquals("template", child.getTag());
        Assert.assertEquals(templateElement, child);

        Element parent = templateElement.getParent();
        Assert.assertEquals("basic", parent.getTag());
        Assert.assertEquals(basicElement, parent);
    }

    @Test
    public void testNestedTemplateElements() {
        ElementTemplateBuilder builder = new ElementTemplateBuilder("parent")
                .addChild(new ElementTemplateBuilder("child0"))
                .addChild(new ElementTemplateBuilder("child1"));

        Element element = createElement(builder);

        Assert.assertEquals(2, element.getChildCount());

        Element child0 = element.getChild(0);
        Assert.assertEquals("child0", child0.getTag());
        Assert.assertEquals(element, child0.getParent());

        Element child1 = element.getChild(1);
        Assert.assertEquals("child1", child1.getTag());
        Assert.assertEquals(element, child1.getParent());
    }

    @Test
    public void testTextNode() {
        TextTemplateBuilder builder = new TextTemplateBuilder(
                new StaticBindingValueProvider("Hello"));

        Element element = createElement(builder);

        Assert.assertTrue(element.isTextNode());
        Assert.assertEquals("Hello", element.getTextContent());
    }

    @Test
    public void testTextNodeInParent() {
        ElementTemplateBuilder builder = new ElementTemplateBuilder("div")
                .addChild(new TextTemplateBuilder(
                        new StaticBindingValueProvider("Hello")));

        Element element = createElement(builder);

        Assert.assertEquals("div", element.getTag());
        Assert.assertEquals("Hello", element.getTextContent());

        Element child = element.getChild(0);
        Assert.assertTrue(child.isTextNode());
        Assert.assertEquals(element, child.getParent());
    }

    @Test
    public void testAppendOverrideChild() {
        Element child = ElementFactory.createAnchor();

        Element parent = createElement("<div></div>");

        parent.appendChild(child);

        List<Element> children = parent.getChildren()
                .collect(Collectors.toList());

        Assert.assertEquals(1, children.size());

        Assert.assertEquals(child, children.get(0));

        Assert.assertEquals(parent, child.getParent());
    }

    @Test
    public void testRemoveOverrideChildByIndex() {
        Element child = ElementFactory.createAnchor();

        Element parent = createElement("<div></div>");

        parent.appendChild(child);

        parent.removeChild(0);

        Assert.assertEquals(0, parent.getChildCount());
        Assert.assertFalse(parent.getChildren().findFirst().isPresent());
    }

    @Test
    public void testRemoveOverrideChildByInstance() {
        Element child = new Element("a");

        Element parent = createElement("<div></div>");

        parent.appendChild(child);

        parent.removeChild(child);

        Assert.assertEquals(0, parent.getChildCount());
        Assert.assertFalse(parent.getChildren().findFirst().isPresent());
    }

    @Test
    public void testRemoveAllOverrideChildren() {
        Element child = ElementFactory.createAnchor();

        Element parent = createElement("<div></div>");

        parent.appendChild(child);

        parent.removeAllChildren();

        Assert.assertEquals(0, parent.getChildCount());
        Assert.assertFalse(parent.getChildren().findFirst().isPresent());
    }

    @Test(expected = IllegalStateException.class)
    public void testAppendWithTemplateChildren() {
        Element parent = createElement("<div><span></span></div>");

        parent.appendChild(new Element("div"));
    }

    @Test(expected = IllegalStateException.class)
    public void testAppendWithTemplateText() {
        Element parent = createElement("<div>Text</div>");

        parent.appendChild(new Element("div"));
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveByIndexWithTemplateChildren() {
        Element parent = createElement("<div><span></span></div>");

        parent.removeChild(0);
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveByInstanceWithTemplateChildren() {
        Element parent = createElement("<div><span></span></div>");

        parent.removeChild(parent.getChild(0));
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveAllWithTemplateChildren() {
        Element parent = createElement("<div><span></span></div>");

        parent.removeAllChildren();
    }

    @Test
    public void emptyChildSlot() {
        Element parent = createElement("<div>@child@</div>");

        Assert.assertEquals(0, parent.getChildCount());
    }

    @Test
    public void populatedChildSlot() {
        Element div = createElement("<div><span>@child@</span></div>");

        Element span = div.getChild(0);

        Element child = ElementFactory.createSpan("child");

        div.getNode().getFeature(TemplateMap.class).setChild(child.getNode());

        Assert.assertEquals(1, span.getChildCount());
        Assert.assertEquals(child, span.getChild(0));

        Assert.assertEquals(span, child.getParent());
    }

    @Test
    public void emptyChildSlotOrder() {
        Element parent = createElement(
                "<div><before></before>@child@<after></after></div>");

        Assert.assertEquals(2, parent.getChildCount());

        Assert.assertEquals(Arrays.asList("before", "after"),
                parent.getChildren().map(Element::getTag)
                        .collect(Collectors.toList()));
    }

    @Test
    public void populatedChildSlotOrder() {
        Element parent = createElement(
                "<div><before></before>@child@<after></after></div>");
        Element child = new Element("child");

        parent.getNode().getFeature(TemplateMap.class)
                .setChild(child.getNode());

        Assert.assertEquals(3, parent.getChildCount());

        Assert.assertEquals(Arrays.asList("before", "child", "after"),
                parent.getChildren().map(Element::getTag)
                        .collect(Collectors.toList()));
    }

    public void clearChildSlot_resetChild() {
        Element parent = createElement("<div>@child@</div>");
        Element child = ElementFactory.createSpan("child");

        TemplateMap templateMap = parent.getNode()
                .getFeature(TemplateMap.class);
        templateMap.setChild(child.getNode());

        Assert.assertEquals(1, parent.getChildCount());

        templateMap.setChild(null);

        Assert.assertEquals(0, parent.getChildCount());
        Assert.assertEquals(0, parent.getChildren().count());
        Assert.assertNull(child.getParent());
    }

    // Currently not implemented, but we might want to support this at some
    // point
    @Test(expected = IllegalStateException.class)
    public void clearChildSlot_removeElement() {
        Element parent = createElement("<div>@child@</div>");
        Element child = ElementFactory.createSpan("child");

        parent.getNode().getFeature(TemplateMap.class)
                .setChild(child.getNode());

        child.removeFromParent();
    }

    @Test
    public void textInChildSlot() {
        Element parent = createElement("<div>@child@</div>");
        Element child = Element.createText("The text");

        parent.getNode().getFeature(TemplateMap.class)
                .setChild(child.getNode());

        Assert.assertEquals(1, parent.getChildCount());
        Assert.assertEquals(1, parent.getChildren().count());
        Assert.assertEquals(parent, child.getParent());

        Assert.assertEquals("The text", parent.getTextContent());
    }

    @Test
    public void templateInChildSlot() {
        Element parent = createElement("<div>@child@</div>");
        Element child = createElement("<span>The text</span>");

        parent.getNode().getFeature(TemplateMap.class)
                .setChild(child.getNode());

        Assert.assertEquals(1, parent.getChildCount());
        Assert.assertEquals(1, parent.getChildren().count());
        Assert.assertEquals(parent, child.getParent());

        Assert.assertEquals("The text", parent.getTextContent());
    }

    @Test(expected = IllegalStateException.class)
    public void setChildWithoutSlot() {
        Element parent = createElement("<div>No child slot here</div>");
        Element child = ElementFactory.createDiv("child");

        parent.getNode().getFeature(TemplateMap.class)
                .setChild(child.getNode());
    }

    @Test
    public void templateBoundClassAttribute() {
        Element element = createElement("<div class='foo bar'></div>");

        Assert.assertEquals("foo bar", element.getAttribute("class"));

        assertClassList(element.getClassList(), "foo", "bar");
    }

    @Test
    public void dynamicClassNames() {
        Element element = createElement(
                "<div class='foo' [class.bar]=hasBar [class.baz]=hasBaz></div>");
        ClassList classList = element.getClassList();

        Assert.assertEquals("foo", element.getAttribute("class"));

        assertClassList(classList, "foo");
        assertNotClassList(classList, "bar", "baz");

        ModelMap modelMap = element.getNode().getFeature(ModelMap.class);

        modelMap.setValue("hasBar", "");
        modelMap.setValue("hasBaz", "yes");
        assertClassList(classList, "foo", "baz");
        assertNotClassList(classList, "bar");

        modelMap.setValue("hasBar", 5);
        modelMap.setValue("hasBaz", 0);
        assertClassList(classList, "foo", "bar");
        assertNotClassList(classList, "baz");

        modelMap.setValue("hasBar", false);
        modelMap.setValue("hasBaz", true);
        assertClassList(classList, "foo", "baz");
        assertNotClassList(classList, "bar");
    }

    @Test
    public void setProperty_regularProperty_elementDelegatesPropertyToOverrideNode() {
        TemplateNode node = TemplateParser.parse("<div></div>",
                new NullTemplateResolver());
        Element element = createElement(node);
        element.setProperty("prop", "foo");

        StateNode overrideNode = element.getNode()
                .getFeature(TemplateOverridesMap.class).get(node, false);
        Assert.assertTrue(BasicElementStateProvider.get()
                .hasProperty(overrideNode, "prop"));
        Assert.assertEquals("foo", BasicElementStateProvider.get()
                .getProperty(overrideNode, "prop"));
        List<String> props = BasicElementStateProvider.get()
                .getPropertyNames(overrideNode).collect(Collectors.toList());
        Assert.assertEquals(1, props.size());
        Assert.assertEquals("prop", props.get(0));
    }

    @Test
    public void setProperty_regularProperty_hasPropertyAndHasProperValue() {
        TemplateNode node = TemplateParser.parse("<div></div>",
                new NullTemplateResolver());
        Element element = createElement(node);
        element.setProperty("prop", "foo");

        Assert.assertTrue(element.hasProperty("prop"));
        Assert.assertEquals("foo", element.getProperty("prop"));
        List<String> props = element.getPropertyNames()
                .collect(Collectors.toList());
        Assert.assertEquals(1, props.size());
        Assert.assertEquals("prop", props.get(0));
    }

    @Test
    public void setRegularProperty_templateHasBoundProperty_hasPropertyAndHasProperValue() {
        TemplateNode node = TemplateParser.parse("<div [foo]='bar'></div>",
                new NullTemplateResolver());
        Element element = createElement(node);
        element.setProperty("prop", "foo");

        Assert.assertTrue(element.hasProperty("prop"));
        Assert.assertEquals("foo", element.getProperty("prop"));
        Set<String> props = element.getPropertyNames()
                .collect(Collectors.toSet());
        Assert.assertEquals(2, props.size());
        Assert.assertTrue(props.contains("foo"));
        Assert.assertTrue(props.contains("prop"));
    }

    @Test
    public void removeRegularProperty_templateHasBoundProperty_hasPropertyAndHasProperValue() {
        TemplateNode node = TemplateParser.parse("<div [foo]='bar'></div>",
                new NullTemplateResolver());
        Element element = createElement(node);
        element.setProperty("prop", "foo");

        element.removeProperty("prop");

        Assert.assertFalse(element.hasProperty("prop"));
        Set<String> props = element.getPropertyNames()
                .collect(Collectors.toSet());
        Assert.assertEquals(1, props.size());
        Assert.assertTrue(props.contains("foo"));
    }

    @Test
    public void removeProperty_regularProperty_hasNoProperty() {
        TemplateNode node = TemplateParser.parse("<div></div>",
                new NullTemplateResolver());
        Element element = createElement(node);
        element.setProperty("prop", "foo");
        element.removeProperty("prop");

        Assert.assertFalse(element.hasProperty("prop"));
        List<String> props = element.getPropertyNames()
                .collect(Collectors.toList());
        Assert.assertEquals(0, props.size());
    }

    @Test
    public void removeProperty_regularProperty_elementDelegatesPropertyToOverrideNode() {
        TemplateNode node = TemplateParser.parse("<div></div>",
                new NullTemplateResolver());
        Element element = createElement(node);
        element.removeProperty("prop");

        StateNode overrideNode = element.getNode()
                .getFeature(TemplateOverridesMap.class).get(node, false);
        Assert.assertFalse(BasicElementStateProvider.get()
                .hasProperty(overrideNode, "prop"));
        List<String> props = BasicElementStateProvider.get()
                .getPropertyNames(overrideNode).collect(Collectors.toList());
        Assert.assertEquals(0, props.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setProperty_boundProperty_throwException() {
        Element element = createElement("<div [prop]='value'></div>");
        element.setProperty("prop", "foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeProperty_boundProperty_throwException() {
        Element element = createElement("<div [prop]='value'></div>");
        element.removeProperty("prop");
    }

    private void assertClassList(ClassList classList, String... expectedNames) {
        HashSet<String> expectedSet = new HashSet<>(
                Arrays.asList(expectedNames));

        Assert.assertEquals(expectedNames.length, classList.size());
        Assert.assertEquals(expectedNames.length, classList.stream().count());
        Assert.assertEquals(expectedNames.length,
                iteratorToStream(classList.iterator()).count());

        for (String className : expectedNames) {
            Assert.assertTrue(classList.contains(className));
        }

        Assert.assertEquals(expectedSet, classList);
        Assert.assertEquals(classList, expectedSet);

        // Does classList.iterator() contain the right values?
        Assert.assertEquals(expectedSet, new HashSet<>(classList));

        // Does classList.stream() contain the right values?
        Assert.assertEquals(expectedSet,
                classList.stream().collect(Collectors.toSet()));
    }

    private Stream<String> iteratorToStream(Iterator<String> iterator) {
        return StreamSupport.stream(Spliterators
                .spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
    }

    private void assertNotClassList(ClassList classList,
            String... forbiddenClassNames) {
        for (String className : forbiddenClassNames) {
            Assert.assertFalse(classList.contains(className));
        }

    }

    @Test(expected = UnsupportedOperationException.class)
    public void classListAddThrows() {
        // Not allowed until we explicitly support override node data for
        // ClassList
        createElement("<input>").getClassList().add("foo");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void classListRemoveThrows() {
        createElement("<input class=foo>").getClassList().remove("foo");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void classListIteratorRemoveThrows() {
        Iterator<String> iterator = createElement("<input class=foo>")
                .getClassList().iterator();
        iterator.next();
        iterator.remove();
    }

    @Test
    public void requiredNodeFeatures() {
        @SuppressWarnings("unchecked")
        Class<? extends NodeFeature>[] requiredFeatures = new Class[] {
                ModelMap.class, TemplateOverridesMap.class };

        TemplateElementStateProvider provider = (TemplateElementStateProvider) createElement(
                "<div></div").getStateProvider();

        // Test that a node with all required features is accepted
        Assert.assertTrue(provider.supports(new StateNode(requiredFeatures)));

        // Test that removing any feature makes it non-accepted
        for (int i = 0; i < requiredFeatures.length; i++) {
            ArrayList<Class<? extends NodeFeature>> list = new ArrayList<>(
                    Arrays.asList(requiredFeatures));
            list.remove(i);
            Assert.assertFalse(provider
                    .supports(new StateNode(list.toArray(new Class[0]))));
        }
    }

    @Test
    public void rootNodeFeatures() {
        assertHasFeatures(TemplateElementStateProvider.createRootNode(),
                ModelMap.class, TemplateOverridesMap.class, TemplateMap.class,
                ComponentMapping.class, ParentGeneratorHolder.class,
                TemplateEventHandlerNames.class);
    }

    @Test
    public void subModelNodeFeatures() {
        assertHasFeatures(TemplateElementStateProvider.createSubModelNode(),
                ModelMap.class, TemplateOverridesMap.class);
    }

    @SafeVarargs
    private static void assertHasFeatures(StateNode node,
            Class<? extends NodeFeature>... features) {
        Set<Class<? extends NodeFeature>> featureSet = new HashSet<>(
                Arrays.asList(features));

        for (Class<? extends NodeFeature> feature : NodeFeatureRegistry
                .getFeatures()) {
            boolean has = node.hasFeature(feature);
            if (featureSet.contains(feature)) {
                Assert.assertTrue("node should have the feature " + feature,
                        has);
            } else {
                Assert.assertFalse("node shouldn't have the feature " + feature,
                        has);
            }
        }
    }

    private static Element createElement(String template) {
        return createElement(
                TemplateParser.parse(template, new NullTemplateResolver()));
    }

    private static Element createElement(TemplateNodeBuilder builder) {
        return createElement(builder.build(null));
    }

    public static Element createElement(TemplateNode templateNode) {
        StateNode stateNode = TemplateElementStateProvider.createRootNode();
        stateNode.getFeature(TemplateMap.class).setRootTemplate(templateNode);

        return Element.get(stateNode);
    }

    public static Optional<StateNode> getOverrideNode(Element element) {
        StateNode node = element.getNode();
        if (!node.hasFeature(TemplateOverridesMap.class)) {
            return Optional.empty();
        } else {
            ElementStateProvider stateProvider = element.getStateProvider();
            assert stateProvider instanceof TemplateElementStateProvider;
            return Optional.of(node.getFeature(TemplateOverridesMap.class)
                    .get(((TemplateElementStateProvider) stateProvider)
                            .getTemplateNode(), false));
        }
    }

}
