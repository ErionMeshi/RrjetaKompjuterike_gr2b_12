package udp;

import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.lang.StringBuilder;
import java.net.ServerSocket;
import java.io.*;
import java.net.Socket;
import java.util.*;

public class udpServeri {
    public static final int PORT = 1234;
    public static final String SERVER_IP = "0.0.0.0";

    public static final int MAX_CLIENTS = 4;

    public static Map<String, Long> clients=new HashMap<>(); 
    public static List<String> messages=new ArrayList<>();
    public static Map<String, String> roles = new HashMap<>();

    public static final long TIMEOUT=30000;


    public static void main(String[]args){
        try{
            DatagramSocket serverSocket=new DatagramSocket(PORT, InetAddress.getByName(SERVER_IP)); 
            byte[]buffer=new byte[1024];

            System.out.println("serveri po ndegjon ne port: "+PORT);

            new Thread(()->StartHttpServer()).start();

            while(true){
                DatagramPacket packet=new DatagramPacket(buffer,buffer.length);
                serverSocket.receive(packet);

                String clientAddress=packet.getAddress().toString()+":"+packet.getPort();

                if(!clients.containsKey(clientAddress)){
                    if(clients.size()>=MAX_CLIENTS){
                        System.out.println("Refuzohet klienti: "+clientAddress);
                        continue;
                    }
                    else{
                        clients.put(clientAddress,System.currentTimeMillis());
                        System.out.println("klient i ri: "+clientAddress);

                    }
                }
                
                clients.put(clientAddress,System.currentTimeMillis());


                String message=new String(packet.getData(),0,packet.getLength()); 

                System.out.println("mesazh nga: "+clientAddress+": "+message);

                messages.add(clientAddress+":"+message);

                removeInactiveClients(); 

                String response=handleRequest(message,clientAddress);
                
                byte[] sendData=response.getBytes();
                DatagramPacket responsePacket=new DatagramPacket(sendData,sendData.length, packet.getAddress(),packet.getPort());

                serverSocket.send(responsePacket);

            }

        }
        catch(Exception e){
            e.printStackTrace();
        }

   }
    public static void removeInactiveClients(){
        long currentTime=System.currentTimeMillis();
        clients.entrySet().removeIf(entry->(currentTime-entry.getValue())>TIMEOUT);
        
    }

    public static String handleRequest(String message, String clientAddress){
        message = message.trim();

        if(message.startsWith("ROLE:")){
            String role = message.substring(5).trim();
            roles.put(clientAddress, role);
            return "Roli u vendos: " + role;
        }

        String role = roles.getOrDefault(clientAddress, "user");


        if(message.equals("/list")){
            if(!role.equals("admin")) return "Nuk ke privilegje!";
            return listFiles();
        }

        if(message.startsWith("/read")){
            String fileName = message.substring(6);
            return readFile(fileName);
        }

        if(message.startsWith("/delete")){
            if(!role.equals("admin")) return "Nuk ke privilegje!";
            File f = new File(message.substring(8));
            return f.exists() && f.delete() ? "File u fshi!" : "File nuk ekziston!";
        }

        if(message.startsWith("/search")){
            if(!role.equals("admin")) return "Nuk ke privilegje!";
            String keyword = message.substring(8);
            StringBuilder sb = new StringBuilder();
            for(File f : new File(".").listFiles()){
                if(f.getName().contains(keyword)){
                    sb.append(f.getName()).append("\n");
                }
            }
            return sb.toString();
        }

        if(message.startsWith("/info")){
            if(!role.equals("admin")) return "Nuk ke privilegje!";
            File f = new File(message.substring(6));
            if(!f.exists()) return "Nuk ekziston";
            return "Size: " + f.length() +
                    "\nLast Modified: " + new Date(f.lastModified());
        }

        if(message.startsWith("/upload")){
            if(!role.equals("admin")) return "Nuk ke privilegje!";
            try{
                String[] parts = message.split(" ", 3);
                FileWriter fw = new FileWriter(parts[1]);
                fw.write(parts[2]);
                fw.close();
                return "Upload OK";
            }catch(Exception e){
                return "Gabim upload";
            }
        }

        if(message.startsWith("/download")){
            if(!role.equals("admin")) return "Nuk ke privilegje!";
            try{
                Scanner sc = new Scanner(new File(message.substring(10)));
                StringBuilder sb = new StringBuilder();
                while(sc.hasNextLine()){
                    sb.append(sc.nextLine()).append("\n");
                }
                return sb.toString();
            }catch(Exception e){
                return "ERROR";
            }
        }

        return "Komande e panjohur";
    }



    public static String listFiles(){ 
        File folder=new File(".");
        StringBuilder sb=new StringBuilder(); 

        for(File file:folder.listFiles()){ 
            sb.append(file.getName()).append("\n"); 

        }
        return sb.toString(); 

    }
    public static String readFile(String fileName){ 
        try{
            Scanner sc=new Scanner(new File(fileName)); 
            StringBuilder sb=new StringBuilder(); 

            while(sc.hasNextLine()){ 
                sb.append(sc.nextLine()).append("\n"); 

            }
            return sb.toString(); 

        }
        catch(Exception e){
            return "gabim ne lexim te file!";

        }
    }

    public static void StartHttpServer(){
        try{
            ServerSocket httpSocket=new ServerSocket(8080);
            System.out.println("http server po punon ne port 8080");

            while(true){ 
                Socket client=httpSocket.accept(); 
                BufferedReader in =new BufferedReader(new InputStreamReader(client.getInputStream())); 

                OutputStream out=client.getOutputStream(); 

                String requestLine=in.readLine();

                if(requestLine!=null && requestLine.contains("GET /stats")){ 
                    String response=getStats(); 

                    String httpResponse="HTTP/1.1 200 OK\r\n"+ "Content-Type: text/plain\r\n\r\n"+response; 
                    out.write(httpResponse.getBytes());

                }
                else{
                    String httpResponse="HTTP/1.1 404 Not Found\r\n\r\n";
                    out.write(httpResponse.getBytes());

                }
                client.close();

            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public static String getStats(){
        StringBuilder sb=new StringBuilder();

        sb.append("klientet aktiv: ").append(clients.size()).append("\n");

        sb.append("IP Adresat:\n");
        for(String c:clients.keySet()){
            sb.append(c).append("\n");
        }

        sb.append("\nMesazhet:\n");
        for(String msg:messages){
            sb.append(msg).append("\n");

        }
        return sb.toString();

    }
}
