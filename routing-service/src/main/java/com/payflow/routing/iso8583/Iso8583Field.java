package com.payflow.routing.iso8583;

public record Iso8583Field(
        int number,
        String name,
        FieldType type,
        int maxLength
) {
    public enum FieldType {
        NUMERIC,
        ALPHA,
        LLVAR,
        LLLVAR
    }

    /**
     * Validates the field value against the field definition.
     */
    public boolean isValid(String value) {
        if (value == null) {
            return false;
        }
        if (value.length() > maxLength) {
            return false;
        }
        if (type == FieldType.NUMERIC) {
            return value.chars().allMatch(Character::isDigit);
        }
        return true;
    }
    /**
     * Returns the byte length required to encode the field value.
     */
    public int getEncodedLength(String value) {
        if (value == null) return 0;
        return switch (type) {
            case NUMERIC, ALPHA -> maxLength;
            case LLVAR -> 2 + value.length();
            case LLLVAR -> 3 + value.length();
        };
    }
}
