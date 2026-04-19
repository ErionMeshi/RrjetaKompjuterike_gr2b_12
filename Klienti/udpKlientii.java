package udp;

import javax.xml.crypto.Data;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import java.net.InetAddress;
import java.net.InetAddresns;
import java.util.Scanner;

public class udpKlientii {

    public static void main(String[]args){
        try{
            DatagramSocket socket =new DatagramSocket();

            InetAddress serverAddresss=InetAddress.getByName("127.0.0.1");
            int port=1234;

            Scanner scanner=new Scanner(System.in);

            System.out.println("Zgjidh rolin (admin/user): ");
            String role = scanner.nextLine().trim().toLowerCase();

            sendMessage(socket, serverAddresss,port, "ROLE: "+role);

            String response=receiveResponse(socket);
            System.out.println("Server: "+response);

            
            while(true){
                System.out.println("shkruaj komanden: ");
                String message=scanner.nextLine();

                if (command.equalsIgnoreCase("exit")) {
                    System.out.println("Klienti po mbyllet...");
                    break;
                }
                if (role.equals("user")) {
                    if (!command.startsWith("/read")) {
                        System.out.println("Vetem /read lejohet për user!");
                        continue;
                    }
                    Thread.sleep(1000); //delay per user
                }
                 if (command.startsWith("/upload")) {
                    String[] parts = command.split(" ");
                    if (parts.length < 2) {
                        System.out.println("Perdor: /upload filename");
                        continue;
                    }
                    String fileName = parts[1];
                    String content = "";
                    try {
                        Scanner fileScanner = new Scanner(new java.io.File(fileName));
                        while (fileScanner.hasNextLine()) {
                            content += fileScanner.nextLine() + "\\n";
                        }
                        fileScanner.close();

                        command = "/upload " + fileName + " " + content;

                    } catch (Exception e) {
                        System.out.println("File nuk ekziston!");
                        continue;
                    }
                }

                sendMessage(socket,serverAddresss,port,command);

                response=receiveResponse(socket);

                    if (command.startsWith("/download") && !response.startsWith("ERROR")) {
                    String fileName = command.split(" ")[1];

                    java.io.FileWriter fw = new java.io.FileWriter("download_" + fileName);
                    fw.write(response);
                    fw.close();

                    System.out.println("File u ruajt si: download_" + fileName);
                } else {
                    System.out.println("Server: " + response);
                }


            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
        public static void sendMessage(DatagramSocket socket,InetAddress address, int port, String message) throws Exception{

        byte[]data=message.getBytes();
        DatagramPacket packet=new DatagramPacket(data,data.length,address,port);
        socket.send(packet);
    }
    public static String receiveResponse(DatagramSocket socket) throws Exception{
        byte[]buffer=new byte[65535];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        return new String(packet.getData(),0,packet.getLength());
    }
}
