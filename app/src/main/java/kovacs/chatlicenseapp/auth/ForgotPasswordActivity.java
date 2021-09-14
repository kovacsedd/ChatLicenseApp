package kovacs.chatlicenseapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import kovacs.chatlicenseapp.R;
import kovacs.chatlicenseapp.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends AppCompatActivity {
    private ActivityForgotPasswordBinding binding;

    private FirebaseAuth firebaseAuth;
    private TextInputEditText mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password);

        firebaseAuth = FirebaseAuth.getInstance();

        mEmail = findViewById(R.id.reset_password_email);

        binding.resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Introduceți adresa dvs. de mail");
                    mEmail.requestFocus();
                } else {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPasswordActivity.this, "Vă rugăm să verificați adresa de email pentru resetarea parolei!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this, "Vă rugam să reincercați, a apărut o eroare!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

}