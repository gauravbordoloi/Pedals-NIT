package com.gmonetix.pedals.dialog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.balysv.materialripple.MaterialRippleLayout;
import com.gmonetix.pedals.App;
import com.gmonetix.pedals.R;
import com.gmonetix.pedals.activity.Home;
import com.gmonetix.pedals.model.User;
import com.gmonetix.pedals.util.SharedPref;
import com.gmonetix.pedals.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.IOException;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Gmonetix
 */

public class RegisterDialog extends AppCompatActivity{

    @BindView(R.id.dialog_register_close) ImageView close;
    @BindView(R.id.dialog_register_image) ImageView profileImage;
    @BindView(R.id.etNITRegNo) EditText etNITRegNo;
    @BindView(R.id.dialog_register_btn) MaterialRippleLayout registerBtn;
    @BindView(R.id.dialog_register_chooseImage) ImageView chooseImage;

    private String name, email, phone, password, nit_reg;

    private int GALLERY_INTENT = 100;

    FirebaseStorage storage ;

    FirebaseAuth mAuth;

    ProgressDialog pDialog;

    private Bitmap bitmap;
    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_register);
        ButterKnife.bind(this);
        sharedPref= App.getSharedPref();

        //file uri exposure for api above 24 hack
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Bundle bundle = getIntent().getBundleExtra("bundle");
        name = bundle.getString("name");
        email = bundle.getString("email");
        phone = bundle.getString("phone");
        password = bundle.getString("password");

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterDialog.this.finish();
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    register();
                }
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

    }

    private void register() {
        StorageReference storageRef = storage.getReference().child(phone+".png");

        byte[] d = Util.getByteArray(bitmap,50);

        pDialog = new ProgressDialog(this);
        pDialog.setTitle("Uploading...");
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();

        UploadTask uploadTask = storageRef.putBytes(d);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                pDialog.dismiss();
                CustomDialog.showTextDialog(RegisterDialog.this,"Alert"," Error - " + exception.getMessage());
                Log.e("TAG",""+exception.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                final String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterDialog.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    User user = new User(name,email,phone,nit_reg,downloadUrl);
                                    App.getDbReference().child("users").child(phone).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            pDialog.dismiss();
                                            sharedPref.setName(name);
                                            sharedPref.setEmail(email);
                                            sharedPref.setPhone(phone);
                                            sharedPref.setPassword(password);
                                            sharedPref.setImageLink(downloadUrl);
                                            sharedPref.setNitReg(nit_reg);
                                            sharedPref.setLogin(true);

                                            Toast.makeText(RegisterDialog.this,"account created successfully !",Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(RegisterDialog.this,Home.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pDialog.dismiss();
                                            Log.e("TAG", "failure"+ e.getMessage());
                                        }
                                    });
                                } else {
                                    pDialog.dismiss();
                                    CustomDialog.showTextDialog(RegisterDialog.this,"Alert"," Error - " + task.getException().getMessage());
                                    // If sign in fails, display a message to the user.
                                    Log.e("TAG", "createUserWithEmail:failure", task.getException());
                                }
                            }
                        });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                int progress = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                pDialog.setProgress(progress);
            }
        });
    }

    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_INTENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                chooseImage.setVisibility(View.GONE);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                this.bitmap = bitmap;
                profileImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean validate() {
        boolean valid = true;
        nit_reg = etNITRegNo.getText().toString().trim();

        if (nit_reg.isEmpty() || nit_reg.length() < 8 || nit_reg.length()>9) {
            etNITRegNo.setError("eg - 16U10362");
            valid = false;
        } else {
            etNITRegNo.setError(null);
        }

        if (!hasImage(profileImage) || bitmap == null) {
            Toast.makeText(this,"Choose your profile pic",Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    private boolean hasImage(ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable !=null);
        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable) drawable).getBitmap() !=null;
        }
        return hasImage;
    }

}
