package com.example.adminapp2020;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterRegBusList extends RecyclerView.Adapter<AdapterRegBusList.ViewHolderAdapterRegBusList> {

    List<ModelRegBus> regBusList;
    Context context;
    AdapterRegBusListCallBack callBack;

    public AdapterRegBusList(List<ModelRegBus> regBusList, Context context, AdapterRegBusListCallBack callBack) {
        this.regBusList = regBusList;
        this.context = context;
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public ViewHolderAdapterRegBusList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_registered_bus_list, parent, false);
        ViewHolderAdapterRegBusList viewHolderAdapterRegBusList = new ViewHolderAdapterRegBusList(view);
        return viewHolderAdapterRegBusList;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderAdapterRegBusList holder, final int position) {
        holder.companyName.setText("Company Name: " + regBusList.get(position).getCompanyname());
        holder.licenceNumber.setText("Licence Number: " + regBusList.get(position).getLicense());
        holder.route.setText("Route: " + regBusList.get(position).getRoute());

        if(regBusList.get(position).isAssignedWithDriver()){
            holder.closeridesession.setVisibility(View.VISIBLE);
            holder.assignDriver.setVisibility(View.GONE);
        }else {
            holder.closeridesession.setVisibility(View.GONE);
            holder.assignDriver.setVisibility(View.VISIBLE);
        }

        holder.assignDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.onAssignDriverClick(regBusList.get(position));
            }
        });

        holder.closeridesession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.closeRideSession(regBusList.get(position).getBusid());
            }
        });


    }

    @Override
    public int getItemCount() {
        return regBusList.size();
    }

    class ViewHolderAdapterRegBusList extends RecyclerView.ViewHolder {

        TextView companyName, licenceNumber, route,assignDriver, closeridesession;

        public ViewHolderAdapterRegBusList(@NonNull View itemView) {
            super(itemView);
            companyName = itemView.findViewById(R.id.tvcompanyname);
            licenceNumber = itemView.findViewById(R.id.tvlicense);
            route = itemView.findViewById(R.id.tvroute);
            assignDriver = itemView.findViewById(R.id.assign_driver);
            closeridesession =itemView.findViewById(R.id.close_ride_session);
        }
    }

}
