package net.tiny.naming;

import java.util.Map;

/**
 * This interface has call-back methods that allow to hook into JMS JNDI server.
 * A JNDIContextHandler can e.g. persist the the bindings or execute certain actions
 * when certain bindings are performed.
 * 
 */
public interface JNDIContextHandler {

    /**
     * @return the context path this handler signs responsible for
     */
    public String getContextPath();

    /**
     * Notifies the handler of a data binding.
     *
     * @param path the path to which the object is bound
     * @param value the value bound
     */
    public void bind(String path, Object value);

    /**
     * Notifies the handler of an unbinding.
     *
     * @param path the path to which the object was bound
     */
    public void unbind(String path);

    /**
     * Asks the context-handler to load bindings
     * from an external storage.
     *
     * @return bindings from an external storage or NULL if the
     *         handler does not persist bindings
     */
    public Map<String, Object> load();

}
