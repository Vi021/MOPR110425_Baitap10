package vn.iotstar.ltmob110425.VideoShortAPI;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.iotstar.ltmob110425.R;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private Context context;
    private List<VideoModel> videoList;
    private boolean isFav = false;
    private Random rnd = new Random();

    public VideoAdapter(Context context, List<VideoModel> videoList) {
        this.context = context;
        this.videoList = videoList;
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        private VideoView videoView;
        private ProgressBar progressBar;
        private TextView txt_videoTitle, txt_videoDesc, txt_likeCount, txt_error;
        private ImageView img_favorite;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);
            progressBar = itemView.findViewById(R.id.progressBar);
            txt_videoTitle = itemView.findViewById(R.id.txt_videoTitle);
            txt_videoDesc = itemView.findViewById(R.id.txt_videoDesc);
            img_favorite = itemView.findViewById(R.id.img_favorite);
            txt_likeCount = itemView.findViewById(R.id.txt_likeCount);
            txt_error = itemView.findViewById(R.id.txt_error);
        }
    }

    @NonNull
    @Override
    public VideoAdapter.VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_singlevideoitem, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.VideoViewHolder holder, int position) {
        VideoModel videoModel = videoList.get(position);

        holder.txt_videoTitle.setText(videoModel.getTitle());
        holder.txt_videoDesc.setText(videoModel.getDescription());

        int rndNum = rnd.nextInt(1000);
        holder.txt_likeCount.setText(String.valueOf(rndNum));

        holder.videoView.setVideoURI(Uri.parse(videoModel.getUrl()));
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
        holder.videoView.setOnCompletionListener(mp -> {
            holder.videoView.setMediaController(new MediaController(context));
            holder.videoView.requestFocus();
            mp.start();
        });

        holder.img_favorite.setOnClickListener(v -> {
            if (isFav) {
                holder.img_favorite.setImageResource(R.drawable.ic_favorite_filled_ff3352);
                holder.txt_likeCount.setText(String.valueOf(rndNum+1));
                isFav = false;
            } else {
                holder.img_favorite.setImageResource(R.drawable.ic_favorite);
                holder.txt_likeCount.setText(String.valueOf(rndNum));
                isFav = true;
            }
        });

        holder.videoView.setOnErrorListener((mp, what, extra) -> {
            holder.progressBar.setVisibility(View.GONE);
            holder.videoView.setVisibility(View.GONE);
            holder.txt_error.setVisibility(View.VISIBLE);
            Log.e("VideoError", "Cannot play video: " + videoModel.getUrl());
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return videoList != null ? videoList.size() : 0;
    }
}
