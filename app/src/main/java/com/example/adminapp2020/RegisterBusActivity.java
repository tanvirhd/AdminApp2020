package com.example.adminapp2020;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.adminapp2020.databinding.ActivityRegisterBusBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterBusActivity extends AppCompatActivity {
    private static final String TAG = "RegisterBusActivity";
    private ActivityRegisterBusBinding binding;
    private Dialog dialog_loading;
    private DatabaseReference regBusRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityRegisterBusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        regBusRef=FirebaseDatabase.getInstance().getReference("registeredbuses");
        dialog_loading=setupDialog(RegisterBusActivity.this);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Register New Bus");
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.btnRegBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.tvCompanyname.equals("")&&binding.tvLicensenumber.equals("")&&binding.tvRoute.equals("")){
                    Toast.makeText(RegisterBusActivity.this, "Fill Up All Fields", Toast.LENGTH_SHORT).show();
                }else {
                    dialog_loading.show();

                    ModelRegBus bus=new ModelRegBus();
                    bus.setCompanyname(binding.tvCompanyname.getText().toString());
                    bus.setLicense(binding.tvLicensenumber.getText().toString());
                    bus.setRoute(binding.tvRoute.getText().toString());
                    bus.setAssignedWithDriver(false);
                    String key=regBusRef.push().getKey();
                    bus.setBusid(key);
                    regBusRef.child(key).setValue(bus).addOnSuccessListener(RegisterBusActivity.this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                           dialog_loading.dismiss();
                           onBackPressed();
                           finish();
                        }
                    }).addOnFailureListener(RegisterBusActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog_loading.dismiss();
                            Toast.makeText(RegisterBusActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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