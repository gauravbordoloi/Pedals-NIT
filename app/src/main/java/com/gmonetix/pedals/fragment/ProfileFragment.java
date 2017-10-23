package com.gmonetix.pedals.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.gmonetix.pedals.App;
import com.gmonetix.pedals.R;
import com.gmonetix.pedals.activity.LoginActivity;
import com.gmonetix.pedals.dialog.CustomDialog;
import com.gmonetix.pedals.model.User;
import com.gmonetix.pedals.util.SharedPref;
import com.gmonetix.pedals.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.IOException;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * @author Gmonetix
 */

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final int GALLERY_INTENT = 100;
    @BindView(R.id.profile_image) ImageView profileImage;
    @BindView(R.id.profile_nit_registration_tv) TextView tvRegistration;
    @BindView(R.id.profile_name) TextView tvName;
    @BindView(R.id.profile_email)TextView tvEmail;
    @BindView(R.id.profile_phone) TextView tvPhone;
    @BindView(R.id.profile_change_password) LinearLayout changePasssword;
    @BindView(R.id.profile_logout) LinearLayout logout;
    @BindView(R.id.profile_contact_us) LinearLayout contactUs;
    @BindView(R.id.profile_rate_us) LinearLayout rateUs;

    private View view;

    private SharedPref sharedPref;
    private FirebaseUser user;
    private Bitmap bitmap = null;

    private ProgressDialog pDialog;

    public ProfileFragment() {}

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_profile, container, false);
            ButterKnife.bind(this,view);
            sharedPref = App.getSharedPref();

            user = FirebaseAuth.getInstance().getCurrentUser();

            Glide.with(getActivity()).load(sharedPref.getImageLink()).into(profileImage);
            tvRegistration.setText(sharedPref.getNitReg());
            tvName.setText(sharedPref.getName());
            tvEmail.setText(sharedPref.getEmail());
            tvPhone.setText(sharedPref.getPhone());

            changePasssword.setOnClickListener(this);
            logout.setOnClickListener(this);
            contactUs.setOnClickListener(this);
            tvEmail.setOnClickListener(this);
            tvPhone.setOnClickListener(this);
            rateUs.setOnClickListener(this);
            profileImage.setOnClickListener(this);
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profile_image:
                chooseImage();
                break;
            
            case R.id.profile_phone:
                new MaterialDialog.Builder(getActivity())
                        .title("Change Phone")
                        .content("Enter new phone")
                        .inputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_PHONE)
                        .negativeText("CANCEL")
                        .input("new phone", "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(final MaterialDialog dialog, CharSequence input) {
                                final String newPhone = input.toString().trim();
                                if (!Util.validatePhone(newPhone)) {
                                    Toast.makeText(getActivity(), "enter valid phone number", Toast.LENGTH_SHORT).show();
                                } else {
                                    final IOSDialog pDialog = new IOSDialog.Builder(getActivity())
                                            .setTitle("Changing phone")
                                            .setCancelable(false)
                                            .setTitleColorRes(R.color.gray)
                                            .build();
                                    pDialog.show();

                                    final User u = sharedPref.getUser();
                                    u.phone = newPhone;
                                    App.getDbReference().child("users").child(sharedPref.getPhone()).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                App.getDbReference().child("users").child(newPhone).setValue(u)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(getActivity(), "Phone changed successfully", Toast.LENGTH_SHORT).show();
                                                                tvPhone.setText(newPhone);
                                                                sharedPref.setPhone(newPhone);
                                                                pDialog.dismiss();
                                                                dialog.dismiss();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(getActivity(), "error - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                pDialog.dismiss();
                                                                dialog.dismiss();
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getActivity(), "error - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                pDialog.dismiss();
                                                dialog.dismiss();
                                            }
                                        });
                                }
                            }
                        }).show();
                break;
            case R.id.profile_email:
                new MaterialDialog.Builder(getActivity())
                        .title("Change Email")
                        .content("Enter new email")
                        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                        .negativeText("CANCEL")
                        .input("new email", "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(final MaterialDialog dialog, CharSequence input) {
                                final String newEmail = input.toString().trim();

                                if (!Util.validateEmail(newEmail)) {
                                    Toast.makeText(getActivity(), "enter a valid email", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (user != null) {
                                        final IOSDialog pDialog = new IOSDialog.Builder(getActivity())
                                                .setTitle("Changing email")
                                                .setCancelable(false)
                                                .setTitleColorRes(R.color.gray)
                                                .build();
                                        pDialog.show();

                                        AuthCredential credential = EmailAuthProvider.getCredential(sharedPref.getEmail(), sharedPref.getPassword());
                                        user.reauthenticate(credential)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        user.updateEmail(newEmail)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            sharedPref.setEmail(newEmail);
                                                                            tvEmail.setText(newEmail);
                                                                            App.getDbReference().child("users").child(sharedPref.getPhone()).child("email").setValue(newEmail)
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            pDialog.dismiss();
                                                                                            dialog.dismiss();
                                                                                            Toast.makeText(getActivity(), "email changed successfully", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    pDialog.dismiss();
                                                                                    dialog.dismiss();
                                                                                    Toast.makeText(getActivity(), "email changed successfully", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                        } else {
                                                                            pDialog.dismiss();
                                                                            dialog.dismiss();
                                                                            CustomDialog.showTextDialog(getActivity(),"Alert"," Error - " + task.getException().getMessage());
                                                                        }
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        pDialog.dismiss();
                                                                        dialog.dismiss();
                                                                        CustomDialog.showTextDialog(getActivity(),"Alert"," Error - " + e.getMessage());
                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                }
                            }
                        }).show();
                break;
            case R.id.profile_change_password:
                new MaterialDialog.Builder(getActivity())
                        .title("Change Password")
                        .content("Enter new password")
                        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        .negativeText("CANCEL")
                        .input("new password", "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(final MaterialDialog dialog, CharSequence input) {
                                final String newPassword = input.toString().trim();

                                if (!Util.validatePassword(newPassword)) {
                                    Toast.makeText(getActivity(), "enter a strong password", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (user != null) {
                                        final IOSDialog pDialog = new IOSDialog.Builder(getActivity())
                                                .setTitle("Changing password")
                                                .setCancelable(false)
                                                .setTitleColorRes(R.color.gray)
                                                .build();
                                        pDialog.show();

                                        AuthCredential credential = EmailAuthProvider.getCredential(sharedPref.getEmail(), sharedPref.getPassword());
                                        user.reauthenticate(credential)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        user.updatePassword(newPassword)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            sharedPref.setPassword(newPassword);
                                                                            pDialog.dismiss();
                                                                            dialog.dismiss();
                                                                            Toast.makeText(getActivity(), "password changed successfully", Toast.LENGTH_SHORT).show();
                                                                        } else {
                                                                            pDialog.dismiss();
                                                                            dialog.dismiss();
                                                                            CustomDialog.showTextDialog(getActivity(),"Alert"," Error - " + task.getException().getMessage());
                                                                        }
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        pDialog.dismiss();
                                                                        dialog.dismiss();
                                                                        CustomDialog.showTextDialog(getActivity(),"Alert"," Error - " + e.getMessage());
                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                }
                            }
                        }).show();
                break;
            case R.id.profile_logout:
                sharedPref.clearPref();
                if (user != null) {
                    FirebaseAuth.getInstance().signOut();
                }
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.profile_contact_us:
                CustomDialog.showContactUsDialog(getActivity(),sharedPref);
                break;
            case R.id.profile_rate_us:
                Util.goToGooglePlay(getActivity(),"com.gmonetix.pedals");
                break;
        }
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
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                this.bitmap = bitmap;

                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(sharedPref.getPhone()+".png");

                byte[] d = Util.getByteArray(bitmap,50);

                pDialog = new ProgressDialog(getActivity());
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
                        CustomDialog.showTextDialog(getActivity(),"Alert"," Error - " + exception.getMessage());
                        Log.e("TAG",""+exception.getMessage());
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pDialog.dismiss();
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                        sharedPref.setImageLink(downloadUrl);
                        Glide.with(getActivity()).load(sharedPref.getImageLink()).into(profileImage);
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        int progress = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                        pDialog.setProgress(progress);
                    }
                });
                
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Error - " + e.getMessage() , Toast.LENGTH_SHORT).show();
            }
        }
    }

}
