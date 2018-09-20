package com.example.sunny.up_education_board;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import static com.example.sunny.up_education_board.LoginActivity.email;
import static com.example.sunny.up_education_board.LoginActivity.password;
import static com.example.sunny.up_education_board.LoginActivity.schoolcode;
import static com.example.sunny.up_education_board.LoginActivity.sname;

public class School_code extends AppCompatActivity {
    public Query myRef;
    public Button button;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_code);

        textView = (TextView)findViewById(R.id.TextViewschoolcode);
        button = (Button)findViewById(R.id.button3);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog pd = new ProgressDialog(School_code.this);

                pd = new ProgressDialog(School_code.this,R.style.MyTheme);
                pd.setProgressStyle(ProgressDialog.BUTTON_NEUTRAL);

                pd.setMessage("Loading......               ");

                pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#11111a")));

                pd.setIndeterminate(false);

                pd.setCancelable(true);

                pd.show();

                GetValue(email,password,pd);
            }
        });

    }
    public void disms(ProgressDialog pd)
    {
        pd.dismiss();
    }
    public void GetValue(final String email, final String password , final ProgressDialog pd)
    {
        myRef = FirebaseDatabase.getInstance().getReference();
        this.myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //   Object key = dataSnapshot.getValue();
                schoolcode = textView.getText().toString();
                if(dataSnapshot.hasChild(schoolcode))
                {
                    Map<String ,Object> key = (Map<String, Object>) dataSnapshot.getValue();
                    disms(pd);
                    myRef.removeEventListener(this);
                    StoreData.schoolcode = schoolcode;
                    Intent intent = new Intent("com.example.sunny.up_education_board.Moniter");
                    //    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    finish();
                }
                else{
                    disms(pd);
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(School_code.this);
                    builder1.setMessage("Check username or password");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                    return;
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
