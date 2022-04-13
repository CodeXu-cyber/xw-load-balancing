package system.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @ClassName por
 * @Author xuwei
 * @DATE 2022/4/12
 */
public class SocketThread extends Thread {

    private static final int BUFFER_SIZE = 8092;

    /**
     * 五分钟超时
     */
    public static final int SO_TIME_OUT = 300000;

    public static void log(Object message, Object... args) {
        Date dat = new Date();
        String msg = String.format("%1$tF %1$tT %2$-5s %3$s%n", dat, Thread.currentThread().getId(), String.format(message.toString(), args));
        System.out.print(msg);
    }


    private final Socket localSocket;
    private Socket remoteSocket;
    private final String remoteHost;
    private final Integer remotePort;
    private InputStream lin;
    private InputStream rin;
    private OutputStream lout;
    private OutputStream rout;

    public SocketThread(Socket socket, String remoteHost, Integer remotePort) {
        this.localSocket = socket;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    @Override
    public void run() {
        try {
            remoteSocket = new Socket();
            remoteSocket.connect(new InetSocketAddress(remoteHost, remotePort));
            //设置超时，超过时间未收到客户端请求，关闭资源
            //5分钟内无数据传输、关闭链接
            remoteSocket.setSoTimeout(SO_TIME_OUT);
            rin = remoteSocket.getInputStream();
            rout = remoteSocket.getOutputStream();
            lin = localSocket.getInputStream();
            lout = localSocket.getOutputStream();

            new ReadThread().start();

            //写数据,负责读取客户端发送过来的数据，转发给远程
            byte[] data = new byte[BUFFER_SIZE];
            int len = 0;
            while ((len = lin.read(data)) > 0) {
                /*
                  读到了缓存大小一致的数据，不需要拷贝，直接使用
                  读到了比缓存大小的数据，需要拷贝到新数组然后再使用
                 */
                if (len == BUFFER_SIZE) {
                    rout.write(data);
                } else {
                    byte[] dest = new byte[len];
                    System.arraycopy(data, 0, dest, 0, len);
                    rout.write(dest);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    /**
     * 关闭资源
     */
    private void close() {
        try {
            if (remoteSocket != null && !remoteSocket.isClosed()) {
                remoteSocket.close();
                log("remoteSocket>>>>" + remoteSocket.getRemoteSocketAddress() + " socket closed ");
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            if (localSocket != null && !localSocket.isClosed()) {
                localSocket.close();
                log("localSocket>>>>" + localSocket.getRemoteSocketAddress() + " socket closed ");
            }
        } catch (IOException e1) {
        }

    }

    /**
     * 读数据线程负责读取远程数据后回写到客户端
     */
    class ReadThread extends Thread {
        @Override
        public void run() {
            try {
                byte[] data = new byte[BUFFER_SIZE];
                int len = 0;
                /*
                读到了缓存大小一致的数据，不需要拷贝，直接使用
                读到了比缓存大小的数据，需要拷贝到新数组然后再使用
                 */
                while ((len = rin.read(data)) > 0) {
                    if (len == BUFFER_SIZE) {
                        lout.write(data);
                    } else {
                        byte[] dest = new byte[len];
                        System.arraycopy(data, 0, dest, 0, len);
                        lout.write(dest);
                    }
                }
            } catch (IOException e) {
            } finally {
                close();
            }
        }

    }


}


