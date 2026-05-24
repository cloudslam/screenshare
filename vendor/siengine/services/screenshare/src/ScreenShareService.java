package siengine.com.screenshare.rpmsg;

import android.os.ServiceManager;
import android.util.Log;

public final class ScreenShareService extends IScreenShareService.Stub {

    private static final String TAG = "ScreenShareService";
    private static final String SERVICE_NAME = "siengine.screenshare.rpmsg";

    private volatile boolean active;

    static {
        System.loadLibrary("siengine_screenshare_native");
    }

    public static void main(String[] args) {
        Log.i(TAG, "Registering " + SERVICE_NAME);
        ServiceManager.addService(SERVICE_NAME, new ScreenShareService());
        Log.i(TAG, SERVICE_NAME + " started");

        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public synchronized boolean enableScreenShare(String packageName, String activityPath, int displayId) {
        boolean ok = nativeEnableScreenShare(packageName, activityPath, displayId);
        active = ok;
        return ok;
    }

    @Override
    public synchronized boolean disableScreenShare() {
        boolean ok = nativeDisableScreenShare();
        if (ok) {
            active = false;
        }
        return ok;
    }

    @Override
    public boolean isScreenShareActive() {
        return active;
    }

    private native boolean nativeEnableScreenShare(String packageName, String activityPath, int displayId);

    private native boolean nativeDisableScreenShare();
}
