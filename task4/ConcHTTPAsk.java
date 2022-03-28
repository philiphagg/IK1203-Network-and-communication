package task4;


import java.net.ServerSocket;
import java.net.Socket;

public class ConcHTTPAsk {
    public static void main(String[] args) {
        try{
            int port = Integer.parseInt(args[0]);
            ServerSocket socket = new ServerSocket(port);

            while(true){
                Socket clientSocket = socket.accept();
                MyRunnable run = new MyRunnable(clientSocket);
                new Thread(run).start();
            }
        }catch(Exception exception){
            System.out.print(exception.getMessage());
        }
    }
}
