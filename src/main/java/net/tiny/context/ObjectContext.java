package net.tiny.context;

import java.io.Serializable;
import java.util.Collection;

public interface ObjectContext<K, V> extends Serializable {

	public static enum Status {
		READY,
		BUSY,
		EXCEPTION
	}

	boolean isReady();
	boolean isBusy();
	boolean hasException();

	Status getStatus();
	void setStatus(Status status);

	boolean isMarked();
	void setMarked(boolean marked);
	void waitReady();


	/*
	 *
	 * The next methods see Map some methods
	 */
	V getValue(K key);

	void setValue(K key, V value);

	V removeValue(K key);

	Collection<K> getKeys();

	Collection<V> getValues();

	int size();

	boolean existKey(K key);

	boolean isEmpty();

	void clear();

	/*
	 *
	 * The next methods to search members on map
	 */

	ObjectContext<K, V> find(String regexKey);

	ObjectContext<K, V> delete(String regexKey);

	void append(ObjectContext<K, V> context);

	ObjectContext<K, V> cloneContext();

	<T>T clone(Class<?> classType);

	String encode();
	String encode(boolean compress);
}