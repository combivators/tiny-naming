package net.tiny.context;

public final class ObjectValue extends ObjectKeyValue<String, Object> {

    private static final long serialVersionUID = 1L;

    public ObjectValue() {
        super();
    }

    public ObjectValue(Status status) {
        super(status);
    }
}
