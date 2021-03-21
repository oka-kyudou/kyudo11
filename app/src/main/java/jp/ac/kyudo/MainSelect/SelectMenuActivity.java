package jp.ac.kyudo.MainSelect;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.sax.StartElementListener;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.print.PrintHelper;

import com.github.mikephil.charting.charts.BarChart;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.Random;

import jp.ac.kyudo.Camera.MemberSet;
import jp.ac.kyudo.Edit.EditRecord;
import jp.ac.kyudo.Member.MemberJoinAll;
import jp.ac.kyudo.Member.MemberLeave;
import jp.ac.kyudo.R;
import jp.ac.kyudo.Report.ItemListActivity;
import jp.ac.kyudo.Yumi.YumiMain;

public class SelectMenuActivity extends AppCompatActivity {

    private TextView textView;
    private DialogFragment dialogFragment;
    private FragmentManager fragmentManager;
    kyudoDBOpenHelper helper;
    SQLiteDatabase db;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectmenu);


        if (helper == null) {
            helper = new kyudoDBOpenHelper(getApplicationContext(),this);
        }
        if (db == null) {
            db = helper.getReadableDatabase();
        }

        BarChart barChart=findViewById(R.id.barChart);

        TotalReport totalReport=new TotalReport(SelectMenuActivity.this,barChart);
//TODO パーミッション
        verifyStoragePermissions(this);


        final ImageButton camera_send=findViewById(R.id.camera_send);
        final ImageButton member_send = findViewById(R.id.member_send);
        final ImageButton edit_send=findViewById(R.id.edit_send);
        final ImageButton report_send=findViewById(R.id.report_send);
        final ImageButton yumi_send=findViewById(R.id.yumi_send);
        final Button settings_send=findViewById(R.id.setting_button);
        final Button print_send=findViewById(R.id.printbutton);
        final Button save_backup=findViewById(R.id.savebackup);
        final Button restore_backup =findViewById(R.id.restorebackup);

        restore_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(SelectMenuActivity.this);
                String dirArr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
                String path = dirArr + "/deeplab/backup.db";
                File bkfile = new File(path);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//日付時刻の書式定義

                builder.setMessage("バックアップを復元しますか？\n（この操作は取り消せません）\n バックアップ日："+simpleDateFormat.format(bkfile.lastModified()));
                builder.setPositiveButton("復元", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        restorebackup();

                        try {
                            importDB();
                            recreate();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                builder.show();

            }
        });

        save_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               backupdb();
        }});

        print_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPhotoPrint();
            }
        });

        settings_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings_send.startAnimation(AnimationUtils.loadAnimation(SelectMenuActivity.this,R.anim.btn_click));
                Intent intent=new Intent(getApplication(),prefences.class);
                startActivity(intent);

//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.settings_layout, new prefences()).addToBackStack(null).commit();
            }
        });
        camera_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(SelectMenuActivity.this,R.anim.rotate);
