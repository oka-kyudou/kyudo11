package jp.ac.kyudo.Member;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import jp.ac.kyudo.MainSelect.kyudoDBOpenHelper;
import jp.ac.kyudo.R;

public class MemberLeave extends AppCompatActivity  {

    // Mapのキー
    private final String[] FROM = {"name","check"};
    // リソースのコントロールID
    private final int[] TO = {R.id.textView2,R.id.checkBox};
    // LoaderのID
    private static final int LOADERID = 0;
    // くるくるダイアログ
    private ProgressDialog dialog;
    //削除リスト
    List<String> deletemember=new ArrayList<>();
    private kyudoDBOpenHelper helper;
    private SQLiteDatabase db;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_settings_all);
        Toolbar toolbar =findViewById(R.id.toolbar4);
        // くるくる設定
        dialog = new ProgressDialog(MemberLeave.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("接続中");
        dialog.show();
        toolbar.setTitle("退部する部員を選択");
        toolbar.setTitleTextColor(Color.parseColor("white"));
        String[] namel = new String[0];


        if (helper == null) {
            helper = new kyudoDBOpenHelper(getApplicationContext(),this);
        }
        if (db == null) {
            db = helper.getReadableDatabase();
        }
            //Toast.makeText(MemberSet.this,"connected", Toast.LENGTH_LONG).show();
//        SQLiteDatabase.deleteDatabase(MemberLeave.this.getDatabasePath(helper.getDatabaseName()));


        Cursor rs = db.rawQuery("SELECT memberID ,family_name, first_name FROM member_list;",null);

            List<String> namelist=new ArrayList<>();
            rs.moveToFirst();
            while (rs.moveToNext()) {
                //int id=rs.getInt(1);
                //int location=rs.getInt(2);
                String tid = rs.getString(1);
                String time = rs.getString(2);
                String id = rs.getString(0);
                //int sex = rs.getInt(4);
                //dataset.add(id+"  "+tid+" "+time);
                //global.setTestString(tid+" "+time);
                namelist.add(id + "  " + tid + " " + time);
            }

//                Log.d("namel", namel.toString());
            namel=namelist.toArray(new String[0]);
        // NameList
        ListView lv = findViewById(R.id.listVIew);
        // リストデータの生成
        List<Map<String,Object>> list = new ArrayList<>();
        for (String datum : namel) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", datum);
            map.put("check", false);
            list.add(map);
        }
        // アダプターの設定
        final MemberListAdapter adapter = new MemberListAdapter(MemberLeave.this,
                list,R.layout.list_member_leave,FROM,TO);
        lv.setAdapter(adapter);
        dialog.dismiss();

        // イベント
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean newState=!adapter.checkList.get(position);
                adapter.checkList.put(position,newState);
                CheckBox ch=view.findViewById(R.id.checkBox);
                ch.setChecked(newState);

                //Log.d("checkedChanged",String.valueOf(position)+"番目がクリックされました。");
            }
        });
        Button sendbutton=findViewById(R.id.member_leave_send);
        final String[] finalData = namel;
        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Boolean> chlist=new ArrayList<>(adapter.checkList.values());
                for(int i=0;i<adapter.checkList.size();i++){
                    if (!chlist.get(i))continue;
                    deletemember.add(finalData[i]);
                }
                StringBuilder dispalyNameList = new StringBuilder();
                for (String names:deletemember){
                    dispalyNameList.append(names);
                    dispalyNameList.append("\n");
                }
                new AlertDialog.Builder(MemberLeave.this)
                        .setTitle("以下の部員の登録を削除します")
                        .setMessage(dispalyNameList)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // OK button pressed
