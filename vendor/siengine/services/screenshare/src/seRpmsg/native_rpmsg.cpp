#include <jni.h>

#include <string>

#include "seRpmsg.h"

extern "C" JNIEXPORT jboolean JNICALL
Java_siengine_com_screenshare_rpmsg_ScreenShareService_nativeEnableScreenShare(
        JNIEnv* env,
        jobject /* thiz */,
        jstring package_name,
        jstring activity_path,
        jint display_id) {
    const char* pkg = env->GetStringUTFChars(package_name, nullptr);
    const char* act = env->GetStringUTFChars(activity_path, nullptr);

    std::string cmd = "START|" + std::string(pkg) + "|" + std::string(act) + "|" + std::to_string(display_id);
    bool ok = rpmsg_startClient() && rpmsg_block_send(cmd.c_str());

    env->ReleaseStringUTFChars(package_name, pkg);
    env->ReleaseStringUTFChars(activity_path, act);
    return ok ? JNI_TRUE : JNI_FALSE;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_siengine_com_screenshare_rpmsg_ScreenShareService_nativeDisableScreenShare(
        JNIEnv* /* env */,
        jobject /* thiz */) {
    bool ok = rpmsg_block_send("STOP");
    rpmsg_stopClient();
    return ok ? JNI_TRUE : JNI_FALSE;
}
