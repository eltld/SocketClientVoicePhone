package aidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by xiajun on 2017/7/2.
 */

public class DDService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("DDService onCreate........" + "Thread: " + Thread.currentThread().getName());
    }
    @Override
    public IBinder onBind(Intent arg0) {
        System.out.println("DDService onBind");
        return mBinder;
    }

    private final IRemoteService.Stub mBinder = new IRemoteService.Stub() {
        public int getPid(){
            System.out.println("Thread: " + Thread.currentThread().getName());
            System.out.println("DDService getPid ");
            int pid = android.os.Process.myPid();
            return pid;
        }
        public void basicTypes(int anInt, long aLong, boolean aBoolean,
                               float aFloat, double aDouble, String aString) {
            System.out.println("Thread: " + Thread.currentThread().getName());
            System.out.println("basicTypes aDouble: " + aDouble +" anInt: " + anInt+" aBoolean " + aBoolean+" aString " + aString);
        }
    };

}

