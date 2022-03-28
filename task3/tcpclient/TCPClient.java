package tcpclient;
import java.net.*;
import java.net.SocketAddress;


import java.io.*;

public class TCPClient {
    private static int BUFFERSIZE = 1024;
    private boolean shutdown;
    private Integer timeout;
    private Integer limit;


    public TCPClient(boolean shutdown, Integer timeout, Integer limit) {
        this.shutdown = shutdown;
        this.timeout = timeout;
        this.limit = limit;
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
        
        if(limit != null && BUFFERSIZE < limit){
            BUFFERSIZE = limit;
        }

        byte[] outputServer = new byte[BUFFERSIZE];
        int outputLength;
        
        ByteArrayOutputStream data = new ByteArrayOutputStream();

        
            Socket socket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(hostname, port); 
        try{
            if(timeout != null){
                socket.connect(socketAddress, timeout);
            }else{
                socket.connect(socketAddress);
            }

        
        
            socket.getOutputStream().write(toServerBytes);
            if(shutdown) {socket.shutdownOutput();}
            if(limit != null){
                while((outputLength = socket.getInputStream().read(outputServer, 0 , limit)) != -1 && data.size() != limit){
                data.write(outputServer, 0 , limit);
            }
            }else {
                while((outputLength = socket.getInputStream().read(outputServer, 0 , BUFFERSIZE)) != -1){
                    data.write(outputServer, 0 , outputLength);
                }
            }

        }catch(IOException e){
            socket.close();
            throw new IOException(e);
        }

        socket.close();
        data.close();
        return data.toByteArray();

    }
}