
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

class client_java_udp
{
    public static DatagramSocket clientsocket;
    public static DatagramPacket dp;
    public static BufferedReader dis;

public static int attempts = 1;
    public static InetAddress ia;
    public static byte buf[] = new byte[1024];
    public static int INITPORT=12222 ,CLIENTPORT=9999;
    

    public static int host;
    public static int port;
    public static String directory;
    public static String command;

    static int clientPort;
    static String filename;
    
 
 public static void putfile(String dir, String host, int port) throws SocketException, UnknownHostException, IOException, InterruptedException{
        File file = null;
        FileInputStream f = null;
        int times=0;
        int bufferlength = 48;
        DatagramSocket clientplace = new DatagramSocket();
        InetAddress addr = null;
        DatagramPacket sendPacket;
            try {
          addr = InetAddress.getByName(host);
             }catch(Exception e){
                System.out.println("Invalid port Number, Terminating");
                System.exit(0);
            }
       byte[] data = new byte[bufferlength];
        String location = dir;
        try{
        file = new File(location);
        f = new FileInputStream(file);
        }catch(Exception e){
            String str = "INVALID DIRECTORY";
        clientsocket.send(new DatagramPacket(str.getBytes(),str.length(), addr, INITPORT));
        System.exit(0);
        }
        int length_file = 0; 
        while((times = f.read(data)) != -1)    //calculate total length of file
        {
            length_file += times;
        }
        int packnumber = length_file/bufferlength;
        String str = Integer.toString(packnumber);            
        clientsocket.send(new DatagramPacket(str.getBytes(),str.length(), addr, INITPORT));
        f.close();
        FileInputStream f1 = new FileInputStream(file);
        while((times = f1.read(data)) != -1 )
        { 
            if(packnumber<=0)
                break;
            
            String str4 = "LENGTH"+data.length;            
            try{
            clientsocket.send(new DatagramPacket(str4.getBytes(),str4.length(), addr, INITPORT));            
            clientsocket.setSoTimeout(500);           
            sendPacket = new DatagramPacket(data, data.length, addr, port);
            clientplace.send(sendPacket);
            }catch(Exception e){
                System.out.println("Failed to send data to server. Terminating.");
                return;
            }
            
            packnumber--;
            String packno = Integer.toString(packnumber- (length_file/bufferlength));
            str = "CHUNK"+packno;
            
            clientsocket.send(new DatagramPacket(str.getBytes(),str.length(), addr, INITPORT));
            
            clientsocket.setSoTimeout(1000);
            try{
            clientsocket.receive(dp);
            }catch(Exception e){
                TimeUnit.SECONDS.sleep(1);
                packnumber++;
                if(attempts>2){
                    System.out.println("Failed to send data to server. Terminating.");
                    return;
                }else{
                    attempts++;
                continue;
                }
            }
            String str2 = new String(dp.getData(), 0,dp.getLength());
            System.out.println(str2);
        }
        try{
        clientsocket.receive(dp);
        String str2 = new String(dp.getData(), 0,dp.getLength());
        System.out.println(str2);
        }catch(Exception e){
            
        }
 }

    
    public static void getfile(String host, int port) throws IOException{
        DatagramSocket clientplace;
        InetAddress addr = null;
        try{  
         clientplace = new DatagramSocket(port);
            }catch(Exception e){
            System.out.println("Invalid port Number, Terminating");
            System.exit(0);
        }
        try{
            addr = InetAddress.getByName(host);
        }catch(Exception e){
            System.out.println("INVALID HOSTNAME");
        }
        byte[] recData = new byte[48];
        int i =0;

        FileWriter file = new FileWriter("UDP_GET.txt");
        PrintWriter out = new PrintWriter(file);
        int length_file = 0; 
        clientsocket.receive(dp);
        String str4 = new String(dp.getData(), 0,dp.getLength());            //Get~c:/users/zeesh/Desktop/File1.txt
        System.out.println(str4);
        int packno = Integer.parseInt(str4);
        int z= 1;
        while(true)
        {
            
            if(packno<=0)
                break;
            packno--;
            
             try{
                 clientsocket.receive(dp);
             }catch(Exception e){
               System.out.println("Did Not Receive valid data from Server. Terminating.");  
               return;
             }
        str4 = new String(dp.getData(), 0,dp.getLength());            //Get~c:/users/zeesh/Desktop/File1.txt   csi516-fa18.arcc.albany.edu.
        System.out.println(str4);
                try{
              clientsocket.setSoTimeout(500);
            clientsocket.receive(dp);
             str4 = new String(dp.getData(), 0,dp.getLength());            
    //        System.out.println(str4);
            length_file += dp.getLength();
            out.println(str4);
            out.flush();
            }catch(Exception e){
                System.out.println("Did Not Receive valid data from Server. Terminating");
                return;
            }

            
            clientsocket.setSoTimeout(500);
            try{
            clientsocket.receive(dp);

            }catch(Exception e){
                System.out.println("Did Not Receive valid data from Client. Terminating");
                return;
            }
            String str = new String(dp.getData(), 0,dp.getLength());            
            System.out.println(str);
            String str1 = "ACK"+z;
            buf = str1.getBytes();
            clientsocket.send(new DatagramPacket(buf,str1.length(), addr, INITPORT));
            z++;
        }
             String str1 = "Message Length Received"+length_file+"Bytes";
            buf = str1.getBytes();
            clientsocket.send(new DatagramPacket(buf,str1.length(), addr, INITPORT));
    }
    
