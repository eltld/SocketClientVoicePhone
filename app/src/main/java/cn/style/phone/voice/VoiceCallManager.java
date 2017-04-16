package cn.style.phone.voice;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.LogRecord;

/**
 * Created by xiajun on 2017/4/14.
 */

public class VoiceCallManager {
    protected String TAG = getClass().getSimpleName();
    private static int PORT_LISTEN = 8743;//本机监听返回端口

    private static String HOST_TARGET = "192.168.0.102";//服务器地址
    private static int PORT_TARGET = 3478;//服务器端口
    public static final int MESSAGE_LOCALHOST_INFO = 1;
    private static VoiceCallManager instance;
    private Handler handler;

    public static VoiceCallManager getInstance() {
        if (instance == null)
            instance = new VoiceCallManager();
        return instance;
    }

    public void init(Handler handler) {
        this.handler  = handler;
    }

    public void getHostInfo() {
        startListen();//监听返回端口信息
        sendPortRequest();
    }

    private void sendPortRequest() {
        ThreadPoolUtil.execute(new Runnable() {
            @Override
            public void run() {
                DatagramSocket sender = null;
                try {
                    sender = new DatagramSocket();
                    InetAddress serverAddress = InetAddress.getByName(HOST_TARGET);
                    String str = "请求获取连接主机IP端口信息";
                    byte[] data = str.getBytes();
                    // 创建一个DatagramPacket对象，并指定要讲这个数据包发送到网络当中的哪个地址，以及端口号
                    DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, PORT_TARGET);
                    // 调用socket对象的send方法，发送数据
                    sender.send(packet);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (sender != null)
                        sender.close();
                    Log.e(TAG, "发送完毕，关闭sender");

                }
            }
        });
    }

    private void startListen() {
        ThreadPoolUtil.execute(new Runnable() {
            @Override
            public void run() {
                DatagramSocket socket = null;
                try {
                    socket = new DatagramSocket(PORT_LISTEN);
                    while (true) {
                        byte data[] = new byte[1024];
                        DatagramPacket packet = new DatagramPacket(data, data.length);
                        socket.receive(packet);//阻塞
                        byte[] buffer = packet.getData();
                        String result = new String(buffer, packet.getOffset(), packet.getLength());
                        String hostAddress = packet.getAddress().getHostAddress();
                        int port = packet.getPort();
                        Log.e(TAG, hostAddress + ":" + port + "--->" + result);
                        String[] s = result.split(":");
                        Message msg = handler.obtainMessage(MESSAGE_LOCALHOST_INFO);
                        Bundle b = new Bundle();
                        b.putStringArray("hostInfo", s);
                        msg.setData(b);
                        handler.sendMessage(msg);
                        //停止监听
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (socket != null)
                        socket.close();
                    Log.e(TAG, "发送完毕，关闭监听");
                }
            }
        });
    }
}
