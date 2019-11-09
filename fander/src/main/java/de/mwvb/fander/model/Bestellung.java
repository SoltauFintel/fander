package de.mwvb.fander.model;

/**
 * Gibt Auskunft darüber welche Gerichte ein User bestellt hat.
 */
public interface Bestellung {

    boolean isBestellt(String gerichtId);
}