//                                progressDialog.show();
//
//                                if (null != lm.getLoader(1)) {
//                                    lm.initLoader(1, null, MemberLeave.this);
//                                }
//                                getSupportLoaderManager().initLoader(1,null,  callback2);
                                if (helper == null) {
                                    helper = new kyudoDBOpenHelper(getApplicationContext(),MemberLeave.this);
                                }
                                if (db == null) {
                                    db = helper.getReadableDatabase();
                                }
                                StringBuilder columns=new StringBuilder();
                                StringBuilder col2=new StringBuilder();


                                for (int i=0; i<deletemember.size();i++) {
                                    db.execSQL("DROP TABLE IF EXISTS 'hitrecord_temp'");
                                    db.execSQL("BEGIN TRANSACTION");
                                    db.execSQL("alter table 'hit_record' rename to 'hitrecord_temp'");
                                    db.execSQL("COMMIT TRANSACTION");
                                    Cursor C=db.rawQuery("pragma table_info(hitrecord_temp)",null);
                                    db.execSQL("create table hit_record (hitID int not null,date date not null,[0] int default '16')");
//                                    db.execSQL("insert into hit_record(yumiID, date) select yumiID,date from hitrecord_temp");
                                    C.moveToFirst();

                                    while (C.moveToNext()){
                                        int flag=0;
                                        for (String name:deletemember) {
                                            if (C.getString(1).equals(name.substring(0, 6)))
                                                flag++;
                                        }
                                        if (flag>0)continue;
                                        if (C.getString(1).equals("yumiID"))continue;
                                        if (C.getString(1).equals("date"))continue;
                                        if (C.getString(1).equals("0")) continue;
                                        if (C.getString(1).equals("name_order")) continue;
                                        db.execSQL("alter table hit_record add column '"+C.getString(1)+"' int DEFAULT '16'");
                                        //TODO これ直す
                                        columns.append("[").append(C.getString(1)).append("] ,");
                                        col2.append("[").append(C.getString(1)).append("] ,");
                                    }
                                    if((!columns.toString().isEmpty())&&(!col2.toString().isEmpty())) {
                                        String col = columns.substring(0, columns.length() - 1);
                                        String colcol = col2.substring(0, columns.length() - 1);
                                        db.execSQL("insert into hit_record(hitID,date," + col + ") select hitID,date," + colcol + " from hitrecord_temp");
                                    }


                                    db.execSQL("update yumi_user set memberID=null where memberID = "+deletemember.get(i).substring(0,6));
                                    db.execSQL("delete from member_list where memberID= "+deletemember.get(i).substring(0,6));

//                    Log.d("sql","alter table kyudodb.hit_record drop " +mlist.get(i).substring(0,6));
                                    C.close();
                                }


                                finish();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();


            }
        });

    }
//    private final LoaderManager.LoaderCallbacks callback2=(LoaderManager.LoaderCallbacks)(new LoaderManager.LoaderCallbacks(){
//
//        @NonNull
//        @Override
//        public Loader onCreateLoader(int id, @Nullable Bundle args) {
//             return new DeleteMembers(getApplication(),deletemember);
//        }
//
//        @Override
//        public void onLoadFinished(@NonNull Loader loader, Object data) {
//            getSupportLoaderManager().destroyLoader(1);
//            Log.d("loadfonosh","finished");
//            dialog.dismiss();
//            finish();
//
//        }
//
//        @Override
//        public void onLoaderReset(@NonNull Loader loader) {
//
//        }
//    });

    // カスタムアダプター
    private class MemberListAdapter extends SimpleAdapter {

        // 外部から呼び出し可能なマップ
        public Map<Integer,Boolean> checkList = new HashMap<>();

        public MemberListAdapter(Context context, List<? extends Map<String, ?>> data,
                         int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);

            // 初期値を設定する
            for(int i=0; i<data.size();i++){
                Map map = data.get(i);
                checkList.put(i,(Boolean)map.get("check"));
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            CheckBox ch = view.findViewById(R.id.checkBox);
            ch.setChecked(checkList.get(position));
            // チェックの状態が変化した場合はマップに記憶する
            ch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean newState=!checkList.get(position);
                    checkList.put(position,newState);

                }
            });

            return view;
        }
    }

//    public Loader<String[]> onCreateLoader(int id, @Nullable Bundle args) {
//       return new getMemberList(getApplication());
//    }
//    public void onLoadFinished(@NonNull Loader<String[]> loader,  String[] data) {
//        getSupportLoaderManager().destroyLoader(LOADERID);
//
////        // NameList
////        ListView lv = findViewById(R.id.listVIew);
////        // リストデータの生成
////        List<Map<String,Object>> list = new ArrayList<>();
////        for (String datum : data) {
////            Map<String, Object> map = new HashMap<>();
////            map.put("name", datum);
////            map.put("check", false);
////            list.add(map);
////        }
////        // アダプターの設定
////        final MemberListAdapter adapter = new MemberListAdapter(MemberLeave.this,
////                list,R.layout.list_member_leave,FROM,TO);
////        lv.setAdapter(adapter);
////        dialog.dismiss();
////
////        // イベント
////        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////            @Override
////            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                boolean newState=!adapter.checkList.get(position);
////                adapter.checkList.put(position,newState);
////                CheckBox ch=view.findViewById(R.id.checkBox);
////                ch.setChecked(newState);
////
////                //Log.d("checkedChanged",String.valueOf(position)+"番目がクリックされました。");
////            }
////        });
////        Button sendbutton=findViewById(R.id.member_leave_send);
////        final LoaderManager lm = getSupportLoaderManager();
////        final String[] finalData = data;
////        sendbutton.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                List<Boolean> chlist=new ArrayList<>(adapter.checkList.values());
////                   for(int i=0;i<adapter.checkList.size();i++){
////                       if (!chlist.get(i))continue;
////                       deletemember.add(finalData[i]);
////                   }
////                   StringBuilder dispalyNameList = null;
////                   for (String names:deletemember){
////                       dispalyNameList.append(names).append("\n");
////                   }
////                   final ProgressDialog progressDialog=dialog;
////                new AlertDialog.Builder(MemberLeave.this)
////                        .setTitle("以下の部員の登録を削除します")
////                        .setMessage(dispalyNameList)
////                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
////                            @Override
////                            public void onClick(DialogInterface dialog, int which) {
////                                // OK button pressed
////                                progressDialog.show();
////
////                                if (null != lm.getLoader(1)) {
////                                    lm.initLoader(1, null, MemberLeave.this);
////                                }
////                                getSupportLoaderManager().initLoader(1,null,  callback2);
////                            }
////                        })
////                        .setNegativeButton("Cancel", null)
////                        .show();
////
////
////            }
////        });
//
//
//    }


