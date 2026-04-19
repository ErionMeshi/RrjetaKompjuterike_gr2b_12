import java.net.*;
import java.util.*;
import java.io.*;

public class UDPServer {

    // 1️. IP + PORT
    public static final int PORT = 1234;
    public static final String SERVER_IP = "0.0.0.0";

    // 2️. LIMIT KLIENTESH
    public static final int MAX_CLIENTS = 4;
    public static Set<String> clients = new HashSet<>();
    public static Map<String, Long> lastSeen = new HashMap<>();

    // 4️. LOG MESAZHESH
    public static List<String> messageLog = new ArrayList<>();

    // ROLE SYSTEM
    public static Map<String, String> roles = new HashMap<>();

    // 5️. TIMEOUT
    public static final long TIMEOUT = 30000;

    public static void main(String[] args) {
        try {
            DatagramSocket serverSocket =
                    new DatagramSocket(PORT, InetAddress.getByName(SERVER_IP));

            System.out.println("Serveri po dëgjon në port: " + PORT);

            // HTTP SERVER
            new Thread(() -> startHttpServer()).start();

            while (true) {

                byte[] buffer = new byte[65535];
                
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                serverSocket.receive(packet);

                String clientAddress =
                        packet.getAddress().toString() + ":" + packet.getPort();

                // LIMIT KLIENTESH
                if (!clients.contains(clientAddress)) {
                    if (clients.size() >= MAX_CLIENTS) {
                        System.out.println("Refuzohet klienti: " + clientAddress);
                        continue;
                    }
                    clients.add(clientAddress);
                    System.out.println("Klient i ri: " + clientAddress);
                }

                // UPDATE AKTIVITET
                lastSeen.put(clientAddress, System.currentTimeMillis());

                String message =
                        new String(packet.getData(), 0, packet.getLength());

                System.out.println("Mesazh nga " + clientAddress + ": " + message);

                // LOG
                messageLog.add(clientAddress + " -> " + message);

                // REMOVE INACTIVE
                removeInactiveClients();

                // HANDLE
                String response = handleRequest(message, clientAddress);

                byte[] data = response.getBytes();

                DatagramPacket responsePacket = new DatagramPacket(
                        data, data.length,
                        packet.getAddress(),
                        packet.getPort()
                );

                serverSocket.send(responsePacket);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // === HANDLE ===

    public static String handleRequest(String message, String clientAddress) {

        message = message.trim();

        // ROLE
        if (message.startsWith("ROLE:")) {
            String role = message.substring(5).trim();
            roles.put(clientAddress, role);
            return "Roli u vendos: " + role;
        }

        String role = roles.getOrDefault(clientAddress, "user").toLowerCase();

        // KOMANDA

        if (message.equals("/list")) {
            if (!role.equals("admin"))
                 return "Nuk ke privilegje!";
            return listFiles();
        }

        if (message.startsWith("/read")) {
            if (!role.equals("admin") && !role.equals("user")) {
                 return "Nuk ke privilegje!";
            return readFile(message.substring(6));
        }

        if (message.startsWith("/delete")) {
            if (!role.equals("admin"))
                return "Nuk ke privilegje!";
            File f = new File(message.substring(8));
            return f.exists() && f.delete()
                    ? "File u fshi!"
                    : "File nuk ekziston!";
        }

        if (message.startsWith("/search")) {
            if (!role.equals("admin")) return "Nuk ke privilegje!";
            String keyword = message.substring(8);

            StringBuilder sb = new StringBuilder();
            for (File f : new File(".").listFiles()) {
                if (f.getName().contains(keyword)) {
                    sb.append(f.getName()).append("\n");
                }
            }
            return sb.toString();
        }

        if (message.startsWith("/info")) {
            if (!role.equals("admin")) return "Nuk ke privilegje!";
            File f = new File(message.substring(6));
            if (!f.exists()) return "Nuk ekziston";

            return "Size: " + f.length() +
                    "\nLast Modified: " + new Date(f.lastModified());
        }

        if (message.startsWith("/upload")) {
            if (!role.equals("admin")) return "Nuk ke privilegje!";
            try {
                String[] parts = message.split(" ", 3);
                FileWriter fw = new FileWriter(parts[1]);
                fw.write(parts[2]);
                fw.close();
                return "Upload OK";
            } catch (Exception e) {
                return "Gabim upload";
            }
        }

        if (message.startsWith("/download")) {
            if (!role.equals("admin")) return "Nuk ke privilegje!";
            try {
                Scanner sc = new Scanner(new File(message.substring(10)));
                StringBuilder sb = new StringBuilder();

                while (sc.hasNextLine()) {
                    sb.append(sc.nextLine()).append("\n");
                }

                return sb.toString();
            } catch (Exception e) {
                return "ERROR";
            }
        }

        return "Komande e panjohur!";
    }

    // ================= FILE =================

    public static String listFiles() {
        File folder = new File(".");
        StringBuilder sb = new StringBuilder();

        for (File f : folder.listFiles()) {
            sb.append(f.getName()).append("\n");
        }
        return sb.toString();
    }

    public static String readFile(String fileName) {
        try {
            Scanner sc = new Scanner(new File(fileName));
            StringBuilder sb = new StringBuilder();

            while (sc.hasNextLine()) {
                sb.append(sc.nextLine()).append("\n");
            }

            return sb.toString();

        } catch (Exception e) {
            return "Gabim ne lexim!";
        }
    }

    // ================= TIMEOUT =================

    public static void removeInactiveClients() {
        long now = System.currentTimeMillis();

        Iterator<String> it = clients.iterator();
        while (it.hasNext()) {
            String client = it.next();

            if (now - lastSeen.getOrDefault(client, 0L) > TIMEOUT) {
                System.out.println("Klienti u largua: " + client);
                it.remove();
                lastSeen.remove(client);
            }
        }
    }

    // ================= HTTP SERVER =================

    public static void startHttpServer() {
        try {
            ServerSocket httpSocket = new ServerSocket(8080);
            System.out.println("HTTP server ne port 8080");

            while (true) {
                Socket client = httpSocket.accept();

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(client.getInputStream())
                );

                OutputStream out = client.getOutputStream();

                String requestLine = in.readLine();

                if (requestLine != null && requestLine.contains("GET /stats")) {
                    String response = getStats();

                    String httpResponse =
                            "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: text/plain\r\n\r\n" +
                            response;

                    out.write(httpResponse.getBytes());
                } else {
                    out.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
                }

                client.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getStats() {
        StringBuilder sb = new StringBuilder();

        sb.append("Klientet aktiv: ").append(clients.size()).append("\n");

        sb.append("IP:\n");
        for (String c : clients) {
            sb.append(c).append("\n");
        }

        sb.append("\nMesazhet:\n");
        for (String m : messageLog) {
            sb.append(m).append("\n");
        }

        return sb.toString();
    }
}