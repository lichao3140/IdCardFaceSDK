LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := ArcSoft_FDEngine
LOCAL_SRC_FILES := libArcSoft_FDEngine.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := ArcSoft_FREngine
LOCAL_SRC_FILES := libArcSoft_FREngine.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := ArcSoft_FTEngine
LOCAL_SRC_FILES := libArcSoft_FTEngine.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := mpbase
LOCAL_SRC_FILES := libmpbase.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := runvision
LOCAL_SRC_FILES := runvision.c
LOCAL_LDLIBS := -llog -landroid
LOCAL_SHARED_LIBRARIES := runvision
include $(BUILD_SHARED_LIBRARY)
