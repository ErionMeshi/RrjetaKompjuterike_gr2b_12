import java.net.*;
import java.util.*;
import java.io.*;

public class UDPServer {

    // HAPI 1
    public static final int PORT = 1234;
    public static final String SERVER_IP = "0.0.0.0";

    // HAPI 2
    public static final int MAX_CLIENTS = 4;
    public static Set<String> clients = new HashSet<>();

    // HAPI 4
    public static List<String> messageLog = new ArrayList<>();

    // HAPI 5 
    public static final long TIMEOUT = 30000; // 30 sekonda
    public static Map<String, Long> lastSeen = new HashMap<>();

    // HAPI 6 
    public static final String ADMIN_CLIENT = "/127.0.0.1:5001";
    public static final String SERVER_FOLDER = "files";

    public static void main(String[] args) {
        try {
            InetAddress ipAddress = InetAddress.getByName(SERVER_IP);
            DatagramSocket serverSocket = new DatagramSocket(PORT, ipAddress);

            byte[] buffer = new byte[1024];

            System.out.println("Serveri po dëgjon në port: " + PORT);

            while (true) {
                DatagramPacket packeta = new DatagramPacket(buffer, buffer.length);
                serverSocket.receive(packeta);

                String clientAddress = packeta.getAddress().toString() + ":" + packeta.getPort();

                // HAPI 5: update last seen
                lastSeen.put(clientAddress, System.currentTimeMillis());

                // HAPI 2: menaxhimi i klientëve
                if (!clients.contains(clientAddress)) {
                    if (clients.size() >= MAX_CLIENTS) {
                        System.out.println("Refuzohet klienti: " + clientAddress);
                        continue;
                    } else {
                        clients.add(clientAddress);
                        System.out.println("Klient i ri: " + clientAddress);
                    }
                }

                // HAPI 3: lexo mesazhin
                String message = new String(packeta.getData(), 0, packeta.getLength());
                System.out.println("Kërkesë nga " + clientAddress + ": " + message);

                // hapi 4: ruan mesazhin
                messageLog.add(clientAddress + " -> " + message);

                String response;

                // HAPI 6: kontroll për file access
                if (message.equalsIgnoreCase("LIST_FILES")) {
                    if (clientAddress.equals(ADMIN_CLIENT)) {
                        response = listFiles();
                    } else {
                        response = "Nuk keni qasje";
                    }
                } else if (message.startsWith("READ_FILE ")) {
                    if (clientAddress.equals(ADMIN_CLIENT)) {
                        String fileName = message.substring(10).trim();
                        response = readFile(fileName);
                    } else {
                        response = "Nuk keni qasje";
                    }
                } else {
                    // kërkesa normale
                    response = handleRequest(message);
                }

                sendResponse(serverSocket, packeta, response);

                // HAPI 5: largo klientët joaktiv
                removeInactiveClients();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // HAPI 3
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

    // dërgimi i përgjigjes
    public static void sendResponse(DatagramSocket socket, DatagramPacket request, String response) throws Exception {
        byte[] data = response.getBytes();
        DatagramPacket packet = new DatagramPacket(
                data,
                data.length,
                request.getAddress(),
                request.getPort()
        );
        socket.send(packet);
    }

    // HAPI 5
    public static void removeInactiveClients() {
        long now = System.currentTimeMillis();

        Iterator<String> it = clients.iterator();
        while (it.hasNext()) {
            String client = it.next();
            if (now - lastSeen.getOrDefault(client, 0L) > TIMEOUT) {
                System.out.println("Klienti u largua (timeout): " + client);
                it.remove();
                lastSeen.remove(client);
            }
        }
    }

    // HAPI 6
    public static String listFiles() {
        File folder = new File(SERVER_FOLDER);
        File[] files = folder.listFiles();

        if (files == null) return "Nuk ka fajlla";

        StringBuilder sb = new StringBuilder();
        for (File f : files) {
            sb.append(f.getName()).append("\n");
        }
        return sb.toString();
    }

    public static String readFile(String fileName) {
        try {
            File file = new File(SERVER_FOLDER + "/" + fileName);
            Scanner sc = new Scanner(file);

            StringBuilder sb = new StringBuilder();
            while (sc.hasNextLine()) {
                sb.append(sc.nextLine()).append("\n");
            }
            sc.close();
            return sb.toString();

        } catch (Exception e) {
            return "Gabim ne leximin e fajllit";
        }
    }
}