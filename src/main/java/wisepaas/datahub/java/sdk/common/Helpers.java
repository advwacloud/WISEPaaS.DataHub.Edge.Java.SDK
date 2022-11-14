package wisepaas.datahub.java.sdk.common;

import com.google.gson.JsonPrimitive;

public class Helpers {
    public static int OS = 0;

    public static Object primitive(JsonPrimitive p) {
        Object retVal = null;
        if (p.isBoolean()) {
            retVal = p.getAsBoolean();
        } else if (p.isNumber()) {
            double doubleVal = p.getAsDouble();

            if (doubleVal == Math.rint(doubleVal)) {
                retVal = (int) doubleVal;
            } else {
                retVal = doubleVal;
            }
        } else if (p.isString()) {
            retVal = p.getAsString();
        }
        return retVal;
    }

    public static Boolean isAndroid() {
        if (OS == Const.OS.Android) {
            return true;
        } else {
            return false;
        }
    }

    public static void osSetter(int OS) {
        Helpers.OS = OS;
    }
}