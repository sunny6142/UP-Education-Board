package com.example.sunny.up_education_board.services;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.WindowManager;

import com.example.sunny.up_education_board.LoginActivity;
import com.example.sunny.up_education_board.Manage;
import com.example.sunny.up_education_board.listeners.OnPictureCapturedListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.TreeMap;

import static android.R.attr.format;


/**
 * The aim of this service is to secretly take pictures (without preview or opening device's camera app)
 * from all available cameras.
 * @author hzitoun (zitoun.hamed@gmail.com)
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP) //camera 2 api was added in API level 21
public class PictureService extends Activity{

    private static final String TAG = PictureService.class.getSimpleName();
    private CameraDevice cameraDevice;
    private ImageReader imageReader;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private Activity context;
    private WindowManager windowManager;
    private CameraManager manager;
    private TreeMap<String, byte[]> picturesTaken;
    private OnPictureCapturedListener capturedListener;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private String currentCameraId;
    private Queue<String> cameraIds;


    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    public void startCapturing(final Activity activity,
                               final OnPictureCapturedListener capturedListener) {
        this.picturesTaken = new TreeMap<>();
        this.context = activity;
        this.manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        this.windowManager = context.getWindowManager();
        this.capturedListener = capturedListener;
        this.cameraIds = new LinkedList<>();
        try {
            final String[] cameraIdList = manager.getCameraIdList();
            if (cameraIdList != null && cameraIdList.length > 0) {
                //    for (final String cameraId : cameraIdList) {
                this.cameraIds.add("0");
                //   }
                this.currentCameraId = this.cameraIds.poll();
                openCameraAndTakePicture();
            } else {
                capturedListener.onDoneCapturingAllPhotos(picturesTaken);
            }
        } catch (CameraAccessException e) {
            Intent i = new Intent("com.example.sunny.up_education_board.Manage");
            startActivity(i);
            finish();
            Log.e(TAG, "Exception occurred while accessing the list of cameras", e);
        }
    }

    private void openCameraAndTakePicture() {
        startBackgroundThread();
        Log.d(TAG, "opening camera " + currentCameraId);
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                manager.openCamera(currentCameraId, stateCallback, null);
            }
        } catch (CameraAccessException e) {
            Intent i = new Intent("com.example.sunny.up_education_board.Manage");
            startActivity(i);
            finish();
            Log.e(TAG, " exception occurred while opening camera " + currentCameraId, e);
        }
    }


    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.d(TAG, "camera " + camera.getId() + " opened");
            cameraDevice = camera;
            Log.i(TAG, "Taking picture from camera " + camera.getId());
            // take the picture after some time on purpose
         /*   new Handler().postDelayed(()  -> {
                 takePicture();
            }, 1000);
*/

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    takePicture();
                }
            }, 3000);

        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.d(TAG, " camera " + camera.getId() + " disconnected");
            if (cameraDevice != null) {
                cameraDevice.close();
            }
        }

        @Override
        public void onClosed(@NonNull CameraDevice camera) {
            Log.d(TAG, "camera " + camera.getId() + " closed");
            stopBackgroundThread();
            if (!cameraIds.isEmpty()) {
             /*   new Handler().postDelayed(() ->
                                takeAnotherPicture()
                        , 100);
*/
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        takePicture();
                    }
                }, 3000);
            } else {
                capturedListener.onDoneCapturingAllPhotos(picturesTaken);
            }
        }


        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.e(TAG, "camera in error, int code " + error);
            if (cameraDevice != null) {
                cameraDevice.close();
                return;
            }
        }
    };


    private void takePicture() {
        if (null == cameraDevice) {
            Log.e(TAG, "cameraDevice is null");
            return;
        }
        try {
            final CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            if (characteristics != null) {
                if (characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP) != null) {
                    jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                            .getOutputSizes(ImageFormat.JPEG);
                }
            }
            int width = 400;
            int height = 700;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            //ImageReader.newInstance(pictureSizeValue.getWidth(), pictureSizeValue.getHeight(), format, maxImages);
            final ImageReader reader =  ImageReader.newInstance(width, height, ImageFormat.JPEG, 5);
            final List<Surface> outputSurfaces = new ArrayList<>(2);
            outputSurfaces.add(reader.getSurface());
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);


            final int rotation = this.windowManager.getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

         //   captureBuilder.set(CaptureRequest.JPEG_QUALITY, (byte) 100);
          /*  ImageReader.OnImageAvailableListener readerListener = (ImageReader readerL) -> {
                final Image image = readerL.acquireLatestImage();
                final ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                final byte[] bytes = new byte[buffer.capacity()];
                buffer.get(bytes);
                saveImageToDisk(bytes);
                if (image != null) {
                    image.close();
                }
            };*/
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener(){
                @Override
                public void onImageAvailable(final ImageReader reader) {

                    final Image image = reader.acquireLatestImage();
                    final ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                    final byte[] bytes = new byte[buffer.capacity()];
                    buffer.get(bytes);
                    saveImageToDisk(bytes);
                    if (image != null) {
                        image.close();
                    }
                }
            };
            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        Intent i = new Intent("com.example.sunny.up_education_board.Manage");
                        startActivity(i);
                        finish();
                        Log.e(TAG, " exception occurred while accessing " + currentCameraId, e);
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            Intent i = new Intent("com.example.sunny.up_education_board.Manage");
            startActivity(i);
            finish();
            Log.e(TAG, " exception occurred while accessing " + currentCameraId, e);
        }
    }

    private void saveImageToDisk(final byte[] bytes) {
        final File file = new File(Environment.getExternalStorageDirectory() + "/" + this.cameraDevice.getId() + "_pic.jpg");
        try (final OutputStream output = new FileOutputStream(file)) {
            output.write(bytes);
            this.picturesTaken.put(file.getPath(), bytes);
            /////////////////////////////
            // showToast("Saved: " + mFile);


            Uri mpfile = Uri.fromFile(new File(file.toString()));
            StorageReference mStorageRef;
            mStorageRef = FirebaseStorage.getInstance().getReference();
            final String date[] = Manage.tdate.split(" ");
            StorageReference riversRef = mStorageRef.child(LoginActivity.schoolcode +"/"+ date[0] + " " +date[1] + " " + date[2]+"/"+LoginActivity.sname +"/"+ Manage.tdate +".png");

            riversRef.putFile(mpfile)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                           Log.v("ABCD","Downloaded");
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Log.v("ABCD","task1");
                            DatabaseReference timeRef1 = FirebaseDatabase.getInstance().getReference().child(LoginActivity.schoolcode).child(date[0] + " " + date[1] + " " + date[2]).child("images").child(LoginActivity.sname).child(Manage.tdate);
                            Log.v("ABCD","task2");
                            timeRef1.setValue(downloadUrl.toString());
                         //   timeRef2.setValue(downloadUrl.toString());
                       /*     Log.v("ABCD","task3");
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(LoginActivity.schoolcode);
                            Map<String, Object> value = new HashMap<>();
                            value.put(Manage.tdate, downloadUrl.toString());

                            ref.child(date[0] + " " + date[1] + " " + date[2]).child("images").child(LoginActivity.sname).setValue(value);


                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(LoginActivity.schoolcode);
                            String key = ref.push().getKey();
                            Map<String, Object> value = new HashMap<>();
                            value.put(key, downloadUrl.toString());
                            ref.child(date[0] + " " + date[1] + " " + date[2]).child(LoginActivity.sname).child("images").setValue(value);
                           */
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                        }
                    });

            //   Log.d(TAG, mFile.toString());
            //////////////////////////////
        } catch (IOException e) {
            Intent i = new Intent("com.example.sunny.up_education_board.Manage");
            startActivity(i);
            finish();
            Log.e(TAG, "Exception occurred while saving picture to external storage ", e);
        }
    }


    private void startBackgroundThread() {
        if (mBackgroundThread == null) {
            mBackgroundThread = new HandlerThread("Camera Background" + currentCameraId);
            mBackgroundThread.start();
            mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
        }
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            Intent i = new Intent("com.example.sunny.up_education_board.Manage");
            startActivity(i);
            finish();
            Log.e(TAG, "exception occurred while stoping BackgroundThread ", e);
        }
    }


    private void takeAnotherPicture() {
        startBackgroundThread();
        this.currentCameraId = this.cameraIds.poll();
        openCameraAndTakePicture();
    }

    private void closeCamera() {
        Log.d(TAG, "closing camera " + cameraDevice.getId());
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }
    }


    final private CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            if (picturesTaken.lastEntry() != null) {
                capturedListener.onCaptureDone(picturesTaken.lastEntry().getKey(), picturesTaken.lastEntry().getValue());
                Log.i(TAG, "done taking picture from camera " + cameraDevice.getId());
            }
            closeCamera();
        }
    };
}
