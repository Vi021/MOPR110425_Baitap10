package vn.iotstar.ltmob110425;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private VideoShortAdapter VS_adapter;
    private ImageView img_myAccount;
    public static boolean isInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewPager = findViewById(R.id.viewPager);
        viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);

        DatabaseReference videoShortRef = FirebaseDatabase
                .getInstance(Refs.VIDEO_SHORTS_FIREBASE_URL)
                .getReference(Refs.VIDEO_SHORTS_URL);    // Firebase Realtime Database
        FirebaseRecyclerOptions<VideoShortModel> options = new FirebaseRecyclerOptions.Builder<VideoShortModel>()
                .setQuery(videoShortRef, VideoShortModel.class)
                .build();
        VS_adapter = new VideoShortAdapter(options);
        viewPager.setAdapter(VS_adapter);

        img_myAccount = findViewById(R.id.img_myAccount);
        img_myAccount.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        VS_adapter.startListening();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        VS_adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        VS_adapter.stopListening();
    }


}