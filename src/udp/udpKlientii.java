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
}
