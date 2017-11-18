package com.company;

public interface TCPConnectionListener {

    void onConectionReady(OurNet tcpConnection);
    void onReceiveString(OurNet tcpConnection, String value);
    void onDisconect(OurNet tcpConnection);
    void onException(OurNet tcpConnection, Exception e);
}
