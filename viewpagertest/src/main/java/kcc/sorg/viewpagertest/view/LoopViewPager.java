package kcc.sorg.viewpagertest.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import kcc.sorg.viewpagertest.R;

/**
 * Author: Kyle Cheng
 * Email: kyle.cheng@live.com
 * Date: 16/10/12
 */
public class LoopViewPager extends ViewPager {
    private static final String TAG = "LoopViewPager";
    private ArrayList<View> mPages = null;
    private int mLayout = 0;
    private PagerAdapter mApdater = null;
    private LayoutInflater mInflater = null;
    private ArrayList mContents = null;
    private boolean mLoop = false;
    private int currentIndex = 0;

    private View view1;
    private View view2;
    private View view3;

    public interface ViewUpdater{
        public void updateView(View view, Object content);
        public void onItemChanged(int index);
    }

    private ViewUpdater mUpdater = null;
    public LoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.LoopViewPager);
        mLayout = t.getResourceId(R.styleable.LoopViewPager_children_layout,0);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mPages = new ArrayList<>();
        view1 = mInflater.inflate(mLayout,null);
        view2 = mInflater.inflate(mLayout,null);
        view3 = mInflater.inflate(mLayout,null);

        mPages.add(view1);
        mPages.add(view2);
        mPages.add(view3);

        mApdater = new PagerAdapter() {
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
                if( position == 1){
                            setCurrentItem(1);
                }
                return view;

            }
        };
        setAdapter(mApdater);
        addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            int pageSelected = 1;
            boolean selfSet = false;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "onPageSelected: "+position);
                if(!selfSet) {
                    Log.i(TAG, "onPageSelected: recount currentIndex: "+currentIndex);
                    currentIndex = currentIndex + (position - pageSelected);
                    if (currentIndex < 0)currentIndex = mContents.size()-1;
                    if (currentIndex > mContents.size() -1 ) currentIndex = 0;
                    mUpdater.onItemChanged(currentIndex);
                } else {
                    selfSet = false;
                }
                Log.i(TAG, "onPageSelected: currentIndex "+currentIndex);
                pageSelected = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(state == ViewPager.SCROLL_STATE_IDLE && pageSelected != 1){
                    if(currentIndex < (mContents.size()-1) && currentIndex > 0) {
//                        textView2.setText("" + currentIndex);
                        mUpdater.updateView(view2,mContents.get(currentIndex));
                        selfSet = true; // must set before setCurrentItem
                        setCurrentItem(1, false);
//                        textView1.setText("" + (currentIndex - 1));
                        mUpdater.updateView(view1,mContents.get(currentIndex-1));
                        mUpdater.updateView(view3,mContents.get(currentIndex+1));
//                        textView3.setText("" + (currentIndex + 1));
                    } else if (mLoop && currentIndex == (mContents.size()-1)){
//                        textView2.setText("" + currentIndex);
                        mUpdater.updateView(view2,mContents.get(currentIndex));
                        selfSet = true;
                        setCurrentItem(1, false);
//                        textView1.setText("" + (currentIndex - 1));
//                        textView3.setText("" + (MIN_NUMBER));

                        mUpdater.updateView(view1,mContents.get(currentIndex-1));
                        mUpdater.updateView(view3,mContents.get(0));

                    } else if(mLoop && currentIndex == 0){
//                        textView2.setText("" + currentIndex);
                        mUpdater.updateView(view2,mContents.get(currentIndex));
                        selfSet = true;
                        setCurrentItem(1, false);
//                        textView1.setText("" + (MAX_NUMBER));
//                        textView3.setText("" + (currentIndex+1));
                        mUpdater.updateView(view1,mContents.get(mContents.size()-1));
                        mUpdater.updateView(view3,mContents.get(currentIndex+1));
                    } else {
                    }
                }
            }
        });
    }
    public void setViewUpdater(ViewUpdater updater){
        mUpdater = updater;
    }
    public void setChildrenLayout(int resID){
        mLayout = resID;
    }
    public void setContent(ArrayList list){
        mContents = list;

    }
    public void setLoop(boolean loop){
        mLoop = loop;
    }

    public void setContentAndViewUpdater(ArrayList list,int startIndex, ViewUpdater updater){
        mContents = list;
        mUpdater = updater;
        currentIndex = startIndex;
        updater.updateView(view1,mContents.get(startIndex-1));
        updater.updateView(view2,mContents.get(startIndex));
        updater.updateView(view3,mContents.get(startIndex+1));
    }
}
