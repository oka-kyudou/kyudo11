package jp.ac.kyudo.Edit;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import jp.ac.kyudo.MainSelect.kyudoDBOpenHelper;
import jp.ac.kyudo.R;
import jp.ac.kyudo.MainSelect.SelectMenuActivity;

public class EditRecord extends AppCompatActivity {
    private String[] namel = new String[0];
    private List<List<Integer>> result= new ArrayList<>();
    private List<String>members=new ArrayList<>();
    private int tatesiki;
    public int date;
    private ProgressDialog dialog;
    final List<Integer> listOfSelection=new ArrayList<>();
    // Resource IDを格納するarray
    private List<Integer> imgList = new ArrayList<>();
    private kyudoDBOpenHelper helper;
    private SQLiteDatabase db;
    GridAdapter adapter;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_ai_result);
        Button addline=findViewById(R.id.plus_line);
        Button endButton = findViewById(R.id.confirm2updateDB);
        Button deleteButton = findViewById(R.id.deleteAll);
        addline.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           AlertDialog.Builder builder=new AlertDialog.Builder(EditRecord.this);
                                           builder.setMessage("列を追加しますか？\n（この操作はやり直しができません）");
                                           builder.setPositiveButton("追加", new DialogInterface.OnClickListener() {
                                               @Override
                                               public void onClick(DialogInterface dialog, int which) {
                                                   //一旦保存（確定ボタン押す）
                                                   String stdate=String.valueOf(date);
                                                   String curdate=stdate.substring(0,4)+"-"+stdate.substring(4,6)+"-"+stdate.substring(6,8);

                                                   List<Integer> templist = new ArrayList<>();
                                                   List<Integer> insertList = new ArrayList<>();
                                                   try {
                                                       if (helper == null) {
                                                           helper = new kyudoDBOpenHelper(getApplicationContext(),EditRecord.this);
                                                       }

                                                       if (db == null) {
                                                           db = helper.getReadableDatabase();
                                                       }
                                                       String datest = String.valueOf(date);
                                                       datest = datest.substring(0, 4) + "-" + datest.substring(4, 6) + "-" + datest.substring(6, 8);
                                                       Cursor C1 = db.rawQuery("select max(hitID) from hit_record where date= '" + datest + "'", null);
                                                       C1.moveToFirst();
                                                       int ID = C1.getInt(0);
                                                       for (int i = 0; i < imgList.size(); i++) {
                                                           templist.add(listOfSelection.indexOf(imgList.get(i)));
                                                       }
                                                       for (int j = 0; j < tatesiki; j += 2) {
                                                           for (int i = j * namel.length; i < namel.length * (j + 1); i++) {
                                                               insertList.add(((templist.get(i)) << 2) + templist.get(i + namel.length));
                                                           }
                                                       }
                                                       int number = 0;
                                                       int temp=0;
                                                       db.beginTransaction();
                                                       for (int i = 0; i < insertList.size(); i++) {
                                                           db.execSQL("update hit_record set [" + result.get(temp).get(0) + "] = " + insertList.get(i) + " where hitID= " + ID + " -" + number);
                                                           temp++;
                                                           if (temp == (result.size())) {
                                                               temp = 0;
                                                               number++;
                                                           }
                                                       }


                                                       C1.close();
                                                       db.setTransactionSuccessful();

                                                   } catch (Exception e) {
                                                       e.printStackTrace();
                                                   }finally {
                                                       db.endTransaction();

                                                   }
                                                   //行増やす
                                                   int ID = 0;
                                                   Cursor C = db.rawQuery("select max(hitID) from hit_record", null);
                                                   C.moveToFirst();

                                                   ID = C.getInt(0) + 1;
                                                   for (int i = 0; i < 1; i++) {

                                                       int tempID = ID + i;
                                                       db.execSQL("insert into hit_record(hitID,date) values(" + tempID + ",'"+curdate+"')");
                                                   }

                                                   C.close();
                                                   Intent reintent=new Intent(getApplication(),EditRecord.class);
                                                   reintent.putExtra("date",date);
                                                   startActivity(reintent);
                                                   finish();
                                               }
                                           });
                                           builder.show();

