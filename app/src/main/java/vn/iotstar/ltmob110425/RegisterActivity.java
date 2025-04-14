package vn.iotstar.ltmob110425;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText eTxt_email, eTxt_password;
    private Button btn_register;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();

        eTxt_email = findViewById(R.id.eTxt_email);
        eTxt_password = findViewById(R.id.eTxt_password);
        btn_register = findViewById(R.id.btn_register);

        btn_register.setOnClickListener(v -> {
            String email = eTxt_email.getText().toString().trim();
            String password = eTxt_password.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast toast = Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT);
                toast.show();
                new Handler().postDelayed(toast::cancel, 1200);
                return;
            }

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                AccountModel account = new AccountModel(user.getEmail(), "", 0); // default

                                DatabaseReference userRef = FirebaseDatabase.getInstance(Refs.VIDEO_SHORTS_FIREBASE_URL).getReference(Refs.USERS_URL);

                                userRef.child(user.getUid()).setValue(account)
                                        .addOnSuccessListener(unused -> {
                                            Intent intent = new Intent(this, ProfileActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("RealtimeDB", "Error saving", e);
                                            Toast t = Toast.makeText(RegisterActivity.this, "Failed to save to RealtimeDB", Toast.LENGTH_SHORT);
                                            t.show();
                                            new Handler().postDelayed(t::cancel, 1200);
                                        });
                            }
                        } else {
                            Toast toast = Toast.makeText(this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT);
                            toast.show();
                            new Handler().postDelayed(toast::cancel, 1200);
                        }
                    });
        });
    }
}