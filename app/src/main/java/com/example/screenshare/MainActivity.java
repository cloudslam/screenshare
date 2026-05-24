package com.example.screenshare;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import siengine.com.screenshare.rpmsg.IScreenShareService;

public class MainActivity extends AppCompatActivity {

    private EditText etPackage;
    private EditText etActivity;
    private EditText etDisplayId;

    private IScreenShareService service;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = IScreenShareService.Stub.asInterface(binder);
            toast(getString(R.string.msg_service_connected));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service = null;
            toast(getString(R.string.msg_service_disconnected));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etPackage = findViewById(R.id.etPackage);
        etActivity = findViewById(R.id.etActivity);
        etDisplayId = findViewById(R.id.etDisplayId);

        Button btnStart = findViewById(R.id.btnStart);
        Button btnStop = findViewById(R.id.btnStop);

        btnStart.setOnClickListener(v -> startProjection());
        btnStop.setOnClickListener(v -> stopProjection());

        bindScreenShareService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    private void bindScreenShareService() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(
                "siengine.com.screenshare.rpmsg",
                "siengine.com.screenshare.rpmsg.ScreenShareService"));
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void startProjection() {
        if (service == null) {
            toast(getString(R.string.msg_service_not_ready));
            return;
        }

        String pkg = safeText(etPackage);
        String activityPath = safeText(etActivity);
        String displayIdInput = safeText(etDisplayId);

        if (TextUtils.isEmpty(pkg) || TextUtils.isEmpty(activityPath) || TextUtils.isEmpty(displayIdInput)) {
            toast(getString(R.string.msg_fill_all_fields));
            return;
        }

        Integer displayId = parseDisplayId(displayIdInput);
        if (displayId == null) {
            toast(getString(R.string.msg_display_id_invalid));
            return;
        }

        try {
            boolean ok = service.enableScreenShare(pkg, activityPath, displayId);
            toast(ok ? getString(R.string.msg_start_success, displayId) : getString(R.string.msg_start_failed, "rpmsg"));
        } catch (RemoteException e) {
            toast(getString(R.string.msg_start_failed, e.getMessage()));
        }
    }

    private void stopProjection() {
        if (service == null) {
            toast(getString(R.string.msg_service_not_ready));
            return;
        }
        try {
            boolean ok = service.disableScreenShare();
            toast(ok ? getString(R.string.msg_stop_success) : getString(R.string.msg_stop_failed, "rpmsg"));
        } catch (RemoteException e) {
            toast(getString(R.string.msg_stop_failed, e.getMessage()));
        }
    }

    private Integer parseDisplayId(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String safeText(EditText editText) {
        CharSequence text = editText.getText();
        return text == null ? "" : text.toString().trim();
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
