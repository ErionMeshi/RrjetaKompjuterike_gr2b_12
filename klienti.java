import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Klienti {
    // Përcaktojmë IP-në dhe Portin e serverit
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // PIKA 9: Qasje e plotë në folderat/përmbajtjen (Zgjedhja e rolit)
        System.out.println("Zgjidhni llojin e përdoruesit për këtë sesion:");
        System.out.println("1. Admin (Qasje e plotë: list, read, upload, delete, etj.)");
        System.out.println("2. Klient i thjeshtë (Vetëm lexim: list, read, info)");
        System.out.print("Zgjedhja juaj (1 ose 2): ");
        String roli = scanner.nextLine().trim().equals("1") ? "ADMIN" : "USER";

        // PIKA 6: Të definohen saktë socket-at dhe lidhja të mos dështojë
        // Përdorimi i 'try-with-resources' siguron që nëse serveri është i fikur, programi nuk "crashes" por jep mesazh.
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("\n[+] Lidhja me serverin u krye me sukses!");
            
            // Dërgojmë rolin në server në mënyrë që ai të dijë çfarë qasje të na japë
            out.println("ROLE:" + roli);
            System.out.println("[*] Jeni kyçur si: " + roli);

            while (true) {
                System.out.print("\nShkruaj komandën (ose 'quit' për dalje): ");
                String mesazhi = scanner.nextLine();

                if (mesazhi.equalsIgnoreCase("quit")) {
                    System.out.println("Po mbyllet lidhja...");
                    break;
                }

                // PIKA 10: Matja e kohës së përgjigjes (Fillimi)
                long kohaFillimit = System.currentTimeMillis();

                // PIKA 8: Të dërgojë mesazh serverit në formë të tekstit
                out.println(mesazhi);

                // PIKA 7: Të jetë në gjendje të lexojë përgjigjet që i kthehen nga serveri
                System.out.println("--- Përgjigja nga Serveri ---");
                String pergjigja;
                
                // Lexojmë përgjigjen (Supozohet që serveri dërgon "END" në fund të çdo mesazhi për të ndaluar loop-in)
                while ((pergjigja = in.readLine()) != null) {
                    if (pergjigja.equals("END")) {
                        break; 
                    }
                    System.out.println(pergjigja);
                }

                if (pergjigja == null) {
                    System.out.println("[-] Lidhja me serverin u shkëput.");
                    break;
                }

                // PIKA 10: Matja e kohës së përgjigjes (Fundi dhe Llogaritja)
                long kohaMbarimit = System.currentTimeMillis();
                long kohaReagimit = kohaMbarimit - kohaFillimit;
                
                System.out.println("-----------------------------");
                System.out.println("[!] Koha e përgjigjes: " + kohaReagimit + " ms");
                
                if (roli.equals("ADMIN")) {
                    System.out.println("[i] Serveri i trajton kërkesat e Admin me prioritet (Thread Priority).");
                }
            }

        } catch (ConnectException e) {
            // Pjesë e PIKËS 6: Parandalimi i dështimit të aplikacionit.
            System.out.println("[-] Lidhja dështoi: Serveri nuk është aktiv ose Porti është i gabuar.");
        } catch (IOException e) {
            System.out.println("[-] Gabim gjatë komunikimit: " + e.getMessage());
        }
    }
}