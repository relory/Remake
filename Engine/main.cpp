#include "jni_exports.h"
#include <jvmti.h>

#ifdef __linux__
#include <cstring>
#endif

#include <string>
#include <algorithm>

jvmtiEnv* jvmti = nullptr;
std::string next_class_to_be_transformed;
bool transformed = false;
jobject* remakeInstance;


// Fixed memory issues in this method. Previously returned a nullptr pointer because
// ReleaseStringUTFChars was called too early, causing the retransforming process to freeze.
static char* get_class_name(JNIEnv* env, jclass clazz) {
	jclass cls = env->FindClass("java/lang/Class");
	jmethodID mid_getName = env->GetMethodID(cls, "getName", "()Ljava/lang/String;");
	jstring jname = (jstring)env->CallObjectMethod(clazz, mid_getName);

	const char* chars = env->GetStringUTFChars(jname, nullptr);

	char* copy = nullptr;
	if (chars) {
		copy = strdup(chars);
	}

	env->ReleaseStringUTFChars(jname, chars);
	env->DeleteLocalRef(jname);
	env->DeleteLocalRef(cls);

	return copy;
}

static void JNICALL ClassFileLoadHook(
	jvmtiEnv* jvmti,
	JNIEnv* env,
	jclass class_being_redefined,
	jobject loader,
	const char* name,
	jobject protection_domain,
	jint class_data_len,
	const unsigned char* class_data,
	jint* new_class_data_len,
	unsigned char** new_class_data
) {
	// Check if the loaded class is the class we want to transform

	if (name == nullptr) {
		return;
	}

	std::replace(next_class_to_be_transformed.begin(), next_class_to_be_transformed.end(), '.', '/');
	if (strcmp(name, next_class_to_be_transformed.c_str()) != 0) {
		return;
	}

	// Find our Remake instance
	jclass remake = env->FindClass("sh/body/remake/Remake");
	jmethodID getInstanceMethod = env->GetStaticMethodID(remake, "getInstance", "()Lsh/body/remake/Remake;");
	jobject remakeInstance = env->CallStaticObjectMethod(remake, getInstanceMethod);

	// Convert our class name & class data into jobjects
	jstring str = env->NewStringUTF(name);
	jbyteArray classfileBuffer = env->NewByteArray(class_data_len);
	env->SetByteArrayRegion(classfileBuffer, 0, class_data_len, (signed char*)class_data);

	// Call our _transform method to edit the java bytecode
	jmethodID transformMethod = env->GetMethodID(remake, "transform", "(Ljava/lang/String;[B)[B");
	jbyteArray result = (jbyteArray)env->CallObjectMethod(remakeInstance, transformMethod, str, classfileBuffer);

	if (result == nullptr) {
		return;
	}

	// Set the returned byte array to the ClassFileLoadHook response
	jint length = env->GetArrayLength(result);
	if (length == 0) {
		return;
	}

	*new_class_data_len = length;
	*new_class_data = (unsigned char*)env->GetByteArrayElements(result, 0);
	transformed = true;
}

/*
	* Class:     sh_body_remake_Remake
	* Method:    _init
	* Signature: ()V
*/

JNIEXPORT void JNICALL Java_sh_body_remake_Remake_nInit(JNIEnv* env, jobject obj) {


	// Get the JVMTI env

	JavaVM* vm;
	JNI_GetCreatedJavaVMs(&vm, 1, nullptr);
	vm->GetEnv(reinterpret_cast<void**>(&jvmti), JVMTI_VERSION_1_0);

	// Enable the retransformClasses capability

	static jvmtiCapabilities capa;
	(void)memset(&capa, 0, sizeof(jvmtiCapabilities));
	capa.can_retransform_classes = 1;
	jvmti->AddCapabilities(&capa);

	// Hook the Class File Load event

	jvmtiEventCallbacks callbacks = { 0 };
	callbacks.ClassFileLoadHook = ClassFileLoadHook;
	jvmti->SetEventCallbacks(&callbacks, sizeof(callbacks));
	jvmti->SetEventNotificationMode(JVMTI_ENABLE,
		JVMTI_EVENT_CLASS_FILE_LOAD_HOOK,
		(jthread)NULL);
}

/*
	* Class:     sh_body_remake_Remake
	* Method:    _remake
	* Signature: (Ljava/lang/Class;)V
*/

JNIEXPORT void JNICALL Java_sh_body_remake_Remake_nRemake(JNIEnv* env, jobject obj, jclass clazz) {
	const char* name = get_class_name(env, clazz);
	next_class_to_be_transformed = std::string(name);
	transformed = false;
	// Retransforming the class will make it call ClassFileLoadHook, allowing us to modify its bytecode
	// While loop to fix some random bug where it doesnt call the ClassFileLoadHook -> retransforming until class is modified
	while (!transformed) {
		jvmti->RetransformClasses(1, &clazz);
	}
}
