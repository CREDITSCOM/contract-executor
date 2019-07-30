package com.credits.scapi.implemetation;

import com.credits.scapi.v2.MapChangeListener;
import com.credits.scapi.v2.ObservableMap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class TokenBalances<K, V> extends HashMap<K, V> implements ObservableMap<K, V>, Serializable {

    private static final long serialVersionUID = -2920999095425546314L;
    private transient Set<MapChangeListener> listeners;

    public TokenBalances() {
        listeners = new HashSet<>();
    }

    public TokenBalances(Set<MapChangeListener> listeners) {
        this.listeners = listeners;
    }

    public TokenBalances(int initialCapacity, Set<MapChangeListener> listeners) {
        super(initialCapacity);
        this.listeners = listeners;
    }

    public TokenBalances(int initialCapacity, float loadFactor, Set<MapChangeListener> listeners) {
        super(initialCapacity, loadFactor);
        this.listeners = listeners;
    }

    public TokenBalances(Map<? extends K, ? extends V> m, Set<MapChangeListener> listeners) {
        super(m);
        this.listeners = listeners;
    }

    @Override
    public void addListener(MapChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(MapChangeListener listener) {
        listeners.remove(listener);
    }

    @Override
    public V put(K key, V value) {
        return putThenNotify(key, value);
    }

    private V putThenNotify(K key, V value) {
        final V oldValue = super.put(key, value);
        listeners.forEach(notifyListener(key, oldValue, value));
        return oldValue;
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return putIfAbsentThenNotify(key, value);
    }

    private V putIfAbsentThenNotify(K key, V value) {
        final var oldValue = super.get(key);

        return oldValue == null
               ? putThenNotify(key, value)
               : oldValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        map.forEach(this::put);
    }

    @Override
    public V remove(Object key) {
        final var oldValue = super.remove(key);
        listeners.forEach(notifyListener(key, oldValue, null));
        return oldValue;
    }

    @Override
    public boolean remove(Object key, Object value) {
        final var isRemoved = super.remove(key, value);
        if (isRemoved) {
            listeners.forEach(notifyListener(key, value, null));
        }
        return isRemoved;
    }

    @Override
    public V replace(K key, V value) {
        final var oldValue = super.replace(key, value);
        listeners.forEach(notifyListener(key, oldValue, value));
        return oldValue;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        final var isReplaced = super.replace(key, oldValue, newValue);
        if (isReplaced) {
            listeners.forEach(notifyListener(key, oldValue, newValue));
        }
        return isReplaced;
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        if (function == null) throw new IllegalArgumentException("function can't be null");

        forEach((key, value) -> {
            final var oldValue = super.get(key);
            final var newValue = function.apply(key, oldValue);
            replace(key, oldValue, newValue);
        });
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        final var oldValue = super.get(key);
        final V newValue;
        if (oldValue == null) {
            newValue = super.computeIfAbsent(key, mappingFunction);
            listeners.forEach(notifyListener(key, null, newValue));
        } else {
            newValue = oldValue;
        }
        return newValue;
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        if (remappingFunction == null) throw new IllegalArgumentException("remapping function can't be null");

        final var oldValue = super.get(key);
        final V newValue;
        if (oldValue != null) {
            newValue = super.computeIfPresent(key, remappingFunction);
            listeners.forEach(notifyListener(key, oldValue, newValue));
        } else {
            newValue = null;
        }
        return newValue;
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        if (remappingFunction == null) throw new IllegalArgumentException("remapping function can't be null");

        final var oldValue = super.get(key);
        final var newValue = super.compute(key, remappingFunction);
        listeners.forEach(notifyListener(key, oldValue, newValue));
        return newValue;
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        if (remappingFunction == null) throw new IllegalArgumentException("remapping function can't be null");
        if (value == null) throw new IllegalArgumentException("value can't be null");

        final var oldValue = super.get(key);
        final var newValue = super.merge(key, value, remappingFunction);
        listeners.forEach(notifyListener(key, oldValue, newValue));
        return newValue;
    }

    @Override
    public void clear() {
        final var keys = keySet().toArray();
        for (Object key : keys) {
            remove(key);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object clone() {
        final TokenBalances<K, V> result;
        try {
            result = ((TokenBalances<K, V>) super.clone());
        } catch (Throwable e) {
            throw new InternalError(e);
        }
        listeners.forEach(result::addListener);
        return result;
    }

    @SuppressWarnings("unchecked")
    private Consumer<MapChangeListener> notifyListener(Object key, Object oldValue, Object newValue) {
        return listener -> {
            try {
                listener.onChanged(new MapChangeListener.EntryChange() {
                    @Override
                    public Object getKey() {
                        return key;
                    }

                    @Override
                    public Object getOldValue() {
                        return oldValue;
                    }

                    @Override
                    public Object getNewValue() {
                        return newValue;
                    }
                });
            } catch (Throwable e) {
                throw new TokenBalancesUpdateException(e);
            }
        };
    }

    public static class TokenBalancesUpdateException extends RuntimeException {
        private static final long serialVersionUID = 8004850404028300836L;

        public TokenBalancesUpdateException(Throwable e) {
            super(e);
        }
    }
}
