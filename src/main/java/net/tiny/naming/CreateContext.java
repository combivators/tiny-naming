package net.tiny.naming;

import java.io.Serializable;


public class CreateContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private String path;

    public CreateContext(String path) {
        if(path == null) {
            throw new IllegalArgumentException("path is null");
        }
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

}
