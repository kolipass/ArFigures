package mobi.tarantino.arfigure;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.metaio.sdk.ARViewActivity;
import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.tools.io.AssetsManager;

import java.io.File;

/**
 * Created by kolipass on 12.06.15.
 */

public class MechActivity extends ARViewActivity implements View.OnClickListener {
    protected String fileName;
    protected IGeometry model;
    protected float scale = 4f;
    /**
     * metaio SDK callback handler
     */
    protected IMetaioSDKCallback mCallbackHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mCallbackHandler = new IMetaioSDKCallback() {
            @Override
            public void onSDKReady() {
                // show GUI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mGUIView.setVisibility(View.VISIBLE);
                    }
                });
            }

        };

        fileName = getIntent().getStringExtra(InstantTrackingActivity.FILE_NAME);
        fileName = fileName != null ? fileName : "Lorenc.txt.obj";

        mGUIView.findViewById(R.id.incButton).setOnClickListener(this);
        mGUIView.findViewById(R.id.decButton).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCallbackHandler.delete();
        mCallbackHandler = null;

    }

    @Override
    protected int getGUILayout() {
        // Attaching layout to the activity
        return R.layout.mech_activity;
    }


    @Override
    protected void loadContents() {
        try {
            // Getting a file path for tracking configuration XML file
            File trackingConfigFile = AssetsManager.getAssetPathAsFile(getApplicationContext(), "TrackingData_MarkerlessFast.xml");

            // Assigning tracking configuration
            boolean result = metaioSDK.setTrackingConfiguration(trackingConfigFile);
            MetaioDebug.log("Tracking data loaded: " + result);

            // Getting a file path for a 3D model
            File metaioManModel = AssetsManager.getAssetPathAsFile(getApplicationContext(), fileName);
            if (metaioManModel != null) {
                // Loading 3D model
                model = metaioSDK.createGeometry(metaioManModel);
                if (model != null) {
                    // Set model properties
                    updateScale();
                } else
                    MetaioDebug.log(Log.ERROR, "Error loading model: " + metaioManModel);
            }
        } catch (Exception e) {
            MetaioDebug.printStackTrace(Log.ERROR, e);
        }
    }


    @Override
    protected void onGeometryTouched(IGeometry geometry) {
        // Not used in this tutorial
    }


    @Override
    protected IMetaioSDKCallback getMetaioSDKCallbackHandler() {
        return mCallbackHandler;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.incButton:
                scale += 1f;
                updateScale();
                break;
            case R.id.decButton:
                scale -= 1f;
                updateScale();
                break;
        }
    }

    protected void updateScale() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) mGUIView.findViewById(R.id.current_scale)).setText(getString(R.string.current_scale, String.valueOf(scale)));
            }
        });
        if (model != null) {
            model.setScale(scale);
        }
    }

    protected class MetaioSDKCallbackHandler extends IMetaioSDKCallback {

        @Override
        public void onSDKReady() {
            // show GUI
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mGUIView.setVisibility(View.VISIBLE);
                }
            });
        }

    }
}
