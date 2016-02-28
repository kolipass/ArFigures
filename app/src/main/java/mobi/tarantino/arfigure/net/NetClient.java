package mobi.tarantino.arfigure.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by kolipass on 08.01.16.
 */
public class NetClient {
    public static final String API_URL = "https://api.projectoxford.ai";

    public HttpApi serverMethods;

    public NetClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .client(client)
                .build();

        serverMethods = restAdapter.create(HttpApi.class);
    }

    private Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(RecognizeResponse.class,
                new JsonDeserializer<RecognizeResponse>() {
                    public RecognizeResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        RecognizeResponse response = null;
                        if (json instanceof JsonObject) {
                            JsonObject jsonObject = (JsonObject) json;
                            response = new RecognizeResponse();
                            if (jsonObject.has(RecognizeResponse.FACE_RECTANGLE)) {
                                Type type = new TypeToken<Map<RecognizeResponse.FaceRectangle, Integer>>() {
                                }.getType();
                                Map<RecognizeResponse.FaceRectangle, Integer> faceRectarnge =
                                        new Gson()
                                                .fromJson(jsonObject.get(RecognizeResponse.FACE_RECTANGLE), type);
                                response.setFaceRectangle(faceRectarnge);
                            }
                            if (jsonObject.has(RecognizeResponse.SCORES)) {
                                Type type = new TypeToken<Map<RecognizeResponse.Scores, Double>>() {
                                }.getType();
                                Map<RecognizeResponse.Scores, Double> faceRectarnge =
                                        new Gson()
                                                .fromJson(jsonObject.get(RecognizeResponse.SCORES), type);
                                response.setScores(faceRectarnge);
                            }


                        }
                        return response;
                    }
                });
        return gsonBuilder.create();
    }
}
