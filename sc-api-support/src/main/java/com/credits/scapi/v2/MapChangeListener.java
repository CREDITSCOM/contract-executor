package com.credits.scapi.v2;


public interface MapChangeListener<K, V> {

    void onChanged(EntryChange<? extends K, ? extends V> entryChange);

    interface EntryChange<K, V> {

        K getKey();

        V getOldValue();

        V getNewValue();
    }
}
