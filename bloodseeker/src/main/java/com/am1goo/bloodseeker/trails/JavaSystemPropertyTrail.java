package com.am1goo.bloodseeker.trails;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.am1goo.bloodseeker.BloodseekerExceptions;
import com.am1goo.bloodseeker.IResult;
import com.am1goo.bloodseeker.Condition;
import com.am1goo.bloodseeker.ITrail;
import com.am1goo.bloodseeker.update.IRemoteUpdateTrail;
import com.am1goo.bloodseeker.update.RemoteUpdateReader;
import com.am1goo.bloodseeker.update.RemoteUpdateSerializable;
import com.am1goo.bloodseeker.update.RemoteUpdateWriter;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

public class JavaSystemPropertyTrail implements IRemoteUpdateTrail, ITrail {

    private static final short VERSION = 1;
    private short version;
    private SystemProperty[] properties;

    private JavaSystemPropertyTrail() {
    }

    public JavaSystemPropertyTrail(SystemProperty[] properties) {
        this.version = VERSION;
        this.properties = properties;
    }

    @Override
    public void load(RemoteUpdateReader reader) throws Exception {
        version = reader.readVersion();
        properties = reader.readArray(SystemProperty.class);
    }

    @Override
    public void save(RemoteUpdateWriter writer) throws Exception {
        writer.writeVersion(version);
        writer.writeArray(properties);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JavaSystemPropertyTrail that = (JavaSystemPropertyTrail) o;
        return Arrays.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(properties);
    }

    @Override
    public void seek(List<IResult> result, BloodseekerExceptions exceptions) {
        if (properties == null)
            return;

        Hashtable<Object,Object> systemProperties = System.getProperties();
        Enumeration<Object> keys = systemProperties.keys();
        while (keys.hasMoreElements()) {
            Object keyObj = keys.nextElement();
            if (!(keyObj instanceof String))
                continue;

            String key = (String)keyObj;
            for (SystemProperty property : properties) {

                String keyRegex = property.getKeyRegex();
                if (keyRegex.isEmpty())
                    continue;

                if (!key.matches(keyRegex))
                    continue;

                String valueRegex = property.getValueRegex();
                if (valueRegex == null) {
                    result.add(new OneNameResult(key, keyRegex));
                    continue;
                }

                Object valueObj = systemProperties.get(keyObj);
                if (!(valueObj instanceof String))
                    continue;

                Condition condition = property.getCondition();
                String value = (String) valueObj;
                if (valueRegex.isEmpty()) {
                    if (check(condition, value.isEmpty(), exceptions)) {
                        result.add(new NameAndEmptyValueResult(key, keyRegex, condition));
                    }
                } else {
                    if (check(condition, value.matches(valueRegex), exceptions)) {
                        result.add(new NameAndValueResult(key, keyRegex, condition, value, valueRegex));
                    }
                }
            }
        }
    }

    private static boolean check(Condition condition, boolean equals, BloodseekerExceptions exceptions) {
        try {
            return check(condition, equals);
        }
        catch (InvalidParameterException ex) {
            exceptions.add(JavaSystemPropertyTrail.class, ex);
            return false;
        }
    }

    private static boolean check(Condition condition, boolean equals) throws InvalidParameterException {
        switch (condition) {
            case Eq:
                return equals;
            case NonEq:
                return !equals;
            default:
                throw new InvalidParameterException("Unsupported condition " + condition);
        }
    }

    public static class SystemProperty implements RemoteUpdateSerializable {
        private static final short VERSION = 1;
        private short version;
        @NonNull
        private String keyRegex;
        @NonNull
        private Condition condition;
        @Nullable
        private String valueRegex;

        private SystemProperty() {
        }

        public SystemProperty(@NonNull String keyRegex) {
            this(keyRegex, null, null);
        }

        public SystemProperty(@NonNull String keyRegex, @NonNull Condition condition, @Nullable String valueRegex) {
            this.version = VERSION;
            this.keyRegex = keyRegex;
            this.condition = condition;
            this.valueRegex = valueRegex;
        }

        @NonNull
        public Condition getCondition() {
            return condition;
        }

        @NonNull
        public String getKeyRegex() {
            return keyRegex;
        }

        @Nullable
        public String getValueRegex() {
            return valueRegex;
        }

        @Override
        public void load(RemoteUpdateReader reader) throws Exception {
            version = reader.readVersion();
            keyRegex = reader.readString();
            condition = Condition.valueOf(reader.readInt());
            valueRegex = reader.readStringOrNull();
        }

        @Override
        public void save(RemoteUpdateWriter writer) throws Exception {
            writer.writeVersion(version);
            writer.writeString(keyRegex, "utf-8");
            writer.writeInt(Condition.valueOf(condition));
            writer.writeStringOrNull(valueRegex, "utf-8");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            SystemProperty that = (SystemProperty) o;
            return Objects.equals(keyRegex, that.keyRegex) && Objects.equals(condition, that.condition) && Objects.equals(valueRegex, that.valueRegex);
        }

        @Override
        public int hashCode() {
            return Objects.hash(keyRegex, condition, valueRegex);
        }
    }

    public static class OneNameResult implements IResult {
        private final String name;
        private final String regex;

        public OneNameResult(String name, String regex) {
            this.name = name;
            this.regex = regex;
        }

        @NonNull
        @Override
        public String toString() {
            return "System property '" + name + "' found by regex '" + regex + "'";
        }
    }

    public static class NameAndEmptyValueResult implements IResult {
        private final String name;
        private final String nameRegex;
        private final Condition condition;

        public NameAndEmptyValueResult(String name, String nameRegex, Condition condition) {
            this.name = name;
            this.nameRegex = nameRegex;
            this.condition = condition;
        }

        @NonNull
        @Override
        public String toString() {
            return "System property '" + name + "' found by regex '" + nameRegex + " and empty value (" + condition + ")";
        }
    }

    public static class NameAndValueResult implements IResult {
        private final String name;
        private final String nameRegex;
        private final Condition condition;
        private final String value;
        private final String valueRegex;

        public NameAndValueResult(String name, String nameRegex, Condition condition, String value, String valueRegex) {
            this.name = name;
            this.nameRegex = nameRegex;
            this.condition = condition;
            this.value = value;
            this.valueRegex = valueRegex;
        }

        @NonNull
        @Override
        public String toString() {
            return "System property '" + name + "' found by regex '" + nameRegex + " and value '" + value + "' found by regex '" + valueRegex + "' (" + condition + ")";
        }
    }
}
