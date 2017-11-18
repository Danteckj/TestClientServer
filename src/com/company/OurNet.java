package com.company;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class OurNet {
    private final Socket socket;
    private final Thread cThred;
    private final TCPConnectionListener eventListner;
    private final BufferedReader in;
    private final BufferedWriter out;

    //Конструктор внешнего соединения. Принимает событие и сокет(IP + порт).
    public OurNet(final TCPConnectionListener eventListner, Socket socket) throws IOException {
        this.socket = socket;
        this.eventListner = eventListner;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        //Создаем поток. Как прамерт принмает ананимный, абстрактный интерфейс Rannable
        cThred = new Thread(new Runnable() {
            public void run() {
                try {
                    eventListner.onConectionReady(OurNet.this);
                    while (!cThred.isInterrupted()) {
                        eventListner.onReceiveString(OurNet.this, in.readLine());
                    }
                    String msg = in.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    disconnect();
                }

            }
        });
        cThred.start();
    }

    //Конструктор внетреннего соединения.
    public OurNet(TCPConnectionListener eventListner, String ipAddr, int port) throws Exception{
        this(eventListner, new Socket(ipAddr, port));
    }

    //Отправка сообщения. Принмает само сообщение.
    public synchronized void sendMsg(String value){
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventListner.onException(OurNet.this, e);
            disconnect();
        }
    }

    //Разрывам соединение.
    public synchronized void  disconnect(){
        //Прерываем поток
        cThred.interrupt();
        try {
            //Закрываем сокет
            socket.close();
        } catch (IOException e){
            eventListner.onException(OurNet.this, e);
        }
    }

    @Override
    public String toString(){
        return "OurNet: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
