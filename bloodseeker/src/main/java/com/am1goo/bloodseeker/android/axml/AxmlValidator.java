package com.am1goo.bloodseeker.android.axml;

import com.am1goo.bloodseeker.android.axml.core.impl.Node;

import java.util.HashMap;
import java.util.Map;

import pxb.android.axml.Axml;

public class AxmlValidator {

    private final Node root;

    public AxmlValidator(String name) {
        root = new Node(this, null, name);
    }

    public Node root() {
        return root;
    }

    public boolean validate(Axml axml) {
        final Map<String, String> nses = new HashMap<>();
        for (Axml.Ns ns : axml.nses) {
            if (nses.containsKey(ns.prefix))
                continue;
            nses.put(ns.prefix, ns.uri);
        }
        return root.validate(nses, axml.firsts);
    }
}