//    public void onLoaderReset(@NonNull Loader<String[]> loader) {
//
//    }


//     static class getMemberList extends AsyncTaskLoader<String[]> {
//        public getMemberList(@NonNull Context context) {
//            super(context);
//            mResult    = null;
//            mIsStarted = false;
//        }
//
//        private String[] mResult;
//        private boolean mIsStarted;
//
//
//        @Override
//        public String[] loadInBackground() {
//            //
//            // ここで非同期の処理をする
//            //
//            String[] namel = new String[0];
//
//            try {
//
//                Connection conn = MySqlConnect.getConnection();
//                //Toast.makeText(MemberSet.this,"connected", Toast.LENGTH_LONG).show();
//                Statement stmt = conn.createStatement();
//                ResultSet rs = stmt.executeQuery("SELECT memberID ,family_name, first_name FROM `kyudodb`.member_list;");
//
//                List<String> namelist=new ArrayList<>();
//                while (rs.next()) {
//                    //int id=rs.getInt(1);
//                    //int location=rs.getInt(2);
//                    String tid = rs.getString(2);
//                    String time = rs.getString(3);
//                    String id = rs.getString(1);
//                    //int sex = rs.getInt(4);
//                    //dataset.add(id+"  "+tid+" "+time);
//                    //global.setTestString(tid+" "+time);
//                    namelist.add(id + "  " + tid + " " + time);
//                }
//
////                Log.d("namel", namel.toString());
//                namel=namelist.toArray(new String[0]);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return namel;
//        }
//            @Override
//        public void deliverResult(String[] result) {
//            // 非同期処理の結果を保存しておく
//            mResult = result;
//            super.deliverResult(result);
//        }
//        @Override
//        protected void onStartLoading() {
//            // 呼び出し元のActivityが回転などで再作成されるとinitLoaderを再度呼ばなければならない
//            // initLoaderがよばれるとココに来るが再度forceLoadしてしまうと
//            // 実行中のloadInBackgroundは破棄されもう一度loadInBackgroundが開始されてしまう
//            // 実行中のloadInBackgroundが無い時だけ実行を開始し終了している時は直ちに結果を返す
//            if (null != mResult) {
//                deliverResult(mResult);
//                return;
//            }
//            if ((! mIsStarted) || takeContentChanged()) {
//                forceLoad();
//            }
//        }
//        @Override
//        protected void onForceLoad() {
//            super.onForceLoad();
//            mIsStarted = true;
//        }
//    }
//    static class DeleteMembers extends AsyncTaskLoader {
//        List<String> mlist=new ArrayList<>();
//        public DeleteMembers(@NonNull Context context,List<String> memberlist) {
//            super(context);
//            mlist=memberlist;
//        }
//
//        @Nullable
//        @Override
//        public Object loadInBackground() {
//
//            Connection conn = null;
//
//          try {
//                conn = MySqlConnect.getConnection();
//                Statement stmt = conn.createStatement();
//                for (int i=0; i<mlist.size();i++) {
//                    stmt.executeQuery("alter table kyudodb.hit_record drop " +mlist.get(i).substring(0,6));
//                    stmt.executeUpdate("update kyudodb.yumi_user set memberID=null where memberID = "+mlist.get(i).substring(0,6));
//                    stmt.executeQuery("delete from kyudodb.member_list where memberID= "+mlist.get(i).substring(0,6));
//
////                    Log.d("sql","alter table kyudodb.hit_record drop " +mlist.get(i).substring(0,6));
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//
//            return null;
//        }
//    }

}
