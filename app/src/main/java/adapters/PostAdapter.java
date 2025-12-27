package adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.CT060104.socialmedia.R;

import java.util.ArrayList;
import java.util.List;

import models.post.PostResponse;
import repository.LikeRepository;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<PostResponse> posts = new ArrayList<>();

    public void setPosts(List<PostResponse> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        PostResponse post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        private final TextView authorText;
        private final TextView contentText;
        private final TextView dateText;
        private final TextView likesText;
        private final TextView commentsText;
        private ImageButton likeButton;
        private LikeRepository likeRepository;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            authorText = itemView.findViewById(R.id.text_author);
            contentText = itemView.findViewById(R.id.text_content);
            dateText = itemView.findViewById(R.id.text_date);
            likesText = itemView.findViewById(R.id.text_likes);
            commentsText = itemView.findViewById(R.id.text_comments);
            likeButton = itemView.findViewById(R.id.post_like);
            likeRepository = new LikeRepository(itemView.getContext());
        }

        public void bind(PostResponse post) {
            if (post.getUser() != null) {
                authorText.setText(post.getUser().getFirstName() + " " + post.getUser().getLastName());
            } else {
                authorText.setText("Unknown User");
            }

            contentText.setText(post.getContent());

            // Xử lý ngày tháng đơn giản (có thể cải thiện bằng DateFormatter)
            dateText.setText(post.getCreatedAt().split("T")[0]);
            if(post.isUserHasLiked()){
                likeButton.setImageResource(R.drawable.liked);
            }
            else{
                likeButton.setImageResource(R.drawable.like);
            }

            likesText.setText(post.getLikeCount() + " Likes");
            commentsText.setText(post.getCommentCount() + " Comments");
        }
    }
}