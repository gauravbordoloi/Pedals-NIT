package com.gmonetix.pedals.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.afollestad.materialdialogs.MaterialDialog;
import com.balysv.materialripple.MaterialRippleLayout;
import com.gmonetix.pedals.App;
import com.gmonetix.pedals.R;
import com.gmonetix.pedals.dialog.CustomDialog;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Gmonetix
 */

public class UserGuideFragment extends Fragment {

    @BindView(R.id.user_guide_privacyPolicy) MaterialRippleLayout privacyPolicy;
    @BindView(R.id.user_guide_termsAndConditions) MaterialRippleLayout termsAndConditions;
    @BindView(R.id.user_guide_pricing) MaterialRippleLayout pricing;

    private View view;

    public UserGuideFragment() {  }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_user_guide, container, false);
            ButterKnife.bind(this,view);

            privacyPolicy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MaterialDialog dialog = CustomDialog.privacyPolicy(getActivity());
                    dialog.show();
                }
            });

            termsAndConditions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MaterialDialog dialog = CustomDialog.termsAndConditions(getActivity());
                    dialog.show();
                }
            });

            pricing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new MaterialDialog.Builder(getActivity())
                            .title("Pricing")
                            .content("The current pricing is - \n\nCycle with GEAR - " + App.getSharedPref().getPriceGear() + "\nCycle without GEAR - " + App.getSharedPref().getPriceGearless())
                            .positiveText("OK")
                            .build().show();
                }
            });
        }
        return view;
    }

}
