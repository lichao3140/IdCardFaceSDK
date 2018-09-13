#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <jni.h>
#include <com_runvision_idcardfacesdk_RunvisionUtil.h>
#include <android/log.h>

#define LOG_TAG "lichao"
#define LOGD(FORMAT,...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, FORMAT, ##__VA_ARGS__);
#define LOGE(FORMAT,...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, FORMAT, ##__VA_ARGS__);
#define LOGI(FORMAT,...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, FORMAT, ##__VA_ARGS__);
#define LOGW(FORMAT,...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, FORMAT, ##__VA_ARGS__);

//°ÑjavaµÄ×Ö·û´®×ª»»³ÉcµÄ×Ö·û´®
char* Jstring2CStr(JNIEnv* env, jstring jstr) {
	char* rtn = NULL;
	jclass clsstring = (*env)->FindClass(env, "java/lang/String");
	jstring strencode = (*env)->NewStringUTF(env, "GB2312");
	jmethodID mid = (*env)->GetMethodID(env, clsstring, "getBytes",
			"(Ljava/lang/String;)[B");
	jbyteArray barr = (jbyteArray) (*env)->CallObjectMethod(env, jstr, mid,
			strencode); // String .getByte("GB2312");
	jsize alen = (*env)->GetArrayLength(env, barr);
	jbyte* ba = (*env)->GetByteArrayElements(env, barr, JNI_FALSE);
	if (alen > 0) {
		rtn = (char*) malloc(alen + 1); //"\0"
		memcpy(rtn, ba, alen);
		rtn[alen] = 0;
	}
	(*env)->ReleaseByteArrayElements(env, barr, ba, 0); //
	return rtn;
}

JNIEXPORT jboolean JNICALL Java_com_runvision_idcardfacesdk_RunvisionUtil_AuthorizeSN(
		JNIEnv *env, jobject obj, jstring jstr) {
	// »ñÈ¡ÊäÈëµÄ×Ö·û´®
	char* cstr = Jstring2CStr(env, jstr);
	char* snArray[2] = {"ML5RR7C92H", "R50A20180807002"};

	LOGI("SN= %s", cstr);
	int i;
	for (i = 0; i < 2; i++) {
		if (strcmp(snArray[i], cstr) == 0) {
			return 1;
		} else {
			return 0;
		}
	}
	return 0;
}

JNIEXPORT jstring JNICALL Java_com_runvision_idcardfacesdk_RunvisionUtil_getAPPID(
		JNIEnv * env, jobject obj) {
	//CÓïÑÔµÄ×Ö·û´®
	char* cstr = "J3Yscp63XC1M1ut6Fk6DguUPSEtRti99pkAagPdxd88j";
	//°ÑCÓïÑÔ×Ö·û´®×ª»»ÎªJavaµÄ×Ö·û´®
	jstring jstr = (*env)->NewStringUTF(env, cstr);
	return jstr;
}

JNIEXPORT jstring JNICALL Java_com_runvision_idcardfacesdk_RunvisionUtil_getSDKKEY(
		JNIEnv * env, jobject obj) {
	//CÓïÑÔµÄ×Ö·û´®
	char* cstr = "B6ZbvRk5qchXtssHMSE6dmUxDDnLtoiHaB1CTXnhdcip";
	//°ÑCÓïÑÔ×Ö·û´®×ª»»ÎªJavaµÄ×Ö·û´®
	jstring jstr = (*env)->NewStringUTF(env, cstr);
	return jstr;
}

