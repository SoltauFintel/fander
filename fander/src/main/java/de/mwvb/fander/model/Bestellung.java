package de.mwvb.fander.model;

/**
 * Gibt Auskunft dar�ber welche Gerichte ein User bestellt hat.
 */
public interface Bestellung {

    boolean isBestellt(String gerichtId);
}
