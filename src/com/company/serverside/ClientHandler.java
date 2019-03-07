package com.company.serverside;
/*
Author: BeGieU
Date: 06.03.2019
*/

import java.io.*;
import java.net.Socket;
import java.util.Vector;
//TODO fix indexing clients(when removing client and then adding another client Program  creates bugged names of clients)
public class ClientHandler implements Runnable
{
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private final Socket socket;

    private Vector<ClientHandler> userContainer;
    private final String name;
    private boolean isLoggedIn;

    public ClientHandler(String name, Socket socket) throws IOException
    {
        this.socket = socket;
        this.name = name;
        isLoggedIn = true;

        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    private void closeConnection() throws IOException
    {

        System.out.println("Client " + this.socket + " sends exit...");
        System.out.println("Closing this connection.");

        this.isLoggedIn = false;
        inputStream.close();
        outputStream.close();
        this.socket.close();
        Server.usersCount--;

        System.out.println("Connection closed");

    }

    private void sendToAll(String msgToSend) throws IOException
    {
        for (ClientHandler user : Server.userContainer)
        {
                user.outputStream.writeUTF(this.name+ "( sent to all)" + " : " + msgToSend);
        }

    }

    //private void sendToOne()

    @Override
    public void run()
    {
        try
        {
            //TODO handle message sent to all users !

            String receivedMsg;
            while (true)
            {

                receivedMsg = inputStream.readUTF();
                System.out.println(receivedMsg);

                if (receivedMsg.equals("Exit"))
                {
                    this.closeConnection();
                    Server.userContainer.remove(this);
                    break;
                }
                else if (receivedMsg.equals("list users"))
                {
                    String connectedUsers = Server.showConnectedUsers();
                    outputStream.writeUTF(connectedUsers);
                    continue;
                }

                //divide receivedMsg into msg to send and recipient to sent to
                String[] result = receivedMsg.split("-> ");
                String msgToSend = result[0];
                String recipient = result[1];

                if(recipient.equals("all"))
                {
                    sendToAll(msgToSend);
                }

                // search for the recipient in the connected devices list.
                // ar is the vector storing client of active users
                for (ClientHandler user : Server.userContainer)
                {
                    // if the recipient is found, write on its
                    // output stream
                    if (user.name.equals(recipient) && user.isLoggedIn)
                    {
                        user.outputStream.writeUTF(this.name + " : " + msgToSend);
                        break;
                    }
                }
            }

            this.closeConnection();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


    }

    @Override
    public String toString()
    {
        return name;
    }

    public void setUserContainer(Vector<ClientHandler> userContainer)
    {
        this.userContainer = userContainer;
    }
}
