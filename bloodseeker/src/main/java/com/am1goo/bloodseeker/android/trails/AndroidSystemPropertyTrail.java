package com.am1goo.bloodseeker.android.trails;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.am1goo.bloodseeker.BloodseekerExceptions;
import com.am1goo.bloodseeker.Condition;
import com.am1goo.bloodseeker.IResult;
import com.am1goo.bloodseeker.update.IRemoteUpdateTrail;
import com.am1goo.bloodseeker.update.RemoteUpdateReader;
import com.am1goo.bloodseeker.update.RemoteUpdateSerializable;
import com.am1goo.bloodseeker.update.RemoteUpdateWriter;
import com.am1goo.bloodseeker.utilities.JavaUtilities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AndroidSystemPropertyTrail extends BaseAndroidTrail implements IRemoteUpdateTrail {

    private static final short VERSION = 1;
    private short version;
    private SystemProperty[] properties;

    private AndroidSystemPropertyTrail() {
    }

    public AndroidSystemPropertyTrail(SystemProperty[] properties) {
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
        AndroidSystemPropertyTrail that = (AndroidSystemPropertyTrail) o;
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

        Class<?> clazz = JavaUtilities.getClass("android.os.SystemProperties", exceptions);
        if (clazz == null)
            return;

        Method method = null;
        try {
            method = clazz.getMethod("get", String.class, String.class);
        }
        catch (NoSuchMethodException ex) {
            exceptions.add(this, ex);
            return;
        }

        for (SystemProperty property : properties) {
            try {
                String key = property.getKey();
                Condition condition = property.getCondition();
                String expectedValue = property.getValue();

                Object actualValueObj = method.invoke(clazz, property.getKey(), null);
                String actualValue = (String)actualValueObj;
                boolean keyExists = actualValue != null;

                if (expectedValue == null) {
                    if (check(condition, keyExists, exceptions)) {
                        result.add(new OneNameResult(key));
                    }
                    continue;
                }

                boolean valueEquals = Objects.equals(actualValue, expectedValue);
                if (check(condition, valueEquals, exceptions)) {
                    result.add(new NameAndValueResult(key, condition, actualValue, expectedValue));
                }
            }
            catch (IllegalAccessException | InvocationTargetException ex) {
                exceptions.add(this, ex);
                continue;
            }
        }
    }

    private static boolean check(Condition condition, boolean equals, BloodseekerExceptions exceptions) {
        try {
            return check(condition, equals);
        }
        catch (InvalidParameterException ex) {
            exceptions.add(AndroidSystemPropertyTrail.class, ex);
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
        private String key;
        @NonNull
        private Condition condition;
        @Nullable
        private String value;

        private SystemProperty() {
        }

        public SystemProperty(@NonNull String key) {
            this(key, null, null);
        }

        public SystemProperty(@NonNull String key, @NonNull Condition condition, @Nullable String value) {
            this.version = VERSION;
            this.key = key;
            this.condition = condition;
            this.value = value;
        }

        @NonNull
        public Condition getCondition() {
            return condition;
        }

        @NonNull
        public String getKey() {
            return key;
        }

        @Nullable
        public String getValue() {
            return value;
        }

        @Override
        public void load(RemoteUpdateReader reader) throws Exception {
            version = reader.readVersion();
            key = reader.readString();
            condition = Condition.valueOf(reader.readInt());
            value = reader.readStringOrNull();
        }

        @Override
        public void save(RemoteUpdateWriter writer) throws Exception {
            writer.writeVersion(version);
            writer.writeString(key, "utf-8");
            writer.writeInt(Condition.valueOf(condition));
            writer.writeStringOrNull(value, "utf-8");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            SystemProperty that = (SystemProperty) o;
            return Objects.equals(key, that.key) && Objects.equals(condition, that.condition) && Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, condition, value);
        }
    }

    public static class OneNameResult implements IResult {
        private final String name;

        public OneNameResult(String name) {
            this.name = name;
        }

        @NonNull
        @Override
        public String toString() {
            return "Android system property '" + name + "' found";
        }
    }

    public static class NameAndValueResult implements IResult {
        private final String name;
        private final Condition condition;
        private final String actualValue;
        private final String expectedValue;

        public NameAndValueResult(String name, Condition condition, String actualValue, String expectedValue) {
            this.name = name;
            this.condition = condition;
            this.actualValue = actualValue;
            this.expectedValue = expectedValue;
        }

        @NonNull
        @Override
        public String toString() {
            return "Android system property '" + name + "' found with actualValue=" + actualValue + ", expectedValue=" + expectedValue + " (" + condition + ")";
        }
    }
}
