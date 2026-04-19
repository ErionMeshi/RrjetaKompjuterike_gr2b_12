package udp;

import javax.xml.crypto.Data;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class udpKlientii {

    public static void main(String[]args){
        try{
            DatagramSocket socket =new DatagramSocket();

            InetAddress serverAddresss=InetAddress.getByName("127.0.0.1");
            int port=1234;

            Scanner scanner=new Scanner(System.in);

            System.out.println("Zgjidh rolin (admin/user): ");
            String role= scanner.nextLine();

            sendMessage(socket, serverAddresss,port, "ROLE: "+role);

            String response=receiveResponse(socket);
            System.out.println("Server: "+response);

            
            while(true){
                System.out.println("shkruaj komanden: ");
                String message=scanner.nextLine();

                byte[]sendData=message.getBytes();

                DatagramPacket sendPacket=new DatagramPacket(sendData,sendData.length,serverAddresss,port);

                socket.send(sendPacket);


                byte[]buffer=new byte[4096];
                DatagramPacket responsePacket=new DatagramPacket(buffer,buffer.length);

                socket.receive(responsePacket);
                String response=new String(responsePacket.getData(),0,responsePacket.getLength());

                System.out.println("server: "+response);

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
