package com.am1goo.bloodseeker.utilities;

import com.am1goo.bloodseeker.BloodseekerException;
import com.am1goo.bloodseeker.BloodseekerExceptions;

public class JavaUtilities {

    public static Class<?> getClass(String className, BloodseekerExceptions exceptions) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
            //do nothing
        }
        catch (Exception ex) {
            exceptions.add(new BloodseekerException(JavaUtilities.class, ex));
        }
        return null;
    }
}
