package net.tiny.naming;

import java.util.List;
import java.util.Map;

public interface BindingContext {

    public interface Listener {
        void createSubcontext(String path);
        void destroySubcontext(String path);
        void bind(String path);
        void unbind(String path);
        void rebind(String path);
        void rename(String path, String oldPath);
        void lookup(String path, Object obj);
        void list(String path, int size);
    }

    public void setListener(Listener listener);

    public Listener getListener();

    public void setSubcontext(List<String> paths);

    public String createSubcontext(String path, String name);

    public void destroySubcontext(String path, String name);

    public void destroy();

    public void bind(String path, String name, Object obj) throws Exception;

    public void unbind(String path, String name) throws Exception;

    public Object lookup(String path, String name);

    public Map<String, Object> list(String path, String name);

    public void rename(String path, String oldName, String newName) throws Exception;

}
