package com.kcc.encodertest;

import android.app.Presentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final boolean VERBOSE = true;           // lots of logging
    private static final boolean DEBUG_SAVE_FILE = true;   // save copy of encoded movie
    private static final String DEBUG_FILE_NAME_BASE = "/sdcard/kyle-test.";

    // Virtual display characteristics.  Scaled down from full display size because not all
    // devices can encode at the resolution of their own display.
    private static final String NAME = TAG;
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final int DENSITY = DisplayMetrics.DENSITY_HIGH;
    private static final int UI_TIMEOUT_MS = 2000;
    private static final int UI_RENDER_PAUSE_MS = 200;

    // Encoder parameters.  We use the same width/height as the virtual display.
    private static final String MIME_TYPE = "video/avc";
    private static final int FRAME_RATE = 15;               // 15fps
    private static final int IFRAME_INTERVAL = 10;          // 10 seconds between I-frames
    private static final int BIT_RATE = 6000000;            // 6Mbps

    // Colors to test (RGB).  These must convert cleanly to and from BT.601 YUV.
    private static final int TEST_COLORS[] = {
            makeColor(10, 100, 200),        // YCbCr 89,186,82
            makeColor(100, 200, 10),        // YCbCr 144,60,98
            makeColor(200, 10, 100),        // YCbCr 203,10,103
            makeColor(10, 200, 100),        // YCbCr 130,113,52
            makeColor(100, 10, 200),        // YCbCr 67,199,154
            makeColor(200, 100, 10),        // YCbCr 119,74,179
    };

    /* TEST_COLORS static initialization; need ARGB for ColorDrawable */
    private static int makeColor(int red, int green, int blue) {
        return 0xff << 24 | (red & 0xff) << 16 | (green & 0xff) << 8 | (blue & 0xff);
    }

    private final ByteBuffer mPixelBuf = ByteBuffer.allocateDirect(4);
    private Handler mUiHandler;                             // Handler on main Looper
    private DisplayManager mDisplayManager;
    volatile boolean mInputDone;
    private MediaProjection projection = null;
    MediaCodec encoder = null;
    MediaCodec decoder = null;
    MediaMuxer mMuxer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDisplayManager = (DisplayManager)getSystemService(DISPLAY_SERVICE);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent: ");
        encoder.signalEndOfInputStream();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        EncodeAndMuxTest test = new EncodeAndMuxTest();
//        test.testEncodeVideoToMp4();
//        checkdrawPermission();
        MediaProjectionManager mProjectionManager = (MediaProjectionManager)getSystemService(MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), 1);
