package com.example.adminapp2020;

import android.content.Context;
import android.media.session.MediaController;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterAssignDriver extends RecyclerView.Adapter<AdapterAssignDriver.ViewHolderAdapterAssignDriver>{

    List<ModelDriver> driverList;
    Context context;
    AdapterAssignDriverCallBAck callBAck;

    public AdapterAssignDriver(List<ModelDriver> driverList, Context context, AdapterAssignDriverCallBAck callBAck) {
        this.driverList = driverList;
        this.context = context;
        this.callBAck = callBAck;
    }

    @NonNull
    @Override
    public ViewHolderAdapterAssignDriver onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_driver_list,parent,false);
        ViewHolderAdapterAssignDriver viewHolderAdapterAssignDriver=new ViewHolderAdapterAssignDriver(view);
        return viewHolderAdapterAssignDriver;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderAdapterAssignDriver holder, final int position) {
          holder.drivername.setText("Driver Name: "+driverList.get(position).getDrivername());
        holder.driverlicense.setText("Driver License: "+driverList.get(position).getRegtrationnumber());
        holder.drivercontact.setText("Driver Contact: "+driverList.get(position).getPhonenumber());

        holder.assign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBAck.assignDriver(driverList.get(position).getPhonenumber());// todo driverphnnumber is uid for driver
            }
        });
    }

    @Override
    public int getItemCount() {
        return driverList.size();
    }

    class ViewHolderAdapterAssignDriver extends RecyclerView.ViewHolder{
         TextView drivername,driverlicense,drivercontact,assign;
        public ViewHolderAdapterAssignDriver(@NonNull View itemView) {
            super(itemView);
            drivername=itemView.findViewById(R.id.drivername);
            driverlicense=itemView.findViewById(R.id.driverlicense);
            drivercontact=itemView.findViewById(R.id.drivercontact);
            assign=itemView.findViewById(R.id.assign);
        }
    }
}
