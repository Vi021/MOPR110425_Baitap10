package vn.iotstar.ltmob110425;

import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class VideoShortAdapter extends FirebaseRecyclerAdapter<VideoShortModel, VideoShortAdapter.VideoShortViewHolder> {
    private boolean isFavorite = false;

    public VideoShortAdapter(@NonNull FirebaseRecyclerOptions<VideoShortModel> options) {
        super(options);
    }

    public class VideoShortViewHolder extends RecyclerView.ViewHolder {
        private VideoView videoView;
        private ProgressBar progressBar;
        private TextView txt_videoTitle;
        private TextView txt_videoDesc;
        private ImageView img_account;
        private ImageView img_favorite;
        private ImageView img_share;
        private TextView txt_error;

        public VideoShortViewHolder(View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);
            progressBar = itemView.findViewById(R.id.progressBar);
            txt_videoTitle = itemView.findViewById(R.id.txt_videoTitle);
            txt_videoDesc = itemView.findViewById(R.id.txt_videoDesc);
            img_account = itemView.findViewById(R.id.img_account);
            img_favorite = itemView.findViewById(R.id.img_favorite);
            img_share = itemView.findViewById(R.id.img_share);
            txt_error = itemView.findViewById(R.id.txt_error);
        }
    }

    @Override
    protected void onBindViewHolder(VideoShortViewHolder holder, int position, VideoShortModel model) {
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
                // Video is wider than the view, scale by width
                scaleY = videoRatio / viewRatio;
            } else {
                // Video is taller than the view, scale by height
                scaleX = viewRatio / videoRatio;
            }
            holder.videoView.setScaleX(1f / scaleX);
            holder.videoView.setScaleY(1f / scaleY);
        });
        holder.videoView.setOnCompletionListener(MediaPlayer::start);
        holder.img_account.setOnClickListener(v -> {
            // account
        });
        holder.img_favorite.setOnClickListener(v -> {
            if (isFavorite) {
                holder.img_favorite.setImageResource(R.drawable.ic_favorite);
                isFavorite = false;
            } else {
                holder.img_favorite.setImageResource(R.drawable.ic_favorite_filled_ff3352);
                isFavorite = true;
            }
        });
        holder.img_share.setOnClickListener(v -> {
            // share
        });

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
