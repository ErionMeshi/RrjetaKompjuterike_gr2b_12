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

            // Sa here klienti dergon mesazh ruajme kohen aktuale
                lastSeen.put(clientAddress, System.currentTimeMillis());
        
        if (!clients.contains(clientAddress)) {
                    if (clients.size() >= MAX_Clients) {
                        System.out.println("Refuzohet klienti: " + clientAddress);
                        continue;
                    } else {
                        clients.add(clientAddress);
                        System.out.println("Klient i ri u lidh: " + clientAddress);
                    }
                } System.out.println("Mesazh nga " + clientAddress);
                 // Kontrollon dhe largo klientet qe kane kaluar kohen e lejuar
                removeInactiveClients();
        }


    }
    catch (Exception e) {
    e.printStackTrace();
} 
}
 // Kjo metode kontrollon se cilet kliente kane qene joaktiv
    public static void removeInactiveClients() {
        long now = System.currentTimeMillis();

        Iterator<Map.Entry<String, Long>> iterator = lastSeen.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();

            // Nese klienti nuk ka derguar mesazh per me shume se TIMEOUT
            if (now - entry.getValue() > TIMEOUT) {
                String inactiveClient = entry.getKey();

                // Hiqet nga lastSeen
                iterator.remove();

                // Hiqet edhe nga lista e klienteve aktiv
                clients.remove(inactiveClient);

                System.out.println("Klienti u largua per shkak te joaktivitetit: " + inactiveClient);
            }
        }
    }

}