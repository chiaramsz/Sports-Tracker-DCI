package com.amazonaws.samples;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BackendThread implements Runnable{
    static final int portNumber = 6400;

    @Override
    public void run() {
        /***************** Wait for clients to connect ****************/
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(portNumber);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ServerThread(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
