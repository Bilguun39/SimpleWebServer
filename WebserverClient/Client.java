/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package day01client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BilguunOyunchimeg
 */
public class Client {
    
    private String host = "127.0.0.1";
    private int port = 8181;
    boolean autoFlush = true;
    String defaultPage = "index.html";
    String url = "/welcome.html";
    String url1 = "/contact.html";
    
    public void start() {
        response(request());
    }
    
    public Socket request() {
        Socket socket = null;
        
        String[] randomList = {"/welcome.html", "/contact.html", "/welcome.html", "/contact.html"};
        String[] randomReqList = {"GET", "PUT", "GET", "PUT"};
        
        try {
            socket = new Socket(host, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), autoFlush);
            out.println(randomString(randomReqList)+" "+randomString(randomList)+" HTTP/1.1");
            out.println("HOST: "+host+":"+port);
            out.println("Connection: Close");
            out.println("");
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return socket;
    }
    
    public void response(Socket socket) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuffer sb = new StringBuffer(8096);
            boolean loop= true;
            while (loop) {
                if (in.ready()) {
                    int i= 0;
                    while (i!=-1) {
                        i = in.read();
                        sb.append((char)i);
                    }
                    loop= false;
                }
                System.out.println(sb);
                Thread.sleep(5000);
            }
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String randomString(String[] randomArr){
        Random random = new Random();
        
        int select = random.nextInt(randomArr.length);
        
        return randomArr[select];
    }
}
