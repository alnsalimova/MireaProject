package ru.mirea.salimovaar.mireaproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
import ru.mirea.salimovaar.mireaproject.databinding.FirebaseBinding;

public class Firebase extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private FirebaseBinding binding;
    // START declare_auth
    private FirebaseAuth mAuth;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialization views
        binding = FirebaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // [START initialize_auth] Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        binding.signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = String.valueOf(binding.editTextEmail.getText());
                String password = String.valueOf(binding.editTextPassword.getText());
                signIn(email, password, v);
            }
        });
        binding.create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = String.valueOf(binding.editTextEmail.getText());
                String password = String.valueOf(binding.editTextPassword.getText());
                createAccount(email, password, v);
            }
        });
        binding.verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {sendEmailVerification();
            }
        });
        binding.signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {signOut();
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    private void updateUI(FirebaseUser user) {
        if (user != null) {

            binding.textView.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));

            binding.textViewUI.setText(getString(R.string.firebase_status_fmt, user.getUid()));
            binding.create.setVisibility(View.GONE);
            binding.editTextPassword.setVisibility(View.GONE);
            binding.signin.setVisibility(View.GONE);
            binding.textViewUI.setEnabled(!user.isEmailVerified());
            binding.verify.setVisibility(View.VISIBLE);
        } else {
            binding.textView.setText(R.string.signed_out);
            binding.textViewUI.setText(null);
            binding.create.setVisibility(View.VISIBLE);
            binding.editTextPassword.setVisibility(View.VISIBLE);
            binding.signin.setVisibility(View.VISIBLE);
        }
    }
    private void createAccount(String email, String password, View v) {
        Log.d(TAG, "createAccount:" + email);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            goSystem(v);
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Firebase.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
    private void signIn(String email, String password, View v) {
        Log.d(TAG, "signIn:" + email);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            goSystem(v);
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());

                            Toast.makeText(Firebase.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        if (!task.isSuccessful()) {

                            binding.textView.setText(R.string.auth_failed);
                        }
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private void sendEmailVerification() {
        binding.verify.setEnabled(false);
        final FirebaseUser user = mAuth.getCurrentUser();
        Objects.requireNonNull(user).sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override

                    public void onComplete(@NonNull Task<Void> task) {
                        binding.verify.setEnabled(true);
                        if (task.isSuccessful()) {
                            Toast.makeText(Firebase.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(Firebase.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    public void goSystem(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
