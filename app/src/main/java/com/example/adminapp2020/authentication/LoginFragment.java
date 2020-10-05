package com.example.adminapp2020.authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.adminapp2020.MainActivity;
import com.example.adminapp2020.R;
import com.example.adminapp2020.databinding.FragmentLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import io.andref.rx.network.RxNetwork;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";
    private FragmentLoginBinding fragmentLoginBinding;

    Fragment_Communication fragmentCommunication;
    F2F_Commuication f2fCommuication;

    private CompositeSubscription mCompositeSubscription;
    private ConnectivityManager mConnectivityManager;

    public static FirebaseAuth mAuth;
    public PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    public static String mVerificationId;
    public PhoneAuthProvider.ForceResendingToken resendToken;


    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConnectivityManager = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
        initFireBaseCallbacks();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentLoginBinding=FragmentLoginBinding.inflate(inflater,container,false);
        View view=fragmentLoginBinding.getRoot();

        fragmentLoginBinding.enterPhnNumber.requestFocus();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        fragmentLoginBinding.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fragmentLoginBinding.enterPhnNumber.equals("") || fragmentLoginBinding.enterPhnNumber.length()!=11) {
                    Toast.makeText(getActivity(), "Invalid Number!!", Toast.LENGTH_SHORT).show();
                }else {
                    sendVerificationCode(fragmentLoginBinding.enterPhnNumber.getText().toString());
                    //resetIn30SEC(15,fragmentLoginBinding.nextButton);
                    hideKeyboard(getActivity());
                    fragmentLoginBinding.nextButton.setText("Please wait...");
                    fragmentLoginBinding.nextButton.setClickable(false);
                    mCompositeSubscription.unsubscribe();
                }
            }
        });

        return view;
    }

    private void initFireBaseCallbacks(){
        Log.d(TAG, "initFireBaseCallbacks: Called");
        mAuth= FirebaseAuth.getInstance();
        mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted: Called");

                if(phoneAuthCredential.getSmsCode() != null){
                    //calling verification fragment
                    f2fCommuication.onAutoRetriveSMS(phoneAuthCredential.getSmsCode());
                    Toast.makeText(getActivity(), "Auto-Retrival", Toast.LENGTH_SHORT).show();
                }else {
                    verifyCode(phoneAuthCredential);
                    Toast.makeText(getActivity(), "Instant Verification", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.d(TAG, "onVerificationFailed: "+e.getMessage());
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                Log.d(TAG, "onCodeSent: Done.");
                super.onCodeSent(s, forceResendingToken);

                mVerificationId=s;
                resendToken=forceResendingToken;
                Toast.makeText(getActivity(), "onCodeSent: Done.", Toast.LENGTH_SHORT).show();

                fragmentCommunication.onCodeSendResponse(TAG,fragmentLoginBinding.enterPhnNumber.getText().toString());

            }
        };
    }


    private void checkAdminStatus(String uid){

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("admin").child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "@ "+dataSnapshot.getValue());

                if(dataSnapshot.getValue()==null){
                    fragmentCommunication.callRegFragment("CallRegFrag");
                    Log.d(TAG, "onDataChange: New admin");
                }else{
                    ModelAdmin admin=dataSnapshot.getValue(ModelAdmin.class);
                    Log.d(TAG, "onDataChange: ========== admin name ================>"+admin.getName());


                    startActivity(new Intent(getActivity(), MainActivity.class));getActivity().finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: database error"+databaseError.getMessage());
            }
        });

        Log.d(TAG, "checkAdminStatus: uid="+uid);
    }


    private void sendVerificationCode(String mobile) {
        Log.d(TAG, "sendVerificationCode: Done.");
        LoginActivity.admin.setPhn("+88"+mobile);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+88" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    private void verifyCode(PhoneAuthCredential credential) {
        Log.d(TAG, "verifyCode: Done");
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(), "Verification Complete.", Toast.LENGTH_SHORT).show();
                    checkAdminStatus(mAuth.getUid());
                }
            }
        });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentCommunication =(Fragment_Communication)getActivity();
        f2fCommuication =(F2F_Commuication)getActivity();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCompositeSubscription.unsubscribe();
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentLoginBinding.nextButton.setText("Next");
        fragmentLoginBinding.nextButton.setClickable(true);

        //checking network connection state
        mCompositeSubscription = new CompositeSubscription();
        mCompositeSubscription.add(

                RxNetwork.connectivityChanges(getContext(), mConnectivityManager)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Boolean>()
                        {
                            @Override
                            public void call(Boolean connected)
                            {
                                if(connected){
                                    fragmentLoginBinding.nextButton.setBackgroundResource(R.drawable.button_layout);
                                    fragmentLoginBinding.nextButton.setText("NEXT");
                                    Log.d(TAG, "call: internet available");
                                }else {
                                    fragmentLoginBinding.nextButton.setBackgroundResource(R.drawable.button_layout_red);
                                    fragmentLoginBinding.nextButton.setText("No Internet!");
                                    Log.d(TAG, "call: internet not available");
                                }
                                fragmentLoginBinding.nextButton.setClickable(connected);
                            }
                        })
        );
    }
}