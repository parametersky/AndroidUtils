package kcc.sorg.android_jni_cpp;

/**
 * Created by ford-pro2 on 15/10/21.
 */
public class People {
    public int age;
    public String name;
    public People(String na, int a){
    	name = na;
    	age = a;
    }
    public native int printPeople(People person);
}
