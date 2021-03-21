package jp.ac.kyudo.Camera;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.aruco.Aruco;
import org.opencv.aruco.DetectorParameters;
import org.opencv.aruco.Dictionary;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.img_hash.ImgHashBase;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import jp.ac.kyudo.MainSelect.kyudoDBOpenHelper;

import static org.opencv.core.CvType.CV_32FC1;

public class DetectImage {
    Mat image;
    int height;
    int width;
    int namelen;
    int tatesiki;
    List<String> namelist=new ArrayList<>();
    private Context context;
    private kyudoDBOpenHelper helper ;
    SQLiteDatabase db;
    Activity activity;
    ProgressDialog dialog;
    DetectImage(Activity activity,Context context,Mat image, int height, int width,int namelen,int tatesiki,List<String> namelist, ProgressDialog dialog){
        this.context=context;
        this.image=image;
        this.height=height;
        this.width=width;
        this.namelen=namelen;
        this.tatesiki=tatesiki;
        this.namelist=namelist;
        this.dialog=dialog;
        this.activity=activity;

    }

   public void detectMarkers(List<Mat> corners, Mat markerIds) {
        int x_mesure=width;
        int y_mesure=height;
        Mat inputImage=new Mat();
        Dictionary dictionary = Aruco.getPredefinedDictionary(Aruco.DICT_4X4_50);



       // List<Mat> corners=new ArrayList<>();
//       String dirArr= Objects.requireNonNull(context.getExternalFilesDir(null)).getAbsolutePath();
       String dirArr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
       // Mat markerIds = new Mat();
        DetectorParameters parameters = DetectorParameters.create();
       Imgproc.cvtColor(image,inputImage,Imgproc.COLOR_RGBA2RGB);
    //    Aruco.detectMarkers(inputImage, dictionary, corners, markerIds, parameters);
       double[][] cornercoo=new double[4][2];
       for (int i=0;i<4;i++) {
           cornercoo[(int) markerIds.get(i, 0)[0]] = corners.get(i).get(0, 0);
       }

       float[] srcPoint = new float[8];
       int temp=0;
       for (int i=0;i<4;i++){
           for (int j=0;j<2;j++){
               srcPoint[temp]= (float) cornercoo[i][j];
               temp++;
           }
       }
        Mat srcPointMat = new Mat(4,2, CvType.CV_32F);
        srcPointMat.put(0, 0,srcPoint );
        float x=64*x_mesure;
        float y=64*y_mesure;
        //変換先座標設定
        float[] dstPoint = new float[]{0,0,x,0,x,y,0,y };
//       float dstPoint[] = new float[]{x, 0, 0, 0, 0, y, x, y };
        Mat dstPointMat = new Mat(4,2,CvType.CV_32F);
        dstPointMat.put(0, 0,dstPoint );
        //変換行列作成
        Mat r_mat = Imgproc.getPerspectiveTransform(srcPointMat, dstPointMat);
//図形変換処理
        Mat dstMat = new Mat((int)y,(int)x,CvType.CV_8UC1);
        Imgproc.warpPerspective(inputImage, dstMat, r_mat, dstMat.size(),Imgproc.INTER_LINEAR);



        File dir=new File(dirArr+"/deeplab/output");
        if (!dir.exists()){
            dir.mkdirs();
        }
        Imgcodecs.imwrite(dirArr+"/deeplab/dst.jpg",dstMat);
       TFModel tfModel=new TFModel(context);
        int times= (int) (x/x_mesure);
        List<Integer> result=new ArrayList<>();
        int count=0;
        tfModel.initialize();
        for (int i=0;i<x_mesure;i++){
            for (int j=0;j<y_mesure;j++){
                Mat finMat=dstMat.submat(times * j, times * (j + 1), times * i, times * (i + 1));
                final Mat tempMat=new Mat();
                Core.bitwise_not(finMat,tempMat);
                Imgcodecs.imwrite(dir.getAbsolutePath()+"/"+i+"_"+j+".jpg",tempMat);
                Bitmap bitmap = Bitmap.createBitmap(finMat.height(),finMat.width(),Bitmap.Config.ARGB_8888);
                Bitmap tempbit=toGrayscale(bitmap);
                Utils.matToBitmap(tempMat,tempbit);
                Mat ttmat=new Mat();
                Imgproc.cvtColor(tempMat,ttmat,Imgproc.COLOR_RGB2GRAY);
                result.add(tfModel.run(ttmat));
                dialog.setProgress(count++);
            }
        }

        int[][] insertsql=new int[namelen][tatesiki];
       List<List<Integer>> templist=new ArrayList<>();
        temp=0;
        List<Integer> tempresult=new ArrayList<>();

        for (int j=0;j<namelen;j++){
            int counter=0;
            for (int i=temp+y_mesure-1;i>temp+(y_mesure-tatesiki)-1; i--){
                insertsql[j][counter]=result.get(i);
                counter++;
            }
            temp+=y_mesure;
        }
       if (helper == null) {
           helper = new kyudoDBOpenHelper(context,activity);
       }
       if (db == null) {
           db = helper.getReadableDatabase();
       }

       int ID=0;
       Cursor C = db.rawQuery("select max(hitID) from hit_record", null);
       C.moveToFirst();
       ID=C.getInt(0)+1;
       for (int i=0;i<tatesiki/2;i++) {

           int tempID=ID+i;
          db.execSQL("insert into hit_record(hitID,date) values(" + tempID + ",current_date)");

       }
       for (int i=0;i<namelen;i++){
            temp=0;
           for (int j=0;j<tatesiki;j+=2){
               int tempID = ID+temp;
               int insertvalue=(insertsql[i][j+1]<<2)+insertsql[i][j];
               if (namelist.get(i).substring(0,1).equals("0"))continue;
              db.execSQL("update hit_record set ["+namelist.get(i).substring(0,6)+"]="+insertvalue+
                       " where hitID="+tempID);
              temp++;
           }
       }












    }
    public static List<Mat> flattenNonRecursive(List<Mat> list) {
        List<Mat> result = new ArrayList<>();
        LinkedList<Mat> stack = new LinkedList<>(list);
        while (!stack.isEmpty()) {
            Mat e = stack.pop();
            if (e instanceof List<?>)
                stack.addAll(0, (Collection<? extends Mat>) e);
            else
                result.add(e);
        }
        return result;
    }
    public Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public static List<Object> list(Object... args) {
        return Arrays.asList(args);
    }


}
