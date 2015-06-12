package mobi.tarantino.arfigure;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.TrackingValues;
import com.metaio.tools.io.AssetsManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//TutorialInstantTracking clone
public class InstantTrackingActivity extends MechActivity {

    public static final String FILE_NAME = "file_name";

    /**
     * Flag to indicate proximity to the tiger
     */
    boolean mIsCloseToTiger;


    private View m2DButton;
    private View m2DRectifiedButton;
    private View m3DButton;
    private View m2DSLAMButton;
    private View m2DSLAMExtrapolationButton;
    protected List<View> buttons = new ArrayList<>();
    /**
     * The flag indicating a mode of instant tracking
     *
     * @see {@link com.metaio.sdk.jni.IMetaioSDKAndroid# startInstantTracking(String, String, boolean)}
     */
    boolean mPreview = true;

    /**
     * Whether to set tracking configuration on onInstantTrackingEvent
     */
    boolean mMustUseInstantTrackingEvent = false;
    private String fileName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fileName = getIntent().getStringExtra(FILE_NAME);
        fileName = fileName != null ? fileName : "Lorenc.txt.obj";

        model = null;
        mCallbackHandler = new MetaioSDKCallbackHandler2();
        mIsCloseToTiger = false;


        m2DButton = mGUIView.findViewById(R.id.instant2DButton);
        m2DRectifiedButton = mGUIView.findViewById(R.id.instant2DRectifiedButton);
        m3DButton = mGUIView.findViewById(R.id.instant3DButton);
        m2DSLAMButton = mGUIView.findViewById(R.id.instant2DSLAMButton);
        m2DSLAMExtrapolationButton = mGUIView.findViewById(R.id.instant2DSLAMExtrapolationButton);

        Collections.addAll(buttons,
                m2DButton,
                m2DRectifiedButton,
                m3DButton,
                m2DSLAMButton,
                m2DSLAMExtrapolationButton);

    }


    /**
     * This method is regularly called in the rendering loop. It calculates the distance between
     * device and the target and performs actions based on the proximity
     */
    private void checkDistanceToTarget() {
        // get tracking values for COS 1
        TrackingValues tv = metaioSDK.getTrackingValues(1);

        // Note, you can use this mechanism also to detect if something is tracking or not.
        // (e.g. for triggering an action as soon as some target is visible on screen)
        if (tv.isTrackingState()) {
            // calculate the distance as sqrt( x^2 + y^2 + z^2 )
            final float distance = tv.getTranslation().norm();

            // define a threshold distance
            final float threshold = 200;

            // moved close to the tiger
            if (distance < threshold) {
                // if not already close to the model
                if (!mIsCloseToTiger) {
                    MetaioDebug.log("Moved close to the tiger");
                    mIsCloseToTiger = true;
                    playSound();
                    model.startAnimation("tap");
                }
            } else {
                if (mIsCloseToTiger) {
                    MetaioDebug.log("Moved away from the tiger");
                    mIsCloseToTiger = false;
                }
            }

        }
    }

    /**
     * Play sound that has been loaded
     */
    private void playSound() {
        try {
            MetaioDebug.log("Playing sound");
            Toast.makeText(this, "click", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            MetaioDebug.log("Error playing sound: " + e.getMessage());
        }
    }


    @Override
    protected int getGUILayout() {
        return R.layout.tutorial_instant_tracking;
    }


    @Override
    public void onDrawFrame() {
        super.onDrawFrame();

        checkDistanceToTarget();

    }

    public void onButtonClick(View v) {
        finish();
    }

    private void startTracking(String type, View nonChangeble) {
        mMustUseInstantTrackingEvent = true;
        model.setVisible(false);

        for (View view : buttons) {
            if (!view.equals(nonChangeble)) {
                view.setEnabled(!mPreview);
            }
        }
        if (type != null) {
            metaioSDK.startInstantTracking(type, new File(""), mPreview);
        }
        mPreview = !mPreview;
    }

    public void on2DButtonClicked(View v) {
        startTracking("INSTANT_2D", m2DButton);
    }

    public void on2DRectifiedButtonClicked(View v) {
        startTracking("INSTANT_2D_GRAVITY", m2DRectifiedButton);
    }

    public void on3DButtonClicked(View v) {
        mMustUseInstantTrackingEvent = false;
        model.setVisible(false);
        metaioSDK.startInstantTracking("INSTANT_3D");
    }

    public void on2DSLAMButtonClicked(View v) {
        startTracking("INSTANT_2D_GRAVITY_SLAM", m2DSLAMButton);
    }

    public void on2DSLAMExtrapolationButtonClicked(View v) {
        startTracking("INSTANT_2D_GRAVITY_SLAM_EXTRAPOLATED", m2DSLAMExtrapolationButton);
    }

    @Override
    protected void loadContents() {
        try {
            AssetsManager.extractAllAssets(getApplicationContext(), BuildConfig.DEBUG);
            // Load tiger model
            final File modelPath =
                    AssetsManager.getAssetPathAsFile(getApplicationContext(), fileName);
            model = metaioSDK.createGeometry(modelPath);

            // Set geometry properties and initially hide it
            updateScale();
            model.setRotation(new Rotation(0f, 0f, (float) Math.PI));
            model.setVisible(false);
            MetaioDebug.log("Loaded geometry " + modelPath);


        } catch (Exception e) {
            MetaioDebug.log(Log.ERROR, "Error loading geometry: " + e.getMessage());
        }
    }


    @Override
    protected void onGeometryTouched(IGeometry geometry) {
        playSound();
        geometry.startAnimation("tap");
    }

    protected class MetaioSDKCallbackHandler2 extends MetaioSDKCallbackHandler {


        @Override
        public void onInstantTrackingEvent(boolean success, File filePath) {
            if (success) {
                // Since SDK 6.0, INSTANT_3D doesn't create a new tracking configuration anymore
                // (see changelog)
                if (mMustUseInstantTrackingEvent) {
                    MetaioDebug.log("MetaioSDKCallbackHandler.onInstantTrackingEvent: " + filePath.getPath());
                    metaioSDK.setTrackingConfiguration(filePath);
                }

                model.setVisible(true);
            } else {
                MetaioDebug.log(Log.ERROR, "Failed to create instant tracking configuration!");
            }
        }

        @Override
        public void onAnimationEnd(IGeometry geometry, String animationName) {
            // Play a random animation from the list
            final String[] animations = {"meow", "scratch", "look", "shake", "clean"};
            final int random = (int) (Math.random() * animations.length);
            geometry.startAnimation(animations[random]);
        }
    }

}
