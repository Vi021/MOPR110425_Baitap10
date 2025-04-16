package vn.iotstar.ltmob110425.VideoShortFirebase;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.iotstar.ltmob110425.R;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private VideoShortAdapter VS_adapter;
    private CircleImageView img_myAccount;
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
        loadMyAccountPFP();

        img_myAccount.setOnClickListener(v -> {
            Intent intent;
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                intent = new Intent(MainActivity.this, LoginActivity.class);
            } else {
                intent = new Intent(MainActivity.this, ProfileActivity.class);
            }
            startActivity(intent);
        });
    }

    private void loadMyAccountPFP() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase
                    .getInstance(Refs.VIDEO_SHORTS_FIREBASE_URL)
                    .getReference(Refs.USERS_URL).child(currentUser.getUid());
            userRef.child("pfpUrl").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String pfpUrl = task.getResult().getValue(String.class);
                    if (pfpUrl != null && !pfpUrl.isEmpty()) {
                        Glide.with(this)
                                .load(pfpUrl)
                                .into(img_myAccount);
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadMyAccountPFP();
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