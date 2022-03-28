import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.io.*;
import tcpclient.TCPClient;


/*
Dessa länkar fungerar
http://localhost:8888/ask?hostname=time.nist.gov&port=13
http://localhost:8888/ask?hostname=java.lab.ssvl.kth.se&port=13
http://localhost:8888/ask?hostname=whois.iis.se&port=43&string=kth.se
http://localhost:8888/ask?hostname=java.lab.ssvl.kth.se&port=7&string=kth.se&shutdown=true
http://localhost:8888/ask?hostname=java.lab.ssvl.kth.se&port=9&string=kth.se&shutdown=true
http://localhost:8888/ask?hostname=java.lab.ssvl.kth.se&port=19&string=kth.se&shutdown=true&limit=500

Dessa länkar fungerar inte
http://localhost:8888/ask?hostname=whois.internic.net&port=43&string=google.com

*/



public class HTTPAsk {

    static boolean shutdown = false;
	static Integer timeout = null;
	static Integer limit = null;
	static String hostname = null;
	static Integer portNo = 0;
	static String toServerBytes = "";
    static int BUFFERSIZE = 1024;
    static int outputLength;
    static boolean httpok = false;


    public static void main(String[] args) throws IOException {

        String header200 = "HTTP/1.1 200 OK\r\n\r\n";
        String header404 = "HTTP/1.1 404 Not Found\r\n";
        String header400 = "HTTP/1.1 400 Bad Request\r\n";
        
        int port = Integer.parseInt(args[0]);

        ServerSocket ServerSocket = new ServerSocket(port);

        ByteArrayOutputStream data = new ByteArrayOutputStream();
        byte[] outputServer = new byte[BUFFERSIZE];


        try{

            while(true){
                //Open socket
                Socket clientSocket = ServerSocket.accept();
                OutputStream os = clientSocket.getOutputStream();
                //Get input
                InputStream is = clientSocket.getInputStream();
                while((outputLength = is.read(outputServer, 0 , BUFFERSIZE)) != -1){
                    data.write(outputServer, 0 , outputLength);
                    if(new String(outputServer).contains("HTTP/1.1")){
                        break;
                    }
                }
                System.out.print(new String(outputServer));
                //prase input
                
                if(ask(outputServer)){
                    try{
                        TCPClient TCPC = new TCPClient(shutdown, timeout, limit);
                        byte[] response = TCPC.askServer(hostname, portNo, toServerBytes.getBytes(StandardCharsets.UTF_8));                
                        os.write(header200.getBytes(StandardCharsets.UTF_8));    
                        os.write((new String(response)).getBytes(StandardCharsets.UTF_8));

                    }catch(IOException e){
                        os.write(header404.getBytes(StandardCharsets.UTF_8));    
                        
                    }
                }else{
                    os.write(header400.getBytes(StandardCharsets.UTF_8));    
                }

                clientSocket.close();
                
            }

        }catch(IOException e){
            System.out.println(e);
        }
    }
    private static boolean ask(byte[] outputServer) {
        return parseLink(new String(outputServer));
    }
    public static boolean parseLink(String link){
        
        
        String[] word = link.split("[?&= ]");
        int index = 0;

        while(index < word.length){
            if(word[index].equals("limit")){
                limit = Integer.parseInt(word[++index]);

            }if(word[index].equals("shutdown")){
                if(word[++index].equals("true"))
                shutdown = true;

            }if(word[index].equals("port")){
                portNo = Integer.parseInt(word[++index]);

            }if(word[index].equals("timeout")){
                timeout = Integer.parseInt(word[++index]);

            }if(word[index].equals("hostname")){
                hostname = word[++index];
            }if(word[index].equals("string")){
                toServerBytes = word[++index];
            }if(word[index].contains("HTTP/1.1")){
                httpok = true;
            }


            index++;
        }

        return word[1].equals("/ask") && httpok && word[0].equals("GET");
    }
}

