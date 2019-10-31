package com.example.digitaltolling.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.digitaltolling.Models.Payment;
import com.example.digitaltolling.Models.Users;
import com.example.digitaltolling.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener {

    private Button payBtn;
    private AlertDialog.Builder alertDialogBuilder;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference ref, ref1, ref2, ref3, ref4;
    String userUID;

    String userNID;
    String vehicleKey;
    String userVehicleID;
    String userVehiclePlateNo;
    String userVehicleColor;
    String userVehicleName;

    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    String currentDate,currentTime;
    String newKey;
    int flag=0, flag1=0, flag2=0;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        dialog = new ProgressDialog(this);
        dialog.setTitle("Pending");
        dialog.setMessage("Please wait for confirmation");
        dialog.setCancelable(true);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        payBtn = findViewById(R.id.payBtn);
        payBtn.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        dialog.show();
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("EEE, d MMM ''yy");
        currentDate = simpleDateFormat.format(calendar.getTime());

        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("h:mm a");
        currentTime = simpleTimeFormat.format(calendar.getTime());

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        userUID = currentUser.getUid();

        ref = database.getReference("Users").child(userUID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                userNID = users.getNid();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ref1 = database.getReference("vehicles").child(userUID);
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Vehicle vehicle = snapshot.getValue(Vehicle.class);
                    vehicleKey = snapshot.getKey();
                    userVehicleID = vehicle.getId();
                    userVehiclePlateNo = vehicle.getPlateNo();
                    userVehicleColor = vehicle.getVehicleColor();
                    userVehicleName = vehicle.getVehicleName();

                    Log.v("Key",snapshot.getKey());
                    Log.v("id",vehicle.getId());
                    Log.v("plateNo",vehicle.getPlateNo());
                    Log.v("color",vehicle.getVehicleColor());
                    Log.v("VehicleName",vehicle.getVehicleName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        while(flag==0){
            flag=1;
            ref2 = FirebaseDatabase.getInstance().getReference("payment").child(userUID).push();
            newKey = ref2.getKey();
            Log.wtf("NewKey",newKey);
            ref2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Payment payment = new Payment(newKey, userNID, userVehicleID, userVehiclePlateNo, userVehicleColor, userVehicleName, currentTime, currentDate);
                    payment.setKey(newKey);
                    ref2.setValue(payment);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        while (flag1==0){
            flag1=1;
            ref3 = FirebaseDatabase.getInstance().getReference("Confirm");
            ref3.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        String value = snapshot.getValue().toString();
                        if(value.equals(newKey)){
                            dialog.dismiss();
                            Log.wtf("hudaiiiiiii", String.valueOf(snapshot.getValue()));
                            alertDialogBuilder = new AlertDialog.Builder(PaymentActivity.this);
                            alertDialogBuilder.setTitle(R.string.title_text);

                            alertDialogBuilder.setMessage("Your Bill paid Successfully.You can go Now!");

                            alertDialogBuilder.setIcon(R.drawable.checked);

                            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    startActivity(new Intent(getApplicationContext(),Home.class));
                                    finish();

                                }
                            });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();

                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        while (flag2==0){
            flag2=1;
            ref4 = FirebaseDatabase.getInstance().getReference("Reject");
            ref4.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        String value = snapshot.getValue().toString();
                        if(value.equals(newKey)){
                            dialog.dismiss();
                            Log.wtf("hudaiiiiiii", String.valueOf(snapshot.getValue()));
                            alertDialogBuilder = new AlertDialog.Builder(PaymentActivity.this);
                            alertDialogBuilder.setTitle(R.string.title_text);

                            alertDialogBuilder.setMessage(R.string.reject_message_text);

                            alertDialogBuilder.setIcon(R.drawable.rejected);

                            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    startActivity(new Intent(getApplicationContext(),Home.class));
                                    finish();

                                }
                            });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();

                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


//        alertDialogBuilder = new AlertDialog.Builder(PaymentActivity.this);
//        alertDialogBuilder.setTitle(R.string.title_text);
//
//        alertDialogBuilder.setMessage(R.string.message_text);
//
//        alertDialogBuilder.setIcon(R.drawable.checked);
//
//        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//
//                startActivity(new Intent(getApplicationContext(),Home.class));
//                finish();
//
//            }
//        });
//
//
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();


    }
}
