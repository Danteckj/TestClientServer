package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ServerWorking implements TCPConnectionListener {
    public static void main(String[] args) {
        new ServerWorking();
    }


    //Массив соеденений
    private final ArrayList<OurNet> connections = new ArrayList<>();

    private ServerWorking(){
        System.out.println("Go");
        try (ServerSocket serverSocket = new ServerSocket(8189)){
            while (true){
                try {
                    new OurNet(this, serverSocket.accept());
                }catch (IOException e){
                    System.out.println("OurNet exception: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public synchronized void onConectionReady(OurNet tcpConnection) {
        connections.add(tcpConnection);
        sedToAllConnections("Connect: " + tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(OurNet tcpConnection, String value) {
        sedToAllConnections(value);

    }

    @Override
    public synchronized void onDisconect(OurNet tcpConnection) {
        connections.remove(tcpConnection);
        sedToAllConnections("Connect: " + tcpConnection);
    }

    @Override
    public synchronized void onException(OurNet tcpConnection, Exception e) {
        System.out.println("OurNet exeption: " + e);
    }

    private void sedToAllConnections(String value){
        System.out.println(value);
        for (int i = 0; i < connections.size(); i++){
            connections.get(i).sendMsg(value);
        }
    }
}
