package com.company.clientside;
/*
Author: BeGieU
Date: 07.03.2019
*/


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;


//TODO handle: logout without causing exceptions  on the client and server sides,

public class Client {
    //flag to stop thread. Reminder thread stops when run() method from thread returned
    private volatile boolean running = true;

    private final Scanner scn;
    private final Socket socket;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;


    private Client() throws IOException {
        scn = new Scanner(System.in);

        // getting localhost ip
        InetAddress ip = InetAddress.getByName("localhost");

        // establish the connection
        socket = new Socket(ip, 5056);

        // obtaining input and out streams
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    private void sendMessage() {
        Thread sendMessage = new Thread(() ->
        {
            while (running) {
                try {
                    String msg = scn.nextLine();
                    if (msg.equals("Exit")) {
                        running = false;
                        scn.close();
                        //you exit the run() you kill thread, return above does it
                    }
                    // write on the output stream
                    outputStream.writeUTF(msg);
                }
                catch (IOException e) {
                    e.printStackTrace();
                   // System.out.println("finally closed");
                }
            }
        });
        sendMessage.start();
    }

    private void readMessage() {
        Thread readMessage = new Thread(() ->
        {
            while (running) {
                try {
                    // read the message sent to this client
                    String msg = inputStream.readUTF();
                    if (msg.equals("Exit")) {
                        running = false; // it is not necessary  since send msg thread set this flag on false;
                        inputStream.close();
                        outputStream.close();
                        socket.close();
                        scn.close();
                        //you exit the run() you kill thread, return above does it
                    }
                    System.out.println(msg);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        readMessage.start();
    }

    public static void main(String args[]) throws IOException, java.io.IOException {
        Client client = new Client();
        client.sendMessage();
        client.readMessage();
    }
}