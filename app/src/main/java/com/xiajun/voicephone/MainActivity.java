package com.xiajun.voicephone;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.style.phone.voice.VoiceCallManager;
import cn.style.phone.voice.VoiceRecorder;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.tv_ip_info)
    TextView tvIpInfo;
    @Bind(R.id.et_ip)
    EditText etIp;
    @Bind(R.id.et_port)
    EditText etPort;
    @Bind(R.id.bt_connect)
    Button btConnect;

    private boolean isStart;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case VoiceCallManager.MESSAGE_LOCALHOST_INFO:
                    Bundle data = msg.getData();
                    String[] hostInfo = data.getStringArray("hostInfo");
                    tvIpInfo.setText(hostInfo[0] + ":" + hostInfo[1]);

                    String remoteHost = "192.168.0.102";//etIp.getText().toString();
                    String remotePort = "3568";//etPort.getText().toString();
                    if (!isStart) {
                        VoiceRecorder.getInstance().startCall(remoteHost, Integer.valueOf(remotePort), Integer.valueOf(hostInfo[1]));
                        isStart = true;
                    }
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect();
            }
        });
        VoiceCallManager.getInstance().init(handler);
    }

    private void connect() {
        VoiceCallManager.getInstance().getHostInfo();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
