package com.am1goo.bloodseeker.android.axml.core.impl;

import com.am1goo.bloodseeker.android.axml.AxmlValidator;
import com.am1goo.bloodseeker.android.axml.core.IElement;

import java.util.Map;
import java.util.Objects;

import pxb.android.axml.Axml;

public class Attribute implements IElement {

    private final AxmlValidator root;
    private final Node node;
    private final String name;
    private final String value;

    Attribute(AxmlValidator root, Node node, String name, String value) {
        this.root = root;
        this.node = node;
        this.name = name;
        this.value = value;
    }

    public AxmlValidator root() {
        return root;
    }

    public Node node() {
        return node;
    }

    public Node parent() {
        return node.parent();
    }

    public Node attribute(String name, String value) {
        Node parent = node();
        return parent.attribute(name, value);
    }

    public boolean validate(Map<String, String> nses, Axml.Node node) {
        for (Axml.Node.Attr attr : node.attrs) {
            String expectedName = prepareName(nses, name);
            if (!Objects.equals(attr.name, expectedName))
                continue;
            if (!Objects.equals(attr.value, value))
                continue;
            return true;
        }
        return false;
    }

    private static String prepareName(Map<String, String> nses, String name) {
        if (!name.contains(":"))
            return name;

        String[] splitted = name.split(":");
        String attrPrefix = splitted[0];
        String attrName = splitted[1];
        if (!nses.containsKey(attrPrefix))
            return attrName;

        String uri = nses.get(attrPrefix);
        if (uri == null)
            return attrName;

        return uri + ":" + attrName;
    }
}