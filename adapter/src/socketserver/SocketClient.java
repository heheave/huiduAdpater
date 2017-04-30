package socketserver;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by xiaoke on 17-4-30.
 */
public class SocketClient {
    public static void main(String[] args) {
        Socket socket = null;
        try {
            socket = new Socket();
            System.out.println("DONE");
            socket.connect(new InetSocketAddress(9999));
            System.out.println("DONE");
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            String OUTSSTR = "{adsfadsfadsfdsaf}";
            out.write(OUTSSTR.getBytes());
            out.flush();
            DataInputStream in = new DataInputStream(socket.getInputStream());
            byte[] inBytes = new byte[1024];
            int readBytes = in.read(inBytes);
            String s = new String(inBytes, 0, readBytes);
            System.out.println(s);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
