package com.example.sunny.up_education_board;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Sunny on 5/23/2017.
 */

public class studentleave extends Fragment {

    View myView;

    EditText msg;
    Button btn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.studentleave, container, false);

        msg = (EditText)myView.findViewById(R.id.msg);
        btn = (Button)myView.findViewById(R.id.submit_holiday);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(msg.getText().toString().length() < 3){
                    msg.setError("Enter your Reason");
                    msg.requestFocus();
                    return;
                }
                else{

                    add( msg.getText().toString() );
                    msg.setText("");
                    msg.requestFocus();
                }
            }
        });

        Toast.makeText(getActivity(), "Date"+ Moniter.DD, Toast.LENGTH_SHORT).show();
        return  myView;
    }
    void add(String msg)
    {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = database;

        if( Manage.tdate != null) {
            String parts[] = Manage.tdate.split(" ");
            usersRef.child(LoginActivity.schoolcode).child(parts[0] + " " + parts[1] + " " + parts[2]).child(LoginActivity.sname).child("holiday").setValue(msg);
            Toast.makeText(getActivity(), "Recorded is added", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getActivity(), "Network Error !", Toast.LENGTH_SHORT).show();
        }

    }
}
