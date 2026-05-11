#include <jni.h>
#include <android/log.h>

#define LOG_TAG "nativeRpmsg"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_screenshare_MainActivity_00024NativeRpmsgBridge_nativeDeinit(JNIEnv* env, jclass clazz) {
    (void)env;
        LOGI("nativeDeinit() called");
    if (clazz == nullptr) {
        LOGE("nativeDeinit() failed: clazz is null");
        return JNI_FALSE;
    }
    // TODO: add rpmsg deinit implementation.
    return JNI_TRUE;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_screenshare_MainActivity_00024NativeRpmsgBridge_nativeInit(JNIEnv* env, jclass clazz) {
    (void)env;
        LOGI("nativeInit() called");
    if (clazz == nullptr) {
        LOGE("nativeInit() failed: clazz is null");
        return JNI_FALSE;
    }
    // TODO: add rpmsg init implementation.
    return JNI_TRUE;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_screenshare_MainActivity_00024NativeRpmsgBridge_nativeStart(JNIEnv* env, jclass clazz) {
    (void)env;
        LOGI("nativeStart() called");
    if (clazz == nullptr) {
        LOGE("nativeStart() failed: clazz is null");
        return JNI_FALSE;
    }
    // TODO: add rpmsg start implementation.
    return JNI_TRUE;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_screenshare_MainActivity_00024NativeRpmsgBridge_nativeStop(JNIEnv* env, jclass clazz) {
    (void)env;
        LOGI("nativeStop() called");
    if (clazz == nullptr) {
        LOGE("nativeStop() failed: clazz is null");
        return JNI_FALSE;
    }
    // TODO: add rpmsg stop implementation.
    return JNI_TRUE;
}
