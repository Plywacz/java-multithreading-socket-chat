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
//todo: make server flexible for ip addresses
//TODO: migrate this project to spring boot

final class Server {
    private static final int SERVER_PORT = 5056;
    private static final int SERVER_SIZE = 10;

    private int idCreator = 0;
    private int usersCount = 0;

    private final ServerSocket socketManager = new ServerSocket(SERVER_PORT);
    //Vector is thread safe
    static Vector<ClientHandler> userContainer = new Vector<>();

    private Server() throws IOException {

    }

    String getConnectedUsers() {
        System.out.println("Connected users: ");

        String result = "";
        for (ClientHandler client : userContainer) {
            result = result + " " + client.toString() + " ";
            System.out.println(result);
        }
        return result;
    }

    private void start() {
        while (usersCount < SERVER_SIZE) {
            Socket newSocket;

            try {
                //new  socket on server to handle new client's connection
                //accept() blocks program when waiting for connection
                ClientHandler user = new ClientHandler(createNewUsername(), socketManager.accept(), this);
                userContainer.add(user);

                Thread t = new Thread(user);
                t.start();

                System.out.println("A new client is connected : ");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void disconnectUser(ClientHandler user) {
        userContainer.remove(user);
        usersCount--;
        idCreator--;
    }

    private String createNewUsername() {
        return "user" + ++idCreator;
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start();
    }
}
