package kcc.sorg.locationtest;

import android.app.Activity;
import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

    private static final String TAG = "LocationActivity";

    private LocationManager mLocationManager = null;
    private  GpsStatus.Listener mGPSListener = null;
    private  LocationListener mLocationListener = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
    public void onResume(){
        super.onResume();
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "location info: " + location.getSpeed() + "  " + location.getLatitude() + "  " + location.getLongitude() + "  accuracy:" + location.getAccuracy());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d(TAG, "location status change " + status);
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d(TAG, "location enabled");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d(TAG, "location disabled");
            }
        };

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, mLocationListener);
        mGPSListener = new GpsStatus.Listener() {
            @Override
            public void onGpsStatusChanged(int event) {
                Log.d(TAG,"gps status changed:"+event);
            }
        };
        mLocationManager.addGpsStatusListener(mGPSListener);
    }
    public void onPause(){
        super.onPause();
        mLocationManager.removeGpsStatusListener(mGPSListener);
        mLocationManager.removeUpdates(mLocationListener);
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
}
