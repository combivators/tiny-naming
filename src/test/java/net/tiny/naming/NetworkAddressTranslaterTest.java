package net.tiny.naming;

import static org.junit.jupiter.api.Assertions.*;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class NetworkAddressTranslaterTest {

    static final String LS = System.getProperty("line.separator");

    @Test
    public void testDefaultNetworkInterface() throws Exception {

        NetworkInterface nic = null;
        Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
        while(nis.hasMoreElements()) {
            NetworkInterface ni = nis.nextElement();
            if(ni.isLoopback() || ni.isVirtual() || !ni.isUp() || ni.isPointToPoint() || !ni.supportsMulticast()) {
                continue;
            }
            System.out.println(ni.getDisplayName() + " " + ni.getName());
            nic = ni;
        }
        System.out.println(nic.getDisplayName() + " MAC: " + NetworkAddressTranslaterService.toHex(nic.getHardwareAddress(), null));
        InterfaceAddress ifa = nic.getInterfaceAddresses().get(0);
        System.out.println(nic.getDisplayName() + " Prefix: " + ifa.getNetworkPrefixLength());
        nic = NetworkAddressTranslaterService.getDefaultNetworkInterface();
        System.out.println(nic.getDisplayName() + " MAC: " + NetworkAddressTranslaterService.toHex(nic.getHardwareAddress(), null));

        assertTrue(nic.isUp());
        assertTrue(nic.supportsMulticast());
        assertFalse(nic.isLoopback());
        assertFalse(nic.isVirtual());
        assertFalse(nic.isPointToPoint());
    }

    @Test
    public void testIsSameNetZone() throws Exception {
        assertTrue(NetworkAddressTranslaterService.isSameNetZone(
                "192.168.10.100", "192.168.10.100", 32));
        assertFalse(NetworkAddressTranslaterService.isSameNetZone(
                "192.168.10.100", "192.168.10.101", 32));

        assertFalse(NetworkAddressTranslaterService.isSameNetZone(
                "192.168.10.100", "192.168.20.100", 32));

        assertTrue(NetworkAddressTranslaterService.isSameNetZone(
                "192.168.10.2", "192.168.10.120", 24));

        assertTrue(NetworkAddressTranslaterService.isSameNetZone(
                "192.168.10.100", "192.168.10.10", 24));
        assertFalse(NetworkAddressTranslaterService.isSameNetZone(
                "192.168.10.100", "192.168.20.100", 24));

        assertFalse(NetworkAddressTranslaterService.isSameNetZone(
                "192.168.10.100", "191.168.10.100", 24));

        assertTrue(NetworkAddressTranslaterService.isSameNetZone(
                "192.168.10.100", "192.168.20.100", 16));
        assertFalse(NetworkAddressTranslaterService.isSameNetZone(
                "192.168.10.100", "192.165.10.100", 16));

        assertTrue(NetworkAddressTranslaterService.isSameNetZone(
                "192.168.10.100", "192.165.10.200", 8));
        assertFalse(NetworkAddressTranslaterService.isSameNetZone(
                "191.168.10.100", "192.168.10.100", 8));

        // On Wireless network DEFAULT_PREFIX may be 64, Can not discriminate same net zone
//        assertTrue(NetworkAddressTranslaterService.isSameNetZone(
//                "192.168.10.100", "192.168.10.101"));
//        assertTrue(NetworkAddressTranslaterService.isSameNetZone(
//                "192.168.10.100", "192.168.20.100"));
//        assertFalse(NetworkAddressTranslaterService.isSameNetZone(
//                "192.165.10.100", "192.168.10.100"));

    }

    @Test
    public void testGetAddressClass() throws Exception {
        NetworkAddressTranslaterService nat = new NetworkAddressTranslaterService();
        assertEquals(NetworkAddressTranslater.AddressClass.CLASS_C, nat.getAddressClass("192.168.10.100"));
        assertEquals(NetworkAddressTranslater.AddressClass.CLASS_B, nat.getAddressClass("172.16.10.100"));
        assertEquals(NetworkAddressTranslater.AddressClass.CLASS_A, nat.getAddressClass("128.50.10.2"));
        assertEquals(NetworkAddressTranslater.AddressClass.CLASS_A, nat.getAddressClass("10.100.1.2"));
        assertEquals(NetworkAddressTranslater.AddressClass.LOCAL, nat.getAddressClass("127.0.0.1"));
        assertEquals(NetworkAddressTranslater.AddressClass.GLOBAL, nat.getAddressClass("74.125.235.82"));
    }

    @Test
    public void testGetMAC() throws Exception {
        NetworkAddressTranslaterService nat = new NetworkAddressTranslaterService();
        assertNotNull(nat.getMAC());
        System.out.println("MAC: "
                + NetworkAddressTranslaterService.toHex(nat.getMAC(), ":"));
    }

    @Test
    public void testGetAddress() throws Exception {
        String address = "128.10.10.1";
        Map<Integer, Integer> ports = new HashMap<Integer, Integer>();

        assertEquals("128.10.10.1:80",
                NetworkAddressTranslaterService.getAddressWithPort(80, address,
                        ports));
        assertEquals("128.10.10.1:81",
                NetworkAddressTranslaterService.getAddressWithPort(81, address,
                        ports));

        ports.put(80, 8080);
        ports.put(81, 8081);

        assertEquals("128.10.10.1:8080",
                NetworkAddressTranslaterService.getAddressWithPort(80, address,
                        ports));
        assertEquals("128.10.10.1:8081",
                NetworkAddressTranslaterService.getAddressWithPort(81, address,
                        ports));
    }

    @Test
    public void testTranslate() throws Exception {
        String localhost = InetAddress.getLocalHost().getHostAddress();
        NetworkAddressTranslaterService nat = new NetworkAddressTranslaterService();
        nat.setLan("172.16.10.1");
        nat.setWan("8.8.8.8");

        assertEquals("localhost", nat.translate("localhost"));
        assertEquals("localhost", nat.translate(localhost));

        assertEquals("8.8.8.8", nat.translate("74.125.235.82"));
    }

    @Test
    public void testTranslateWithPort() throws Exception {

        String localhost = InetAddress.getLocalHost().getHostAddress();
        NetworkAddressTranslaterService nat = new NetworkAddressTranslaterService();
        nat.setLan("172.16.10.1");

        List<String> ports = new ArrayList<String>();
        ports.add("80:8080");
        ports.add("81:8081");
        nat.setLanPorts(ports);

        nat.setWan("8.8.8.8");

        ports = new ArrayList<String>();

        ports.add("80:9010");

        ports.add("81:9011");

        nat.setWanPorts(ports);

        assertEquals("localhost:80", nat.translate(localhost, 80));

        assertEquals("8.8.8.8:9011", nat.translate("74.125.235.82", 81));
    }

    @Test
    public void testTranslateWithoutLanWan() throws Exception {
        String localhost = InetAddress.getLocalHost().getHostAddress();
        System.out.println("localhost: " + localhost);
        NetworkAddressTranslater nat = new NetworkAddressTranslaterService();
        assertEquals("localhost:80", nat.translate(localhost, 80));
        assertEquals(localhost + ":80", nat.translate("128.50.2.1", 80));
        assertEquals(localhost + ":81", nat.translate("192.168.10.2", 81));
        assertEquals(localhost + ":81", nat.translate("74.125.235.82", 81));
    }

    @Test
    public void testTranslateURL() throws Exception {
        String localhost = InetAddress.getLocalHost().getHostAddress();
        NetworkAddressTranslaterService nat = new NetworkAddressTranslaterService();
        nat.setLan("172.16.10.1");

        List<String> ports = new ArrayList<String>();
        ports.add("80:8080");
        ports.add("81:8081");
        nat.setLanPorts(ports);

        nat.setWan("8.8.8.8");

        ports = new ArrayList<String>();

        ports.add("80:9010");
        ports.add("81:9011");
        nat.setWanPorts(ports);

        assertEquals(
                "https://" + localhost + ":80/ws/ns",
                nat.translate(localhost,
                        new URL("https://" + localhost + ":80/ws/ns")).toString());

        assertEquals(
                "http://8.8.8.8:9011/ws/ns",
                nat.translate("74.125.235.82",
                        new URL("http://" + localhost + ":81/ws/ns")).toString());

    }

}
