#include "People.h"
#include <android/log.h>
#include "jni.h"
JNIEXPORT jint JNICALL Java_kcc_sorg_android_1jni_1cpp_People_printPeople(
		JNIEnv *env, jobject obj, jobject people) {
	jclass cls = env->GetObjectClass(people);
	jfieldID jage = env->GetFieldID(cls,"age","I");
	int age = env->GetIntField(people,jage);
//	LOGD("age is %d",age);
	jfieldID jname = env->GetFieldID(cls,"name","Ljava/lang/String;");
	jstring str1 = (jstring)env->GetObjectField(people,jname);
	return age;
}

