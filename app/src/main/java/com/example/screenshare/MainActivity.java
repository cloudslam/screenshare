package com.example.screenshare;

import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private EditText etPackage;
    private EditText etActivity;
    private EditText etDisplayId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etPackage = findViewById(R.id.etPackage);
        etActivity = findViewById(R.id.etActivity);
        etDisplayId = findViewById(R.id.etDisplayId);

        if (!RpmsgBinderBridge.init()) {
            toast(getString(R.string.msg_rpmsg_init_failed));
        }

        Button btnStart = findViewById(R.id.btnStart);
        Button btnStop = findViewById(R.id.btnStop);

        btnStart.setOnClickListener(v -> startProjection());
        btnStop.setOnClickListener(v -> pullBackToMainDisplay());
    }

    @Override
    protected void onDestroy() {
        RpmsgBinderBridge.deinit();
        super.onDestroy();
    }

    private void startProjection() {
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

        if (!RpmsgBinderBridge.start()) {
            toast(getString(R.string.msg_rpmsg_start_failed));
            return;
        }

        String className = normalizeActivityClassName(pkg, activityPath);

        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setComponent(new ComponentName(pkg, className));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            ActivityOptions options = ActivityOptions.makeBasic();
            options.setLaunchDisplayId(displayId);

            startActivity(intent, options.toBundle());
            toast(getString(R.string.msg_start_success, displayId));
        } catch (Exception e) {
            toast(getString(R.string.msg_start_failed, e.getMessage()));
        }
    }

    private void pullBackToMainDisplay() {
        String pkg = safeText(etPackage);
        String activityPath = safeText(etActivity);

        if (TextUtils.isEmpty(pkg) || TextUtils.isEmpty(activityPath)) {
            toast(getString(R.string.msg_fill_pkg_activity));
            return;
        }

        if (!RpmsgBinderBridge.stop()) {
            toast(getString(R.string.msg_rpmsg_stop_failed));
            return;
        }

        String className = normalizeActivityClassName(pkg, activityPath);

        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setComponent(new ComponentName(pkg, className));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            ActivityOptions options = ActivityOptions.makeBasic();
            options.setLaunchDisplayId(0);

            startActivity(intent, options.toBundle());
            toast(getString(R.string.msg_stop_success));
        } catch (Exception e) {
            toast(getString(R.string.msg_stop_failed, e.getMessage()));
        }
    }

    private String normalizeActivityClassName(String packageName, String activityPath) {
        if (activityPath.startsWith(".")) {
            return packageName + activityPath;
        }
        return activityPath;
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

    private static final class RpmsgBinderBridge {
        private static final String SERVICE_MANAGER_CLASS = "android.os.ServiceManager";
        private static final String SERVICE_NAME = "rpmsg_service";
        private static final String INTERFACE_TOKEN = "android.rpmsg.IRpmsgService";

        private static final int TRANSACTION_INIT = 1;
        private static final int TRANSACTION_DEINIT = 2;
        private static final int TRANSACTION_START = 3;
        private static final int TRANSACTION_STOP = 4;

        private RpmsgBinderBridge() {
        }

        static boolean init() {
            return transact(TRANSACTION_INIT, "init");
        }

        static boolean deinit() {
            return transact(TRANSACTION_DEINIT, "deinit");
        }

        static boolean start() {
            return transact(TRANSACTION_START, "start");
        }

        static boolean stop() {
            return transact(TRANSACTION_STOP, "stop");
        }

        private static boolean transact(int code, String methodName) {
            IBinder binder = getRpmsgService();
            if (binder == null) {
                Log.e(TAG, "rpmsg binder service is null, " + methodName + "() skipped");
                return false;
            }

            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                data.writeInterfaceToken(INTERFACE_TOKEN);
                boolean status = binder.transact(code, data, reply, 0);
                if (!status) {
                    Log.e(TAG, "rpmsg binder transact failed for " + methodName + "(), code=" + code);
                    return false;
                }
                reply.readException();
                return true;
            } catch (Exception e) {
                Log.e(TAG, "rpmsg binder " + methodName + "() call failed", e);
                return false;
            } finally {
                reply.recycle();
                data.recycle();
            }
        }

        private static IBinder getRpmsgService() {
            try {
                Class<?> clazz = Class.forName(SERVICE_MANAGER_CLASS);
                Method getService = clazz.getMethod("getService", String.class);
                Object service = getService.invoke(null, SERVICE_NAME);
                if (service instanceof IBinder) {
                    return (IBinder) service;
                }
                return null;
            } catch (Exception e) {
                Log.e(TAG, "get rpmsg binder service failed", e);
                return null;
            }
        }
    }
}
