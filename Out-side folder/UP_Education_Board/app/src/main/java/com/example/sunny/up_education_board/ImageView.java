package com.example.sunny.up_education_board;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ImageView extends AppCompatActivity {

    public ProgressDialog pd;

    DatabaseReference myRefimageview;

    Integer image_id[] = {R.drawable.aaa, R.drawable.aaa, R.drawable.aaa,
            R.drawable.aaa, R.drawable.aaa, R.drawable.aaa,
            R.drawable.aaa, R.drawable.aaa, R.drawable.aaa};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Images");

      //  setTitle("Select Date");
        ImageButton imageButton = (ImageButton)findViewById(R.id.backbutton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.example.sunny.up_education_board.Moniter");
                startActivity(intent);
                finish();
            }
        });
        LoadImage();
    }
    @Override
    public void setTitle(CharSequence title) {
        TextView tvTitle = (TextView) findViewById(R.id.title);

        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }
    void LoadImage()
    {
        pd = new ProgressDialog(this);

        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // Set the progress dialog title and message
        pd.setCancelable(false);
        pd.setTitle("Please Wait");
        pd.setMessage("Loading IM......               ");

        //   pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#11111a")));

        pd.setIndeterminate(false);

        pd.show();

        myRefimageview = FirebaseDatabase.getInstance().getReference().child(StoreData.schoolcode).child(StoreData.DD).child("images").child(StoreData.transfername);

        myRefimageview.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final long[] pendingLoadCount = { dataSnapshot.getChildrenCount() };
                int it = 0;
  //              String ct = String.valueOf(pendingLoadCount);
//                int siz = Integer.parseInt(ct) +1;
                String androidListViewStrings[] = new String[50];

                String images[] = new String[50];

                if(pendingLoadCount[0] == 0){
                    pd.dismiss();
                    AndroidListAdapter androidListAdapter = new AndroidListAdapter(ImageView.this,images , androidListViewStrings);
                    ListView androidListView = (ListView)findViewById(R.id.custom_listview_example);
                    androidListView.setAdapter(androidListAdapter);
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
                        AndroidListAdapter androidListAdapter = new AndroidListAdapter(ImageView.this,images , androidListViewStrings);
                        ListView androidListView = (ListView)findViewById(R.id.custom_listview_example);
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
    }
}
