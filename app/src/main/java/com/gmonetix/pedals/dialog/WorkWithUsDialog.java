package com.gmonetix.pedals.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.ImageView;
import com.balysv.materialripple.MaterialRippleLayout;
import com.gmonetix.pedals.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import static com.gmonetix.pedals.App.sharedPref;

/**
 * @author Gmonetix
 */

public class WorkWithUsDialog extends AppCompatDialog {

    @BindView(R.id.dialog_workWithUs_close)
    ImageView dialogClose;
    @BindView(R.id.dialog_workWithUs_contact)
    MaterialRippleLayout contactUs;

    private Context context;

    public WorkWithUsDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_work_with_us);
        ButterKnife.bind(this);

        dialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WorkWithUsDialog.this.dismiss();
            }
        });

        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialog.showContactUsDialog(context,sharedPref);
            }
        });

    }

}
