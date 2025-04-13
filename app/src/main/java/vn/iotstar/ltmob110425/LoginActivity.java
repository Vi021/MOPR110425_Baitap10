package vn.iotstar.ltmob110425;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText eTxt_email, eTxt_password;
    private Button btn_login;
    private TextView txt_register;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();

        eTxt_email = findViewById(R.id.eTxt_email);
        eTxt_password = findViewById(R.id.eTxt_password);
        btn_login = findViewById(R.id.btn_register);
        txt_register = findViewById(R.id.txt_login);

        btn_login.setOnClickListener(v -> {
            String email = eTxt_email.getText().toString().trim();
            String password = eTxt_password.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast toast = Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT);
                toast.show();
                new Handler().postDelayed(toast::cancel, 1200);
                return;
            }

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(this, ProfileActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        txt_register.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
}