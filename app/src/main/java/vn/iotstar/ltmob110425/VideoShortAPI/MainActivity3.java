package vn.iotstar.ltmob110425.VideoShortAPI;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.iotstar.ltmob110425.R;
import vn.iotstar.ltmob110425.databinding.ActivityMain3Binding;

public class MainActivity3 extends AppCompatActivity {
    private ActivityMain3Binding binding;
    private VideoAdapter videoAdapter;
    private List<VideoModel> videoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMain3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getVideos();
    }

    private void getVideos() {
        APIService.service.getVideos().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<MessageVideoModel> call, Response<MessageVideoModel> response) {
                assert response.body() != null;
                videoList = response.body().getResult();

                videoAdapter = new VideoAdapter(MainActivity3.this, videoList);
                binding.viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
                binding.viewPager.setAdapter(new VideoAdapter(MainActivity3.this, videoList));
            }

            @Override
            public void onFailure(Call<MessageVideoModel> call, Throwable t) {
                Log.e("ltmob110425", "(Retrofit) Failed to get videos: " + t.getMessage());
            }
        });
    }
}