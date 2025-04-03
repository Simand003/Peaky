package com.example.peaky.model.user;

public enum GenderUser {
    MALE("Male"),
    FEMALE("Female"),
    NON_BINARY("Non binary"),
    PREFER_NOT_TO_SAY("Prefer not to say");

    private final String genderLabel;

    // Costruttore che associa una stringa al genere
    GenderUser(String genderLabel) {
        this.genderLabel = genderLabel;
    }

    // Metodo per ottenere la stringa associata
    public String getGenderLabel() {
        return genderLabel;
    }

    public static GenderUser fromString(String genderString) {
        if (genderString != null) {
            switch (genderString) {
                case "Male":
                    return MALE;
                case "Female":
                    return FEMALE;
                case "Non binary":
                    return NON_BINARY;
                default:
                    return PREFER_NOT_TO_SAY;
            }
        }
        return null;
    }
}
