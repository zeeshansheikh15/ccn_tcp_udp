
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
class server_java_udp
{
public static int attempts = 1;    
public static DatagramSocket serversocket;
public static DatagramPacket dp;
public static BufferedReader dis;
public static InetAddress IpAddress;
public static byte buf[] = new byte[1024];
public static int INITPORT=12222 , CLIENTPORT;  

public static String host;
public static int port;
public static String directory;
public static String command;
    
static int serverPort;
static String filename;

 public static void putfile( String host, int port) throws IOException{
          DatagramSocket sockserver;
        try{
           sockserver = new DatagramSocket(port);
        }catch(SocketException e){
            System.out.println("INVALID PORT ");
            return;
        }
        try{
            InetAddress IpAddress2 = InetAddress.getByName(host);
        }catch(UnknownHostException e){
            System.out.println("INVALID HOSTNAME");
            return;
        }
        byte[] received = new byte[48];
        int i =0;

        FileWriter file = new FileWriter("UDP_PUT.txt");
        PrintWriter out = new PrintWriter(file);
         int fullength = 0; 
        serversocket.receive(dp);
        String str4 = new String(dp.getData(), 0,dp.getLength());            //Get~c:/users/zeesh/Desktop/File1.txt
        System.out.println(str4);
        int packno = Integer.parseInt(str4);
        int z= 1;

        while(true)
        {
             if(packno<=0)
                break;
            packno--;
            
            serversocket.setSoTimeout(500);
             try{
            serversocket.receive(dp);
             str4 = new String(dp.getData(), 0,dp.getLength()); 
             fullength += dp.getLength();
            System.out.println( str4);
            }catch(Exception e){
                System.out.println("Did Not Receive valid data from Client. Terminating");
                return;
            }
            DatagramPacket incomingpacket = new DatagramPacket(received, received.length);
            sockserver.setSoTimeout(500);
            try{
            sockserver.receive(incomingpacket);
            }catch(Exception e){
               System.out.println("Did Not Receive valid data from Client. Terminating");
               return;
            }
            String line = new String(incomingpacket.getData());
            out.println(line);
            out.flush();
            
            serversocket.setSoTimeout(500);
            try{
            serversocket.receive(dp);
            }catch(Exception e){
                System.out.println("Did Not Receive valid data from Client. Terminating");
                return;
            }
            String str = new String(dp.getData(), 0,dp.getLength());            
            System.out.println(str);
            String str1 = "ACK"+z;
            buf = str1.getBytes();
            serversocket.send(new DatagramPacket(buf,str1.length(), IpAddress, CLIENTPORT));
            z++;
        }
         String str1 = "Message Length Received "+fullength+" Bytes";
            buf = str1.getBytes();
            serversocket.send(new DatagramPacket(buf,str1.length(), IpAddress, CLIENTPORT));
    }      

