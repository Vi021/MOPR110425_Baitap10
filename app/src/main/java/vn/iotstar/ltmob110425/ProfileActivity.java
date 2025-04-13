package vn.iotstar.ltmob110425;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private ImageView img_logout, img_pfp;
    private Button btn_uploadVideo;
    private ProgressBar uploadProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // initialize Cloudinary
        if (!MainActivity.isInitialized) {
            MediaManager.init(this, Refs.CLOUDINARY_CONFIGS);
            MainActivity.isInitialized = true;
        }

        img_logout = findViewById(R.id.img_logout);
        img_logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        img_pfp = findViewById(R.id.img_pfp);

        btn_uploadVideo = findViewById(R.id.btn_uploadVideo);
        btn_uploadVideo.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                checkPermissionsAndPickVideo();
            } else {
                Toast toast = Toast.makeText(this, "Please sign in first!", Toast.LENGTH_SHORT);
                toast.show();
                new Handler().postDelayed(toast::cancel, 1200);
            }
        });

        uploadProgressBar = findViewById(R.id.uploadProgressBar);
    }

    private void checkPermissionsAndPickVideo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pickVideo();
        } else if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{READ_EXTERNAL_STORAGE}, 200);
        } else {
            pickVideo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200 && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickVideo();
        } else {
            Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
        }
    }

    private void pickVideo() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(Intent.createChooser(intent, "Select Video"), 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            Uri videoUri = data.getData();
            handleVideoUri(videoUri);
        }
    }

    private void handleVideoUri(Uri videoUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(videoUri);
            File tempFile = File.createTempFile("upload_", ".mp4", getCacheDir());
            FileOutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();

            uploadVideoToCloudinary(tempFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(this, "Failed to process video", Toast.LENGTH_SHORT);
            toast.show();
            new Handler().postDelayed(toast::cancel, 1200);
        }
    }

    private void uploadVideoToCloudinary(String filePath) {
        MediaManager.get().upload(filePath)
                .option("resource_type", "video")
                .option("upload_preset", "mopr110425_unsigned")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d("Cloudinary", "Upload started...");
                        runOnUiThread(() -> {
                            uploadProgressBar.setVisibility(View.VISIBLE);
                            uploadProgressBar.setProgress(0);
                        });
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        int percent = Math.round((100f * bytes) / totalBytes);
                        Log.d("Cloudinary", "Uploading: " + percent + "%");
                        runOnUiThread(() -> uploadProgressBar.setProgress(percent));
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String videoUrl = (String) resultData.get("secure_url");
                        Log.d("Cloudinary", "Video uploaded: " + videoUrl);
                        runOnUiThread(() -> uploadProgressBar.setVisibility(View.GONE));
                        Toast toast = Toast.makeText(ProfileActivity.this, "Uploaded Successfully!", Toast.LENGTH_SHORT);
                        toast.show();
                        new Handler().postDelayed(toast::cancel, 1200);

                        // store in Firestore
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            LayoutInflater inflater = LayoutInflater.from(ProfileActivity.this);
                            View dialogView = inflater.inflate(R.layout.dialog_videometadata, null);

                            EditText eTxt_title = dialogView.findViewById(R.id.eTxt_title);
                            EditText eTxt_desc = dialogView.findViewById(R.id.eTxt_desc);

                            new AlertDialog.Builder(ProfileActivity.this)
                                    .setTitle("Video Details")
                                    .setView(dialogView)
                                    .setPositiveButton("Save", (dialog, which) -> {
                                        String title = eTxt_title.getText().toString().trim();
                                        String desc = eTxt_desc.getText().toString().trim();
                                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                        VideoShortModel videoData = new VideoShortModel(title, desc, videoUrl, userId);

                                        DatabaseReference dbRef = FirebaseDatabase.getInstance(Refs.VIDEO_SHORTS_FIREBASE_URL).getReference(Refs.VIDEO_SHORTS_URL);
                                        String uploadId = dbRef.push().getKey();

                                        if (uploadId != null) {
                                            dbRef.child(uploadId).setValue(videoData)
                                                    .addOnSuccessListener(unused -> {
                                                        Log.d("RealtimeDB", "Metadata saved: " + uploadId);
                                                        Toast t = Toast.makeText(ProfileActivity.this, "Metadata saved to Realtime DB!", Toast.LENGTH_SHORT);
                                                        t.show();
                                                        new Handler().postDelayed(t::cancel, 1200);
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Log.e("RealtimeDB", "Error saving", e);
                                                        Toast t = Toast.makeText(ProfileActivity.this, "Failed to save metadata", Toast.LENGTH_SHORT);
                                                        t.show();
                                                        new Handler().postDelayed(t::cancel, 1200);
                                                    });
                                        }
                                    })
                                    .setNegativeButton("Cancel", null)
                                    .show();
                        }
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.e("Cloudinary", "Upload failed: " + error.getDescription());
                        Toast toast = Toast.makeText(ProfileActivity.this, "Upload Failed", Toast.LENGTH_SHORT);
                        toast.show();
                        new Handler().postDelayed(toast::cancel, 1200);
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        Log.e("Cloudinary", "Upload rescheduled: " + error.getDescription());
                    }
                }).dispatch();
    }
}