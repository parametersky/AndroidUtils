package kcc.sorg.viewpagertest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentViewPager extends AppCompatActivity {

    private ViewPager mPager;
    private int LIST_NUMBER =12;
    private int currentNumber = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPager = (ViewPager)findViewById(R.id.pager);
        CustFragmentAdapter adapter = new CustFragmentAdapter(getSupportFragmentManager());
        mPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class CustFragmentAdapter extends FragmentStatePagerAdapter{
        private static final String TAG = "CustFragmentAdapter";
        private FirstFragment first = null;
        private SecondFragment second = null;
        private FirstFragment third = null;
        private int initialIndex = 0;
        private int start = 0;
        private int end = 0;
        public CustFragmentAdapter(FragmentManager fm){
            super(fm);

        }
        public void setInitialIndex(int start, int end, int current){
            initialIndex = current;
            this.start = start;
            this.end = end;
        }
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position){
                case 0:
                    if (first == null){
                        first = new FirstFragment();
                        if(initialIndex > start){
                            Bundle bundle = new Bundle();
                            bundle.putInt("INDEX",initialIndex-1);
                            first.setArguments(bundle);
                        }
                        fragment = first;
                    }
                    break;
                case 1:
                    if (second == null){
                        second = new SecondFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt("INDEX",initialIndex);
                        second.setArguments(bundle);
                        fragment = second;
                    }
                    break;
                case 2:
                    if (third == null){
                        third = new FirstFragment();
                        if(initialIndex < end){
                            Bundle bundle = new Bundle();
                            bundle.putInt("INDEX",initialIndex+1);
                            third.setArguments(bundle);
                        }
                        fragment = third;
                    }
                    break;
            }
            return fragment;
        }


        @Override
        public int getCount() {
            return 2;
        }
    }

    public static class FirstFragment extends Fragment{
        TextView textview;
        private int index = -1;
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(
                    R.layout.first_fragment, container, false);
            textview = (TextView) rootView.findViewById(R.id.textView);
            if(index != -1)
                textview.setText(index+"");
            return rootView;
        }
        public void setText(String str){
            textview.setText(str);
        }
    }
    public static class SecondFragment extends Fragment{
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(
                    R.layout.second_fragment, container, false);

            return rootView;
        }
    }
}
