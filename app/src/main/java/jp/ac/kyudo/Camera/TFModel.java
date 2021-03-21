package jp.ac.kyudo.Camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.Tensor;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import jp.ac.kyudo.R;

import static org.opencv.core.CvType.CV_32F;
import static org.opencv.core.CvType.CV_32FC1;

public class TFModel {

    private static final int MAXWIDTH = 64;
    private static final int MAXHEIGHT = 64;
    Context context;

    TFModel(Context context){
        this.context=context;
    }
    private TensorFlowInferenceInterface mTFInterface=null;

    public boolean isInitialized() {return mTFInterface!=null;}

    //初期化
    public boolean initialize() {
        //モデルファイルを開く
       InputStream is=context.getResources().openRawResource(R.raw.model);

        //TensorFlowにモデルファイルを読み込む
        mTFInterface=new TensorFlowInferenceInterface(is);

        //モデルファイルを閉じる
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    //実行
    public int run(Mat mat) {

        if(mTFInterface==null || mat==null) return Integer.parseInt(null);

        int wOrg=mat.width();
        int hOrg=mat.height();
        int w=wOrg,h=hOrg;

//        //BitmapをTensorFlowに与えるデータ形式に変換する
//        //  …今回のモデルではRGBのbyte配列
//        int srcInt[]=new int[w*h];
//        src.getPixels(srcInt,0,w,0,0,w,h);
//
//        float[] srcfloat=new float[w*h];
//        byte[] bytes=new byte[h*w];
//        for(int i=0,c=srcInt.length;i<c;i++) {
//            int col=srcInt[i];
//            float rChannel = (col >> 16) & 0xFF;
//            float gChannel = (col >> 8) & 0xFF;
//            float bChannel = (col) & 0xFF;
//            bytes[i]= (byte) ((rChannel + gChannel + bChannel) / 3 / 255.f);//B
//        }


        Mat floatMat = new Mat();
        mat.convertTo(floatMat, CV_32FC1);


        float[] buff = new float[(int) (floatMat.total() * floatMat.channels())];
        floatMat.get(0, 0, buff);
        for (int i=0;i<buff.length;i++){
            buff[i]= (float) (buff[i]/255.0);
        }


        //TensorFlow実行
        //  inputName,outputNameにはモデルで規定された名前を与える
        float[] resultInt = new float[10];   //戻り値:推定された物体のIDの配列。0なら背景
        mTFInterface.feed("conv2d_12_input",buff,1,h,w,1);
        mTFInterface.run(new String[]{"dense_9/Softmax"});//,true);
        mTFInterface.fetch("dense_9/Softmax", resultInt);



        int intMax=0;

        for(int i = 0; i < 5; i++) {// 要素0番目のは代入済みのため1番目から開始する

            //intMaxに代入されている値と配列の要素を比較して、配列の要素のほうが大きい場合値を上書きする.
            if (resultInt[intMax] < resultInt[i]) {
                intMax = i;
            }
        }

            return intMax;
    }


}