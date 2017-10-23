package com.gmonetix.pedals.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.gmonetix.pedals.R;
import com.gmonetix.pedals.dialog.CycleDialog;
import com.gmonetix.pedals.model.Cycle;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Gmonetix
 */

public class CycleAdapter extends RecyclerView.Adapter<CycleAdapter.MyViewHolder>{

    private Context context;
    private List<Cycle> cycleList = new ArrayList<>();

    public CycleAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<Cycle> cycleList) {
        this.cycleList = cycleList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cycle, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Cycle cycle = cycleList.get(position);

        holder.tvCycleAvailability.setText(cycle.getAvailability());
        holder.tvCycleName.setText(cycle.getModel());
        Glide.with(context).load(cycle.getImage()).into(holder.imageView);

        if (cycle.getAvailability().equals("AVAILABLE")) {
            holder.tvCycleAvailability.setBackgroundColor(Color.parseColor("#388E3C"));
        } else {
            holder.tvCycleAvailability.setBackgroundColor(Color.parseColor("#D32F2F"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CycleDialog cycleDialog = new CycleDialog(context,cycle);
                cycleDialog.setCancelable(false);
                cycleDialog.setCanceledOnTouchOutside(false);
                cycleDialog.show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return cycleList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row_cycle_image)
        ImageView imageView;
        @BindView(R.id.row_cycle_availability)
        TextView tvCycleAvailability;
        @BindView(R.id.row_cycle_name)
        TextView tvCycleName;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
