import java.net.*;
import java.util.*;

public class UDPServer {

    //IP dhe PORT
public static final int PORT = 1234;
public static final String SERVER_IP = "0.0.0.0";

//Limiti dhe lista e klienteve
public static final int MAX_Clients = 4;

public static Set<String> clients = new HashSet<>();
public static void main(String[] args) {

    try {
        InetAddress ipAddress = InetAddress.getByName(SERVER_IP);
        DatagramSocket serverSocket = new DatagramSocket(PORT, ipAddress);

        byte[] buffer = new byte[1024];

        System.out.println("Serveri po dëgjon në port: " + PORT);

        while (true) { //Pranon mesazhin
            DatagramPacket packeta = new DatagramPacket(buffer, buffer.length);
            serverSocket.receive(packeta);

            String clientAddress = packeta.getAddress().toString() +  ":" + packeta.getPort();
        
        //Menaxhimi i klienteve    
            if (!clients.contains(clientAddress)) {
                if (clients.size() >= MAX_Clients) {
                     System.out.println("Refuzohet klienti: " + clientAddress);
                     continue;
                 } else {
                     clients.add(clientAddress);
                    System.out.println("Klient i ri: " + clientAddress);
                 }
             } System.out.println("Mesazh nga " + clientAddress);

                 // HAPI 3: leximi dhe përpunimi i kërkesës
            String message = new String(packeta.getData(), 0, packeta.getLength());
            System.out.println("Kërkesë nga " + clientAddress + ": " + message);

            String response = handleRequest(message);

        
        // dërgo përgjigje
                byte[] sendData = response.getBytes();
                DatagramPacket responsePacket = new DatagramPacket(
                        sendData,
                        sendData.length,
                        packeta.getAddress(),
                        packeta.getPort()
                );
        serverSocket.send(responsePacket);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Përpunimi i kërkesave
    public static String handleRequest(String request) {
        request = request.trim().toLowerCase();

        switch (request) {
            case "hello":
                return "Pershendetje nga serveri!";
            case "time":
                return new Date().toString();
            case "clients":
                return "Numri i klienteve: " + clients.size();
            default:
                return "Kerkese e panjohur!";
        }
    }
}