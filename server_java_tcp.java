
/**
 *
 * @author zeesh
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
public class server_java_tcp
{
 public static String DIRECT;
   public static int INITPORT=12222;  
    
     public static void getfile(String dir, String host, int port) throws IOException{
       ServerSocket servingsocket = null;
       FileInputStream fis = null;
        try{
           InetAddress IA = InetAddress.getByName(host);
       } catch(Exception e){
           System.out.println("Invalid host");
           System.exit(0);
       }
       try{
           servingsocket = new ServerSocket(port);
       }catch(Exception e){
           System.out.println("Invalid port");
           System.exit(0);
       }
        Socket socket = servingsocket.accept();      

        File file = new File(dir);
        try{
            fis = new FileInputStream(file);
        }
        catch(Exception e){
           System.out.println("Invalid directory");
           System.exit(0);
       }
        BufferedInputStream bis = new BufferedInputStream(fis); 
          
        //Get socket's output stream
        OutputStream os = socket.getOutputStream();
                
        //Read File Contents into contents array 
        byte[] contents;
        long fileLength = file.length(); 
        long current = 0;
         
        long start = System.nanoTime();
        while(current!=fileLength){ 
            int size = 10000;
            if(fileLength - current >= size)
                current += size;    
            else{ 
                size = (int)(fileLength - current); 
                current = fileLength;
            } 
            contents = new byte[size]; 
            bis.read(contents, 0, size); 
            os.write(contents);
            System.out.print("Sending file ... "+(current*100)/fileLength+"% complete!");
        }   
        
        os.flush(); 
        //File transfer done. Close the socket connection!
        socket.close();
        servingsocket.close();
        System.out.println("File sent succesfully!");
       
   }
     
     
  public static void putfile(String directory, String host, int port) throws UnknownHostException, IOException{
       ServerSocket servingsocket = null;
       FileInputStream fis = null;
        try{
           InetAddress IA = InetAddress.getByName(host);
       } catch(Exception e){
           System.out.println("Invalid host");
           System.exit(0);
       }
       try{
           servingsocket = new ServerSocket(port);
       }catch(Exception e){
           System.out.println("Invalid port");
           System.exit(0);
       }
        Socket socket = servingsocket.accept();                  
      byte[] contents = new byte[10000];
        //Initialize the FileOutputStream to the output file's full path.
        FileOutputStream fos = new FileOutputStream("tcp_put.txt");
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        InputStream is = socket.getInputStream(); 
        //No of bytes read in one read() call
        int incomingdata = 0; 
       try{ 
        while((incomingdata=is.read(contents))!=-1)
            bos.write(contents, 0, incomingdata); 
        
        bos.flush(); 
        socket.close(); 
       }catch(Exception e){
          System.out.println("Invalid directory");
          return;
       }
        System.out.println("File saved successfully!");
  }
    
  public static void main(String[] args) throws Exception
  {
//      String initport = args[0];
//       INITPORT = Integer.parseInt(initport);
       System.out.println("server running");
       Scanner input = new Scanner(System.in);
      ServerSocket seerver_socket = new ServerSocket(INITPORT);
      
      Socket sock = seerver_socket.accept( );
      OutputStream ostream = sock.getOutputStream(); 
      PrintWriter pwrite = new PrintWriter(ostream, true);
 
                              // receiving from server ( receiveRead  object)
      InputStream istream = sock.getInputStream();
      BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));
 
      String incomingmessage, outgoingmessage; 
      int port = 0;
      String command = null, directory = null, file = null, host=null; 
      
      while(true){
        if((incomingmessage = receiveRead.readLine()) != null)  
        {
           System.out.println(incomingmessage);         
        }         
        outgoingmessage = "enter Host Name  "; 
        pwrite.println(outgoingmessage);             
        pwrite.flush();   
        
        if((incomingmessage = receiveRead.readLine()) != null)  
        {
            host = incomingmessage;
            try{
           InetAddress IA = InetAddress.getByName(host);
       } catch(Exception e){
          System.exit(0);
       }
           System.out.println(host);         
        }         
        outgoingmessage = "enter Port  "; 
        pwrite.println(outgoingmessage);             
        pwrite.flush();  
        
        try{ 
            if((incomingmessage = receiveRead.readLine()) != null)  
        {
            port = Integer.parseInt(incomingmessage);
            if(port>=65535){
            System.exit(0);
            }
           System.out.println(port);         
        } 
        } catch(Exception e){
            System.exit(0);
        }       
        outgoingmessage = "enter the command"; 
        pwrite.println(outgoingmessage);             
        pwrite.flush();
        
        if((incomingmessage = receiveRead.readLine()) != null)  
        {      
 		DIRECT = incomingmessage;     
           try{
               String[] arrOfStr = incomingmessage.split("~", 2);
            command = arrOfStr[0];
            directory = arrOfStr[1];
             }catch(Exception e){
             outgoingmessage = "Failed to send data to Server. Terminating."; 
        pwrite.println(outgoingmessage);             
        pwrite.flush();
            System.exit(0);
        }
           System.out.println(incomingmessage); 
           
        }         
                 
          
        //tcpserver.getfile(directory);
          if(command.equalsIgnoreCase("put")){
        server_java_tcp.putfile(directory, host, port);
        }else if(command.equalsIgnoreCase("get")){
         server_java_tcp.getfile(directory, host, port);       
        }else if(command.equalsIgnoreCase("cd")){
            String cwd = null;
             String out1;
             try{
                                    File dir = new File(directory);
                            if(dir.isDirectory()==true) {
                                System.setProperty("user.dir", dir.getAbsolutePath());
                                out1 = "successfull directory change ";                
                           
                        } else {
                                out1 = "No such Directory"; 
                                }
                   cwd = System.getProperty("user.dir");
                    outgoingmessage = out1+cwd;
                            pwrite.println(outgoingmessage);             
                            pwrite.flush();
                    
               }catch(Exception e){
           out1 = "No such Directory";
            cwd = System.getProperty("user.dir");
                  outgoingmessage = out1+cwd;
                            pwrite.println(outgoingmessage);             
                            pwrite.flush();
           System.exit(0);
       }
        }else if(command.equalsIgnoreCase("ls")){   
            try{
                                       File dir = new File(directory);
                           
                             String childs[] = dir.list();
                            StringBuilder resultStr = new StringBuilder();
                     for (int i = 0; i < childs.length; i++) {
                        if (i > 0) {
                           resultStr.append(" ");
                         }
                        resultStr.append(childs[i]);
                     }
                      outgoingmessage = resultStr.toString();
                        pwrite.println(outgoingmessage);             
                        pwrite.flush();
                       //System.exit(0);
                }
                catch(Exception e){
                    String out1 = "No such Directory"; 
                    outgoingmessage = out1;
                    pwrite.println(outgoingmessage);             
                    pwrite.flush();
                   System.out.println("Invalid directory");
                   System.exit(0);
                                }        
             }
      
      
      
      
      }
      
      
      
      
      
      
      
      
      
      
  }
}