package com.company.clientside;
/*
Author: BeGieU
Date: 07.03.2019
*/


import com.company.serverside.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


//TODO handle: logout without causing exceptions  on the client and server sides, and remove client form Server's usersContainer
public class Client
{
    //flag to stop thread. Reminder thread stops when run() method from thread returned
    private volatile boolean running = true;
    private final Scanner scn;
    private final Socket socket;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;

    public Client() throws UnknownHostException, IOException
    {
        scn = new Scanner(System.in);

        // getting localhost ip
        InetAddress ip = InetAddress.getByName("localhost");

        // establish the connection
        socket = new Socket(ip, 5056);

        // obtaining input and out streams
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public static void main(String args[]) throws IOException, java.io.IOException
    {
       Scanner scn = new Scanner(System.in);

        // getting localhost ip
        InetAddress ip = InetAddress.getByName("localhost");

        // establish the connection
        Socket socket = new Socket(ip, 5056);

        // obtaining input and out streams
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        // sendMessage thread
        Thread sendMessage = new Thread(() ->
        {
            while (true)
            {

                try
                {
                    String msg = scn.nextLine();
                    if (msg.equals("Exit"))
                    {
                        socket.close();
                        return;
                        //you exit the run() you kill thread, return above does it
                    }
                    // write on the output stream
                    outputStream.writeUTF(msg);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });

        // readMessage thread
        Thread readMessage = new Thread(() ->
        {
            while (true)
            {
                try
                {
                    // read the message sent to this client
                    String msg = inputStream.readUTF();
                    System.out.println(msg);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });

        sendMessage.start();
        readMessage.start();

    }
}