package com.example.sunny.up_education_board;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by Sunny on 4/21/2017.
 */

public class AndroidListAdapter extends ArrayAdapter {
    String[] androidListViewStrings;
    String[] imagesId;
    Context context;

    public AndroidListAdapter(Activity context, String[] imagesId, String[] textListView) {
        super(context, R.layout.layout_language_list_item, textListView);
        this.androidListViewStrings = textListView;
        this.imagesId = imagesId;
        this.context = context;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewRow = layoutInflater.inflate(R.layout.layout_language_list_item, null,
                true);

        TextView mtextView = (TextView) viewRow.findViewById(R.id.text_view);
        ImageView mimageView = (ImageView) viewRow.findViewById(R.id.image_view);

     //   String imgUrl = "https://firebasestorage.googleapis.com/v0/b/upeducationboard-e9761.appspot.com/o/0532%2FApr+24+2017%2Fsdm%2FApr+24+2017+12%3A20%3A37+PM.png?alt=media&token=c22be2c4-cb1a-44de-8a1e-cc04af37453c";
       // ImageView iv = (ImageView) viewRow.findViewById(R.id.my_image_view);


        Glide.with(context).load(imagesId[i])
                .thumbnail(1f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mimageView);

        mtextView.setText(androidListViewStrings[i]);
        Integer image_id = R.drawable.nodata;
     //   mimageView.setImageResource(image_id);
        return viewRow;
    }
}
