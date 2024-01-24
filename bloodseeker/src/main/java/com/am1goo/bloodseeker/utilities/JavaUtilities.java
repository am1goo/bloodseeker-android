package com.am1goo.bloodseeker.utilities;

import java.util.List;

public class JavaUtilities {

    public static Class<?> getClass(String className, List<Exception> exceptions) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
            //do nothing
        }
        catch (Exception ex) {
            exceptions.add(ex);
        }
        return null;
    }
}
