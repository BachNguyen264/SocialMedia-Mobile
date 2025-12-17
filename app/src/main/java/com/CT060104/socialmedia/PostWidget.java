package com.CT060104.socialmedia;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Random;

import Controller.GenerateTimeline;
import Controller.ReadPostComments;
import Controller.ReadPostLikes;
import Controller.ReadUser;
import Model.Database;
import Model.Post;
import Model.User;

public class PostWidget extends AppWidgetProvider {

    public static final String ACTION_REFRESH = "com.CT060104.socialmedia.ACTION_REFRESH";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_REFRESH.equals(intent.getAction())) {
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context, PostWidget.class);
            int[] ids = manager.getAppWidgetIds(thisWidget);
            for (int id : ids) {
                updateAppWidget(context, manager, id);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_post);

        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE);
            String email = sharedPreferences.getString("Email", "");
            String password = sharedPreferences.getString("Password", "");

            if (email.isEmpty() || password.isEmpty()) {
                views.setTextViewText(R.id.widget_author, "Chưa đăng nhập");
                views.setTextViewText(R.id.widget_content, "Hãy đăng nhập để xem bài viết.");
                views.setTextViewText(R.id.widget_date, "");
                views.setTextViewText(R.id.widget_likes, "");
                views.setTextViewText(R.id.widget_comments, "");
            } else {
                Database db = new Database();
                User user = new ReadUser(email, password, db).getUser();
                ArrayList<Post> posts = new GenerateTimeline(user, db).getPosts();

                if (posts.isEmpty()) {
                    views.setTextViewText(R.id.widget_author, "Không có bài viết");
                    views.setTextViewText(R.id.widget_content, "Bạn chưa follow ai hoặc chưa có bài viết nào.");
                    views.setTextViewText(R.id.widget_date, "");
                    views.setTextViewText(R.id.widget_likes, "");
                    views.setTextViewText(R.id.widget_comments, "");
                } else {
                    // Lấy ID bài hiện tại (lần trước)
                    SharedPreferences widgetPrefs = context.getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE);
                    int lastPostId = widgetPrefs.getInt("LastPostId",0);

                    // Random bài mới khác bài cũ
                    Random random = new Random();
                    Post post;
                    int tries = 0;
                    do {
                        post = posts.get(random.nextInt(posts.size()));
                        tries++;
                    } while (post.getID() == lastPostId && tries < 5); // tránh lặp vô hạn nếu chỉ có 1 bài

                    // Lưu lại ID bài mới để lần sau so sánh
                    widgetPrefs.edit().putInt("LastPostId", post.getID()).apply();
                    int likesCount = new ReadPostLikes(post, db).getLikesCount();
                    int commentsCount = new ReadPostComments(post, db).getCommentsCounter();

                    String likesText = (likesCount < 2) ? "❤️ " + likesCount + " Like" : "❤️ " + likesCount + " Likes";
                    String commentsText = (commentsCount < 2)
                            ? "💬 " + commentsCount + " Comment"
                            : "💬 " + commentsCount + " Comments";
                    views.setTextViewText(R.id.widget_author, post.getUser().getName());
                    views.setTextViewText(R.id.widget_content, post.getContent());
                    views.setTextViewText(R.id.widget_date, post.getDateToString()); // ví dụ: 2025-10-08 23:10
                    views.setTextViewText(R.id.widget_likes, likesText);
                    views.setTextViewText(R.id.widget_comments, commentsText);
                }
            }

            // Intent cho nút "Đăng bài"
            Intent openAppIntent = new Intent(context, email.isEmpty() ? LoginActivity.class : HomeActivity.class);
            PendingIntent openAppPendingIntent = PendingIntent.getActivity(
                    context, 0, openAppIntent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
            );
            views.setOnClickPendingIntent(R.id.widget_post_button, openAppPendingIntent);

            // Intent cho nút "Làm mới"
            Intent refreshIntent = new Intent(context, PostWidget.class);
            refreshIntent.setAction(ACTION_REFRESH);
            PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(
                    context, 0, refreshIntent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
            );
            views.setOnClickPendingIntent(R.id.widget_refresh_button, refreshPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        } catch (Exception e) {
            e.printStackTrace();
            views.setTextViewText(R.id.widget_author, "Lỗi");
            views.setTextViewText(R.id.widget_content, "Không thể tải dữ liệu.");
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

}