//
//        DisplayMetrics dm = getResources().getDisplayMetrics();
//        Log.i(TAG, "onResume: "+dm.toString());

    }
    int mResultcode = 0;
    Intent mResultData = null;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if (resultCode == RESULT_OK){
                mResultcode = resultCode;
                mResultData = data;
                encodeVirtualDisplayTest();
            }
        }
    }

    public void checkdrawPermission() {
        int REQUEST_CODE_ASK_PERMISSIONS = 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE_ASK_PERMISSIONS);
            } else {
                // display over lay from service
            }
        }else
        {
            // display over lay from service
        }
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

    int mCurrentDisplayId = -1;
    boolean mDisplayAdded = false;
    DisplayManager.DisplayListener listener = new DisplayManager.DisplayListener() {
        @Override
        public void onDisplayAdded(int displayId) {
            Log.i(TAG, "onDisplayAdded: "+displayId);
            mCurrentDisplayId = displayId;
            mDisplayAdded = true;
        }

        @Override
        public void onDisplayRemoved(int displayId) {
            Log.i(TAG, "onDisplayRemoved: "+displayId);
            mCurrentDisplayId = -1;
            mDisplayAdded = false;
        }

        @Override
        public void onDisplayChanged(int displayId) {
            Log.i(TAG, "onDisplayChanged: "+displayId);
            Log.i(TAG, "onDisplayChanged: get Display "+(mDisplayManager.getDisplay(displayId) == null));
            if(displayId == mCurrentDisplayId){
                new ColorSlideShow(mDisplayManager.getDisplay(displayId)).start();
            }
        }
    };

    private void encodeVirtualDisplayTest() {

        int mTrackIndex = 0;
        OutputSurface outputSurface = null;
        VirtualDisplay virtualDisplay = null;

        try {
            // Encoded video resolution matches virtual display.
            MediaFormat encoderFormat = MediaFormat.createVideoFormat(MIME_TYPE, WIDTH, HEIGHT);
            encoderFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                    MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            encoderFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
            encoderFormat.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
            encoderFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);

            try {
                encoder = MediaCodec.createEncoderByType(MIME_TYPE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            encoder.configure(encoderFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            Surface inputSurface = encoder.createInputSurface();
            encoder.start();

            try {
                mMuxer = new MediaMuxer(DEBUG_FILE_NAME_BASE+"mp4",MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Create a virtual display that will output to our encoder.

            mDisplayManager.registerDisplayListener(listener,null);
            MediaProjectionManager manager = (MediaProjectionManager)getSystemService(MEDIA_PROJECTION_SERVICE);
            MediaProjection projection = manager.getMediaProjection(mResultcode,mResultData);
            virtualDisplay = projection.createVirtualDisplay(NAME,
                    WIDTH, HEIGHT, getResources().getDisplayMetrics().densityDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION,inputSurface,null,null);
            
            mInputDone = false;

            // move this line to DisplayListener
//            new ColorSlideShow(virtualDisplay.getDisplay()).start();


            // Record everything we can and check the results.
            doTestEncodeVirtual(encoder, decoder,mMuxer, outputSurface);

        } finally {
            if (VERBOSE) Log.d(TAG, "releasing codecs, surfaces, and virtual display");
            if (virtualDisplay != null) {
                virtualDisplay.release();
            }
            if (outputSurface != null) {
                outputSurface.release();
            }
            if (encoder != null) {
                encoder.stop();
                encoder.release();
            }
            if (decoder != null) {
                decoder.stop();
                decoder.release();
            }
        }
    }

    public  MainActivity getInstance(){
        return this;
    }
    /**
     * Drives the encoder and decoder.
     */
    private void doTestEncodeVirtual(MediaCodec encoder, MediaCodec decoder,MediaMuxer muxer,
                                     OutputSurface outputSurface) {
        final int TIMEOUT_USEC = 10000;
        ByteBuffer[] encoderOutputBuffers = encoder.getOutputBuffers();
//        ByteBuffer[] decoderInputBuffers = decoder.getInputBuffers();
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        boolean inputEosSignaled = false;
        int lastIndex = -1;
        int goodFrames = 0;
        int debugFrameCount = 0;

        int mTrackIndex = 0;
        // Save a copy to disk.  Useful for debugging the test.  Note this is a raw elementary
        // stream, not a .mp4 file, so not all players will know what to do with it.
        FileOutputStream outputStream = null;
        if (DEBUG_SAVE_FILE) {
            String fileName = DEBUG_FILE_NAME_BASE + WIDTH + "x" + HEIGHT + ".mp4";
            try {
                outputStream = new FileOutputStream(fileName);
                Log.d(TAG, "encoded output will be saved as " + fileName);
            } catch (IOException ioe) {
                Log.w(TAG, "Unable to create debug output file " + fileName);
                throw new RuntimeException(ioe);
            }
        }

        // Loop until the output side is done.
        boolean encoderDone = false;
        boolean outputDone = false;
//        while (!outputDone) {
//            if (VERBOSE) Log.d(TAG, "loop");
//
//            if (!inputEosSignaled && mInputDone) {
//                if (VERBOSE) Log.d(TAG, "signaling input EOS");
//                encoder.signalEndOfInputStream();
//                inputEosSignaled = true;
//            }

            boolean decoderOutputAvailable = true;
            boolean encoderOutputAvailable = !encoderDone;
//            while (decoderOutputAvailable || encoderOutputAvailable) {
            while ( encoderOutputAvailable) {
                Log.i(TAG, "doTestEncodeVirtual: while loop");
                if (!encoderDone) {
                    int encoderStatus = encoder.dequeueOutputBuffer(info, TIMEOUT_USEC);
                    if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                        // no output available yet
                        if (VERBOSE) Log.d(TAG, "no output from encoder available");
//                        encoderOutputAvailable = false;
                    } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                        // not expected for an encoder
                        encoderOutputBuffers = encoder.getOutputBuffers();
                        if (VERBOSE) Log.d(TAG, "encoder output buffers changed");
                    } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        // received before first buffer
                        MediaFormat newFormat = encoder.getOutputFormat();
                        mTrackIndex = muxer.addTrack(newFormat);
                        muxer.start();
                        if (VERBOSE) Log.d(TAG, "encoder output format changed: " + newFormat);
                    } else if (encoderStatus < 0) {
                        Log.e(TAG,"unexpected result from encoder.dequeueOutputBuffer: " + encoderStatus);
                    } else { // encoderStatus >= 0
                        ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                        if (encodedData == null) {
                            Log.e(TAG,"encoderOutputBuffer " + encoderStatus + " was null");
                        }

                        // It's usually necessary to adjust the ByteBuffer values to match BufferInfo.
                        encodedData.position(info.offset);
                        encodedData.limit(info.offset + info.size);

                        muxer.writeSampleData(mTrackIndex,encodedData,info);
                        if (outputStream != null) {
                            byte[] data = new byte[info.size];
                            encodedData.get(data);
                            encodedData.position(info.offset);
                            try {
                                outputStream.write(data);
                            } catch (IOException ioe) {
                                Log.w(TAG, "failed writing debug data to file");
                                throw new RuntimeException(ioe);
                            }
                            debugFrameCount++;
                        }

                        // Get a decoder input buffer, blocking until it's available.  We just
                        // drained the decoder output, so we expect there to be a free input
                        // buffer now or in the near future (i.e. this should never deadlock
                        // if the codec is meeting requirements).
                        //
                        // The first buffer of data we get will have the BUFFER_FLAG_CODEC_CONFIG
                        // flag set; the decoder will see this and finish configuring itself.
//                        int inputBufIndex = decoder.dequeueInputBuffer(-1);
//                        ByteBuffer inputBuf = decoderInputBuffers[inputBufIndex];
//                        inputBuf.clear();
//                        inputBuf.put(encodedData);
//                        decoder.queueInputBuffer(inputBufIndex, 0, info.size,
//                                info.presentationTimeUs, info.flags);

                        // If everything from the encoder has been passed to the decoder, we
                        // can stop polling the encoder output.  (This just an optimization.)
                        if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                            encoderDone = true;
                            encoderOutputAvailable = false;
                        }
                         Log.d(TAG, "passed " + info.size + " bytes to decoder"
                                + (encoderDone ? " (EOS)" : ""));

                        encoder.releaseOutputBuffer(encoderStatus, false);
                    }
                }
            }
//        }

        if (outputStream != null) {
            try {
                outputStream.close();
                if (VERBOSE) Log.d(TAG, "Wrote " + debugFrameCount + " frames");
            } catch (IOException ioe) {
                Log.w(TAG, "failed closing debug file");
                throw new RuntimeException(ioe);
            }
        }


        if (goodFrames != TEST_COLORS.length) {
            Log.e(TAG,"Found " + goodFrames + " of " + TEST_COLORS.length + " expected frames");
        }
    }

    /**
     * Creates a series of colorful Presentations on the specified Display.
     */
    private class ColorSlideShow extends Thread {
        private Display mDisplay;

        public ColorSlideShow(Display display) {
            mDisplay = display;
        }

        @Override
        public void run() {
            for (int i = 0; i < TEST_COLORS.length; i++) {
                showPresentation(TEST_COLORS[i]);
            }

            if (VERBOSE) Log.d(TAG, "slide show finished");
            mInputDone = true;
        }

        private void showPresentation(final int color) {
            final TestPresentation[] presentation = new TestPresentation[1];
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Want to create presentation on UI thread so it finds the right Looper
                        // when setting up the Dialog.
                        presentation[0] = new TestPresentation(getBaseContext(), mDisplay, color);
                        if (VERBOSE) Log.d(TAG, "showing color=0x" + Integer.toHexString(color));
                        presentation[0].show();
                    }
                });

                // Give the presentation an opportunity to render.  We don't have a way to
                // monitor the output, so we just sleep for a bit.
                try { Thread.sleep(UI_RENDER_PAUSE_MS); }
                catch (InterruptedException ignore) {}
            } finally {
                if (presentation[0] != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            presentation[0].dismiss();
                        }
                    });
                }
            }
        }
    }

    /**
     * Determines if two color values are approximately equal.
     */
    private static boolean approxEquals(int expected, int actual) {
        final int MAX_DELTA = 4;
        return Math.abs(expected - actual) <= MAX_DELTA;
    }

    /**
     * Checks the contents of the current EGL surface to see if it matches expectations.
     * <p>
     * The surface may be black or one of the colors we've drawn.  We have sufficiently little
     * control over the rendering process that we don't know how many (if any) black frames
     * will appear between each color frame.
     * <p>
     * @return the color index, or -2 for black
     * @throw RuntimeException if the color isn't recognized (probably because the RGB<->YUV
     *     conversion introduced too much variance)
     */
    private int checkSurfaceFrame() {
        boolean frameFailed = false;

        // Read a pixel from the center of the surface.  Might want to read from multiple points
        // and average them together.
        int x = WIDTH / 2;
        int y = HEIGHT / 2;
        GLES20.glReadPixels(x, y, 1, 1, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mPixelBuf);
        int r = mPixelBuf.get(0) & 0xff;
        int g = mPixelBuf.get(1) & 0xff;
        int b = mPixelBuf.get(2) & 0xff;
        if (VERBOSE) Log.d(TAG, "GOT: r=" + r + " g=" + g + " b=" + b);

        if (approxEquals(0, r) && approxEquals(0, g) && approxEquals(0, b)) {
            return -2;
        }

        // Walk through the color list and try to find a match.  These may have gone through
        // RGB<->YCbCr conversions, so don't expect exact matches.
        for (int i = 0; i < TEST_COLORS.length; i++) {
            int testRed = (TEST_COLORS[i] >> 16) & 0xff;
            int testGreen = (TEST_COLORS[i] >> 8) & 0xff;
            int testBlue = TEST_COLORS[i] & 0xff;
            if (approxEquals(testRed, r) && approxEquals(testGreen, g) &&
                    approxEquals(testBlue, b)) {
                if (VERBOSE) Log.d(TAG, "Matched color " + i + ": r=" + r + " g=" + g + " b=" + b);
                return i;
            }
        }

        throw new RuntimeException("No match for color r=" + r + " g=" + g + " b=" + b);
    }




    /**
     * Presentation we can show on a virtual display.  The view is set to a single color value.
     */
    private class TestPresentation extends Presentation {
        private final int mColor;

        private TextView view;
        private int count = 0;
        public TestPresentation(Context context, Display display, int color) {
            super(context, display);
            mColor = color;
        }
        final Handler handler = new Handler(){
            public void handleMessage(Message msg){
                view.setText(""+(++count));
                    handler.sendEmptyMessageDelayed(0,50);
            }
        };
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.i(TAG, "Presentation onCreate: ");
            setTitle("Encode Virtual Test");
            getWindow().setType(WindowManager.LayoutParams.TYPE_PRIVATE_PRESENTATION);

            // Create a solid color image to use as the content of the presentation.
//            ImageView view = new ImageView(getContext());
//            view.setImageDrawable(new ColorDrawable(mColor));
//            view.setLayoutParams(new ViewGroup.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            view = new TextView(getContext());
            view.setText(count);

            view.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            setContentView(view);

            Log.i(TAG, "Presentation onCreate: setContentView");
        }

        @Override
        protected void onStart() {
            super.onStart();
            handler.sendEmptyMessage(0);
        }
    }

}
