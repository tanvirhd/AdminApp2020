package com.example.adminapp2020;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.adminapp2020.authentication.LoginActivity;
import com.example.adminapp2020.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterRegBusListCallBack {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private DatabaseReference regBusRef,regDriverRef,rideSessionRef;

    private List<ModelRegBus> regBusList;
    private AdapterRegBusList adapterRegBusList;
    private Dialog dialog_wait;
    private int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dialog_wait=setupDialog(MainActivity.this);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Admin Panel");

        regDriverRef= FirebaseDatabase.getInstance().getReference("registereddriver");
        rideSessionRef=FirebaseDatabase.getInstance().getReference("ridesession");
        regBusRef = FirebaseDatabase.getInstance().getReference("registeredbuses");
        regBusList = new ArrayList<>();
        adapterRegBusList = new AdapterRegBusList(regBusList, this, this);
        binding.registeredbuslist.setAdapter(adapterRegBusList);
        binding.registeredbuslist.setLayoutManager(new LinearLayoutManager(this));

        getDataForRecyc();
        binding.swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataForRecyc();
            }
        });

        binding.btnRegBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,RegisterBusActivity.class));
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                Log.d(TAG, "onOptionsItemSelected: downloading...");
                FirebaseAuth.getInstance().signOut();startActivity(new Intent(MainActivity.this, LoginActivity.class));finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void getDataForRecyc(){
        binding.swipe.setRefreshing(true);
        regBusRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    regBusList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ModelRegBus bus = ds.getValue(ModelRegBus.class);
                        regBusList.add(bus);
                    }
                    adapterRegBusList.notifyDataSetChanged();
                    binding.swipe.setRefreshing(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onAssignDriverClick(ModelRegBus bus) {

        Intent intent=new Intent(MainActivity.this,AssignDriverActivity.class);
        intent.putExtra("parcel",bus);
        startActivity(intent);
    }

    @Override
    public void closeRideSession(String busid) {
        dialog_wait.show();
        findSessionId(rideSessionRef,busid);
    }

    void findSessionId(DatabaseReference ref, final String busid){
        count=0;
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot ds:snapshot.getChildren()){
                        ModelRideSession rideSession=ds.getValue(ModelRideSession.class);
                        if(rideSession.getBusId().equals(busid)){
                             removeRideSession(rideSession);
                             count++;
                        }
                    }
                    if(count==0){
                        dialog_wait.dismiss();
                        Toast.makeText(MainActivity.this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    void removeRideSession(final ModelRideSession session){
        rideSessionRef.child(session.getSessionId()).removeValue().addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updateDriverStatus(session);
            }
        });
    }

    private void updateDriverStatus(final ModelRideSession rideSession){
        regDriverRef.child(rideSession.getDriverId()).child("assignWithBus").setValue(false).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updateBusStatus(rideSession);
            }
        });
    }

    private void updateBusStatus(ModelRideSession rideSession){
        regBusRef.child(rideSession.getBusId()).child("assignedWithDriver").setValue(false).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
               dialog_wait.dismiss();
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