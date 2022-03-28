package tcpclient;
import java.net.*;


import java.io.*;

public class TCPClient {
    private static int BUFFERSIZE = 1024;
    public TCPClient() {
    }

    /**
     * Responsible to send queries to the server and 
     * returns the response
     * 
     * @param hostname          Domain name of the server client connects to
     * @param port              Servers TCP port number
     * @param bytesToServer     Data that shall be sent to the server
     * @return data             Data sent from the server
     * @throws IOException      
     */

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
        if( port <= 0 || port > 65535)
            throw new IllegalArgumentException("Portnumber out of bounds");
        
        byte[] outputServer = new byte[BUFFERSIZE];
        int outputLength;
        Socket socket = new Socket(hostname, port);
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        
        try{
            socket.getOutputStream().write(toServerBytes);
            while((outputLength = socket.getInputStream().read(outputServer, 0 , BUFFERSIZE)) != -1)  
                data.write(outputServer, 0 , outputLength);
            socket.close();
            data.close();
            return data.toByteArray();

        }catch(Exception ex){
            throw new IOException("Something went wrong.. " + ex.getMessage());

        }      


    }

}
