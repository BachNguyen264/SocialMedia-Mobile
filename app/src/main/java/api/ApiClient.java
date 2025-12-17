package api;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import utils.AuthInterceptor;

public class ApiClient {
    private static final String TAG = "ApiClient";
    private static final String BASE_URL = "http://10.0.2.2:3000"; // For Android emulator

    private static Retrofit retrofit = null;
    private static SocialMediaApi apiService = null;

    public static synchronized SocialMediaApi getClient(Context context) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getOkHttpClient(context))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(SocialMediaApi.class);
        }
        return apiService;
    }

    private static OkHttpClient getOkHttpClient(Context context) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);

        // Add AuthInterceptor for automatic token injection
        builder.addInterceptor(new AuthInterceptor(context));

        // Add logging interceptor for debug builds
        if (android.util.Log.isLoggable(TAG, android.util.Log.DEBUG)) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
            Log.d(TAG, "Debug logging enabled for network requests");
        }

        return builder.build();
    }

    public static void resetClient() {
        retrofit = null;
        apiService = null;
        Log.d(TAG, "API client reset");
    }
}