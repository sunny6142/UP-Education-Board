package com.example.sunny.up_education_board;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.ImageView;

import com.google.android.gms.vision.Frame;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.example.sunny.up_education_board.Manage.tdate;

/**
 * Created by Sunny on 5/15/2017.
 */

public class Attendence extends Fragment {

    View myView;
    android.widget.ImageView mimageView;
    private static final int CONTENT_REQUEST=1337;
    private File output=null;
    File dir;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.attendance , container , false);



        mimageView = (ImageView) myView.findViewById(R.id.cameraimage);
        Button button = (Button) myView.findViewById(R.id.take_image_from_camera);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                 dir=
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

                output=new File(dir, "CameraContentDemo.jpeg");
                i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));

                startActivityForResult(i, CONTENT_REQUEST);
            }
        });

        return myView;
    }



    private Bitmap myBitmap;
    private int width, height;
    private FaceDetector.Face[] detectedFaces;
    private int NUMBER_OF_FACES=50;
    private FaceDetector faceDetector;
    private int NUMBER_OF_FACE_DETECTED;
    private float eyeDistance;
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CONTENT_REQUEST && resultCode == RESULT_OK) {
         //   Intent i=new Intent(Intent.ACTION_VIEW);

          //  i.setDataAndType(Uri.fromFile(output), "image/jpeg");
           // startActivity(i);
            BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();
            BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;

            Bitmap bmp = BitmapFactory.decodeFile(String.valueOf(output));
            mimageView.setImageBitmap(bmp);

        //    int a[] = {bmp};
            myBitmap = BitmapFactory.decodeFile(output.toString(), BitmapFactoryOptionsbfo);
        //    myBitmap = BitmapFactory.decodeResource(getResources(),
               //     bmp, BitmapFactoryOptionsbfo);
             //       R.drawable.jennifer, BitmapFactoryOptionsbfo);

           int width=myBitmap.getWidth();
            int height=myBitmap.getHeight();
            detectedFaces=new FaceDetector.Face[NUMBER_OF_FACES];
            faceDetector=new FaceDetector(width,height,NUMBER_OF_FACES);
            NUMBER_OF_FACE_DETECTED=faceDetector.findFaces(myBitmap, detectedFaces);

            Log.v("ABCD FACE"," "+NUMBER_OF_FACE_DETECTED);
            TextView tx = (TextView)myView.findViewById(R.id.statt);
            tx.setText("Number of student : "+NUMBER_OF_FACE_DETECTED);

            Button send = (Button)myView.findViewById(R.id.sendatt);
            send.setVisibility(View.VISIBLE);

            send.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Query myRef;
                                            DatabaseReference mDatabase;
                                            String parts[] = tdate.split(" ");
                                            FirebaseDatabase Database = FirebaseDatabase.getInstance();
                                            Database.getReference().child(LoginActivity.schoolcode).child(parts[0] + " " + parts[1] + " " + parts[2]).child(LoginActivity.sname).child("attendence").setValue(NUMBER_OF_FACE_DETECTED);
                                        }
                                    });
            ///    Bitmap tmpBmp = image.copy(Config.RGB_565, true);

       //     Bitmap mphoto = (Bitmap) data.getExtras().get("data");
        //    mimageView.setImageResource((Integer) data.getExtras().get("data"));

        }
    }
}
