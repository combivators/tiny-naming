package net.tiny.ks;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ObjectKeyValue<K, V> extends AbstractObjectContext<K, V> {

    private static final long serialVersionUID = 1L;

    private Status status = Status.READY;
    private boolean marked = true;
    private BlockingQueue<Status> statusQueue;

    public ObjectKeyValue() {
        super();
    }

    public ObjectKeyValue(Status status) {
        this();
        setStatus(status);
    }

    public ObjectKeyValue(K key, V value) {
        this();
        super.put(key, value);
    }

    @Override
    public boolean isReady() {
        return Status.READY.equals(status);
    }

    @Override
    public void waitReady() {
        if(isBusy()) {
            if(statusQueue != null) {
                try { statusQueue.take();
                } catch(InterruptedException ex) {}
                statusQueue = null;
            }
        }
    }

    @Override
    public boolean isBusy() {
        return Status.BUSY.equals(status);
    }

    @Override
    public boolean hasException() {
        return Status.EXCEPTION.equals(status);
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void setStatus(Status status) {
        if(!this.status.equals(status)) {
            this.status = status;
            if(isBusy()) {
                statusQueue = new ArrayBlockingQueue<Status>(1);
            } else {
                if(statusQueue != null && statusQueue.isEmpty()) {
                    statusQueue.offer(status);
                }
            }
        }
    }

    @Override
    public boolean isMarked() {
        return marked;
    }

    @Override
    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    @Override
    public String encode() {
        return encode(true);
    }

    @Override
    public String encode(boolean compress) {
        return ObjectBytes.encode(this, compress);
    }

    @Override
    protected ObjectContext<K, V> createObjectContext() {
        return new ObjectKeyValue<K, V>();
    }

    @SuppressWarnings("unchecked")
    public ObjectContext<K, V> decode(String data, boolean compress) {
        return (ObjectContext<K, V>)ObjectBytes.decode(data, compress);
    }

}