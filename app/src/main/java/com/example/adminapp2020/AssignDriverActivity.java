package com.example.adminapp2020;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.adminapp2020.databinding.ActivityAssignDriverBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AssignDriverActivity extends AppCompatActivity implements AdapterAssignDriverCallBAck{
    private static final String TAG = "AssignDriverActivity";
    private ActivityAssignDriverBinding binding;
    private ModelRegBus regBus;
    private List<ModelDriver> driverList;
    private AdapterAssignDriver adapterAssignDriver;
    DatabaseReference regDriverRef,RideSessionRef,regBusRef;
    private Dialog dialog_load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAssignDriverBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dialog_load=setupDialog(AssignDriverActivity.this);
        dialog_load.show();

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Assign Driver");
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        RideSessionRef=FirebaseDatabase.getInstance().getReference("ridesession");
        regBusRef=FirebaseDatabase.getInstance().getReference("registeredbuses");

        regBus= getIntent().getParcelableExtra("parcel");
        if(regBus!=null){
            binding.companyname.setText("Company Name: "+regBus.getCompanyname());
            binding.licencenumber.setText("License Plate: "+regBus.getLicense());
        }
        regDriverRef= FirebaseDatabase.getInstance().getReference("registereddriver");
        driverList=new ArrayList<>();
        adapterAssignDriver=new AdapterAssignDriver(driverList,this,this);
        binding.recycDriverlist.setLayoutManager(new LinearLayoutManager(this));
        binding.recycDriverlist.setAdapter(adapterAssignDriver);

        regDriverRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    dialog_load.dismiss();
                    driverList.clear();
                    for(DataSnapshot ds:snapshot.getChildren()){
                        ModelDriver driver=ds.getValue(ModelDriver.class);
                        if(!driver.isAssignWithBus()){
                            driverList.add(driver);
                        }
                    }
                    adapterAssignDriver.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog_load.dismiss();
            }
        });

    }


    @Override
    public void assignDriver(final String driverid) {
            dialog_load.show();
            String key=RideSessionRef.push().getKey();
            ModelRideSession rideSession=new ModelRideSession(key,driverid,regBus.getBusid());
            RideSessionRef.child(key).setValue(rideSession).addOnSuccessListener(this, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    updateDriverStatus(driverid);
                }
            });
    }

    private void updateDriverStatus(String driverid){
        regDriverRef.child(driverid).child("assignWithBus").setValue(true).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updateBusStatus(regBus.getBusid());
            }
        });
    }

    private void updateBusStatus(String busid){
        regBusRef.child(busid).child("assignedWithDriver").setValue(true).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dialog_load.dismiss();
                Toast.makeText(AssignDriverActivity.this, "Driver Assigned SuccessFully", Toast.LENGTH_SHORT).show();
                onBackPressed();
                finish();
            }
        });
    }

    private Dialog setupDialog(Activity activity) {

        Dialog dialog=new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_pleasewait);

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        //window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);  //this prevents dimming effect
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }
}