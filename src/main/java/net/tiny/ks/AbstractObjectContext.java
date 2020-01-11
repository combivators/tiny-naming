package net.tiny.ks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

public abstract class AbstractObjectContext<K, V> extends LinkedHashMap<K, V>
    implements ObjectContext<K, V>, Cloneable {

    private static final long serialVersionUID = 1L;

    abstract protected ObjectContext<K, V> createObjectContext();

    @Override
    public V getValue(K key) {
        return super.get(key);
    }

    @Override
    public void setValue(K key, V value) {
        super.put(key, value);
    }

    @Override
    public V removeValue(K key) {
        return super.remove(key);
    }

    @Override
    public Collection<K> getKeys() {
        List<K> keys = new ArrayList<K>();
        Iterator<K> it = keySet().iterator();
        while(it.hasNext()) {
            keys.add(it.next());
        }
        return keys;
    }

    @Override
    public Collection<V> getValues() {
        List<V> values = new ArrayList<V>();
        Iterator<K> it = keySet().iterator();
        while(it.hasNext()) {
            values.add(get(it.next()));
        }
        return values;
    }


    @Override
    public ObjectContext<K, V> find(String regex) {

        ObjectContext<K, V> ov = createObjectContext();
        Iterator<K> it = keySet().iterator();
        while (it.hasNext()) {
            K key = it.next();

            if (Pattern.matches(regex, key.toString())) {
                ov.setValue(key, super.get(key));
            }
        }
        return ov;
    }

    @Override
    public ObjectContext<K, V> delete(String regex) {

        ObjectContext<K, V> ov = createObjectContext();
        List<K> keys = new ArrayList<K>();
        Iterator<K> it = keySet().iterator();
        while (it.hasNext()) {
            K key = it.next();
            if (Pattern.matches(regex, key.toString())) {
                keys.add(key);
            }
        }
        for (K key : keys) {
            V value = super.remove(key);
            if (null != value) {
                ov.setValue(key, value);
            }
        }
        return ov;
    }

    @Override
    public void append(ObjectContext<K, V> context) {
        Collection<K> keys = context.getKeys();
        for(K key : keys) {
            setValue(key, context.getValue(key));
        }
    }

    @Override
    public ObjectContext<K, V> cloneContext() {
        ObjectContext<K, V> context = createObjectContext();
        context.append(this);
        context.setStatus(getStatus());
        context.setMarked(isMarked());
        return context;
    }

    @Override
    public boolean existKey(K key) {
        return super.containsKey(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T>T clone(Class<?> classType) {
        return  (T)cloneContext();
    }
}
