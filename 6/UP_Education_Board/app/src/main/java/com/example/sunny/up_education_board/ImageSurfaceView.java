package com.example.sunny.up_education_board;

import android.content.Context;
import android.graphics.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by Sunny on 4/13/2017.
 */

public class ImageSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Camera camera;
    private SurfaceHolder surfaceHolder;

    public ImageSurfaceView(Context context, Camera camera) {
        super(context);
        this.camera = camera;
        this.surfaceHolder = getHolder();
        this.surfaceHolder.addCallback(this);
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
   //     this.camera.stopPreview();
     //   this.camera.release();
    }
}
