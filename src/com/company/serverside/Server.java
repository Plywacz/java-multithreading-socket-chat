package com.company.serverside;
/*
Author: BeGieU
Date: 06.03.2019
*/

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

//TODO: check if server counts users properly
public class Server
{
    public static final int SERVER_PORT = 5056;
    public static final int SERVER_SIZE = 10;

    private int idCreator = 0;
    private final ServerSocket socketManager;
    //Vector is thread safe
    static Vector<ClientHandler> userContainer = new Vector<>();
    static int usersCount;

    public Server() throws IOException
    {
        socketManager = new ServerSocket(SERVER_PORT);
        usersCount = 0;
    }

    static String showConnectedUsers()
    {
        System.out.println("Connected users: ");

        String result = "";
        for (ClientHandler clients : userContainer)
        {
            result = result + " " + clients.toString() + " ";
            System.out.println(result);
        }
        return result;
    }

    private void start()
    {
        while (usersCount < SERVER_SIZE)
        {
            Socket newSocket;

            try
            {
                //new  socket on server to handle new client's connection
                //accept() blocks program when waiting for connection
                newSocket = socketManager.accept();
                System.out.println("A new client is connected : " + newSocket);

                idCreator++;
                ClientHandler user = new ClientHandler("user" + idCreator, newSocket);
                userContainer.add(user);

                Thread t = new Thread(user);
                t.start();

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException
    {
        Server server = new Server();
        server.start();
    }
}
