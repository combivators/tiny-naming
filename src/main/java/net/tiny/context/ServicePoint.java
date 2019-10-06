package net.tiny.context;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

public class ServicePoint extends ObjectKeyValue<String, String> implements Comparable<ServicePoint> {
    private static final long serialVersionUID = 1L;

    static final String DEFAULT_NAME = "Endpoint";
    static final String DEFAULT_HOST = "localhost";
    static final int DEFAULT_PORT = -1;
    static final int DEFAULT_RMI_PORT = 1099;
    static final int DEFAULT_JMX_PORT = 1099;
    static final int DEFAULT_IIOP_PORT = 2809;
    static final int DEFAULT_HTTP_PORT = 80;
    static final int DEFAULT_WS_PORT = 8080;

    static final ServiceType DEFAULT_TYPE = ServiceType.LOCAL;

    static final String KEY_NAME = "name";
    static final String KEY_HOST = "host";
    static final String KEY_PORT = "port";
    static final String KEY_OBJECTNAME = "objectName";
    static final String KEY_TYPE = "type";

    public static final String LOCALHOST_ADDRESS = getLocalHostAddress();

    private static String getLocalHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    static final String[] REGEXS = new String[] {
            // ip
            "^[0-9]{1,3}[.][0-9]{1,3}[.][0-9]{1,3}[.][0-9]{1,3}$",
            // ip:port
            "^[0-9]{1,3}[.][0-9]{1,3}[.][0-9]{1,3}[.][0-9]{1,3}[:][0-9]{1,5}$",
            // port
            "^[0-9]{1,5}$",
            // hostname
            "^[a-zA-Z][a-z0-9A-Z_-]+$",
            // hostname:port
            "^[a-zA-Z][a-z0-9A-Z_-]+[:][0-9]{1,5}$" };

    private static int indexOfRegex(String address) {
        for (int i = 0; i < REGEXS.length; i++) {
            if (Pattern.matches(REGEXS[i], address)) {
                return i;
            }
        }
        return -1;
    }

    static boolean isValid(String address) {
        if (indexOfRegex(address) != -1) {
            return true;
        }
        return false;
    }

    static String parseHost(String address) {
        int idx = indexOfRegex(address);
        if (idx == -1) {
            throw new IllegalArgumentException("'" + address + "'");
        }
        String host = "localhost";
        switch (idx) {
        case 0:
        case 3:
            host = address;
            break;
        case 1:
        case 4:
            int pos = address.indexOf(":");
            host = address.substring(0, pos);
            break;
        }
        return host;
    }

    static int parsePort(String address, int defaultPort) {
        int idx = indexOfRegex(address);
        if (idx == -1) {
            throw new IllegalArgumentException("'" + address + "'");
        }

        int port = defaultPort;
        switch (idx) {
        case 1:
        case 4:
            int pos = address.indexOf(":");
            port = Integer.valueOf(address.substring(pos + 1)).intValue();
            break;
        case 2:
            port = Integer.valueOf(address);
            break;
        }
        return port;
    }

    static String[] parseAddress(String address, int defaultPort) {
        int idx = indexOfRegex(address);
        if (idx == -1) {
            throw new IllegalArgumentException("'" + address + "'");
        }
        String[] result = { "localhost", Integer.toString(defaultPort) };
        switch (idx) {
        case 0:
        case 3:
            result[0] = address;
            break;
        case 2:
            result[1] = address;
            break;
        case 1:
        case 4:
            int pos = address.indexOf(":");
            result[0] = address.substring(0, pos);
            result[1] = address.substring(pos + 1);
            break;
        }
        return result;
    }


    public ServicePoint(final String host, final int port, final String name, final ServiceType type) {
        setValue(KEY_NAME, name);
        setValue(KEY_HOST, host);
        setValue(KEY_PORT, Integer.toString(port));
        setValue(KEY_TYPE, type.name());
    }

    public ServicePoint(final String host, final int port, final ServiceType type) {
        this(host, port, DEFAULT_NAME, type);
    }

    public ServicePoint(final String host, final int port) {
        this(host, port, DEFAULT_NAME, DEFAULT_TYPE);
    }

