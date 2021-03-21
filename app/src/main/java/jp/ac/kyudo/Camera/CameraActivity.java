package jp.ac.kyudo.Camera;

import org.apache.commons.io.IOUtils;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.aruco.Dictionary;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.aruco.Aruco;
import org.opencv.aruco.DetectorParameters;
import org.opencv.imgcodecs.Imgcodecs;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ParseException;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import jp.ac.kyudo.MainSelect.kyudoDBOpenHelper;
import jp.ac.kyudo.R;

public class CameraActivity extends Activity implements CvCameraViewListener2 {
    private static final String TAG = "CameraAI::Activity";
    private Mat mOutputFrame;

    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat outPutImg;
    private ProgressDialog dialog;
    private int data_number;
    private Button shutter_send;
    private ImageView result_show ;
    private Button kakutei ;
    private Button take_again_send;
    private kyudoDBOpenHelper helper;
    private SQLiteDatabase db;
    private List<Mat> finalcorner=new ArrayList<>();
    private Mat finalids;


    private final Handler handler = new Handler();


    private final BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                Log.i(TAG, "OpenCV loaded successfully");
                mOpenCvCameraView.enableView();
            } else {
                super.onManagerConnected(status);
            }
        }
    };

    public CameraActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        setContentView(R.layout.camera_view);

        Intent intent=getIntent();
        data_number=intent.getIntExtra("NUMBER",0);

        shutter_send=findViewById(R.id.shutter_send);
        result_show =findViewById(R.id.result_image_view);
        kakutei = findViewById(R.id.final_check_send);
        take_again_send =findViewById(R.id.take_again_send);


        take_again_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOpenCvCameraView.enableView();
                take_again_send.setVisibility(SurfaceView.INVISIBLE);
                result_show.setVisibility(SurfaceView.INVISIBLE);
                kakutei.setVisibility(SurfaceView.INVISIBLE);
                mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
                shutter_send.setVisibility(SurfaceView.VISIBLE);
            }
        });


        shutter_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //shutter=1;
                //Toast.makeText(CameraActivity.this,"took", Toast.LENGTH_LONG).show();
                FileWriter writer = null;
//                String dirArr= Objects.requireNonNull(CameraActivity.this.getExternalFilesDir(null)).getAbsolutePath();
                String dirArr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();

                File dir=new File(dirArr+"/deeplab");
                if(!dir.exists()){
                    dir.mkdirs();
                }
                String filename=dirArr+"/deeplab/result.jpg";

                Bitmap dsc= Bitmap.createBitmap(mOutputFrame.width(), mOutputFrame.height(), Bitmap.Config.RGB_565);
                //Imgproc.cvtColor(outPutImg,outPutImg,Imgproc.COLOR_RGB2GRAY);
                Mat saveImg = new Mat();
                Core.bitwise_not(outPutImg,saveImg);
                Imgcodecs.imwrite(filename,saveImg);
                List<Mat>corners=new ArrayList<>();
                Mat ids=new Mat();
                Utils.matToBitmap(detectMarker(saveImg,corners,ids),dsc);
                finalcorner=new ArrayList<>(corners);
                finalids=ids;
                mOpenCvCameraView.disableView();
                shutter_send.setVisibility(SurfaceView.INVISIBLE);
                result_show.setImageBitmap(dsc);
                result_show.setVisibility(SurfaceView.VISIBLE);
                kakutei.setVisibility(SurfaceView.VISIBLE);
                take_again_send.setVisibility(SurfaceView.VISIBLE);

            }
        });


        //finish activity
        kakutei.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
              //  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
               // String currentDateandTime = sdf.format(new Date());
                //String fileName = Environment.getExternalStorageDirectory().getPath() +
                        //"/sample_picture_" + currentDateandTime + ".jpg";
                //Toast.makeText(CameraActivity.this, fileName + " saved", Toast.LENGTH_SHORT).show();
                Mat saveImg= new Mat(mOutputFrame.height(), mOutputFrame.width(), CvType.CV_8UC3);
                FileWriter writer = null;
//                String dirArr= Objects.requireNonNull(CameraActivity.this.getExternalFilesDir(null)).getAbsolutePath();
                String dirArr=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();

                File dir=new File(dirArr+"/deeplab");
                if(!dir.exists()){
                    dir.mkdirs();
                }
                String filename=dirArr+"/deeplab/result.jpg";
                BitmapDrawable temp_drawable=(BitmapDrawable)result_show.getDrawable();
                Bitmap out_bit = temp_drawable.getBitmap();
                Utils.bitmapToMat(out_bit,saveImg);
                //Core.bitwise_not(saveImg,saveImg);


                dialog = new ProgressDialog(CameraActivity.this);
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setProgress(0);
                dialog.setMessage("解析中");
