package de.unknowncity.astralib.common.message.lang;

public record Language(String langIdentifier) {
    /*
    messages_de-DE.yml
    messages_en-US.yml
     */
    public static final Language GERMAN = new Language("de_DE");
    public static final Language ENGLISH = new Language("en_US");
}