 public static void getfile(String dir, String host, int port) throws SocketException, UnknownHostException, IOException, InterruptedException{
        File file;
        FileInputStream fis = null;
        int num=0;
        int bufferlength = 48;
        DatagramSocket sockserver = new DatagramSocket();
        InetAddress IpAddress2 = null;
        DatagramPacket sendPacket;
        try{
        IpAddress2 = InetAddress.getByName(host);
        }catch(Exception e){
            System.out.println("INVALID HOSTNAME");
            return;
        }
       byte[] outgoingdata = new byte[bufferlength];
        String filePath = dir;
        try{
        file = new File(filePath);
        fis = new FileInputStream(file);
        }catch(Exception e){
            String str = "No such Directory";
        serversocket.send(new DatagramPacket(str.getBytes(),str.length(), IpAddress, CLIENTPORT));
        return;
        }
        int fullength = 0; 
        while((num = fis.read(outgoingdata)) != -1)    //calculate total length of file
        {
            fullength += num;
        }
        int pno = fullength/bufferlength;
        String str = Integer.toString(pno);            
            serversocket.send(new DatagramPacket(str.getBytes(),str.length(), IpAddress, CLIENTPORT));
        fis.close();
        FileInputStream fis1 = new FileInputStream(file);
        while((num = fis1.read(outgoingdata)) != -1 )
        { 
            if(pno<=0)
                break;
             try{
            str = "LENGTH"+outgoingdata.length;            
            serversocket.send(new DatagramPacket(str.getBytes(),str.length(), IpAddress, CLIENTPORT));
          
            serversocket.send(new DatagramPacket(outgoingdata, outgoingdata.length, IpAddress, CLIENTPORT));            
            serversocket.setSoTimeout(500);
         
            pno--;
            String packno = Integer.toString(pno- (fullength/bufferlength));
            str = "CHUNK"+packno;
            
            serversocket.send(new DatagramPacket(str.getBytes(),str.length(), IpAddress, CLIENTPORT));
            
            serversocket.setSoTimeout(1000);
           
            serversocket.receive(dp);
            }catch(Exception e){
                pno++;
                if(attempts>2){
                    System.out.println("Result transmission failed.Terminating.");
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
        serversocket.receive(dp);
        String str2 = new String(dp.getData(), 0,dp.getLength());
        System.out.println(str2);
        }catch(Exception e){
            
        }
        
 }

    public static void main(String args[]) throws SocketException, IOException, UnknownHostException, InterruptedException
    {
         serversocket = new DatagramSocket(INITPORT);
         dp = new DatagramPacket(buf, buf.length); 
         
         
        System.out.println(" SERVER...");
      
        serversocket.receive(dp);
         CLIENTPORT=dp.getPort();
         IpAddress = dp.getAddress();
        String aa = new String(dp.getData(), 0,dp.getLength());
        
        System.out.println("CLIENT: " + aa);
        
        while(true){
            serversocket.setSoTimeout(100000);
        String str = "ENTER HOST NAME OR HOST ADDRESS";
        serversocket.send(new DatagramPacket(str.getBytes(),str.length(), IpAddress, CLIENTPORT));        
        serversocket.receive(dp);
        String host = new String(dp.getData(), 0,dp.getLength());
                if(host.equalsIgnoreCase("exit"))
            return;
        System.out.println("CLIENT: " + host); 
                 try{
           InetAddress IA = InetAddress.getByName(host);
       } catch(Exception e){
          System.exit(0);
       }
        
        
        str = "ENTER PORT";
        serversocket.send(new DatagramPacket(str.getBytes(),str.length(), IpAddress, CLIENTPORT));
        try{
            serversocket.receive(dp);
        String str2 = new String(dp.getData(), 0,dp.getLength());
        if(str2.equalsIgnoreCase("exit"))
            return;
        System.out.println("CLIENT: " + str2); 
        
            port = Integer.parseInt(str2);
        if(port>=65535){
            System.exit(0);
        }
       } catch(Exception e){
            System.exit(0);
        } 
        
        str = "ENTER  THE COMMAND";
        serversocket.send(new DatagramPacket(str.getBytes(),str.length(), IpAddress, CLIENTPORT));
        serversocket.receive(dp);
        String command1 = new String(dp.getData(), 0,dp.getLength());
        if(command1.equalsIgnoreCase("exit"))
            return;
        try{
            String[] arrOfStr = command1.split("~", 2);
           command = arrOfStr[0];
            directory = arrOfStr[1];
            
        System.out.println("CLIENT: " + command); 
        }catch(Exception e){
            System.out.println("Failed to send data to Server. Terminating.");
            System.exit(0);
        }
        
        
        if(command.equalsIgnoreCase("put")){
        server_java_udp.putfile( host, port);
        }else if(command.equalsIgnoreCase("get")){
         server_java_udp.getfile(directory, host, port);       
        }else if(command.equalsIgnoreCase("ls")){
              try{
                                       // File dir = new File(System.getProperty(directory));
                                       File dir = new File(directory);
                           
                             String childs[] = dir.list();
                            StringBuilder resultStr = new StringBuilder();
                     for (int i = 0; i < childs.length; i++) {
                        if (i > 0) {
                           resultStr.append(" ");
                         }
                        resultStr.append(childs[i]);
                     }
                      String sendMessage = resultStr.toString();
                      String out1 = sendMessage;
             serversocket.send(new DatagramPacket(out1.getBytes(),out1.length(), IpAddress, CLIENTPORT));
             }catch(Exception e){
                 String out1 = "No such Directory";
                serversocket.send(new DatagramPacket(out1.getBytes(),out1.length(), IpAddress, CLIENTPORT));
                System.exit(0);
            }         
        }else if(command.equalsIgnoreCase("cd")){
             String cwd = null;
                                 String out1;
                                 try{
                                    File dir = new File(directory);
                            if(dir.isDirectory()==true) {
                                System.setProperty("user.dir", dir.getAbsolutePath());
                                out1 = "successfull directory change "+directory;                
                           
                        } else {
                                out1 = "No such Directory"; 
                                }
                   cwd = System.getProperty("user.dir");
                   out1 = out1+cwd;                
             serversocket.send(new DatagramPacket(out1.getBytes(),out1.length(), IpAddress, CLIENTPORT));
            }catch(Exception e){
               out1 = "No such Directory";
                serversocket.send(new DatagramPacket(out1.getBytes(),out1.length(), IpAddress, CLIENTPORT));
                System.exit(0);
            }
            
            }        
         }
        }
    }

