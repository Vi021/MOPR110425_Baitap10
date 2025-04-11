package vn.iotstar.ltmob110425;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Objects;

import vn.iotstar.ltmob110425.databinding.ActivityMain2Binding;

public class MainActivity2 extends AppCompatActivity {
    private ActivityMain2Binding MA2_binding;

    @SuppressLint({"SetJavaScriptEnabled", "WebViewApiAvailability"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        MA2_binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(MA2_binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(MA2_binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        MA2_binding.webView.getSettings().setLoadWithOverviewMode(true);
        MA2_binding.webView.getSettings().setUseWideViewPort(true);
        MA2_binding.webView.getSettings().setJavaScriptEnabled(true);
        MA2_binding.webView.setWebViewClient(new WebViewClient());
        MA2_binding.webView.getSettings().setSupportZoom(true); //MA2_binding.webView.getSettings().setBuiltInZoomControls(true);
        MA2_binding.webView.getSettings().setDomStorageEnabled(true);
        MA2_binding.webView.getSettings().setDatabaseEnabled(true);
        MA2_binding.webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        MA2_binding.webView.setWebChromeClient(new WebChromeClient());
        MA2_binding.webView.loadUrl("http://iotstar.vn/");
    }
}