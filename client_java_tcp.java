/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.

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
import java.util.Scanner;
public class client_java_tcp
{
   public static int INITPORT=12222;  
  
    
  public static void main(String[] args) throws Exception
  {
      System.out.println("client running");
       Scanner input = new Scanner(System.in);
     Socket sockets = new Socket("csi516-fa18.arcc.albany.edu", 12222);
     OutputStream ostream = sockets.getOutputStream(); 
     PrintWriter pwrite = new PrintWriter(ostream, true);
 
     InputStream istream = sockets.getInputStream();
     BufferedReader dataincoming = new BufferedReader(new InputStreamReader(istream));
 
     String incoming, outgoing, host;
     int port = 0;
     String command = null, directory = null, file;
     
     while(true){
        outgoing = "clent connected"; 
        pwrite.println(outgoing);   
        pwrite.flush();                 
        if((incoming = dataincoming.readLine()) != null)
        {
            System.out.println(incoming); 
        }   
        
        outgoing = input.nextLine(); 
        host= outgoing;
        pwrite.println(host);      
        pwrite.flush();
         try{
           InetAddress IA = InetAddress.getByName(host);
       } catch(Exception e){
           System.out.println("Could not connect to server. Terminating. ");
           System.exit(0);
       }// flush the data
        if((incoming = dataincoming.readLine()) != null)
        {
            System.out.println(incoming);
        }  
        
        outgoing = input.nextLine();
        try{       
            port= Integer.parseInt(outgoing);
        pwrite.println(port);     
        pwrite.flush();
                if(port>=65535){
            System.out.println("Invalid port Number, Terminating");
            System.exit(0);
        }}
        catch(Exception e){
        System.out.println("Invalid port Number, Terminating");
        System.exit(0);
        }
// flush the data
        if((incoming = dataincoming.readLine()) != null) //receive from server
        {
            System.out.println(incoming);
        } 

        
        outgoing = input.nextLine();  // keyboard reading
        pwrite.println(outgoing);     
        pwrite.flush(); 
        try{
            String[] arrOfStr = outgoing.split("~");
            command = arrOfStr[0];
            directory = arrOfStr[1];
        }catch(Exception e){
            System.out.println("Failed to send data to Server. Terminating.");
            System.exit(0);
        }
                           
        
        
        if(command.equalsIgnoreCase("put")){
             client_java_tcp.putfile(directory,host, port);
        }else if(command.equalsIgnoreCase("get")){
             client_java_tcp.getfile(directory, host, port);       
        }else if(command.equalsIgnoreCase("ls")){
           if((incoming = dataincoming.readLine()) != null)
            {
                System.out.println(incoming); 
                if(incoming.equalsIgnoreCase("No such Directory"))
                             {
                                 System.exit(0);
                             }
            }  
        }else if(command.equalsIgnoreCase("cd")){
           if((incoming = dataincoming.readLine()) != null)
            {
                System.out.println(incoming); 
            } 
        }else{
            if((incoming = dataincoming.readLine()) != null) 
            {
                System.out.println(incoming); 
            }
            System.exit(0);
        }
       
       
        
     }//while loop end
        
  
    }   
  
  public static void getfile(String directory, String host, int port) throws UnknownHostException, IOException{
       //Initialize socket
       InetAddress IA = null ;
       Socket socket = null ;
       try{
           IA = InetAddress.getByName(host);
       } catch(Exception e){
           System.out.println("Invalid host");              //csi516-fa18.arcc.albany.edu
           System.exit(0);
       }
       try{
           socket = new Socket(IA, port);
       }catch(Exception e){
           System.out.println("Invalid port");
           System.exit(0);
       }
        
      byte[] contents = new byte[10000];
        FileOutputStream fos = new FileOutputStream("tcp_get.txt");
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        InputStream is = socket.getInputStream();
        
        int bytesRead = 0;  
        try{ 
        while((bytesRead=is.read(contents))!=-1)
            bos.write(contents, 0, bytesRead); 
        
        bos.flush(); 
        socket.close(); 
       }catch(Exception e){
          System.out.println("Invalid Directory!");
          return;
       }
        
        System.out.println("File saved successfully!");
  }
  
  
     public static void putfile(String dir,String host, int port) throws IOException{
       Socket socket = null ;
       InetAddress  IA = null;
        byte[] contents;
       long ccc = 0;
       FileInputStream fis = null;
       try{
         IA = InetAddress.getByName(host);
       } catch(Exception e){
           System.out.println("Invalid host");
           System.exit(0);
       }
       try{
             socket = new Socket(IA, port);     //put~c:/users/zeesh/Desktop/aa.txt
         }
         catch(Exception e){
           System.out.println("Invalid port");
           System.exit(0);
       }
       
            File file = new File(dir);
        try{
            fis = new FileInputStream(file);
        }
        catch(Exception e){
           System.out.println("Invalid directory");
           System.exit(0);
       }
        BufferedInputStream inputstream = new BufferedInputStream(fis); 
        OutputStream os = socket.getOutputStream();

       
        long total_length_file = file.length(); 
        
        while(ccc!=total_length_file){ 
            int size = 10000;
            if(total_length_file - ccc >= size)
                ccc += size;    
            else{ 
                size = (int)(total_length_file - ccc); 
                ccc = total_length_file;
            } 
            contents = new byte[size]; 
            inputstream.read(contents, 0, size); 
            os.write(contents);
        }   
        
        os.flush(); 
        socket.close();
        System.out.println("File sent succesfully!");
       
   }
}