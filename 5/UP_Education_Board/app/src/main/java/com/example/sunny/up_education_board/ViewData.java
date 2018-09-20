package com.example.sunny.up_education_board;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Sunny on 4/21/2017.
 */

public class ViewData  extends Fragment {

    public ProgressDialog pd;
    DatabaseReference myRef1;
    String androidListViewStrings[] = new String[200];

    String images[] = new String[200];
    Integer image_id[] = {R.drawable.aaa, R.drawable.aaa, R.drawable.aaa,
            R.drawable.aaa, R.drawable.aaa, R.drawable.aaa,
            R.drawable.aaa, R.drawable.aaa, R.drawable.aaa};

    View myView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.emptyview , container , false);
        pd = new ProgressDialog(getActivity());

        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // Set the progress dialog title and message
        pd.setCancelable(false);
        pd.setTitle("Please Wait");
        pd.setMessage("Loading IM......               ");

        //   pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#11111a")));

        pd.setIndeterminate(false);

        pd.show();

        myRef1 = FirebaseDatabase.getInstance().getReference().child(LoginActivity.schoolcode).child(Moniter.DD).child("images").child(Moniter.transfername);

        myRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final long[] pendingLoadCount = { dataSnapshot.getChildrenCount() };
                if(pendingLoadCount[0] == 0){
                    pd.dismiss();
                }
                for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()){

                    Log.v("ABCDP", itemSnapshot.getValue().toString());
                    String value = itemSnapshot.getValue().toString();
                    //    String parts[] = value.split("#");
                    images[it] = value;
                    androidListViewStrings[it] = "Image no : " +it;
                    it = it +1 ;

                    pendingLoadCount[0] = pendingLoadCount[0] - 1;
                    if (pendingLoadCount[0] == 0) {
                        pd.dismiss();
                        AndroidListAdapter androidListAdapter = new AndroidListAdapter(getActivity(),images , androidListViewStrings);
                        ListView androidListView = (ListView)myView.findViewById(R.id.custom_listview_example);
                        androidListView.setAdapter(androidListAdapter);
                        Log.v("ABCD", "Nothing found");
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //           myownpd.cancel();
            }
        });
        return myView;
    }

    int it = 0;
    void setImage()
    {

    }
}
