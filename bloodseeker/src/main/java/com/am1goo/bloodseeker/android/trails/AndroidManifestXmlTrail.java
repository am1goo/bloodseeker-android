package com.am1goo.bloodseeker.android.trails;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.am1goo.bloodseeker.BloodseekerExceptions;
import com.am1goo.bloodseeker.android.AndroidAppContext;
import com.am1goo.bloodseeker.IResult;
import com.am1goo.bloodseeker.update.IRemoteUpdateTrail;
import com.am1goo.bloodseeker.update.RemoteUpdateFile;
import com.am1goo.bloodseeker.update.RemoteUpdateReader;
import com.am1goo.bloodseeker.update.RemoteUpdateSerializable;
import com.am1goo.bloodseeker.update.RemoteUpdateWriter;
import com.am1goo.bloodseeker.utilities.IOUtilities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import pxb.android.axml.Axml;
import pxb.android.axml.AxmlReader;

public class AndroidManifestXmlTrail extends BaseAndroidTrail implements IRemoteUpdateTrail {

    private static final String androidManifestFilename = "AndroidManifest.xml";
    private static final String androidManifestNamespace = "http://schemas.android.com/apk/res/android";

    private static final short VERSION = 1;
    private short version;
    private Looker[] lookers;
    private BloodseekerExceptions exceptions;

    private AndroidManifestXmlTrail() {
    }

    public AndroidManifestXmlTrail(Looker[] lookers) {
        this.version = VERSION;
        this.lookers = lookers;
        this.exceptions = new BloodseekerExceptions();

        for (int i = 0; i < lookers.length; ++i) {
            Looker looker = lookers[i];
            looker.fixIfNeed();
        }
    }

    @Override
    public void load(RemoteUpdateReader reader) throws Exception {
        this.version = reader.readVersion();
        this.lookers = reader.readArray(Looker.class);
    }

    @Override
    public void save(RemoteUpdateWriter writer) throws Exception {
        writer.writeVersion(version);
        writer.writeArray(lookers);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        AndroidManifestXmlTrail that = (AndroidManifestXmlTrail) o;
        return Arrays.equals(lookers, that.lookers);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(lookers);
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

        JarFile jarFile = context.getBaseApkJar();
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
                final String[] paths = looker.getNodes();
                final String attribute = looker.getAttribute();
                final String expectedValue = looker.getValue();
                final Looker.Condition condition = looker.getCondition();

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
                boolean equals = Objects.equals(actualValue, expectedValue);
                switch (condition) {
                    case Eq:
                        if (!equals) {
                            String pathsAsStr = String.join("/", paths);
                            result.add(new InvalidValueResult(pathsAsStr, condition.toString(), actualValue, expectedValue));
                            continue;
                        }
                        break;
                    case NotEq:
                        if (equals) {
                            String pathsAsStr = String.join("/", paths);
                            result.add(new InvalidValueResult(pathsAsStr, condition.toString(), actualValue, expectedValue));
                            continue;
                        }
                        break;
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

    public static class Looker implements RemoteUpdateSerializable {

        public static final String rootManifestNode = "manifest";

        private static final short VERSION = 1;
        private short version;
        private String[] nodes;
        private String attribute;
        private String value;
        private Condition condition;

        public Looker() {
            version = VERSION;
        }

        public Looker(String[] nodes, String attribute, String value, Condition condition) {
            this();
            this.nodes = nodes;
            this.attribute = attribute;
            this.value = value;
            this.condition = condition;
        }

        @Override
        public void load(RemoteUpdateReader reader) throws Exception {
            version = reader.readVersion();
            nodes = reader.readStringArray();
            attribute = reader.readString();
            value = reader.readString();
            condition = Condition.valueOf(reader.readInt());
        }

        @Override
        public void save(RemoteUpdateWriter writer) throws Exception {
            writer.writeVersion(version);
            writer.writeStringArray(nodes, RemoteUpdateFile.CHARSET_NAME);
            writer.writeString(attribute, RemoteUpdateFile.CHARSET_NAME);
            writer.writeString(value, RemoteUpdateFile.CHARSET_NAME);
            writer.writeInt(condition.getValue());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;

            if (o == null || getClass() != o.getClass())
                return false;

            Looker looker = (Looker) o;
            return Arrays.equals(nodes, looker.nodes)
                    && Objects.equals(attribute, looker.attribute)
                    && Objects.equals(value, looker.value)
                    && condition == looker.condition;
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(attribute, value, condition);
            result = 31 * result + Arrays.hashCode(nodes);
            return result;
        }

        public short getVersion() {
            return version;
        }

        public String[] getNodes() {
            return nodes;
        }

        public void setNodes(String[] nodes) {
            this.nodes = nodes;
        }

        public String getAttribute() {
            return attribute;
        }

        public void setAttribute(String attribute) {
            this.attribute = attribute;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Condition getCondition() {
            if (condition == null)
                return Condition.Eq;

            return condition;
        }

        public void setCondition(Condition condition) {
            this.condition = condition;
        }

        public void fixIfNeed() {
            nodes = fixIfNeed(nodes);
        }

        private static String[] fixIfNeed(String[] nodes) {
            if (nodes.length > 0 && Objects.equals(nodes[0], rootManifestNode))
                return nodes;

            String[] array = new String[nodes.length + 1];
            array[0] = rootManifestNode;
            System.arraycopy(nodes, 0, array, 1, nodes.length);
            return array;
        }

        public enum Condition {
            Eq(0, "Equals"),
            NotEq(1, "NotEquals");

            private final int value;
            private final String label;

            Condition(int value, String label) {
                this.value = value;
                this.label = label;
            }

            public int getValue() {
                return value;
            }

            public String getLabel() {
                return label;
            }

            public static Condition valueOf(int value) {
                for (Condition condition : values()) {
                    if (condition.value == value) {
                        return condition;
                    }
                }
                return Condition.Eq;
            }

            public static Condition labelOf(String label) {
                for (Condition condition : values()) {
                    if (condition.label.equals(label)) {
                        return condition;
                    }
                }
                return Condition.Eq;
            }
        }
    }

    public static class NodeNotFoundResult implements IResult {
        private final String nodePath;

        public NodeNotFoundResult(String nodePath) {
            this.nodePath = nodePath;
        }

        @NonNull
        @Override
        public String toString() {
            return "AndroidManifest.xml doesn't have node " + nodePath;
        }
    }

    public static class AttrNotFoundResult implements IResult {
        private final String nodePath;
        private final String attr;

        public AttrNotFoundResult(String nodePath, String attr) {
            this.nodePath = nodePath;
            this.attr = attr;
        }

        @NonNull
        @Override
        public String toString() {
            return "AndroidManifest.xml doesn't have attr " + attr + " in node " + nodePath;
        }
    }

    public static class InvalidValueResult implements IResult {
        private final String nodePath;
        private final String condition;
        private final String actualValue;
        private final String expectedValue;

        public InvalidValueResult(String nodePath, String condition, String actualValue, String expectedValue) {
            this.nodePath = nodePath;
            this.condition = condition;
            this.actualValue = actualValue;
            this.expectedValue = expectedValue;
        }

        @NonNull
        @Override
        public String toString() {
            return "AndroidManifest.xml node " + nodePath + " has wrong " + condition + " value (actual '" + actualValue + "', expected '" + expectedValue + "')";
        }
    }
}
