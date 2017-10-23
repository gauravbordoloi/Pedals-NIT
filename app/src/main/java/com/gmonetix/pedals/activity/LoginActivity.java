package com.gmonetix.pedals.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.gmonetix.pedals.App;
import com.gmonetix.pedals.R;
import com.gmonetix.pedals.dialog.CustomDialog;
import com.gmonetix.pedals.model.User;
import com.gmonetix.pedals.util.SharedPref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Gmonetix
 */

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;

    private String email, password;
    private SharedPref sharedPref;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPref = App.getSharedPref();
        if (sharedPref.isLogin()) {
            Intent intent = new Intent(LoginActivity.this, Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();

    }

    public void showTermsConditions(View view) {
        MaterialDialog dialog = CustomDialog.termsAndConditions(this);
        dialog.show();
    }

    public void showPrivacyPolicy(View view) {
        MaterialDialog dialog = CustomDialog.privacyPolicy(this);
        dialog.show();
    }

    public void login(View view) {
        if (validate()) {
            final IOSDialog pDialog = new IOSDialog.Builder(this)
                    .setTitle("Logging In")
                    .setCancelable(false)
                    .setTitleColorRes(R.color.gray)
                    .build();
            pDialog.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                App.getDbReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        User user = null;
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            user = ds.getValue(User.class);
                                            if (user.email.equals(email))
                                                break;
                                        }
                                        if (user != null) {
                                            sharedPref.setName(user.name);
                                            sharedPref.setEmail(email);
                                            sharedPref.setPhone(user.phone);
                                            sharedPref.setPassword(password);
                                            sharedPref.setImageLink(user.image);
                                            sharedPref.setNitReg(user.nit_registration);
                                            sharedPref.setLogin(true);

                                            pDialog.dismiss();

                                            Intent intent = new Intent(LoginActivity.this, Home.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(LoginActivity.this, "some error occurred", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e("TAG", "" + databaseError.getMessage());
                                        pDialog.dismiss();
                                        CustomDialog.showTextDialog(LoginActivity.this, "Alert", " Error - " + databaseError.getMessage());
                                    }
                                });
                            } else {
                                pDialog.dismiss();
                                CustomDialog.showTextDialog(LoginActivity.this, "Alert", " Error - " + task.getException().getMessage());
                                Log.e("TAG", "signInWithEmail:failure", task.getException());
                            }
                        }
                    });
        }
    }

    public void registerHere(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public void forgotPassword(View view) {
        new MaterialDialog.Builder(this)
                .title("Forgot Password?")
                .content("Enter your registered email . Reset password link will be sent to your email")
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                .negativeText("CANCEL")
                .input("registered email", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(final MaterialDialog dialog, CharSequence input) {
                        final String emailAddress = input.toString().trim();
                        if (emailAddress.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches() || emailAddress.length()<8 || emailAddress.length() >50) {
                            Toast.makeText(LoginActivity.this, "enter a valid email", Toast.LENGTH_SHORT).show();
                        } else {
                            final IOSDialog pDialog = new IOSDialog.Builder(LoginActivity.this)
                                    .setTitle("Sending reset password link")
                                    .setCancelable(false)
                                    .setTitleColorRes(R.color.gray)
                                    .build();
                            pDialog.show();
                            mAuth.sendPasswordResetEmail(emailAddress)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(LoginActivity.this, "Reset link successfully sent to your registered email", Toast.LENGTH_LONG).show();
                                                pDialog.dismiss();
                                                dialog.dismiss();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pDialog.dismiss();
                                            dialog.dismiss();
                                        }
                                    });
                        }
                    }
                }).show();
    }

    public void contactUs(View view) {
        CustomDialog.showContactUsDialog(this,sharedPref);
    }

    private boolean validate() {
        boolean valid = true;
        email = etEmail.getText().toString().trim();
        password = etPassword.getText().toString().trim();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.length()<8 || email.length() >50) {
            etEmail.setError("enter valid email");
            valid = false;
        } else {
            etEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 100) {
            etPassword.setError("enter strong password");
            valid = false;
        } else {
            etPassword.setError(null);
        }

        return valid;
    }

}
