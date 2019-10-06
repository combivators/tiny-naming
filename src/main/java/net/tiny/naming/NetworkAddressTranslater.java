package net.tiny.naming;

import java.net.URL;

public interface NetworkAddressTranslater {
    public enum AddressClass {
        CLASS_A,
        CLASS_B,
        CLASS_C,
        LOCAL,
        GLOBAL
    }
    //NodepointContext translate(NodepointContext source, NodepointContext target);
    URL translate(String address, URL source);
    String translate(String address, int port);
    String translate(String address);
    boolean isGlobalAddress(String address);
    boolean isLocalAddress(String address);
    byte[] getMAC();
}
