#include <jni.h>

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_screenshare_MainActivity_00024NativeRpmsgBridge_nativeInit(JNIEnv* env, jclass clazz) {
    (void)env;
    (void)clazz;
    // TODO: add rpmsg init implementation.
    return JNI_TRUE;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_screenshare_MainActivity_00024NativeRpmsgBridge_nativeStart(JNIEnv* env, jclass clazz) {
    (void)env;
    (void)clazz;
    // TODO: add rpmsg start implementation.
    return JNI_TRUE;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_screenshare_MainActivity_00024NativeRpmsgBridge_nativeStop(JNIEnv* env, jclass clazz) {
    (void)env;
    (void)clazz;
    // TODO: add rpmsg stop implementation.
    return JNI_TRUE;
}
