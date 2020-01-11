package net.tiny.naming;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NetworkAddressTranslaterService implements NetworkAddressTranslater {


    private static final byte FF = (byte)0xFF;

    private static final byte[] LOOPBACK = new byte[] { 0x7F, 0x00, 0x00, 0x01 };
    private static final byte[] PREFIX8  = new byte[] { FF, 0x00, 0x00, 0x00 };
    private static final byte[] PREFIX12 = new byte[] { FF, 0x0F, 0x00, 0x00 };
    private static final byte[] PREFIX16 = new byte[] { FF, FF, 0x00, 0x00 };
    private static final byte[] PREFIX24 = new byte[] { FF, FF, FF, 0x00 };
    private static final byte[] PREFIX32 = new byte[] { FF, FF, FF, FF };

    // ClassA 10.0.0.0/8

    private static final byte[] PRIVATEIP_10  = toByteAddress("10.0.0.0");   // ClassA 128.0.0.0/8
    private static final byte[] PRIVATEIP_128 = toByteAddress("128.0.0.0");  // ClassB 172.,  16.0.0/12
    private static final byte[] PRIVATEIP_172 = toByteAddress("172.16.0.0"); // ClassC 192.168.0.0/16
    private static final byte[] PRIVATEIP_192 = toByteAddress("192.168.0.0");

    private static final InetAddress LOCAL_ADDRESS;
    private static final byte[] LOCAL;
    private static final byte[] MAC;
    private static final NetworkInterface DEFAULT_NIC;
    private static final int DEFAULT_PREFIX;

    static {
        try {
            LOCAL_ADDRESS = InetAddress.getLocalHost();
            DEFAULT_NIC = getDefaultNetworkInterface();
            MAC = DEFAULT_NIC.getHardwareAddress();
            LOCAL = LOCAL_ADDRESS.getAddress();
            List<InterfaceAddress> ifs = DEFAULT_NIC.getInterfaceAddresses();
            InterfaceAddress ifa = ifs.get(0);
            DEFAULT_PREFIX = ifa.getNetworkPrefixLength();
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    private String lanAddress;
    private Map<Integer, Integer> lanPorts = new HashMap<Integer, Integer>();
    private String wanAddress;
    private Map<Integer, Integer> wanPorts = new HashMap<Integer, Integer>();

    @Override
    public URL translate(String address, URL source) {
        URL target = source;
        byte[] client = toByteAddress(address);
        if (isSameAddress(client)) {
            return source;
        } else if (isSameNetZone(client, LOCAL, DEFAULT_PREFIX)) {
            target = getURL(source, LOCAL_ADDRESS.getHostAddress(), null);
        } else if (isGlobalAddress(client)) {
            target = getURL(source, wanAddress, wanPorts);
        } else {
            target = getURL(source, lanAddress, lanPorts);
        }
        return target;
    }

    @Override
    public String translate(String address, int port) {
        byte[] client = toByteAddress(address);
        if (isSameAddress(client)) {
            return "localhost:" + port;
        } else if (isSameNetZone(client, LOCAL, DEFAULT_PREFIX)) {
            return LOCAL_ADDRESS.getHostAddress() + ":" + port;
        } else if (isGlobalAddress(client)) {
            return getAddressWithPort(port, wanAddress, wanPorts);
        } else {
            return getAddressWithPort(port, lanAddress, lanPorts);
        }
    }

    @Override
    public String translate(String address) {
        byte[] client = toByteAddress(address);
        if (isSameAddress(client)) {
            return "localhost";
        } else if (isSameNetZone(client, LOCAL, DEFAULT_PREFIX)) {
            return LOCAL_ADDRESS.getHostAddress();
        } else if (isGlobalAddress(client) && null != wanAddress) {
            return wanAddress;
        } else if(null != lanAddress) {
            return lanAddress;
        } else {
            return LOCAL_ADDRESS.getHostAddress();
        }
    }

    @Override
    public boolean isGlobalAddress(String address) {
        return AddressClass.GLOBAL.equals(getAddressClass(address));
    }

    @Override
    public boolean isLocalAddress(String address) {
        return !isGlobalAddress(address);
    }

    @Override
    public byte[] getMAC() {
        return MAC;
    }

    public boolean isSameAddress(byte[] address) {
        return isSameNetZone(address, LOCAL, 32);
    }

    public boolean isSameNetZone(String address) {
        return isSameNetZone(toByteAddress(address), LOCAL, DEFAULT_PREFIX);
    }

    public void setLan(String address) {
        this.lanAddress = address;
    }

    // lanPorts=80:8080, 81:8081
    public void setLanPorts(List<String> ports) {
        setPorts(ports, this.lanPorts);
    }

    public void setWan(String address) {
        this.wanAddress = address;
    }

    public void setWanPorts(List<String> ports) {
        setPorts(ports, this.wanPorts);
    }

    boolean isGlobalAddress(byte[] address) {
        return AddressClass.GLOBAL.equals(getAddressClass(address));
    }

    AddressClass getAddressClass(String address) {
        return getAddressClass(toByteAddress(address));
    }

    private void setPorts(List<String> ports, Map<Integer, Integer> map) {
        for (String value : ports) {
            String[] p = value.split("[:]");
            if (p.length == 2) {
                map.put(Integer.valueOf(p[0]), Integer.valueOf(p[1]));
            }
        }
    }

    static boolean isSameNetZone(String address, String local, int prefix) {
        return isSameNetZone(toByteAddress(address), toByteAddress(local),
                prefix);
    }

    static boolean isSameNetZone(String address, String local) {
        return isSameNetZone(toByteAddress(address), toByteAddress(local), DEFAULT_PREFIX);
    }

    static AddressClass getAddressClass(byte[] address) {
        if (isSameNetZone(address, PRIVATEIP_10, 8)) {
            return AddressClass.CLASS_A;
        } else
        if (isSameNetZone(address, PRIVATEIP_128, 8)) {
            return AddressClass.CLASS_A;
        } else if (isSameNetZone(address, PRIVATEIP_172, 12)) {
            return AddressClass.CLASS_B;
        } else if (isSameNetZone(address, PRIVATEIP_192, 16)) {
            return AddressClass.CLASS_C;
        } else
        if (isSameNetZone(address, LOOPBACK, 32)) {
            return AddressClass.LOCAL;
        } else {
            return AddressClass.GLOBAL;
        }

    }

    private static boolean isSameNetZone(byte[] address, byte[] local,
            int prefix) {
        byte[] mask = PREFIX32;
        switch (prefix) {
        case 8:
            mask = PREFIX8;
            break;
        case 12:
            mask = PREFIX12;
            break;
        case 16:
            mask = PREFIX16;
            break;
        case 24:
            mask = PREFIX24;
            break;
        case 32:
        default:
            mask = PREFIX32;
            break;
        }
        for (int i = 0; i < mask.length; i++) {
            if (!((mask[i] & local[i]) == (mask[i] & address[i]))) {
                return false;
            }
        }
        return true;
    }

    static String getAddressWithPort(int port, String address,
            Map<Integer, Integer> ports) {
        if (null == address || address.isEmpty()) {
            return LOCAL_ADDRESS.getHostAddress() + ":" + port;
        }

        if (ports.isEmpty() || !ports.containsKey(Integer.valueOf(port))) {
            return address + ":" + port;
        }

        Integer p = ports.get(Integer.valueOf(port));
        return address + ":" + p;
    }

    static URL getURL(URL source, String address, Map<Integer, Integer> ports) {
        try {
            int port = source.getPort();
            if (null == address || address.isEmpty()) {
                return new URL(source.getProtocol(),
                LOCAL_ADDRESS.getHostAddress(), port,
                source.getFile());
            }

            if (null == ports || ports.isEmpty()
                    || !ports.containsKey(Integer.valueOf(port))) {
                return new URL(source.getProtocol(), address, port, source.getFile());
            }
            Integer p = ports.get(Integer.valueOf(port));
            return new URL(source.getProtocol(), address, p.intValue(), source.getFile());

        } catch (MalformedURLException ex) {
            return source;
        }
    }

    static byte[] toByteAddress(String address) {
        if ("localhost".equalsIgnoreCase(address)) {
            return LOCAL;
        }
        if ("0:0:0:0:0:0:0:1".equalsIgnoreCase(address)) {
            return LOCAL;
        }
        byte[] value = new byte[] { 0x00, 0x00, 0x00, 0x00 };
        String[] ip = address.split("[.]");
        for (int i = 0; i < ip.length; i++) {
            value[i] = Integer.valueOf(ip[i]).byteValue();
        }
        return value;
    }

    static String toHex(byte[] data) {
        return toHex(data, null);
    }

    static String toHex(byte[] data, String delim) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            String d = Integer.toHexString(data[i] & 0xff);
            if (d.length() == 1) {
                sb.append("0");
            }
            sb.append(d);
            if (null != delim && i < data.length - 1) {
                sb.append(delim);
            }
        }
        return sb.toString().toUpperCase();
    }

    static InetAddress getDefaultLocalAddress() {
        return LOCAL_ADDRESS;
    }

    static NetworkInterface getDefaultNetworkInterface() {

        try {
            NetworkInterface nic = null;
            //InetAddress localhost = InetAddress.getLocalHost();
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while(nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                if(ni.isLoopback() || ni.isVirtual() || !ni.isUp() || ni.isPointToPoint() || !ni.supportsMulticast()) {
                    continue;
                }
                //System.out.println(String.format("%s Loopback:%s, Virtual:%s, Up;%s, P2P:%s, Multicast:%s",
                //		ni.getName(), ni.isLoopback(), ni.isVirtual(), ni.isUp(), ni.isPointToPoint(), ni.supportsMulticast()));
                Enumeration<InetAddress> addrs = ni.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    if (!"127.0.0.1".equals(addrs.nextElement().getHostAddress())) {
                        nic = ni;
                        break;
                    }
                }
                if (null != nic)
                    break;
            }
            return nic;
        } catch (Exception ex) {
        	ex.printStackTrace();
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

}
