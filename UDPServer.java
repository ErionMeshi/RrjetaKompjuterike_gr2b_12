import java.net.*;
import java.util.*;

public class UDPServer {

public static final int PORT = 1234;
public static final String SERVER_IP = "0.0.0.0";

public static final int MAX_Clients = 4;


public static Set<String> clients = new HashSet<>();

 // ================== 5 ==================
    // TIMEOUT 
    public static final long TIMEOUT = 30000; // 30 sekonda

    // bahet rujtja e kohes te fundit kur klienti ka derguar mesazh
    public static Map<String, Long> lastSeen = new HashMap<>();
    // ==================  5 ==================

public static void main(String[] args) {

    try {
        InetAddress ipAddress = InetAddress.getByName(SERVER_IP);
        DatagramSocket serverSocket = new DatagramSocket(PORT, ipAddress);

        byte[] buffer = new byte[1024];

        System.out.println("Serveri po dëgjon në port: " + PORT);

        while (true) {
            DatagramPacket packeta = new DatagramPacket(buffer, buffer.length);
            serverSocket.receive(packeta);

            String clientAddress = packeta.getAddress().toString() +  ":" + packeta.getPort();
        
        if (!clients.contains(clientAddress)) {
                    if (clients.size() >= MAX_Clients) {
                        System.out.println("Refuzohet klienti: " + clientAddress);
                        continue;
                    } else {
                        clients.add(clientAddress);
                        System.out.println("Klient i ri u lidh: " + clientAddress);
                    }
                } System.out.println("Mesazh nga " + clientAddress);
        }


    }
    catch (Exception e) {
    e.printStackTrace();
} 
}

}