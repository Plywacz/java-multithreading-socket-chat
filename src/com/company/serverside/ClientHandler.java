package com.company.serverside;
/*
Author: BeGieU
Date: 06.03.2019
*/

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

//TODO fix indexing clients(when removing client and then adding another client Program  creates bugged names of clients)
final class ClientHandler implements Runnable {
    private final Server ownerServer;

    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private final Socket socket;

    private final String name;
    private boolean isLoggedIn = true;

    ClientHandler(String name, Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.name = name;
        this.ownerServer = server;

        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }


    @Override
    public void run() {
        try {
            //TODO handle message sent to all users !

            String receivedMsg;
            while (true) {

                receivedMsg = inputStream.readUTF();
                System.out.println(receivedMsg);

                if (receivedMsg.equals("Exit")) {
                    outputStream.writeUTF("Exit"); //server tells client that it has closed connection for him
                    outputStream.writeUTF("Closing connection for this user");
                    this.closeConnection();
                    ownerServer.disconnectUser(this);
                    break;
                }
                else if (receivedMsg.equals("list users")) {
                    String connectedUsers = ownerServer.getConnectedUsers();
                    outputStream.writeUTF(connectedUsers);
                    continue;
                }

                //divide receivedMsg into msg to send and recipient to sent to
                String[] result = receivedMsg.split("-> ");
                String msgToSend = result[0];
                String recipient = result[1];

                if (recipient.equals("all")) {
                    this.sendToAll(msgToSend);
                }
                else {
                    sendToOne(msgToSend, recipient);
                }
            }
            this.closeConnection();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() throws IOException {
        System.out.println("Client " + this.socket + " sends exit...");
        System.out.println("Closing this connection.");

        this.isLoggedIn = false;
        inputStream.close();
        outputStream.close();
        this.socket.close();

        System.out.println("Connection closed");
    }

    private void sendToAll(String msgToSend) throws IOException {
        for (ClientHandler user : ownerServer.getUserContainer()) {
            user.outputStream.writeUTF(this.name + "( sent to all)" + " : " + msgToSend);
        }

    }

    private void sendToOne(String msgToSend, String recipient) throws IOException {
        // search for the recipient in the connected devices list.
        // ar is the vector storing client of active users
        for (ClientHandler user : ownerServer.getUserContainer()) {
            // if the recipient is found, write on its
            // output stream
            if (user.name.equals(recipient) && user.isLoggedIn) {
                user.outputStream.writeUTF(this.name + " : " + msgToSend);
                break;
            }
        }
    }


    @Override
    public String toString() {
        return name;
    }
}
