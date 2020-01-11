package net.tiny.context;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

public class ServicePoint implements Comparable<ServicePoint> {

    static final String DEFAULT_NAME = "Endpoint";
    static final String DEFAULT_HOST = "localhost";
    static final int DEFAULT_RMI_PORT = 1099;
    static final int DEFAULT_JMX_PORT = 1099;
    static final int DEFAULT_IIOP_PORT = 2809;
    static final int DEFAULT_HTTP_PORT = 80;
    static final int DEFAULT_HTTPS_PORT = 443;
    static final int DEFAULT_WS_PORT = 8080;
    static final int DEFAULT_WS_TLS_PORT = 8443;
    static final int DEFAULT_PORT = DEFAULT_HTTP_PORT;

    static final ServiceType DEFAULT_TYPE = ServiceType.LOCAL;

    public static final String LOCALHOST_ADDRESS = getLocalHostAddress();

    private static String getLocalHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    static final String REGEXS = "^(https?|rmi|iiop)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|?]";

    static boolean isValid(String endpoint) {
        return Pattern.matches(REGEXS, endpoint);
    }

    /**
     * Parse a formatted end-point. 'http://user:password@abc.xyz:8080/api/q?s=10'
     *
     * @param endpoint
     * @return [0]: protocol [1]: account [2]: password [3]: host [4]: port  [5]: path
     */
    public static String[] parseEndpoint(String endpoint) {
        String[] result = new String[6];
        int protocol = endpoint.indexOf("://");
        if (protocol == -1) {
            // Local endpoint
            return new String[] {endpoint};
        }
        result[0] = endpoint.substring(0, protocol);
        int defaultPort = DEFAULT_PORT;
        switch (result[0]) {
        case "rmi":
            defaultPort = DEFAULT_RMI_PORT;
            break;
        case "iiop":
            defaultPort = DEFAULT_IIOP_PORT;
            break;
        case "https":
            defaultPort = DEFAULT_HTTPS_PORT;
            break;
        case "http":
            defaultPort = DEFAULT_PORT;
            break;
        default:
            defaultPort = -1;
            break;
        }

        int pos = protocol+3;
        int cred = endpoint.indexOf("@", protocol + 3);

        int port;
        if (cred != -1) {
            pos = endpoint.indexOf(":", protocol + 3);
            if (pos != -1) {
                result[1] =	endpoint.substring(protocol+3, pos); // account
                result[2] =	endpoint.substring(pos+1, cred);       // password
            }
            pos = cred + 1;
            port = endpoint.indexOf(":", pos);
        } else {
            port = endpoint.indexOf(":", pos);
        }

        int path = endpoint.indexOf("/", pos);
        if (port > 0) {
            result[3] =	endpoint.substring(pos, port); // host name or address
            result[4] =	endpoint.substring(port+1, path);     // port
        } else {
            result[3] =	endpoint.substring(pos, path); // host name or address
            result[4] =	Integer.toString(defaultPort);        // port
        }
        result[5] =	endpoint.substring(path+1);  // path
        return result;
    }

    public static ServicePoint valueOf(String endpoint) {
        final String[] points = parseEndpoint(endpoint);
        ServiceType serviceType = ServiceType.REST;
        switch (points[0]) {
        case "rmi":
            serviceType = ServiceType.RMI;
            break;
        case "iiop":
            serviceType = ServiceType.IIOP;
            break;
        case "https":
        case "http":
            serviceType = ServiceType.REST;
            break;
        default:
            serviceType = ServiceType.LOCAL;
            break;
        }
        if (points.length == 6) {
            return new ServicePoint(points[3],
                Integer.parseInt(points[4]),
                points[5],
                serviceType,
                points[0].endsWith("s"),
                points[5]);
        } else {
            // Local service
            return new ServicePoint(points[0]);
        }
    }

    private String name;
    private String host;
    private String path;
    private String credentials;
    private String token;
    private int port;
    private ServiceType type;
    private boolean secret = false;

    public ServicePoint(final String host, final int port, final String name, final ServiceType type, final boolean secret, final String path) {
        this.host = host;
        this.port = port;
        this.name = name;
        this.type = type;
        this.secret = secret;
        this.path = path;
    }

    public ServicePoint(URL endpoint) {
        this.host = endpoint.getHost();
        this.port = endpoint.getPort();
        if (-1 == this.port) {
            this.port = endpoint.getDefaultPort();
        }
        this.path = endpoint.getPath();
        final String query = endpoint.getQuery();
        if (query != null) {
            this.path = this.path.concat("?").concat(query);
        }
        this.name = DEFAULT_NAME;
        this.type = ServiceType.REST;
        this.secret = endpoint.getProtocol().endsWith("s");
    }

    public ServicePoint(String name) {
        // Local service
        this(DEFAULT_HOST, -1, name, DEFAULT_TYPE, false, "");
    }

    public ServicePoint() {
        // Local end point
        this(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_NAME, DEFAULT_TYPE, false, "/");
    }


    public final String getName() {
        return name;
    }

    public final String getHost() {
        return host;
    }

    public final String getPath() {
        return path;
    }

    public final int getPort() {
        return port;
    }

    public final boolean isSecret() {
        return secret;
    }

    public final String getToken() {
        return token;
    }

    public final boolean hasCredentials() {
        return credentials != null;
    }

    public final String[] getCredentials() {
        if (credentials != null) {
            return credentials.split(":");
        } else {
            return null;
        }
    }

    public final void setCredentials(String sec) {
        credentials = sec;
    }

    public ServiceType getServiceType() {
        return type;
    }

    public void setServiceType(ServiceType type) {
        boolean valid = (port != -1);
        this.type = type;
        switch (type) {
        case LOCAL:
            port = -1;
            break;
        case RMI:
            if (!valid) port = DEFAULT_RMI_PORT;
            break;
        case IIOP:
            if (!valid) port = DEFAULT_IIOP_PORT;
            break;
        case JMX:
            if (!valid) port = DEFAULT_JMX_PORT;
            break;
        case REST:
            if (!valid) {
                if (secret) {
                    port = DEFAULT_HTTPS_PORT;
                } else {
                    port = DEFAULT_HTTP_PORT;
                }
            }
            break;
        case WS:
            if (!valid)
            if (!valid) {
                if (secret) {
                    port = DEFAULT_WS_TLS_PORT;
                } else {
                    port = DEFAULT_WS_PORT;
                }
            }
            break;
        default:
            break;
        }
    }


    @Override
    public boolean equals(Object target) {
        if (target instanceof ServicePoint) {
            return compareTo((ServicePoint)target) == 0;
        }
        return false;
    }

    @Override
    public int compareTo(ServicePoint target) {
        int cp = compareHost(getHost(), target.getHost(), LOCALHOST_ADDRESS);
        if (cp != 0) {
            return cp;
        }
        cp = getPort() - target.getPort();
        if (cp > 0) {
            return 1;
        } else if (cp < 0) {
            return -1;
        }
        cp = getName().compareTo(target.getName());
        if (cp != 0) {
            return cp;
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

    @Override
    public String toString() {
        return String.format("%s(%s) %s:%d '%s'", name, type.name(), host, port, path);
    }

}
