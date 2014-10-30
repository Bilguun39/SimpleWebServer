/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package day01server;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BilguunOyunchimeg
 */
public class Server {

    private static final String REQUEST_GET = "GET";
    private static final String REQUEST_PUT = "PUT";
    private static final String OUTPUT = "<html><head><title>Example</title></head><body><p>Worked!!!</p></body></html>";
    private static final String OUTPUT_HEADERS = "HTTP 200 OK /1.1\r\n"
            + "Content-Type: text/html\r\n"
            + "Content-Length: ";
    private static final String OUTPUT_END_OF_HEADERS = "\r\n\r\n";
    private static final String OUTPUT_NOT_ALLOWED = "HTTP 405 Method Not Allowed /1.1";
    private static final String OUTPUT_NOT_FOUND = "HTTP 404 NOT FOUND! /1.1";

    private int port = 8181;
    private int backlog = 5;
    String bindingAddress = "127.0.0.1";
    private String requestType = "";
    private boolean checkRequest = true;

    public void startup() {
        try {
            ServerSocket server = new ServerSocket(port, backlog, InetAddress.getByName(bindingAddress));
            System.out.println("Ready");
            System.out.println("Before Accept");
            Socket socket = server.accept();
            System.out.println("After accept");
            InputStream input = socket.getInputStream();

            int i = 0;

            StringBuffer sb = new StringBuffer();
            
            while (input.available() != 0) {
                i = input.read();
                sb.append((char) i);

                if (checkRequest) {
                    if (sb.toString().equals(REQUEST_GET) || sb.toString().equals(REQUEST_PUT)) {
                        checkRequest = false;
                        requestType = sb.toString();
                    }
                }
            }

            String[] requestParam = sb.toString().split(" ");
            String path = null;
            if(requestParam.length > 1){
                path = requestParam[1];
            }
            
            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(
                            new BufferedOutputStream(socket.getOutputStream()), "UTF-8")
            );

            System.out.println(sb);

            if (requestType.equals(REQUEST_PUT)) {
                out.write(OUTPUT_NOT_ALLOWED);
            } else if (requestType.equals(REQUEST_GET)) {
                if(isFileExist(path) && path != null){
                    String fromFile = readFile(path);
                    out.write(OUTPUT_HEADERS + OUTPUT.length() + OUTPUT_END_OF_HEADERS + fromFile);
                }else{
                    out.write(OUTPUT_NOT_FOUND);
                }
            } else if (requestType == null || requestType == "") {
                out.write(OUTPUT_NOT_FOUND);
            }

            out.flush();
            out.close();
            socket.close();

        } catch (UnknownHostException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean isFileExist(String filename){
        filename = "." + filename;
        File file = new File(filename);
        
        return file.exists();
    }

    public String readFile(String filename) {
        String content = null;
        filename = "." + filename;
        File file = new File(filename);
        try {
            FileReader reader = new FileReader(file);
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            content = new String(chars);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }
}
