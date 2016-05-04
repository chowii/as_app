package au.com.ahbeard.sleepsense.services;

import android.content.Context;
import android.os.Build;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by neal on 2/05/2016.
 */
public class RemoteSleepDataService {

    private static RemoteSleepDataService sRemoteSleepDataService;

    private final Gson mGson;
    private final RemoteSleepDataApi mRemoteSleepDataApi;
    private Context mContext;

    public static void initialize(Context context) {
        sRemoteSleepDataService = new RemoteSleepDataService(context);
    }

    public static RemoteSleepDataService instance() {
        return sRemoteSleepDataService;
    }

    private RemoteSleepDataService(Context context) {

        mContext = context;

        mGson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();


        Interceptor loggingInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                // Log.d("ContentService", chain.request().urlString());

                Response response = chain.proceed(chain.request());

                // Log.d("ContentService", "response: " + response);

                return response;
            }
        };

        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(loggingInterceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(mGson)).baseUrl(
                        "http://10.1.0.43:8080").build();

        mRemoteSleepDataApi = retrofit.create(RemoteSleepDataApi.class);

    }

    public Observable<String> saveSleepData(String id,byte[] data) {

        return mRemoteSleepDataApi.postSessionData(
                Build.SERIAL,
                id,
                RequestBody.create(MediaType.parse("application-x/binary"), data));
    }

    public interface RemoteSleepDataApi {

        @POST("/gcs/${BUCKET}/${SESSION}")
        Observable<String> postSessionData(@Path("BUCKET") String bucket,
                                           @Path("SESSION") String session,
                                           @Body RequestBody sessionData);


    }

}
