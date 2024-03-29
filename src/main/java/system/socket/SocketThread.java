package system.socket;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 套接字线
 *
 * @ClassName SocketThread
 * @Author xuwei
 * @DATE 2022/4/12
 */
public class SocketThread extends Thread {

    /**
     * 五分钟超时
     */
    public static final int SO_TIME_OUT = 8000;
    private static final int BUFFER_SIZE = 8092;
    private static final Logger log = Logger.getLogger(SocketThread.class);


    private final Socket localSocket;
    private final String remoteHost;
    private final Integer remotePort;
    private Socket remoteSocket;
    private InputStream remoteSocketInputStream;
    private OutputStream localSocketOutputStream;

    public SocketThread(Socket socket, String remoteHost, Integer remotePort) {
        this.localSocket = socket;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    /**
     * 运行
     */
    @Override
    public void run() {
        try {
            remoteSocket = new Socket();
            remoteSocket.connect(new InetSocketAddress(remoteHost, remotePort));
            //设置超时，超过时间未收到客户端请求，关闭资源
            //5分钟内无数据传输、关闭链接
            remoteSocket.setSoTimeout(SO_TIME_OUT);
            remoteSocketInputStream = remoteSocket.getInputStream();
            OutputStream remoteSocketOutputStream = remoteSocket.getOutputStream();
            InputStream localSocketInputStream = localSocket.getInputStream();
            localSocketOutputStream = localSocket.getOutputStream();
            new ReadThread().start();
            //写数据,负责读取客户端发送过来的数据，转发给远程
            dataTransmission(localSocketInputStream, remoteSocketOutputStream);
        } catch (Exception ignored) {

        } finally {
            close();
        }
    }

    private void dataTransmission(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] data = new byte[BUFFER_SIZE];
        int len;
        while ((len = inputStream.read(data)) > 0) {
            /*
              读到了缓存大小一致的数据，不需要拷贝，直接使用
              读到了比缓存大小的数据，需要拷贝到新数组然后再使用
             */
            if (len == BUFFER_SIZE) {
                outputStream.write(data);
            } else {
                byte[] dest = new byte[len];
                System.arraycopy(data, 0, dest, 0, len);
                outputStream.write(dest);
            }
        }
    }

    /**
     * 关闭资源
     */
    private void close() {
        try {
            if (localSocket != null && !localSocket.isClosed()) {
                localSocket.close();
                //log.info("localSocket ---> " + localSocket.getRemoteSocketAddress().toString().replace("/", "") + " socket closed");
            }
            if (remoteSocket != null && !remoteSocket.isClosed()) {
                remoteSocket.close();
                //log.info("remoteSocket ---> " + remoteSocket.getRemoteSocketAddress().toString().replace("/", "") + " socket closed");
            }
        } catch (IOException e1) {
            log.warn(e1);
        }
    }

    /**
     * 读数据线程负责读取远程数据后回写到客户端
     */
    class ReadThread extends Thread {
        @Override
        public void run() {
            try {
                dataTransmission(remoteSocketInputStream, localSocketOutputStream);
            } catch (IOException ignored) {

            } finally {
                close();
            }
        }
    }


}


