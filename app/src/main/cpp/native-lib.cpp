#include <jni.h>
#include <string>
#include <opencv2/highgui.hpp>
#include <opencv2/imgproc.hpp>
#include <iostream>




using namespace cv;
using namespace std;

extern "C"{

JNIEXPORT jlong JNICALL
Java_org_androidtown_new_1chatting_Fragment_Option_1Fragment_loadCascade(JNIEnv *env, jclass type,
                                                                         jstring cascadeFileName_) {
    const char *cascadeFileName = env->GetStringUTFChars(cascadeFileName_, 0);

    // TODO

    env->ReleaseStringUTFChars(cascadeFileName_, cascadeFileName);
}


JNIEXPORT void JNICALL
Java_org_androidtown_new_1chatting_Fragment_Option_1Fragment_bilateralFilter(JNIEnv *env,
                                                                             jobject instance,
                                                                             jlong addrInputImage,
                                                                             jlong addrOutputImage) {


    Mat &img_input = *(Mat *) addrInputImage;
    Mat &img_output = *(Mat *) addrOutputImage;
    cvtColor(img_input, img_output, CV_RGBA2GRAY);


}

JNIEXPORT void JNICALL
Java_org_androidtown_new_1chatting_Fragment_Option_1Fragment_blackFilter(JNIEnv *env,
                                                                         jobject instance,
                                                                         jlong addrInputImage,
                                                                         jlong addrOutputImage) {

    Mat &img_input = *(Mat *) addrInputImage;
    Mat &img_output = *(Mat *) addrOutputImage;



    cvtColor(img_input, img_output, CV_RGBA2GRAY);



}

JNIEXPORT void JNICALL
Java_org_androidtown_new_1chatting_Fragment_Option_1Fragment_whiteFilter(JNIEnv *env,
                                                                         jobject instance,
                                                                         jlong addrInputImage,
                                                                         jlong addrOutputImage) {


    Mat &img_input = *(Mat *) addrInputImage;
    Mat &img_output = *(Mat *) addrOutputImage;

    cvtColor( img_input, img_output, CV_RGB2GRAY);

    adaptiveThreshold(img_input,img_output,255,ADAPTIVE_THRESH_MEAN_C,THRESH_BINARY,21,5);



}



JNIEXPORT void JNICALL
Java_org_androidtown_new_1chatting_Fragment_Option_1Fragment_imageprocessing(JNIEnv *env,
                                                                             jobject ,
                                                                             jlong addrInputImage,
                                                                             jlong addrOutputImage) {

    Mat &img_input = *(Mat *) addrInputImage;
    Mat &img_output = *(Mat *) addrOutputImage;

    cvtColor( img_input, img_input, CV_BGR2RGB);
    cvtColor( img_input, img_output, CV_RGB2GRAY);
    blur( img_output, img_output, Size(5,5) );
    Canny( img_output, img_output, 50, 150, 5 );

}

JNIEXPORT void JNICALL
Java_org_androidtown_new_1chatting_Fragment_Option_1Fragment_loadImage(JNIEnv *env,
                                                                       jobject,
                                                                       jstring imageFileName_,
                                                                       jlong addrImage) {
    Mat &img_input = *(Mat *) addrImage;

    const char *nativeFileNameString = env->GetStringUTFChars(imageFileName_, JNI_FALSE);

    string baseDir("/storage/emulated/0/");
    baseDir.append(nativeFileNameString);
    const char *pathDir = baseDir.c_str();

    img_input = imread(nativeFileNameString, IMREAD_COLOR);
}


JNIEXPORT jstring JNICALL
Java_org_androidtown_new_1chatting_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}



}

