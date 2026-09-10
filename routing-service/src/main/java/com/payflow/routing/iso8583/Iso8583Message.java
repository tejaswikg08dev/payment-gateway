package com.payflow.routing.iso8583;

import java.util.HashMap;
import java.util.Map;

public class Iso8583Message {

    private String mti;
    private Map<Integer, String> fields;
    private byte[] bitmap;

    public Iso8583Message() {
        this.fields = new HashMap<>();
        this.bitmap = new byte[8];
    }

    public Iso8583Message(String mti, Map<Integer, String> fields, byte[] bitmap) {
        this.mti = mti;
        this.fields = fields != null ? new HashMap<>(fields) : new HashMap<>();
        this.bitmap = bitmap != null ? bitmap.clone() : new byte[8];
    }

    public String getMti() {
        return mti;
    }

    public void setMti(String mti) {
        this.mti = mti;
    }

    public Map<Integer, String> getFields() {
        return fields;
    }

    public void setFields(Map<Integer, String> fields) {
        this.fields = fields;
    }

    public String getField(int field) {
        return fields.get(field);
    }
    public void setField(int field, String value) {
        fields.put(field, value);
        BitmapUtils.setFieldPresent(bitmap, field);
    }

    public boolean hasField(int fieldNumber) {
        return fields.containsKey(fieldNumber);
    }

    public byte[] getBitmap() {
        return bitmap != null ? bitmap.clone() : null;
    }

    public void setBitmap(byte[] bitmap) {
        this.bitmap = bitmap != null ? bitmap.clone() : new byte[8];
    }

    @Override
    public String toString() {
        return "Iso8583Message{" +
                "mti='" + mti + '\'' +
                ", fields=" + fields.keySet() +
                '}';
    }

}
