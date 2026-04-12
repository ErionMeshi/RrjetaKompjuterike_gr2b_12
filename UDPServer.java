import java.net.*;

public class UDPServer {

public static final int PORT = 1234;
public static final String SERVER_IP = "0.0.0.0";


public static void main(String[] args) {

    try {
        InetAddress ipAddress = InetAddress.getByName(SERVER_IP);
        DatagramSocket serverSocket = new DatagramSocket(PORT, ipAddress);

        System.out.println("Serveri po punon...");
        System.out.println("IP: " + SERVER_IP);
        System.out.println("Port: " + PORT);
    }
    catch (Exception e) {
    e.printStackTrace();
} 
}

}