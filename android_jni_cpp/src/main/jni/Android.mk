LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := people
LOCAL_SRC_FILES := people.cpp

include $(BUILD_SHARED_LIBRARY)
