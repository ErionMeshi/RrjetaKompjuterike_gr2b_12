import java.net.*;
import java.util.*;
import java.io.*;

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
   //  --6 
    //  klienti do te kete qasje te plote
    public static final String ADMIN_CLIENT = "/127.0.0.1:5001";

    // Folderi ku serveri do te shikoje fajllat
    public static final String SERVER_FOLDER = "files";
    //  6 
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

                                //  6 
                // Kontrollojme nese klienti po kerkon qasje ne fajlla
                if (message.equalsIgnoreCase("LIST_FILES")) {
                    if (clientAddress.equals(ADMIN_CLIENT)) {
                        String response = listFiles();
                        sendResponse(serverSocket, packeta, response);
                    } else {
                        sendResponse(serverSocket, packeta, "Nuk keni qasje per LIST_FILES");
                    }
                }
                else if (message.startsWith("READ_FILE ")) {
                    if (clientAddress.equals(ADMIN_CLIENT)) {
                        String fileName = message.substring(10).trim();
                        String response = readFile(fileName);
                        sendResponse(serverSocket, packeta, response);
                    } else {
                        sendResponse(serverSocket, packeta, "Nuk keni qasje per READ_FILE");
                    }
                }
                //  6 
                 // Kontrollon dhe largon klientet qe kane kaluar kohen e lejuar
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
        // 6 
    public static void sendResponse(DatagramSocket serverSocket, DatagramPacket requestPacket, String response) throws IOException {
        byte[] responseData = response.getBytes();

        DatagramPacket responsePacket = new DatagramPacket(
                responseData,
                responseData.length,
                requestPacket.getAddress(),
                requestPacket.getPort()
        );

        serverSocket.send(responsePacket);
    }

    public static String listFiles() {
        File folder = new File(SERVER_FOLDER);

        if (!folder.exists() || !folder.isDirectory()) {
            return "Folderi nuk ekziston.";
        }

        File[] files = folder.listFiles();

        if (files == null || files.length == 0) {
            return "Nuk ka fajlla ne folder.";
        }

        StringBuilder result = new StringBuilder("Fajllat:\n");
        for (File file : files) {
            result.append(file.getName()).append("\n");
        }

        return result.toString();
    }

    public static String readFile(String fileName) {
        File file = new File(SERVER_FOLDER, fileName);

        if (!file.exists() || !file.isFile()) {
            return "Fajlli nuk ekziston.";
        }

        StringBuilder content = new StringBuilder();

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine()).append("\n");
            }
        } catch (Exception e) {
            return "Gabim gjate leximit te fajllit.";
        }

        return content.toString();
    }
    //  6 

}