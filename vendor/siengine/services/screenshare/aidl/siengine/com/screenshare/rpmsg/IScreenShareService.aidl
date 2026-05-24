package siengine.com.screenshare.rpmsg;

interface IScreenShareService {
    boolean enableScreenShare(in String packageName, in String activityPath, int displayId);
    boolean disableScreenShare();
    boolean isScreenShareActive();
}
