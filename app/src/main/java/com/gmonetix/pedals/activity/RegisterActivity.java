package com.gmonetix.pedals.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gmonetix.pedals.R;
import com.gmonetix.pedals.dialog.CustomDialog;
import com.gmonetix.pedals.dialog.RegisterDialog;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Gmonetix
 */

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.etPhone)
    EditText etPhone;

    private String name, email, phone, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

    }

    private boolean validate() {
        boolean valid = true;
        name = etName.getText().toString().trim();
        email = etEmail.getText().toString().trim();
        phone = etPhone.getText().toString().trim();
        password = etPassword.getText().toString().trim();

        if (name.isEmpty() || name.length() < 4 || name.length()>30) {
            etName.setError("enter valid name");
            valid = false;
        } else {
            etName.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.length()<8 || email.length() >50) {
            etEmail.setError("enter valid email");
            valid = false;
        } else {
            etEmail.setError(null);
        }

        if (phone.isEmpty() || phone.length() != 10 || !phone.matches("[0-9]*")) {
            etPhone.setError("enter valid phone number");
            valid = false;
        } else {
            etPhone.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 100) {
            etPassword.setError("enter strong password");
            valid = false;
        } else {
            etPassword.setError(null);
        }

        return valid;
    }

    public void register(View view) {
        if (validate()) {
            Bundle bundle = new Bundle();
            bundle.putString("name",name);
            bundle.putString("email",email);
            bundle.putString("phone",phone);
            bundle.putString("password",password);

            Intent intent = new Intent(this,RegisterDialog.class);
            intent.putExtra("bundle",bundle);
            startActivity(intent);
        }
    }

    public void showPrivacyPolicy(View view) {
        MaterialDialog dialog = CustomDialog.privacyPolicy(this);
        dialog.show();
    }

    public void showTermsConditions(View view) {
        MaterialDialog dialog = CustomDialog.termsAndConditions(this);
        dialog.show();
    }

    public void loginHere(View view) {
        startActivity(new Intent(this,LoginActivity.class));
    }


}
