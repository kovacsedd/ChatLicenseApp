package kovacs.chatlicenseapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import kovacs.chatlicenseapp.R;

public class AuthSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_selection);
    }
 
    public void call_login_activity(View view) {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    public void call_register_activity(View view) {
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
    }
}