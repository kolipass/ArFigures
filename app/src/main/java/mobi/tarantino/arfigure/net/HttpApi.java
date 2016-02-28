package mobi.tarantino.arfigure.net;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by kolipass on 08.01.16.
 */
public interface HttpApi {
    /**
     * @param faceRectangles optional A face rectangle is in the form “left,top,width,height”. Delimited multiple face rectangles with a “;”.
     * @return
     */
    @Headers("Content-Type: application/octet-stream")
    @POST("/emotion/v1.0/recognize")
    Observable<List<RecognizeResponse>> recognize(
            @Header("Ocp-Apim-Subscription-Key") String authorization,
            @Query("faceRectangles") String faceRectangles,
            @Body RequestBody attachment
    );
}