//                                           db.close();
//                                           //dataset の再読み込み
//
//                                               helper = new kyudoDBOpenHelper(getApplicationContext());
//                                               db = helper.getReadableDatabase();
//
//                                           result.clear();
//                                           imgList.clear();
//                                           for (int i=0;i<namel.length;i++) {
//                                               List<Integer> result_line = new ArrayList<>();
//                                               result_line.add(converttoInt(namel[i]));
//                                               Cursor c1 = db.rawQuery("select ["+converttoInt(namel[i])+"] from hit_record where [date]='"+stdate+"'" , null);
//                                               Log.d("date",stdate);
//
//                                               if (c1.moveToFirst()) {
//                                                   do {
//                                                       result_line.add(c1.getInt(0));
//                                                   } while (c1.moveToNext());
//                                               }
//                                               result.add(result_line);
//                                               Log.d("result", result_line.toString());
//                                               c1.close();
//                                           }
//                                           if(!result.isEmpty()) {
//                                               tatesiki=(result.get(0).size()-1)*2;
//                                           }
//
//                                           dialog.dismiss();
//                                           members.addAll(setmember(result,tatesiki));
//                                           for (int i = 0;i<members.size();i++){
//                                               int imageId = getResources().getIdentifier(
//                                                       members.get(i),"drawable", getPackageName());
//                                               imgList.add(imageId);
//                                           }
//                                           String[] nameOflist={"batu0","batu1","batu2","maru","none"};
//
//                                           adapter.notifyDataSetChanged();



                                       }
                                   });
        endButton.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             dialog = new ProgressDialog(EditRecord.this);
                                             dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                             dialog.setMessage("データ更新中");
                                             dialog.show();

                                             List<Integer> templist = new ArrayList<>();
                                             List<Integer> insertList = new ArrayList<>();
                                             try {
                                                 if (helper == null) {
                                                     helper = new kyudoDBOpenHelper(getApplicationContext(),EditRecord.this);
                                                 }

                                                 if (db == null) {
                                                     db = helper.getReadableDatabase();
                                                 }
                                                 String datest = String.valueOf(date);
                                                 datest = datest.substring(0, 4) + "-" + datest.substring(4, 6) + "-" + datest.substring(6, 8);
                                                 Cursor C = db.rawQuery("select max(hitID) from hit_record where date= '" + datest + "'", null);
                                                 C.moveToFirst();
                                                 int ID = C.getInt(0);
                                                 for (int i = 0; i < imgList.size(); i++) {
                                                     templist.add(listOfSelection.indexOf(imgList.get(i)));
                                                 }
                                                 for (int j = 0; j < tatesiki; j += 2) {
                                                     for (int i = j * namel.length; i < namel.length * (j + 1); i++) {
                                                         insertList.add(((templist.get(i)) << 2) + templist.get(i + namel.length));
                                                     }
                                                 }
                                                 int number = 0;
                                                 int temp=0;
                                                 for (int i = 0; i < insertList.size(); i++) {
                                                     db.execSQL("update hit_record set [" + result.get(temp).get(0) + "] = " + insertList.get(i) + " where hitID= " + ID + " -" + number);
                                                     temp++;
                                                     if (temp == (result.size())) {
                                                         temp = 0;
                                                         number++;
                                                     }
                                                 }
                                                 dialog.dismiss();
                                                 Intent intent = new Intent(getApplication(), SelectMenuActivity.class);
                                                 startActivity(intent);
                                                 finish();
                                             } catch (Exception e) {
                                                 e.printStackTrace();
                                             }
                                         }

                                     });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reintent=new Intent(getApplication(),EditRecord.class);
                reintent.putExtra("date",date);
                startActivity(reintent);
                finish();
            }
        });
        Intent intent=getIntent();
        date=intent.getIntExtra("date",0);
        String stdate=String.valueOf(date);
        stdate=stdate.substring(0,4)+"-"+stdate.substring(4,6)+"-"+stdate.substring(6,8);
        //tatesiki=4
        dialog = new ProgressDialog(EditRecord.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("データ取得中");
        dialog.show();

//        final EditRecord.GetResult task = new GetResult();
//        task.execute();
        if (helper == null) {
            helper = new kyudoDBOpenHelper(getApplicationContext(),this);
        }
        if (db == null) {
            db = helper.getReadableDatabase();
        }
        Cursor C=db.rawQuery("SELECT memberID ,family_name, first_name FROM member_list;",null);

        List<String> namelist=new ArrayList<>();
        while (C.moveToNext()) {
            //int id=rs.getInt(1);
            //int location=rs.getInt(2);
            String tid = C.getString(1);
            String time = C.getString(2);
            String id = C.getString(0);
            //int sex = rs.getInt(4);
            //dataset.add(id+"  "+tid+" "+time);
            //global.setTestString(tid+" "+time);
            namelist.add(id + "  " + tid + " " + time);
        }

//                Log.d("namel", namel.toString());
        namel=namelist.toArray(new String[0]);
        for (int i=0;i<namel.length;i++) {
            List<Integer> result_line = new ArrayList<>();
            result_line.add(converttoInt(namel[i]));
            Cursor c1 = db.rawQuery("select ["+converttoInt(namel[i])+"] from hit_record where [date]='"+stdate+"'" , null);
            Log.d("date",stdate);

            if (c1.moveToFirst()) {
                do {
                    result_line.add(c1.getInt(0));
                } while (c1.moveToNext());
            }
            result.add(result_line);
            Log.d("result", result_line.toString());
            c1.close();
        }
        if(!result.isEmpty()) {
            tatesiki=(result.get(0).size()-1)*2;
        }

        C.close();

        dialog.dismiss();
        members.addAll(setmember(result,tatesiki));
        for (int i = 0;i<members.size();i++){
            int imageId = getResources().getIdentifier(
                    members.get(i),"drawable", getPackageName());
            imgList.add(imageId);
        }
        String[] nameOflist={"batu0","batu1","batu2","maru","none"};
        for (int i=0;i<5;i++){
            int IDD=getResources().getIdentifier(
                    nameOflist[i],"drawable",getPackageName()
            );
            listOfSelection.add(IDD);
        }
        Log.d("members", imgList.toString());
        Log.d("pass","passed");

        // for-each member名をR.drawable.名前としてintに変換してarrayに登録

        String[] memberArray=members.toArray(new String[0]);
        Log.d("imglist",imgList.toString());

        // GridViewのインスタンスを生成
        final GridView gridview = findViewById(R.id.gridview);
        GridView namegrid = findViewById(R.id.namegrid);
        TextView emptyview=findViewById(R.id.textView24);
        gridview.setEmptyView(emptyview);
        // BaseAdapter を継承したGridAdapterのインスタンスを生成
        // 子要素のレイアウトファイル grid_items.xml を
        // activity_main.xml に inflate するためにGridAdapterに引数として渡す
         adapter = new GridAdapter(
                EditRecord.this.getApplicationContext(),
                R.layout.grid_items,
                imgList
        );
        NameAdapter adapter1 = new NameAdapter(
                EditRecord.this.getApplicationContext(),
                R.layout.name_items,
                namel
        );

        // gridViewにadapterをセット
        gridview.setAdapter(adapter);
        namegrid.setAdapter(adapter1);
        gridview.setNumColumns(result.size());
        namegrid.setNumColumns(result.size());
        namegrid.setHorizontalSpacing(1);





        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //@Override
            int changedValue=0;
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    changedValue=listOfSelection.get(listOfSelection.indexOf(imgList.get(position)) + 1);
                }catch (IndexOutOfBoundsException e){
                    changedValue=listOfSelection.get(0);
                }
                imgList.set(position,changedValue);
                adapter.notifyDataSetChanged();
                //Toast.makeText(EditAIResult.this,String.valueOf(Messege),Toast.LENGTH_LONG).show();
            }
        });




    }