    public ServicePoint(final int port, final ServiceType type) {
        this(DEFAULT_HOST, port, DEFAULT_NAME, type);
    }

    public ServicePoint(final int port) {
        this(DEFAULT_HOST, port, DEFAULT_NAME, DEFAULT_TYPE);
    }

    public ServicePoint(final String host, final ServiceType type) {
        this(host, DEFAULT_PORT, DEFAULT_NAME, type);
    }

    public ServicePoint(final String host) {
        this(host, DEFAULT_PORT, DEFAULT_NAME, DEFAULT_TYPE);
    }

    public ServicePoint(final ServiceType type) {
        this(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_NAME, type);
    }

    public ServicePoint() {
        this(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_NAME, DEFAULT_TYPE);
    }

    @Override
    protected ObjectContext<String, String> createObjectContext() {
        return new ServicePoint();
    }

    public final String getName() {
        return getValue(KEY_NAME);
    }

    public final String getHost() {
        return getValue(KEY_HOST);
    }

    public final int getPort() {
        return Integer.parseInt(getValue(KEY_PORT));
    }

    public String getObjectName() {
        return getValue(KEY_OBJECTNAME);
    }

    public void setObjectName(String name) {
        setValue(KEY_OBJECTNAME, name);
    }

    public ServiceType getServiceType() {
        return ServiceType.valueOf(getValue(KEY_TYPE));
    }

    public void setServiceType(ServiceType type) {
        setValue(KEY_TYPE, type.name());
        boolean valid = isValidPort();
        switch (type) {
        case LOCAL:
            setValue(KEY_PORT, "-1");
            break;
        case RMI:
            if (!valid)
                setValue(KEY_PORT, Integer.toString(DEFAULT_RMI_PORT));
            break;
        case IIOP:
            if (!valid)
                setValue(KEY_PORT, Integer.toString(DEFAULT_IIOP_PORT));
            break;
        case JMX:
            if (!valid)
                setValue(KEY_PORT, Integer.toString(DEFAULT_JMX_PORT));
            break;
        case HTTP:
            if (!valid)
                setValue(KEY_PORT, Integer.toString(DEFAULT_HTTP_PORT));
            break;
        case WS:
            if (!valid)
                setValue(KEY_PORT, Integer.toString(DEFAULT_WS_PORT));
            break;
        default:
            break;
        }
    }

    boolean isValidPort() {
        return (-1 != getPort());
    }

    public String getAddress() {
        return getHost() + ":" + getPort();
    }

    public void setAddress(String address) {
        String[] array = parseAddress(address, DEFAULT_PORT);
        setValue(KEY_HOST, array[0]);
        setValue(KEY_PORT, array[1]);
    }

    @Override
    public int compareTo(ServicePoint target) {
        int cp = compareHost(getHost(), target.getHost(), LOCALHOST_ADDRESS);
        if (cp != 0) {
            return cp;
        }
        cp = getPort() - target.getPort();
        if (cp != 0) {
            return cp;
        }
        cp = getName().compareTo(target.getName());
        if (cp != 0) {
            return cp;
        }
        if (getObjectName() != null) {
            cp = getObjectName().compareTo(target.getObjectName());
        }
        if (getServiceType() != null) {
            cp = getServiceType().compareTo(target.getServiceType());
        }
        return cp;
    }

    // Find the nearest neighbour
    int compareHost(String source, String target, String local) {
        // The top priority is localhost
        if (DEFAULT_HOST.equals(source) && !DEFAULT_HOST.equals(target))
            return -1;
        if (!DEFAULT_HOST.equals(source) && DEFAULT_HOST.equals(target))
            return 1;

        // Same network bound is priority
        int rl = compareHost(source, local);
        int r2 = compareHost(target, local);
        if (rl < r2)
            return -1;
        if (rl > r2)
            return 1;
        return source.compareTo(target);
    }

    int compareHost(String target, String local) {
        char[] a = target.toCharArray();
        char[] x = local.toCharArray();
        int dis = x.length;
        for (int i = 0; i < x.length || i < a.length; i++) {
            if (a[i] != x[i]) {
                break;
            }
            dis--;
        }
        return Math.abs(dis);
    }
}
