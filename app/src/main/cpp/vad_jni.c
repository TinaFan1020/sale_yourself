#include "webrtc/common_audio/vad/include/webrtc_vad.h"
#include "webrtc/common_audio/signal_processing/include/signal_processing_library.h"
#include "../../../../../../AppData/Local/Android/Sdk/ndk/21.4.7075529/toolchains/llvm/prebuilt/windows-x86_64/sysroot/usr/include/jni.h"
#include "../../../../../../AppData/Local/Android/Sdk/ndk/21.4.7075529/toolchains/llvm/prebuilt/windows-x86_64/sysroot/usr/include/stdlib.h"
#include "../../../../../../AppData/Local/Android/Sdk/ndk/21.4.7075529/toolchains/llvm/prebuilt/windows-x86_64/sysroot/usr/include/stdio.h"
#include "../../../../../../AppData/Local/Android/Sdk/ndk/21.4.7075529/toolchains/llvm/prebuilt/windows-x86_64/sysroot/usr/include/string.h"


VadInst *internalHandle;
int sampleRate;
int frameSize;


JNIEXPORT jint JNICALL
Java_com_konovalov_vad_Vad_nativeStart(JNIEnv *env, jobject obj, jint jSampleRate, jint jFrameSize,
                                       jint jMode) {
    sampleRate = jSampleRate;
    frameSize = jFrameSize;

    internalHandle = WebRtcVad_Create();

    if (WebRtcVad_Init(internalHandle) < 0) return -1;
    if (WebRtcVad_set_mode(internalHandle, jMode) == -1) return -2;

    return 0;
}

JNIEXPORT void JNICALL Java_com_konovalov_vad_Vad_nativeStop(JNIEnv *env, jobject object) {
    WebRtcVad_Free(internalHandle);
    internalHandle = NULL;
}

JNIEXPORT jboolean JNICALL
Java_com_konovalov_vad_Vad_nativeIsSpeech(JNIEnv *env, jobject object, jshortArray bytes) {
    jshort *arrayElements = (*env)->GetShortArrayElements(env, bytes, 0);
    int resultVad = WebRtcVad_Process(internalHandle, sampleRate, arrayElements, (size_t) frameSize);
    (*env)->ReleaseShortArrayElements(env, bytes, arrayElements, 0);

    if (resultVad > 0) {
        return JNI_TRUE;
    } else {
        return JNI_FALSE;
    }
}