package com.lasalletech.market_data.fast;

import org.openfast.GroupValue;
import org.openfast.Message;
import org.openfast.SequenceValue;
import org.openfast.template.ComposedScalar;
import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.Scalar;
import org.openfast.template.Sequence;

import com.lasalletech.market_data.fast.error.FieldNotFound;

public class FastUtil {
    public static String getString(GroupValue grp, int index) throws FieldNotFound {
        if (!grp.isDefined(index)) {
            throw new FieldNotFound(index);
        }
        return grp.getString(index);
    }

    public static String getString(GroupValue grp, String field) throws FieldNotFound {
        if (!grp.isDefined(field)) {
            throw new FieldNotFound(field);
        }
        return grp.getString(field);
    }

    public static int getInt(GroupValue grp, String field) throws FieldNotFound {
        if (!grp.isDefined(field)) {
            throw new FieldNotFound(field);
        }
        return grp.getInt(field);
    }

    public static double getDouble(GroupValue grp, String field) throws FieldNotFound {
        if (!grp.isDefined(field)) {
            throw new FieldNotFound(field);
        }
        return grp.getDouble(field);
    }

    public static char getChar(GroupValue grp, String field) throws FieldNotFound {
        if (!grp.isDefined(field)) {
            throw new FieldNotFound(field);
        }
        return (char) grp.getByte(field);
    }

    public static long getLong(GroupValue grp, String field) throws FieldNotFound {
        if (!grp.isDefined(field)) {
            throw new FieldNotFound(field);
        }
        return grp.getLong(field);
    }

    public static GroupValue[] getSequence(GroupValue grp, String field) throws FieldNotFound {
        if (!grp.isDefined(field)) {
            throw new FieldNotFound(field);
        }
        return grp.getSequence(field).getValues();
    }

    public static byte getByte(GroupValue grp, String field) throws FieldNotFound {
        if (!grp.isDefined(field)) {
            throw new FieldNotFound(field);
        }
        return grp.getByte(field);
    }

    public static String getString(GroupValue grp, String field, String def) {
        if (!grp.isDefined(field))
            return def;
        else
            return grp.getString(field);
    }

    public static int getInt(GroupValue grp, String field, int def) {
        if (!grp.isDefined(field))
            return def;
        else
            return grp.getInt(field);
    }

    public static double getDouble(GroupValue grp, String field, double def) {
        if (!grp.isDefined(field))
            return def;
        else
            return grp.getDouble(field);
    }

    public static char getChar(GroupValue grp, String field, char def) {
        if (!grp.isDefined(field))
            return def;
        else
            return (char) grp.getByte(field);
    }

    public static String fastMsgToFixString(GroupValue inMsg, String beginStr, String checksum) {
        String msgStr = fastGroupToFixString(inMsg);

        return "8=" + beginStr + SEPARATOR // beginstring
                + "9=" + String.valueOf(msgStr.length()) + SEPARATOR // message
                                                                     // length
                + msgStr + SEPARATOR // actual message
                + "10=" + checksum + SEPARATOR; // checksum
    }

    public static String fastGroupToFixString(GroupValue value) {
        Group group = value.getGroup();
        int start = value instanceof Message ? 1 : 0;

        boolean first = true;

        String out = new String();

        for (int i = start; i < group.getFieldCount(); ++i) {
            if (value.isDefined(i)) {
                if (!first)
                    out = out.concat(SEPARATOR);
                first = false;

                Field field = group.getField(i);
                if (field instanceof Scalar || field instanceof ComposedScalar) {
                    out = out.concat(fastScalarToFixString(field, value.getString(i)));
                } else if (field instanceof Sequence) {
                    out = out.concat(fastSequenceToFixString(value.getSequence(i)));
                } else if (field instanceof Group) {
                    out = out.concat(fastGroupToFixString(value.getGroup(i)));
                }
            }
        }

        return out;
    }

    public static String fastScalarToFixString(Field field, String value) {
        return field.getId() + "=" + value;
    }

    public static String fastSequenceToFixString(SequenceValue sequence) {
        String out = fastScalarToFixString(sequence.getSequence().getLength(), String.valueOf(sequence.getLength()));

        for (int i = 0; i < sequence.getLength(); ++i) {
            out = out.concat(SEPARATOR + fastGroupToFixString(sequence.get(i)));
        }

        return out;
    }

    private static final String SEPARATOR = String.valueOf((char) 0x01);
}