//                camera_send.startAnimation(rotateAnimation);
                camera_send.startAnimation(AnimationUtils.loadAnimation(SelectMenuActivity.this,R.anim.btn_click));
                Intent intent = new Intent(getApplication(), MemberSet.class);
                startActivity(intent);
            }
        });
        member_send.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               //Intent intent = new Intent(getApplication(),sampleActivity.class);
                                               member_send.startAnimation(AnimationUtils.loadAnimation(SelectMenuActivity.this,R.anim.btn_click));

                                               fragmentManager = getSupportFragmentManager();

                                               // DialogFragment を継承したAlertDialogFragmentのインスタンス
                                               dialogFragment = new AlertDialogFragment();
                                               // DialogFragmentの表示
                                               dialogFragment.show(fragmentManager, "option set dialog");
                                           }
                                       });
        edit_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_send.startAnimation(AnimationUtils.loadAnimation(SelectMenuActivity.this,R.anim.btn_click));
                // ダイアログクラスをインスタンス化
                CalendarDialogFragment dialog = new CalendarDialogFragment();
                dialog.setCancelable(false);
                // 表示  getFagmentManager()は固定、sampleは識別タグ
                dialog.show(getSupportFragmentManager(),"dateSelect");



            }
        });
        report_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                report_send.startAnimation(AnimationUtils.loadAnimation(SelectMenuActivity.this,R.anim.btn_click));
                Intent intent = new Intent(getApplication(), ItemListActivity.class);
                startActivity(intent);
            }
        });
        yumi_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yumi_send.startAnimation(AnimationUtils.loadAnimation(SelectMenuActivity.this,R.anim.btn_click));
                startActivity(new Intent(getApplication(), YumiMain.class));
            }
        });


    }

    public void onReturnValue(String value) {
    if (value!=null) {
            Intent intent = new Intent(getApplication(), EditRecord.class);
            intent.putExtra("date", Integer.valueOf(value));
            startActivity(intent);

        }
    }


    public void callmemberactivity(int idx){
        if (idx==0){
            Intent intent=new Intent(getApplication(), MemberLeave.class);
            startActivity(intent);
        }
        if (idx==1){
            startActivity(new Intent(getApplication(), MemberJoinAll.class));
        }
    }


    // DialogFragment を継承したクラス
    public static class AlertDialogFragment extends DialogFragment {
        // 選択肢のリスト
        private String[] menulist = {"退部","入部"};

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

            // タイトル
            alert.setTitle("機能を選択");
            alert.setItems(menulist, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int idx) {
                    // 選択１
                    if (idx == 0) {
                        callActivity(idx);
                    }
                    // 選択２
                    else if (idx == 1) {
                        callActivity(idx);
                    }

                }
            });

            return alert.create();
        }


        private void callActivity(int idx) {
            SelectMenuActivity mainActivity = (SelectMenuActivity) getActivity();
            if(mainActivity!= null) {
                mainActivity.callmemberactivity(idx);
            }
        }
    }
    public void doPhotoPrint(){
        PrintHelper printHelper=new PrintHelper(SelectMenuActivity.this);
        printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.markers);
        String job_name="marker_print";
        printHelper.printBitmap(job_name,bitmap);
    }

    void backupdb(){
        try {
            db.close();
            String path = db.getPath();
            File dbfile = new File(path);
            FileInputStream fis = new FileInputStream(dbfile);

//            String dirArr= Objects.requireNonNull(SelectMenuActivity.this.getExternalFilesDir(null)).getAbsolutePath();
            String dirArr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();

            File dir=new File(dirArr+"/deeplab");
            if(!dir.exists()){
                dir.mkdirs();
            }
            String OutputFileName=dirArr+"/deeplab/backup.db";
            FileOutputStream output=new FileOutputStream(OutputFileName);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer))>0){
                output.write(buffer, 0, length);
            }
            output.flush();
            output.close();
            fis.close();
            Toast.makeText(SelectMenuActivity.this,"バックアップが保存されました",Toast.LENGTH_LONG).show();

        }catch (FileNotFoundException e){
            e.printStackTrace();
            Toast.makeText(SelectMenuActivity.this,"File not Found!",Toast.LENGTH_LONG).show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    void restorebackup(){
        try {
            db.close();

            String dirArr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
            String path = dirArr + "/deeplab/backup.db";
            File bkfile = new File(path);
            InputStream fis = new FileInputStream(bkfile);
            String dbpath = db.getPath();

            File dbfile = new File(dbpath);
            File dbPath=SelectMenuActivity.this.getDatabasePath("kyudodb");


            dbfile.delete();

            OutputStream output=new FileOutputStream(dbPath);
//            byte[] buffer = new byte[1024];
//            int length;
//            while ((length = fis.read(buffer))>0){
//                output.write(buffer, 0, length);
//            }
//            output.flush();
//            output.close();
//            copy(fis,output);
//
//            fis.close();
//            db=helper.getReadableDatabase();
            Toast.makeText(SelectMenuActivity.this,"バックアップが復元されました",Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {
            Toast.makeText(SelectMenuActivity.this,"BackupFileNotFound",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    private static int copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024 * 4];
        int count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
    private static final int REQUEST_EXTERNAL_STORAGE_CODE = 0x01;
    private static String[] mPermissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    private static void verifyStoragePermissions(Activity activity) {
        int readPermission = ContextCompat.checkSelfPermission(activity, mPermissions[0]);
        int writePermission = ContextCompat.checkSelfPermission(activity, mPermissions[1]);

        if (writePermission != PackageManager.PERMISSION_GRANTED ||
                readPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    mPermissions,
                    REQUEST_EXTERNAL_STORAGE_CODE
            );
        }
    }

    private void importDB() {
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
        File sd = new File(dir);
        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;
        String dirArr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
        String path = "Documents/deeplab/backup.db";
        kyudoDBOpenHelper helper = new kyudoDBOpenHelper(SelectMenuActivity.this, SelectMenuActivity.this);
        SQLiteDatabase db = helper.getReadableDatabase();
        String backupDBPath = db.getPath();
        File currentDB = new File(sd, path);
        File backupDB = new File(backupDBPath);


        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(this, "復元に成功しました。", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

