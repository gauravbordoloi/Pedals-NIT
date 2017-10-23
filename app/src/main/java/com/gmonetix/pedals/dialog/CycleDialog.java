package com.gmonetix.pedals.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.gmonetix.pedals.App;
import com.gmonetix.pedals.R;
import com.gmonetix.pedals.model.Cycle;
import com.gmonetix.pedals.model.Request;
import com.gmonetix.pedals.util.SharedPref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Gmonetix
 */

public class CycleDialog extends Dialog {

    @BindView(R.id.dialog_cycle_close) ImageView closeDialog;
    @BindView(R.id.dialog_cycle_id) TextView tvID;
    @BindView(R.id.dialog_cycle_price) TextView tvPrice;
    @BindView(R.id.dialog_cycle_model) TextView tvModel;
    @BindView(R.id.dialog_cycle_type) TextView tvType;
    @BindView(R.id.dialog_cycle_buy) MaterialRippleLayout btnBuy;
    @BindView(R.id.dialog_cycle_image) ImageView cycleImage;

    private Context context;
    private Cycle cycle;

    private SharedPref sharedPref;

    public CycleDialog(Context context, Cycle cycle) {
        super(context);
        this.context = context;
        this.cycle = cycle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_cycle);
        ButterKnife.bind(this);
        sharedPref = App.getSharedPref();

        tvID.setText("CYCLE-" + cycle.getId());
        tvModel.setText(cycle.getModel());
        tvType.setText(cycle.getType());
        if (cycle.getType().equals("GEAR"))
            tvPrice.setText("Rs " + sharedPref.getPriceGear() + "/hour");
        else tvPrice.setText("Rs " + sharedPref.getPriceGearless() + "/hour");
        Glide.with(context).load(cycle.getImage()).into(cycleImage);

        if (cycle.getAvailability().equals("UNAVAILABLE")) {
            btnBuy.setVisibility(View.GONE);
        }

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CycleDialog.this.dismiss();
            }
        });
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final IOSDialog pDialog = new IOSDialog.Builder(context)
                        .setTitle("Requesting a ride")
                        .setCancelable(false)
                        .setTitleColorRes(R.color.gray)
                        .build();
                pDialog.show();

                App.getDbReference().child("request").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(sharedPref.getPhone()).exists()) {
                            //free up previous cycle
                            Request request = dataSnapshot.child(sharedPref.getPhone()).getValue(Request.class);
                            App.getDbReference().child("CYCLE").child("CYCLE-" + request.cycle_id).child("availability").setValue("AVAILABLE")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            App.getDbReference().child("request").child(sharedPref.getPhone()).removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            // check server once again for availability
                                                            App.getDbReference().child("CYCLE").child("CYCLE-" + cycle.getId()).child("availability")
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    String available = dataSnapshot.getValue(String.class);
                                                                    if (available.equals("AVAILABLE")) {
                                                                        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MMM-yyyy");
                                                                        Date date = new Date();
                                                                        Request request = new Request(cycle.getId(),"REQUESTED",sharedPref.getPhone(),"00:00","00:00",0,dateFormat.format(date));
                                                                        App.getDbReference().child("request").child(sharedPref.getPhone()).setValue(request);
                                                                        App.getDbReference().child("CYCLE").child("CYCLE-" + cycle.getId()).child("availability").setValue("UNAVAILABLE")
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        pDialog.dismiss();
                                                                                        CycleDialog.this.dismiss();
                                                                                        CustomDialog.showTextDialog(context,"Booked","The bike is successfully booked for you. \nYou will shortly receive a call from us for the pickup location");
                                                                                    }
                                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                pDialog.dismiss();
                                                                                Log.e("TAG",""+e.getMessage());
                                                                            }
                                                                        });
                                                                    } else {
                                                                        pDialog.dismiss();
                                                                        Toast.makeText(context, "some one else booked it just now...oooops !", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {
                                                                    pDialog.dismiss();
                                                                    Log.e("TAG",""+databaseError.getMessage());
                                                                }
                                                            });
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            pDialog.dismiss();
                                                            Log.e("TAG",""+e.getMessage());
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pDialog.dismiss();
                                            Log.e("TAG",""+e.getMessage());
                                        }
                                    });
                        } else {
                            // check server once again for availability
                            App.getDbReference().child("CYCLE").child("CYCLE-" + cycle.getId()).child("availability").
                            addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String available = dataSnapshot.getValue(String.class);
                                    if (available.equals("AVAILABLE")) {
                                        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MMM-yyyy");
                                        Date date = new Date();
                                        Request request = new Request(cycle.getId(),"REQUESTED",sharedPref.getPhone(),"00:00","00:00",0,dateFormat.format(date));
                                        App.getDbReference().child("request").child(sharedPref.getPhone()).setValue(request);
                                        App.getDbReference().child("CYCLE").child("CYCLE-" + cycle.getId()).child("availability").setValue("UNAVAILABLE")
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        pDialog.dismiss();
                                                        CycleDialog.this.dismiss();
                                                        CustomDialog.showTextDialog(context,"Booked","The bike is successfully booked for you. \nYou will shortly receive a call from us for the pickup location");
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                pDialog.dismiss();
                                                Log.e("TAG",""+e.getMessage());
                                            }
                                        });
                                    } else {
                                        pDialog.dismiss();
                                        Toast.makeText(context, "some one else booked it just now...oooops !", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    pDialog.dismiss();
                                    Log.e("TAG",""+databaseError.getMessage());
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        pDialog.dismiss();
                        Log.e("TAG",""+databaseError.getMessage());
                    }
                });

            }
        });
    }

}
