package jp.ac.kyudo.MainSelect;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jp.ac.kyudo.R;

public class kyudoDBOpenHelper extends SQLiteOpenHelper {
    // データーベースのバージョン
    public static final int DATABASE_VERSION = 1;
    // データーベース名
    public static final String DATABASE_NAME = "kyudodb";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + "connection";

    private InputStream connection;
    private InputStream hit_record;
    private InputStream member_list;
    private InputStream yumi_list;
    private InputStream yumi_user;
    Activity activity;
    Context context;

    private File dbPath;
    private boolean createDatabase = false;


    public kyudoDBOpenHelper(Context context, Activity activity) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        connection=context.getResources().openRawResource(R.raw.kyudodb_connection);
        hit_record=context.getResources().openRawResource(R.raw.kyudodb_hit_record);
        member_list=context.getResources().openRawResource(R.raw.kyudodb_member_list);
        yumi_list=context.getResources().openRawResource(R.raw.kyudodb_yumi_list);
        yumi_user=context.getResources().openRawResource(R.raw.kyudodb_yumi_user);
        this.context=context;
        this.dbPath = context.getDatabasePath("kyudodb");
        this.activity=activity;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public synchronized SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase database = super.getReadableDatabase();
        if (createDatabase) {
            try {
                database = copyDatabase(database);
            } catch (IOException e) {
                Toast.makeText(context,"failed", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(context,"failed", Toast.LENGTH_LONG).show();
            }
        }
        return database;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase database = super.getWritableDatabase();
        if (createDatabase) {
            try {
                database = copyDatabase(database);
            } catch (IOException e) {
            }
        }
        return database;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private SQLiteDatabase copyDatabase(SQLiteDatabase database) throws IOException {
        // dbがひらきっぱなしなので、書き換えできるように閉じる
        database.close();

        // コピー！
        InputStream input = context.getAssets().open("backup.db");
//        verifyStoragePermissions(activity);
//        String dirArr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
////        String path = dirArr + "/deeplab/backup.db";
//        File bkfile = new File(path);
//        InputStream input = new FileInputStream(bkfile);
        OutputStream output = new FileOutputStream(this.dbPath);
        copy(input, output);

        createDatabase = false;
        // dbを閉じたので、また開く
        return super.getWritableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String connectionqueries=null;
        String hitrecordqueries=null;
        String memberlistqueries=null;
        String yumilistqueries=null;
        String yumiuserqueries=null;
        try {
            connectionqueries = IOUtils.toString(connection);
            hitrecordqueries=IOUtils.toString(hit_record);
            memberlistqueries=IOUtils.toString(member_list);
            yumilistqueries=IOUtils.toString(yumi_list);
            yumiuserqueries=IOUtils.toString(yumi_user);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String query : connectionqueries.split(";")) {
            if (query.contains("--"))continue;
            db.execSQL(query);
        }
        for (String query : hitrecordqueries.split(";")) {
            if (query.contains("--"))continue;
            db.execSQL(query);
        }
        for (String query : memberlistqueries.split(";")) {
            if (query.contains("--"))continue;
            db.execSQL(query);
        }
        for (String query : yumilistqueries.split(";")) {
            if (query.contains("--"))continue;
            db.execSQL(query);
        }
        for (String query : yumiuserqueries.split(";")) {
            if (query.contains("--"))continue;
            db.execSQL(query);
        }

//        super.onOpen(db);
//        this.createDatabase = true;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.disableWriteAheadLogging();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // アップデートの判別、古いバージョンは削除して新規作成
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    // CopyUtilsからのコピペ
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
}
