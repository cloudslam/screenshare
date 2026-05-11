package com.example.screenshare;

import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    static {
        try {
            System.loadLibrary("nativeRpmsg");
            NativeRpmsgBridge.setLibraryLoaded(true);
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Failed to load nativeRpmsg", e);
            NativeRpmsgBridge.setLibraryLoaded(false);
        }
    }

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

        if (!NativeRpmsgBridge.init()) {
            toast(getString(R.string.msg_rpmsg_init_failed));
        }

        Button btnStart = findViewById(R.id.btnStart);
        Button btnStop = findViewById(R.id.btnStop);

        btnStart.setOnClickListener(v -> startProjection());
        btnStop.setOnClickListener(v -> pullBackToMainDisplay());
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

        if (!NativeRpmsgBridge.start()) {
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

        if (!NativeRpmsgBridge.stop()) {
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

    private static final class NativeRpmsgBridge {
        private static boolean libraryLoaded;

        private NativeRpmsgBridge() {
        }

        static void setLibraryLoaded(boolean loaded) {
            libraryLoaded = loaded;
        }

        static boolean init() {
            if (!libraryLoaded) {
                Log.e(TAG, "nativeRpmsg is not loaded, init() skipped");
                return false;
            }
            try {
                return nativeInit();
            } catch (UnsatisfiedLinkError e) {
                Log.e(TAG, "nativeRpmsg init() call failed", e);
                return false;
            }
        }

        static boolean start() {
            if (!libraryLoaded) {
                Log.e(TAG, "nativeRpmsg is not loaded, start() skipped");
                return false;
            }
            try {
                return nativeStart();
            } catch (UnsatisfiedLinkError e) {
                Log.e(TAG, "nativeRpmsg start() call failed", e);
                return false;
            }
        }

        static boolean stop() {
            if (!libraryLoaded) {
                Log.e(TAG, "nativeRpmsg is not loaded, stop() skipped");
                return false;
            }
            try {
                return nativeStop();
            } catch (UnsatisfiedLinkError e) {
                Log.e(TAG, "nativeRpmsg stop() call failed", e);
                return false;
            }
        }

        private static native boolean nativeInit();

        private static native boolean nativeStart();

        private static native boolean nativeStop();
    }
}
