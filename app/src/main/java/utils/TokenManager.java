package utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class TokenManager {
    private static final String TAG = "TokenManager";
    private static final String PREFS_NAME = "secure_prefs";
    private static final String TOKEN_KEY = "auth_token";
    private static final String USER_ID_KEY = "user_id";
    private static final String EMAIL_KEY = "user_email";

    private static TokenManager instance;
    private final SharedPreferences encryptedPrefs;
    private final Context context;

    private TokenManager(Context context) {
        this.context = context.getApplicationContext();
        this.encryptedPrefs = createEncryptedSharedPreferences();
    }

    public static synchronized TokenManager getInstance(Context context) {
        if (instance == null) {
            instance = new TokenManager(context);
        }
        return instance;
    }

    private SharedPreferences createEncryptedSharedPreferences() {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            return EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "Error creating encrypted preferences", e);
            throw new RuntimeException("Failed to create secure storage", e);
        }
    }

    public void saveToken(String token) {
        try {
            encryptedPrefs.edit()
                    .putString(TOKEN_KEY, token)
                    .apply();
            Log.d(TAG, "Token saved successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error saving token", e);
            throw new RuntimeException("Failed to save token", e);
        }
    }

    public String getToken() {
        try {
            return encryptedPrefs.getString(TOKEN_KEY, null);
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving token", e);
            return null;
        }
    }

    public void saveUserInfo(int userId, String email) {
        try {
            encryptedPrefs.edit()
                    .putInt(USER_ID_KEY, userId)
                    .putString(EMAIL_KEY, email)
                    .apply();
            Log.d(TAG, "User info saved successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error saving user info", e);
        }
    }

    public int getUserId() {
        try {
            return encryptedPrefs.getInt(USER_ID_KEY, -1);
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving user ID", e);
            return -1;
        }
    }

    public String getUserEmail() {
        try {
            return encryptedPrefs.getString(EMAIL_KEY, null);
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving user email", e);
            return null;
        }
    }

    public void clearToken() {
        try {
            encryptedPrefs.edit()
                    .remove(TOKEN_KEY)
                    .remove(USER_ID_KEY)
                    .remove(EMAIL_KEY)
                    .apply();
            Log.d(TAG, "Token and user info cleared successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error clearing token", e);
        }
    }

    public boolean isTokenValid() {
        String token = getToken();
        return token != null && !token.isEmpty() && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        try {
            // JWT tokens have 3 parts separated by dots
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return true;
            }

            // Decode the payload (middle part)
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));

            // Parse JSON to get expiration time
            com.google.gson.Gson gson = new com.google.gson.Gson();
            JwtPayload jwtPayload = gson.fromJson(payload, JwtPayload.class);

            if (jwtPayload.exp != null) {
                long currentTime = System.currentTimeMillis() / 1000;
                return currentTime >= jwtPayload.exp;
            }

            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error checking token expiration", e);
            return true; // Assume expired if we can't parse
        }
    }

    private static class JwtPayload {
        Long exp;
        Long iat;
        String sub;
    }

    public boolean isLoggedIn() {
        return isTokenValid() && getUserId() != -1;
    }
}