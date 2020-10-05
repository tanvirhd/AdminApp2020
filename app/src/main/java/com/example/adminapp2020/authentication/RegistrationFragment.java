package com.example.adminapp2020.authentication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.adminapp2020.MainActivity;
import com.example.adminapp2020.R;
import com.example.adminapp2020.databinding.FragmentRegistrationBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegistrationFragment extends Fragment {
    private static String TAG="RegistrationFragment";
    private FragmentRegistrationBinding fragmentRegistrationBinding;


    public RegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentRegistrationBinding=FragmentRegistrationBinding.inflate(inflater,container,false);
        View view=fragmentRegistrationBinding.getRoot();

        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Registration");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_back);

        fragmentRegistrationBinding.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fragmentRegistrationBinding.etUserName.getText().toString().equals("") && fragmentRegistrationBinding.etReferalcode.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "Invalid Input", Toast.LENGTH_SHORT).show();
                }else {
                    if(!fragmentRegistrationBinding.etReferalcode.getText().toString().equals("adminreg2020")){
                        Toast.makeText(getActivity(), "Invalid Referal Code", Toast.LENGTH_SHORT).show();
                    }else {
                        hideKeyboard(getActivity());
                        fragmentRegistrationBinding.btnContinue.setText("Please wait...");
                        fragmentRegistrationBinding.btnContinue.setClickable(false);

                        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("admin");
                        String key=LoginFragment.mAuth.getUid();
                        LoginActivity.admin.setUid(key);
                        LoginActivity.admin.setName(fragmentRegistrationBinding.etUserName.getText().toString());
                        ref.child(key).setValue(LoginActivity.admin).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(getActivity(), MainActivity.class));getActivity().finish();
                            }
                        });


                    }

                }
            }
        });


        return view;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}