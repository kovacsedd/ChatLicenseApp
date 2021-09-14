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
import com.google.firebase.auth.FirebaseUser;

import kovacs.chatlicenseapp.R;
import kovacs.chatlicenseapp.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private ActivityRegisterBinding binding;

    private TextInputEditText mEmail, mPassword, mConfirmPassword;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        mEmail = findViewById(R.id.input_email_address);
        mPassword = findViewById(R.id.input_password);
        mConfirmPassword = findViewById(R.id.input_confirm_password);

        initRegisterButtonClick();
    }

    private void initRegisterButtonClick() {
        firebaseAuth = FirebaseAuth.getInstance();

        binding.createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String confirmPassword = mConfirmPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Introduceți adresă de mail !");
                    mEmail.requestFocus();
                } else if (TextUtils.isEmpty(password) || password.length() < 6) {
                    mPassword.setError("Introduceti o parola mai mare de 6 caractere");
                    mPassword.requestFocus();
                } else if (TextUtils.isEmpty(confirmPassword) || !password.equals(confirmPassword)) {
                    mConfirmPassword.setError("Confirmați parola !");
                    mConfirmPassword.requestFocus();
                } else {
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Create user
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser firebaseUser = task.getResult().getUser();
                                Toast.makeText(RegisterActivity.this, "Datele au fost salvate", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, SetProfileInfoActivity.class));
                            } else {
                                Log.d(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegisterActivity.this, "Eroare în timpul procesării datelor, reîncearcă mai târziu!", Toast.LENGTH_SHORT).show();
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

    public void call_login_activity(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }
}