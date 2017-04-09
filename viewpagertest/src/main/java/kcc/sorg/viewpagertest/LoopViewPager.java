package kcc.sorg.viewpagertest;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.TextView;

import java.util.ArrayList;

public class LoopViewPager extends Activity {

    private static final String TAG = "LoopViewPager";
    private ViewPager mPager = null;
    private ArrayList<View> mPages = null;
    private Scroller mScroller = null;
    View view1;
    View view2;
    View view3;
    TextView textView1;
    TextView textView2;
    TextView textView3;

    private int currentIndex = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mPager = (ViewPager) findViewById(R.id.pager);

        mPages = new ArrayList<View>();
        view1 = getLayoutInflater().inflate(R.layout.numbers,null);
        view2 = getLayoutInflater().inflate(R.layout.numbers,null);
        view3 = getLayoutInflater().inflate(R.layout.numbers,null);

        textView1 = (TextView)view1.findViewById(R.id.text);
        textView2 = (TextView)view2.findViewById(R.id.text);
        textView3 = (TextView)view3.findViewById(R.id.text);

        textView1.setText(""+(currentIndex));
        textView2.setText(""+(currentIndex+1));
        textView3.setText(""+(currentIndex+2));

        mPages.add(view1);
        mPages.add(view2);
        mPages.add(view3);

        Log.i(TAG, "onCreate: view1 == view2"+(view1 == view2));
        Log.i(TAG, "onCreate: mPages size "+mPages.size());
        PagerAdapter mAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return mPages.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return (view == object);
            }


            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeViewAt(position);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                Log.i(TAG, "instantiateItem: "+position);
                View view = mPages.get(position);
                container.addView(view, position);
                return view;

            }
        };

//        mScroller = new Scroller(this);

//            // Revert any animation currently in progress
//            mScroller.forceFinished(true);
//            // Start scrolling by providing a starting point and
//            // the distance to travel
//            mScroller.startScroll(0, 0, 100, 0);
//            // Invalidate to request a redraw

        mPager.setAdapter(mAdapter);
        Log.i(TAG, "onCreate: current item"+mPager.getCurrentItem());
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int pageSelected = 0;
            boolean selfSet = false;
            private int MAX_NUMBER = 12;
            private int MIN_NUMBER = 0;
            private boolean isLoop = false;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "onPageSelected: "+position+ " selfset: "+selfSet);
                if(!selfSet) {
                    Log.i(TAG, "onPageSelected: recount currentIndex: "+currentIndex);
                    currentIndex = currentIndex + (position - pageSelected);
                } else {
                    selfSet = false;
                }
                Log.i(TAG, "onPageSelected: currentIndex "+currentIndex);
                pageSelected = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i(TAG, "onPageScrollStateChanged: "+state);
                if(state == ViewPager.SCROLL_STATE_IDLE && pageSelected != 1){
                    Log.i(TAG, "onPageScrollStateChanged: setItem to middle: currentIndex"+currentIndex);
                    if(currentIndex < MAX_NUMBER && currentIndex > MIN_NUMBER) {
                        textView2.setText("" + currentIndex);
                        selfSet = true;
                        mPager.setCurrentItem(1, false);
                        textView1.setText("" + (currentIndex - 1));
                        textView3.setText("" + (currentIndex + 1));
                    } else if (isLoop && currentIndex == MAX_NUMBER){
                            textView2.setText("" + currentIndex);
                            selfSet = true;
                            mPager.setCurrentItem(1, false);
                            textView1.setText("" + (currentIndex - 1));
                            textView3.setText("" + (MIN_NUMBER));
                        } else if(isLoop && currentIndex == MIN_NUMBER){
                            textView2.setText("" + currentIndex);
                            selfSet = true;
                            mPager.setCurrentItem(1, false);
                            textView1.setText("" + (MAX_NUMBER));
                            textView3.setText("" + (currentIndex+1));
                        }
                    }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
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
}
