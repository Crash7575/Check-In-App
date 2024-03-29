/*
Test program demonstrating how to use Server.java (Socket Server) and ServerSocketThread.java (Socket Client)
*/

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws Exception {
        Server s = new Server();
        Client c1 = new Client();
        Client c2 = new Client();
        Client c3 = new Client();
        System.out.println("a");
        c1.write(3);
        c2.write(44141);
        c1.read();
        c2.read();
        c3.read();
        c3.read();
        System.out.println(s.getAllIds());
        c2.write(-3);

        c3.read();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        System.out.println(s.getAllIds());
        c1.close();
        c2.close();
        c3.close();
        try {
            s.sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("c");
    }
}
