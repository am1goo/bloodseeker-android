package com.am1goo.bloodseeker.android.axml.core.impl;

import com.am1goo.bloodseeker.android.axml.AxmlValidator;
import com.am1goo.bloodseeker.android.axml.core.IElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import pxb.android.axml.Axml;

public class Node implements IElement {

    private final Node parent;
    private final List<Node> children;
    private final List<Attribute> attributes;
    private final AxmlValidator root;
    private final String name;

    public Node(AxmlValidator root, Node parent, String name) {
        this.parent = parent;
        this.children = new ArrayList<>();
        this.attributes = new ArrayList<>();
        this.root = root;
        this.name = name;
    }

    public AxmlValidator root() {
        return root;
    }

    public Node parent() {
        return parent;
    }

    public Node node(String name) {
        Node node = new Node(root, this, name);
        children.add(node);
        return node;
    }

    public Node attribute(String name, String value) {
        Attribute attr = new Attribute(root, this, name, value);
        attributes.add(attr);
        return this;
    }

    public boolean validate(Map<String, String> nses, List<Axml.Node> nodes) {
        List<Axml.Node> found = nodes.stream()
                .filter(x -> Objects.equals(x.name, name))
                .collect(Collectors.toList());

        if (!validateChildren(children, nses, found))
            return false;

        boolean result = false;
        for (Axml.Node node : nodes) {
            result |= validateAttributes(attributes, nses, node);
        }
        return result;
    }

    private static boolean validateChildren(List<Node> children, Map<String, String> nses, List<Axml.Node> nodes) {
        if (nodes.size() == 0)
            return false;

        if (children.size() == 0) {
            return true;
        } else {
            for (Node child : children) {
                boolean result = validateChild(child, nses, nodes);
                if (!result)
                    return false;
            }
            return true;
        }
    }

    private static boolean validateChild(Node child, Map<String, String> nses, List<Axml.Node> nodes) {
        boolean result = false;
        for (Axml.Node node : nodes) {
            result |= child.validate(nses, node.children);
        }
        return result;
    }

    private static boolean validateAttributes(List<Attribute> attributes, Map<String, String> nses, Axml.Node node) {
        if (attributes.size() == 0) {
            return true;
        }
        else {
            boolean result = false;
            for (Attribute attribute : attributes) {
                result = attribute.validate(nses, node);
            }
            return result;
        }
    }
}
