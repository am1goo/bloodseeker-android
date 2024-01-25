package com.am1goo.bloodseeker.android.trails;

import android.app.Activity;

import com.am1goo.bloodseeker.BloodseekerExceptions;
import com.am1goo.bloodseeker.android.AndroidAppContext;
import com.am1goo.bloodseeker.IResult;
import com.am1goo.bloodseeker.utilities.IOUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import pxb.android.axml.Axml;
import pxb.android.axml.AxmlReader;

public class AndroidManifestXmlTrail extends BaseAndroidTrail {

    private static final String androidManifestFilename = "AndroidManifest.xml";
    private static final String androidManifestNamespace = "http://schemas.android.com/apk/res/android";

    private final Looker[] lookers;
    private final BloodseekerExceptions exceptions;

    public AndroidManifestXmlTrail(String json) {
        this(new String[] { json } );
    }

    public AndroidManifestXmlTrail(String[] jsons) {
        this.lookers = new Looker[jsons.length];
        this.exceptions = new BloodseekerExceptions();

        for (int i = 0; i < jsons.length; ++i) {
            String json = jsons[i];
            try {
                JSONObject deserializer = new JSONObject(json);
                Looker looker = new Looker();

                JSONArray nodesArray = deserializer.getJSONArray("nodes");
                int nodesLength = nodesArray.length();
                String[] nodes = new String[nodesLength];
                for (int ii = 0; ii < nodesLength; ++ii) {
                    nodes[ii] = nodesArray.getString(ii);
                }
                looker.nodes = nodes;
                looker.attribute = deserializer.getString("attribute");
                looker.value = deserializer.getString("value");
                looker.fixIfNeed();

                this.lookers[i] = looker;
            }
            catch (Exception ex){
                exceptions.add(this, ex);
            }
        }
    }

    @Override
    public void seek(List<IResult> result, BloodseekerExceptions exceptions) {
        exceptions.add(this.exceptions);

        AndroidAppContext context = getContext();
        if (context == null)
            return;

        Activity activity = context.getActivity();
        if (activity == null)
            return;

        JarFile jarFile = context.getBaseApk();
        if (jarFile == null)
            return;

        ZipEntry zipEntry = jarFile.getEntry(androidManifestFilename);
        if (zipEntry == null) {
            exceptions.add(this, new FileNotFoundException(androidManifestFilename));
            return;
        }

        InputStream inputStream = null;
        try {
            inputStream = jarFile.getInputStream(zipEntry);

            byte[] bytes = IOUtilities.readAllBytes(inputStream);
            AxmlReader reader = new AxmlReader(bytes);
            Axml axml = new Axml();
            reader.accept(axml);

            Map<String, String> nses = new HashMap<String, String>();
            for (Axml.Ns ns : axml.nses) {
                String uri = ns.uri != null ? ns.uri : androidManifestNamespace;
                nses.put(ns.prefix, uri);
            }

            for (Looker looker : lookers) {
                String[] paths = looker.nodes;
                String attribute = looker.attribute;
                String expectedValue = looker.value;

                Axml.Node foundNode = findAxmlNode(axml.firsts, paths);
                if (foundNode == null) {
                    String pathsAsStr = String.join("/", paths);
                    result.add(new NodeNotFoundResult(pathsAsStr));
                    continue;
                }

                Axml.Node.Attr attr = findAxmlAttr(foundNode.attrs, nses, attribute);
                if (attr == null) {
                    String pathsAsStr = String.join("/", paths);
                    result.add(new AttrNotFoundResult(pathsAsStr, attribute));
                    continue;
                }

                String actualValue = String.valueOf(attr.value);
                if (!Objects.equals(actualValue, expectedValue)) {
                    String pathsAsStr = String.join("/", paths);
                    result.add(new InvalidValueResult(pathsAsStr, actualValue, expectedValue));
                    continue;
                }

                //do nothing, everything okay
            }
        }
        catch (IOException ex) {
            exceptions.add(this, ex);
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (IOException ex) {
                    exceptions.add(this, ex);
                }
            }
        }
    }

    private static Axml.Node findAxmlNode(List<Axml.Node> nodes, String[] paths) {
        if (paths.length == 0)
            return null;

        String parent = paths[0];
        String[] children = paths.length > 1 ? Arrays.copyOfRange(paths, 1, paths.length) : null;

        return findNodeByName(nodes, parent, children);
    }

    private static Axml.Node findNodeByName(List<Axml.Node> nodes, String name, String[] children) {
        for (Axml.Node node : nodes) {
            if (Objects.equals(node.name, name)) {
                if (children != null) {
                    Axml.Node found = findAxmlNode(node.children, children);
                    if (found != null)
                        return found;
                }
                else {
                    return node;
                }
            }
        }
        return null;
    }

    private static Axml.Node.Attr findAxmlAttr(List<Axml.Node.Attr> attrs, Map<String, String> nses, String name) {
        if (attrs == null)
            return null;

        String attrNs = null;
        String attrName = name;
        if (attrName.contains(":")) {
            String[] splitted = attrName.split(":");
            attrNs = splitted[0];
            if (nses.containsKey(attrNs))
                attrNs = nses.get(attrNs);
            attrName = splitted[1];
        }

        for (Axml.Node.Attr attr : attrs) {
            if ((attrNs == null || Objects.equals(attr.ns, attrNs)) && Objects.equals(attr.name, attrName)) {
                return attr;
            }
        }
        return null;
    }

    public class Looker {

        public static final String rootManifestNode = "manifest";

        public String[] nodes;
        public String attribute;
        public String value;

        public void fixIfNeed() {
            nodes = fixIfNeed(nodes);
        }

        private String[] fixIfNeed(String[] nodes) {
            if (nodes.length > 0 && Objects.equals(nodes[0], rootManifestNode))
                return nodes;

            String[] array = new String[nodes.length + 1];
            array[0] = rootManifestNode;
            System.arraycopy(nodes, 0, array, 1, nodes.length);
            return array;
        }
    }

    public class NodeNotFoundResult implements IResult {
        private final String nodePath;

        public NodeNotFoundResult(String nodePath) {
            this.nodePath = nodePath;
        }

        @Override
        public String toString() {
            return "AndroidManifest.xml doesn't have node " + nodePath;
        }
    }

    public class AttrNotFoundResult implements IResult {
        private final String nodePath;
        private final String attr;

        public AttrNotFoundResult(String nodePath, String attr) {
            this.nodePath = nodePath;
            this.attr = attr;
        }

        @Override
        public String toString() {
            return "AndroidManifest.xml doesn't have attr " + attr + " in node " + nodePath;
        }
    }

    public class InvalidValueResult implements IResult {
        private final String nodePath;
        private final String actualValue;
        private final String expectedValue;

        public InvalidValueResult(String nodePath, String actualValue, String expectedValue) {
            this.nodePath = nodePath;
            this.actualValue = actualValue;
            this.expectedValue = expectedValue;
        }

        @Override
        public String toString() {
            return "AndroidManifest.xml node " + nodePath + " has wrong value (actual '" + actualValue + "', expected '" + expectedValue + "')";
        }
    }
}