//    class GetResult extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//
//            try {
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
//
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            //Toast.makeText(EditAIResult.this, "end", Toast.LENGTH_LONG).show();
//            dialog.dismiss();
//            members.addAll(setmember(result,tatesiki));
//            for (int i = 0;i<members.size();i++){
//                int imageId = getResources().getIdentifier(
//                        members.get(i),"drawable", getPackageName());
//                imgList.add(imageId);
//            }
//            String[] nameOflist={"batu0","batu1","batu2","maru","none"};
//            for (int i=0;i<5;i++){
//                int IDD=getResources().getIdentifier(
//                        nameOflist[i],"drawable",getPackageName()
//                );
//                listOfSelection.add(IDD);
//            }
//            Log.d("members", imgList.toString());
//            Log.d("pass","passed");
//
//            // for-each member名をR.drawable.名前としてintに変換してarrayに登録
//
//            String[] memberArray=members.toArray(new String[0]);
//            Log.d("imglist",imgList.toString());
//
//            // GridViewのインスタンスを生成
//            final GridView gridview = findViewById(R.id.gridview);
//            GridView namegrid = findViewById(R.id.namegrid);
//            // BaseAdapter を継承したGridAdapterのインスタンスを生成
//            // 子要素のレイアウトファイル grid_items.xml を
//            // activity_main.xml に inflate するためにGridAdapterに引数として渡す
//            final GridAdapter adapter = new GridAdapter(
//                    EditRecord.this.getApplicationContext(),
//                    R.layout.grid_items,
//                    imgList
//            );
//            NameAdapter adapter1 = new NameAdapter(
//                    EditRecord.this.getApplicationContext(),
//                    R.layout.name_items,
//                    namel
//            );
//
//            // gridViewにadapterをセット
//            gridview.setAdapter(adapter);
//            namegrid.setAdapter(adapter1);
//            gridview.setNumColumns(result.size());
//            namegrid.setNumColumns(result.size());
//            namegrid.setHorizontalSpacing(1);
//
//
//
//
//
//            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                //@Override
//                int changedValue=0;
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    try {
//                        changedValue=listOfSelection.get(listOfSelection.indexOf(imgList.get(position)) + 1);
//                    }catch (IndexOutOfBoundsException e){
//                        changedValue=listOfSelection.get(0);
//                    }
//                    imgList.set(position,changedValue);
//                    adapter.notifyDataSetChanged();
//                    //Toast.makeText(EditAIResult.this,String.valueOf(Messege),Toast.LENGTH_LONG).show();
//                }
//            });
//        }




//    class UploadTask extends AsyncTask<Void,Void,Void>{
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            try {
//                List<Integer> templist=new ArrayList<>();
//                List<Integer> insertList = new ArrayList<>();
//                Connection conn=MySqlConnect.getConnection();
//                Statement stmt = conn.createStatement();
//                int temp=0;
//                stmt.execute("select max(hitID) into @maxID from kyudodb.hit_record where date= "+date);
//                for (int i=0;i<imgList.size();i++){
//                    templist.add(listOfSelection.indexOf(imgList.get(i)));
//                }
//                for (int j=0; j<tatesiki;j+=2) {
//                    for (int i = j*namel.length; i < namel.length*(j+1); i++) {
//                        insertList.add(((templist.get(i))<<2)+ templist.get(i + namel.length));
//                    }
//                }
//                int number=0;
//                for (int i=0;i<insertList.size();i++){
//                    stmt.executeUpdate("update kyudodb.hit_record set hit_record."+result.get(temp).get(0)+" = "+insertList.get(i)+" where hitID=@maxID-"+ number);
//                    //Log.d("wsql","update kyudodb.hit_record set hit_record."+result.get(temp).get(0)+" = "+insertList.get(i)+" where hitID=@maxID-"+ number);
//                    temp++;
//                    if (temp==(result.size())) {
//                        temp=0;
//                        number++;
//                    }
//                }
//                stmt.close();
//                conn.setAutoCommit(false);
//                conn.commit();
//                conn.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            dialog.dismiss();
//            Intent intent = new Intent(getApplication(), SelectMenuActivity.class);
//            startActivity(intent);
//            finish();
//        }
//    }

//    class deleteEveryThing extends AsyncTask<Void,Void,Void>{
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            Connection conn= null;
//            try {
//                conn = MySqlConnect.getConnection();
//                Statement stmt = conn.createStatement();
//                stmt.execute("select max(hitID) into @maxID from kyudodb.hit_record");
//                for(int i=0;i<(tatesiki/2);i++) {
//                    stmt.executeUpdate("delete from kyudodb.hit_record where hitID=@maxID-"+i);
//                }
//                conn.setAutoCommit(false);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }

//        @Override
//        protected void onPostExecute(Void aVoid) {
//            dialog.dismiss();
//            Intent intent=new Intent(getApplication(), CameraActivity.class);
//            intent.putExtra("NUMBER",tatesiki);
//            startActivity(intent);
//            finish();
//        }
//    }

    static int converttoInt(String str){
        String[] list=str.split("  ");
        Log.d("int",list[0]);
        return Integer.parseInt(list[0]);

    }
    List<String> setmember(List<List<Integer>> list,int tatesiki){
        List<String> member_list=new ArrayList<>();
        List<Integer> inputTemp=new ArrayList<>();
        List<List<Integer>> resultTemp = new ArrayList<>();

        for (int i =0 ; i<result.size();i++){
            List<Integer> Temp=new ArrayList<>();
            for(int j=1;j<result.get(0).size();j++){
                if (result.get(i).get(j)<16) {
                    Temp.add(result.get(i).get(j) & 3);
                    Temp.add((result.get(i).get(j) & 12) >> 2);
                }else{
                    Temp.add(4);
                    Temp.add(4);
                }
            }
            resultTemp.add(Temp);
        }
        for (int i=resultTemp.get(0).size()-1;i>-1;i--){
            for (int j=0;j<resultTemp.size();j++){
                inputTemp.add(resultTemp.get(j).get(i));
            }
        }
        for (int i=0;i<inputTemp.size();i++){
            if (inputTemp.get(i)==0) member_list.add("batu0");
            else if (inputTemp.get(i)==1) member_list.add("batu1");
            else if (inputTemp.get(i)==2) member_list.add("batu2");
            else if (inputTemp.get(i)==3) member_list.add("maru");
            else member_list.add("none");
        }
        Log.d("member_list",member_list.toString());
        return member_list;
    }
    void setDate(String str){
        this.date=converttoInt(str);
    }
}