//                final CameraActivity.SampleTask task = new CameraActivity.SampleTask(dialog);
//                task.execute(out_bit);
                try{

                    helper = new kyudoDBOpenHelper(getApplicationContext(),CameraActivity.this);



                    db = helper.getReadableDatabase();

                db.execSQL("DELETE FROM connection where messge=1");

                // INSERT文を準備
                SQLiteStatement pstmt = db.compileStatement("INSERT INTO connection (messge,image,name_order)VALUES(1,?,?)");
                // BLOBデータを設定


                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String textfilename = dirArr + "/deeplab/result.txt";

                FileInputStream oFIS = new FileInputStream(filename);

                FileInputStream namel = new FileInputStream(textfilename);
                File FIS = new File(filename);
                File names = new File(textfilename);
                pstmt.bindBlob(1, Files.readAllBytes(FIS.toPath()));
                pstmt.bindBlob(2, Files.readAllBytes(names.toPath()));
                List<String> namelist= IOUtils.readLines(namel);

                // INSERT文を実行
                pstmt.execute();
                pstmt.close();
                oFIS.close();
                db.close();


                //TODO ここでTensor flow
                    SharedPreferences preferenceManager= PreferenceManager.getDefaultSharedPreferences(CameraActivity.this);
                    int tatemasu=10;
                    int yokomasu=30;

                        tatemasu=Integer.parseInt(preferenceManager.getString("tatemasu","10"));
                        yokomasu=Integer.parseInt(preferenceManager.getString("yokomasu","30"));


                    dialog.setMax(tatemasu*yokomasu);
                    DetectImage detectImage = new DetectImage(CameraActivity.this,CameraActivity.this, saveImg, tatemasu,yokomasu,namelist.size(),data_number,namelist,dialog);
                    RunAI runAI=new RunAI(detectImage);
                    dialog.show();
                    runAI.start();




                } catch (IOException e) {
                    e.printStackTrace();
                }catch (ParseException | NumberFormatException ignored){
                    Toast.makeText(CameraActivity.this,"設定に誤りがあります",Toast.LENGTH_LONG).show();
                    take_again_send.callOnClick();
                }

                // くるくるを消去


            }
        });



        getPermissionCamera(this);

        mOpenCvCameraView = findViewById(R.id.tutorial1_activity_java_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);


    }


    private static void getPermissionCamera(Activity activity) {
        if (ContextCompat.checkSelfPermission(
                activity,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = new String[]{Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(
                    activity,
                    permissions,
                    0);

        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    private Mat detectMarker(Mat inputImage, List<Mat> corners, Mat markerIds) {
        Dictionary dictionary = Aruco.getPredefinedDictionary(Aruco.DICT_4X4_50);

        //Mat inputImage = Imgcodecs.imread("F:\\users\\smk7758\\Desktop\\marker_2018-12-01_test.png");
        Mat destimg=new Mat();
        //ArrayList markerIds=new ArrayList<Integer>();
        DetectorParameters parameters = DetectorParameters.create();


        Aruco.detectMarkers(inputImage, dictionary, corners, markerIds, parameters);




        Aruco.drawDetectedMarkers(inputImage, corners, markerIds);
        //Log.d(TAG, String.valueOf(markerIds));

        return inputImage;
    }




    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {

        mOutputFrame = new Mat(height, width, CvType.CV_8UC1);
        outPutImg = new Mat(height, width, CvType.CV_8UC1);

    }

    public void onCameraViewStopped() {


    }

    @Override


    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        //Imgproc.Canny(inputFrame.rgba(), mOutputFrame, 80, 100);


        Mat dst = new Mat();
        outPutImg=inputFrame.gray();
        Core.bitwise_not(inputFrame.gray(),dst);


        List<Mat> corners = new ArrayList<>();
        Mat ids = new Mat();

        mOutputFrame = detectMarker(dst,corners,ids);

        if (corners.size()==4){
            //4
            Set<Integer> cornerIdCheck=new HashSet<>();
            for (int i=0;i<corners.size();i++){
                cornerIdCheck.add((int)ids.get(i,0)[0]);
            }
            List<Integer> cornerstemplist=new ArrayList<>(cornerIdCheck);
            Collections.sort(cornerstemplist);
            if (cornerstemplist.get(3)==3) handler.post(new Runnable() {
                @Override
                public void run() {
                    shutter_send.performClick();
                }
            });
            
        }



        return  mOutputFrame;
    }
    class RunAI extends Thread{
        final DetectImage detectImage;
        RunAI(DetectImage detectImage){
            this.detectImage=detectImage;
        }
        @Override
        public void run() {
            detectImage.detectMarkers(finalcorner,finalids);
            Handler mainThredHandler=new Handler(Looper.getMainLooper());
            mainThredHandler.post(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    Intent intent = new Intent(getApplication(), EditAIResult.class);
                    intent.putExtra("tatesiki", data_number);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}





