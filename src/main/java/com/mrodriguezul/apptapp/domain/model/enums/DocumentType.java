package com.mrodriguezul.apptapp.domain.model.enums;

public enum DocumentType {
    DNI("01", "DNI"),
    FOREIGNER_CARD("04", "CE"),
    PASSPORT("07", "Passport");

    private final String code;
    private final String description;

    DocumentType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static DocumentType fromCode(String code) {
        for (DocumentType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}
