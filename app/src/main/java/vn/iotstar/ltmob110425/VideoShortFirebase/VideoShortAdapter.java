package vn.iotstar.ltmob110425.VideoShortFirebase;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.iotstar.ltmob110425.R;

public class VideoShortAdapter extends FirebaseRecyclerAdapter<VideoShortModel, VideoShortAdapter.VideoShortViewHolder> {
    private boolean isFavorite = false;

    public VideoShortAdapter(@NonNull FirebaseRecyclerOptions<VideoShortModel> options) {
        super(options);
    }

    public class VideoShortViewHolder extends RecyclerView.ViewHolder {
        private VideoView videoView;
        private ProgressBar progressBar;
        private TextView txt_accountEmail, txt_videoTitle, txt_videoDesc, txt_likeCount, txt_error;
        private CircleImageView img_account;
        private ImageView img_favorite, img_share;

        public VideoShortViewHolder(View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);
            progressBar = itemView.findViewById(R.id.progressBar);
            txt_accountEmail = itemView.findViewById(R.id.txt_accountEmail);
            txt_videoTitle = itemView.findViewById(R.id.txt_videoTitle);
            txt_videoDesc = itemView.findViewById(R.id.txt_videoDesc);
            img_account = itemView.findViewById(R.id.img_account);
            img_favorite = itemView.findViewById(R.id.img_favorite);
            txt_likeCount = itemView.findViewById(R.id.txt_likeCount);
            img_share = itemView.findViewById(R.id.img_share);
            txt_error = itemView.findViewById(R.id.txt_error);
        }
    }

    @Override
    protected void onBindViewHolder(VideoShortViewHolder holder, int position, VideoShortModel model) {
        DatabaseReference userRef = FirebaseDatabase.getInstance(Refs.VIDEO_SHORTS_FIREBASE_URL)
                .getReference(Refs.USERS_URL);
        if (model.getUserId() == null || model.getUserId().isEmpty()) {
            holder.txt_accountEmail.setText("Unknown User");
            holder.img_account.setImageResource(R.drawable.ic_account);
        } else {
            userRef.child(model.getUserId())
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            AccountModel account = task.getResult().getValue(AccountModel.class);
                            if (account != null) {
                                holder.txt_accountEmail.setText(account.getEmail());
                                if (account.getPfpUrl() != null && !account.getPfpUrl().isEmpty()) {
                                    Glide.with(holder.img_account.getContext())
                                            .load(account.getPfpUrl())
                                            .placeholder(R.drawable.ic_account)
                                            .into(holder.img_account);
                                }
                            } else {
                                Log.e("FirebaseError", "Failed to get user data: " + task.getException().getMessage());
                            }
                        }});
        }

        holder.txt_videoTitle.setText(model.getTitle());
        holder.txt_videoDesc.setText(model.getDesc());
        holder.videoView.setVideoURI(Uri.parse(model.getUrl()));   //holder.videoView.setVideoPath(model.getVideoUrl());   //?
        holder.videoView.setOnPreparedListener(mp -> {
            holder.progressBar.setVisibility(View.GONE);
            mp.start();
            float videoRatio = (float)mp.getVideoWidth() / (float)mp.getVideoHeight();
            float viewRatio = (float)holder.videoView.getWidth() / (float)holder.videoView.getHeight();
            float scaleX = 1f;
            float scaleY = 1f;
            if (videoRatio > viewRatio) {
                // video is wider than the view, scale by width
                scaleY = videoRatio / viewRatio;
            } else {
                // video is taller than the view, scale by height
                scaleX = viewRatio / videoRatio;
            }
            holder.videoView.setScaleX(1f / scaleX);
            holder.videoView.setScaleY(1f / scaleY);
        });
        holder.videoView.setOnCompletionListener(MediaPlayer::start);
        holder.img_account.setOnClickListener(v -> {});
        holder.txt_likeCount.setText(String.valueOf(model.getLikeCount()));
        holder.img_favorite.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast toast = Toast.makeText(holder.img_favorite.getContext(), "Please sign in first!", Toast.LENGTH_SHORT);
                toast.show();
                new Handler().postDelayed(toast::cancel, 1200);
                return;
            }
            if (isFavorite) {
                holder.img_favorite.setImageResource(R.drawable.ic_favorite_filled_ff3352);
                holder.txt_likeCount.setText(String.valueOf(model.getLikeCount()+1));
                isFavorite = false;
            } else {
                holder.img_favorite.setImageResource(R.drawable.ic_favorite);
                holder.txt_likeCount.setText(String.valueOf(model.getLikeCount()));
                isFavorite = true;
            }
        });
        holder.img_share.setOnClickListener(v -> {});
        holder.videoView.setOnErrorListener((mp, what, extra) -> {
            holder.progressBar.setVisibility(View.GONE);
            holder.videoView.setVisibility(View.GONE);  // hide broken video
            holder.txt_error.setVisibility(View.VISIBLE);
            Log.e("VideoError", "Cannot play video: " + model.getUrl());
            return true; // consume the error
        });
    }

    @NonNull
    @Override
    public VideoShortViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VideoShortViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_singlevideoitem, parent, false));
    }
}
