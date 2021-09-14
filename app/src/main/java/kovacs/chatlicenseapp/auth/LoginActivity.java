package kovacs.chatlicenseapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import kovacs.chatlicenseapp.MainActivity;
import kovacs.chatlicenseapp.R;
import kovacs.chatlicenseapp.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";
    private ActivityLoginBinding binding;

    private TextInputEditText mEmail;
    private TextInputEditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        mEmail = findViewById(R.id.input_email_address);
        mPassword = findViewById(R.id.input_password);

        initLoginButtonClick();
    }

    private void initLoginButtonClick() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Introduceți adresa dvs. de email");
                    mEmail.requestFocus();
                } else if (TextUtils.isEmpty(password) && password.length() < 6) {
                    mPassword.setError("Acest câmp este obligatoriu!");
                    mPassword.requestFocus();
                } else {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "signInWithEmail:success");
                                        Toast.makeText(LoginActivity.this, "Bun venit în contul dvs.", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    } else {
                                        Log.d(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Eroare în timpul autentificării", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

    }

    public void back_to_auth_select_activity(View view) {
        super.onBackPressed();
        finish();
    }

    public void call_register_activity(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        finish();
    }

    public void call_forgot_password(View view) {
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        finish();
    }
}