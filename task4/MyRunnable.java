package task4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


/*
    http://localhost:8888/ask?hostname=time.nist.gov&port=13
    http://localhost:8888/ask?hostname=java.lab.ssvl.kth.se&port=13
    http://localhost:8888/ask?hostname=whois.iis.se&port=43&string=kth.se
    http://localhost:8888/ask?hostname=whois.iis.se&port=43&string=kth.se&limit=300
    http://localhost:8888/ask?hostname=java.lab.ssvl.kth.se&port=7&string=kth.se&shutdown=true
    http://localhost:8888/ask?hostname=java.lab.ssvl.kth.se&port=9&string=kth.se&shutdown=true
    http://localhost:8888/ask?hostname=java.lab.ssvl.kth.se&port=19&string=kth.se&shutdown=true&limit=500
*/


public class MyRunnable implements Runnable {

    static final int BUFFERSIZE = 1024;
    boolean shutdown = false;
    Integer timeout = null;
    Integer limit = null;
    String hostname = null;
    Integer portNo = 0;
    String toServerBytes = "";
    boolean httpok = false;
    boolean favicon = false;
    Socket clientSocket;

    public MyRunnable(Socket s) {
        this.clientSocket = s;
    }

    public void run() {

        String header200 = "HTTP/1.1 200 OK\r\n\r\n";
        String header404 = "HTTP/1.1 404 Not Found\r\n";
        String header400 = "HTTP/1.1 400 Bad Request\r\n";

        try {
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            byte[] outputServer = new byte[BUFFERSIZE];


            OutputStream os = clientSocket.getOutputStream();
            InputStream is = clientSocket.getInputStream();
            int readBytes = is.read(outputServer);
            while (readBytes != -1) {
                data.write(outputServer, 0, readBytes);
                if (new String(outputServer).contains("HTTP/1.1")) {
                    break;
                }
            }
            System.out.println(data);
            favicon = false;
            parseLink(data.toString());

            if (!favicon){
                if (hostname != null) {
                    try {
                        TCPClient TCPC = new TCPClient(shutdown, timeout, limit);
                        byte[] response = TCPC.askServer(hostname, portNo, toServerBytes.getBytes(StandardCharsets.UTF_8));
                        os.write(header200.getBytes(StandardCharsets.UTF_8));
                        os.write((new String(response)).getBytes(StandardCharsets.UTF_8));

                    } catch (IOException e) {
                        os.write(header404.getBytes(StandardCharsets.UTF_8));

                    }
                } else {
                    os.write(header400.getBytes(StandardCharsets.UTF_8));
                }
            os.flush();
            clientSocket.close();
        }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parseLink(String link) {
        if(!link.contains("/favicon.ico")) {
            String[] lines = link.split("\\r?\\n");

            String[] word = lines[0].split("[?&= ]");
            int index = 0;

            while (index < word.length) {
                if (word[index].equals("limit")) {
                    limit = Integer.parseInt(word[++index]);
                }
                if (word[index].equals("shutdown")) {
                    shutdown = Boolean.parseBoolean(word[++index]);
                }
                if (word[index].equals("port")) {
                    portNo = Integer.parseInt(word[++index]);
                }
                if (word[index].equals("timeout")) {
                    timeout = Integer.parseInt(word[++index]);
                }
                if (word[index].equals("hostname")) {
                    hostname = word[++index];
                }
                if (word[index].equals("string")) {
                    toServerBytes = word[++index];
                }
                if (word[index].contains("HTTP/1.1")) {
                    httpok = true;
                }
                index++;
            }

        }else{
            favicon = true;
        }

    }
}