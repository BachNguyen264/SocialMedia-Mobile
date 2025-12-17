package utils;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private static final String TAG = "AuthInterceptor";
    private final TokenManager tokenManager;

    public AuthInterceptor(Context context) {
        this.tokenManager = TokenManager.getInstance(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // Skip auth for login and register endpoints
        String path = originalRequest.url().encodedPath();
        if (path.contains("/api/auth/login") || path.contains("/api/auth/register")) {
            return chain.proceed(originalRequest);
        }

        // Get token and add to header if available
        String token = tokenManager.getToken();
        if (token != null && !token.isEmpty()) {
            Request authenticatedRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();

            Log.d(TAG, "Added Authorization header for: " + path);
            return chain.proceed(authenticatedRequest);
        }

        Log.d(TAG, "No token available, proceeding without Authorization header for: " + path);
        return chain.proceed(originalRequest);
    }
}