    public static void main(String args[]) throws IOException, SocketException, UnknownHostException, InterruptedException
    {
              
        clientsocket = new DatagramSocket(CLIENTPORT);
        dp = new DatagramPacket(buf,buf.length);
        dis = new BufferedReader(new InputStreamReader(System.in));
          ia = InetAddress.getByName("csi516-fa18.arcc.albany.edu");
          
          
              TimeUnit.SECONDS.sleep(2);
        System.out.println(" CLIENT...");
        
        String aa = "connected";
        clientsocket.send(new DatagramPacket(aa.getBytes(),aa.length(), ia, INITPORT));
              
        while(true){
        clientsocket.setSoTimeout(10000);        
        try{
            clientsocket.receive(dp);
        }catch(Exception e){
           System.out.println("Could not connect to server. Terminating. ");
           System.exit(0);
       }
        String str = new String(dp.getData(), 0,dp.getLength()); 
        if(str.equalsIgnoreCase("exit"))
            return;
        System.out.println("SERVER: " + str);
        String host = new String(dis.readLine());
        buf = host.getBytes();
        clientsocket.send(new DatagramPacket(buf,host.length(), ia, INITPORT));
         try{
           InetAddress IA = InetAddress.getByName(host);        //csi516-fa18.arcc.albany.edu.
       } catch(Exception e){
           System.out.println("Could not connect to server. Terminating. ");
           System.exit(0);
       }
       
        clientsocket.receive(dp);
        str = new String(dp.getData(), 0,dp.getLength()); 
                if(str.equalsIgnoreCase("exit"))
            return;
        System.out.println("SERVER: " + str);
        String str1 = new String(dis.readLine());
         buf = str1.getBytes();
        clientsocket.send(new DatagramPacket(buf,str1.length(), ia, INITPORT));
        try{
            port = Integer.parseInt(str1);
       
        if(port>=65535){
            System.out.println("Invalid port Number, Terminating");
            System.exit(0);
        }
        } catch(Exception e){
            System.out.println("Invalid port Number, Terminating");
            System.exit(0);
        }     
        clientsocket.receive(dp);
        str = new String(dp.getData(), 0,dp.getLength());  
                if(str.equalsIgnoreCase("exit"))
            return;
        System.out.println("SERVER: " + str);
        str1 = new String(dis.readLine());
        String command1 = str1;
        buf = str1.getBytes();
        clientsocket.send(new DatagramPacket(buf,str1.length(), ia, INITPORT));

        try{
            String[] arrOfStr = command1.split("~", 2);
        command = arrOfStr[0];
            directory = arrOfStr[1];
            }catch(Exception e){
            System.out.println("Failed to send data to Server. Terminating.");
            System.exit(0);
        }
        
              
       if(command.equalsIgnoreCase("put")){client_java_udp.putfile(directory, host, port);
        }else if(command.equalsIgnoreCase("get")){
                          client_java_udp.getfile(host, port);       
        }else if(command.equalsIgnoreCase("ls")){
                           clientsocket.receive(dp);
                         str = new String(dp.getData(), 0,dp.getLength());            
                         System.out.println("SERVER: " + str);
                         if(str.equalsIgnoreCase("No such Directory"))
                             {
                                 System.exit(0);
                             }

         }else if(command.equalsIgnoreCase("cd")){
                                clientsocket.receive(dp);
                         str = new String(dp.getData(), 0,dp.getLength());            
                         System.out.println("SERVER: " + str);
                             if(str.equalsIgnoreCase("No such Directory"))
                             {
                                 System.exit(0);
                             }
                }
          }
        
    }
}






 