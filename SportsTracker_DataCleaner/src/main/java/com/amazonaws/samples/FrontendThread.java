package com.amazonaws.samples;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FrontendThread implements Runnable {
    // port to listen connection
    static final int PORT = 8080;
    @Override
    public void run() {
        try {
            ServerSocket serverConnect = new ServerSocket(PORT);
            System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");

            // we listen until user halts server execution
            while (true) {
                JavaHTTPServer myServer = new JavaHTTPServer(serverConnect.accept());

                // create dedicated thread to manage the client connection
                new Thread(myServer).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
