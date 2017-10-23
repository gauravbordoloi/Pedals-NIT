package com.gmonetix.pedals.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.gmonetix.pedals.App;
import com.gmonetix.pedals.R;
import com.gmonetix.pedals.adapter.CycleAdapter;
import com.gmonetix.pedals.util.Const;
import com.gmonetix.pedals.model.Cycle;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Gmonetix
 */

public class BookFragment extends Fragment {

    private View view;
    @BindView(R.id.book_rv_LL) LinearLayout rvLL;
    @BindView(R.id.book_ongoing_LL) LinearLayout ongoingLL;
    @BindView(R.id.book_recyclerView) RecyclerView recyclerView;
    @BindView(R.id.book_cycle_checkBox) CheckBox checkBox;

    //ongoing data
    @BindView(R.id.ongoing_cycleImage) ImageView cycleImage;
    @BindView(R.id.ongoing_cycleId) TextView cycleId;
    @BindView(R.id.ongoing_cycleType) TextView cycleType;
    @BindView(R.id.ongoing_cycleModel) TextView cycleModel;
    @BindView(R.id.ongoing_cyclePrice) TextView cyclePrice;
    @BindView(R.id.ongoing_cycleCall) MaterialRippleLayout cycleCall;

    CycleAdapter cycleAdapter;
    List<Cycle> cycleList;

    private boolean showUnavailableCycles = true;

    public BookFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_book, container, false);
            ButterKnife.bind(this,view);

            final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
            recyclerView.setLayoutManager(gridLayoutManager);
            cycleList = new ArrayList<>();
            cycleAdapter = new CycleAdapter(getActivity());
            recyclerView.setAdapter(cycleAdapter);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        showUnavailableCycles = true;
                        cycleAdapter.setList(cycleList);
                        cycleAdapter.notifyDataSetChanged();
                    } else {
                        showUnavailableCycles = false;
                        cycleAdapter.setList(getOnlyAvailableCycle());
                        cycleAdapter.notifyDataSetChanged();
                    }
                }
            });

            App.getDbReference().child("request").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(App.getSharedPref().getPhone()).exists()) {
                        if (dataSnapshot.child(App.getSharedPref().getPhone()).child("status").getValue(String.class).equals("ONGOING")) {
                            rvLL.setVisibility(View.GONE);
                            ongoingLL.setVisibility(View.VISIBLE);

                            String cId = dataSnapshot.child(App.getSharedPref().getPhone()).child("cycle_id").getValue(String.class);

                            App.getDbReference().child("CYCLE").child("CYCLE-" + cId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Cycle cycle = dataSnapshot.getValue(Cycle.class);

                                    Glide.with(getActivity()).load(cycle.getImage()).into(cycleImage);
                                    cycleId.setText("CYCLE-" + cycle.getId());
                                    cycleType.setText(cycle.getType());
                                    cycleModel.setText(cycle.getModel());
                                    if (cycle.getType().equals("GEAR"))
                                        cyclePrice.setText(String.valueOf(App.getSharedPref().getPriceGear()));
                                    else cyclePrice.setText(String.valueOf(App.getSharedPref().getPriceGearless()));
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    } else {
                        rvLL.setVisibility(View.VISIBLE);
                        ongoingLL.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("TAG",""+databaseError.getMessage());
                }
            });

            App.getDbReference().child(Const.CYCLE).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    cycleList.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Cycle cycle = ds.getValue(Cycle.class);
                        cycleList.add(cycle);
                    }
                    if (showUnavailableCycles) {
                        cycleAdapter.setList(cycleList);
                    } else {
                        cycleAdapter.setList(getOnlyAvailableCycle());
                    }
                    cycleAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("TAG",""+databaseError.getMessage());
                }
            });

            cycleCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + App.getSharedPref().getAdminPhone()));
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getActivity(), "permission not granted", Toast.LENGTH_SHORT).show();
                    } else getActivity().startActivity(callIntent);
                }
            });

        }
        return view;
    }

    public List<Cycle> getOnlyAvailableCycle() {
        List<Cycle> list = new ArrayList<>();
        for (Cycle cycle : cycleList) {
            if (cycle.getAvailability().equals("AVAILABLE"))
                list.add(cycle);
        }
        return list;
    }

}
