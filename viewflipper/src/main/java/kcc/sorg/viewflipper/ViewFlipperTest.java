package kcc.sorg.viewflipper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ViewFlipper;

/*
 *ViewFlipper cannot react to drag event automatically, it's not like Gallery, just the same layout and can flip automatically.

 */
public class ViewFlipperTest extends AppCompatActivity {

    private ViewFlipper mViewFlipper;
    private float lastX;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_flipper);
        mViewFlipper = (ViewFlipper)findViewById(R.id.viewFlipper);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_flipper, menu);
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
    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction()) {

            case MotionEvent.ACTION_DOWN: {
                lastX = touchevent.getX();
                break;
            }
            case MotionEvent.ACTION_UP: {
                float currentX = touchevent.getX();

                if (lastX < currentX) {

                    if (mViewFlipper.getDisplayedChild() == 0)
                        break;

//                    mViewFlipper.setInAnimation(this, R.anim.in_from_left);
//                    mViewFlipper.setOutAnimation(this, R.anim.out_to_right);
                    // Show The Previous Screen
                    mViewFlipper.showPrevious();
//                    mViewFlipper.
                }

                // if right to left swipe on screen
                if (lastX > currentX) {
                    if (mViewFlipper.getDisplayedChild() == 3)
                        break;

//                    mViewFlipper.setInAnimation(this, R.anim.in_from_right);
//                    mViewFlipper.setOutAnimation(this, R.anim.out_to_left);
                    // Show the next Screen
                    mViewFlipper.showNext();
                }
                break;
            }
        }
        return false;
    }
}
