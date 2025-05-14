package org.example.bookreccomender2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketConnection {
    private static Socket socket;
    private static BufferedReader in;
    private static PrintWriter out;

    public static void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public BufferedReader getIn() {
        return in;
    }

    public PrintWriter getOut() {
        return out;
    }

    public static Socket getConnection() {return socket;}



    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
