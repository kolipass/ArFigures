package mobi.tarantino.arfigure;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.commonsware.cwac.cam2.AbstractCameraActivity;
import com.commonsware.cwac.cam2.CameraController;
import com.commonsware.cwac.cam2.CameraEngine;
import com.commonsware.cwac.cam2.CameraSelectionCriteria;
import com.commonsware.cwac.cam2.CameraView;
import com.commonsware.cwac.cam2.FlashMode;
import com.commonsware.cwac.cam2.PictureTransaction;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.greenrobot.event.EventBus;
import mobi.tarantino.arfigure.net.NetClient;
import mobi.tarantino.arfigure.net.RecognizeResponse;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by kolipass on 28.01.16.
 */
public class WebQuestActivity extends Activity implements CustomSwipeRefreshLayout.CanChildScrollUpCallback {
    public WebView mWebView;
    public ProgressBar progressBar;
    public CustomSwipeRefreshLayout swipeRefreshLayout;
    private CameraController cameraController;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private static String createFileName(String name, String expression) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy_MM_dd_HH_mm_ss");
        return name
                + dateFormat.format(new Date(System.currentTimeMillis()))
                + "."
                + expression;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webquest);

        initWebVIew();

        cameraController = new CameraController(AbstractCameraActivity.FocusMode.CONTINUOUS, new ArrayList<FlashMode>() {{
            add(FlashMode.OFF);
        }}, false);


        boolean match = false;
        CameraSelectionCriteria criteria =
                new CameraSelectionCriteria.Builder()
                        .facing(AbstractCameraActivity.Facing.FRONT)
                        .facingExactMatch(match)
                        .build();
        boolean forceClassic = false;
        if ("samsung".equals(Build.MANUFACTURER) &&
                ("ha3gub".equals(Build.PRODUCT) ||
                        "k3gxx".equals(Build.PRODUCT))) {
            forceClassic = true;
        }

        cameraController.setEngine(CameraEngine.buildInstance(this, forceClassic), criteria);
        if (cameraController != null && cameraController.getNumberOfCameras() > 0) {
            prepController();
        }

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean canSwipeRefreshChildScrollUp() {
        return mWebView.getScrollY() > 0;
    }

    private void prepController() {
        LinkedList<CameraView> cameraViews = new LinkedList<CameraView>() {{
            add((CameraView) findViewById(R.id.preview_surface));
            add((CameraView) findViewById(R.id.preview_surface2));
//            add((CameraView) findViewById(R.id.preview_surface3));
        }};

        cameraController.setCameraViews(cameraViews);
    }

    private void takePicture() {
        Uri output = Uri.fromFile(new File(getApplication().getExternalCacheDir(),
                createFileName("shot", "jpg")));
        PictureTransaction.Builder b = new PictureTransaction.Builder().toUri(this, output, false);

        Log.d("qwest", output.toString());

        cameraController.takePicture(b.build());
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(CameraController.ControllerReadyEvent event) {
        if (event.isEventForController(cameraController)) {
            prepController();
        }
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(CameraEngine.PictureTakenEvent event) {
        if (event.exception == null) {
            Uri output = event.getPictureTransaction().getProperties().getParcelable("output");
//
//            if (output != null) {
//                Log.d("qwest", output.toString());
//
//                File file = new File(output.toString());
//                if (file.exists()) {
            swipeRefreshLayout.setRefreshing(true);
            new NetClient().serverMethods.recognize("f04d596f075a4f16bddefc896fc3fc6f",
                    null,
                    RequestBody.create(MediaType.parse("image/jpg"), event.getImageContext().getJpeg()))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<RecognizeResponse>>() {
                                   @Override
                                   public void onCompleted() {
                                       Toast.makeText(WebQuestActivity.this, "onCompleted", Toast.LENGTH_SHORT).show();
                                       swipeRefreshLayout.setRefreshing(false);
                                   }

                                   @Override
                                   public void onError(Throwable e) {
                                       e.printStackTrace();
                                       Toast.makeText(WebQuestActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                                       swipeRefreshLayout.setRefreshing(false);
                                   }

                                   @Override
                                   public void onNext(List<RecognizeResponse> recognizeResponses) {
                                       for (RecognizeResponse response : recognizeResponses) {
                                           Log.d("web", response.getScores().toString());
                                       }

                                       swipeRefreshLayout.setRefreshing(false);

                                       Toast.makeText(WebQuestActivity.this, recognizeResponses.toString(), Toast.LENGTH_SHORT).show();
                                   }
                               }
                    );
        }
//        else {
//                    Toast.makeText(WebQuestActivity.this, "file !exist", Toast.LENGTH_SHORT).show();
//                }
//            }    else {
//                Toast.makeText(WebQuestActivity.this, "uri == null", Toast.LENGTH_SHORT).show();
//
//            }

//        }
    }

    private void initWebVIew() {
        String url = "http://deutschquest.6931.ru/word_translate.html";


        swipeRefreshLayout = (CustomSwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setCanChildScrollUpCallback(this);

        mWebView = (WebView) findViewById(R.id.webView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setSaveEnabled(false);
        mWebView.loadUrl(url);
        mWebView.getSettings().setBuiltInZoomControls(true);
//        mWebView.getSettings().setDisplayZoomControls(false);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("LoginWebActivity", url);

            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress != 100) {

                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(progress);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.reload();
            }
        });
    }

    /**
     * Standard lifecycle method, passed along to the CameraController.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if (cameraController != null) {
            try {
                cameraController.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Standard lifecycle method, for when the fragment moves into
     * the stopped state. Passed along to the CameraController.
     */
    @Override
    public void onStop() {
        if (cameraController != null) {
            cameraController.stop();
        }

        EventBus.getDefault().unregister(this);

        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "WebQuest Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://mobi.tarantino.arfigure/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    /**
     * Standard lifecycle method, for when the fragment is utterly,
     * ruthlessly destroyed. Passed along to the CameraController,
     * because why should the fragment have all the fun?
     */
    @Override
    public void onDestroy() {
        if (cameraController != null) {
            cameraController.destroy();
        }

        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "WebQuest Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://mobi.tarantino.arfigure/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }
}
