package kcc.sorg.android_jni_cpp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
	static {
		System.loadLibrary("people");
	}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        People people = new People("Susan",45);
        Log.d("Kyle","printpeole return :"+ people.printPeople(people));
    }

}
