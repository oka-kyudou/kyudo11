package jp.ac.kyudo.Camera;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import jp.ac.kyudo.Edit.EditRecord;
import jp.ac.kyudo.MainSelect.kyudoDBOpenHelper;
import jp.ac.kyudo.R;
import jp.ac.kyudo.MainSelect.SelectMenuActivity;

public class EditAIResult extends AppCompatActivity {
    private String[] namel = new String[0];
    private List<List<Integer>> result= new ArrayList<>();
    private List<String>members=new ArrayList<>();
    private int tatesiki;
    private ProgressDialog dialog;
    final List<Integer> listOfSelection=new ArrayList<>();
    // Resource IDを格納するarray
    private List<Integer> imgList = new ArrayList<>();
    private kyudoDBOpenHelper helper;
    private SQLiteDatabase db;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_ai_result);
        Button endButton = findViewById(R.id.confirm2updateDB);
        Button deleteButton = findViewById(R.id.deleteAll);
        Button plusButton = findViewById(R.id.plus_line);
        plusButton.setVisibility(View.INVISIBLE);
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new ProgressDialog(EditAIResult.this);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("データ更新中");
                dialog.show();
//                final EditAIResult.UploadTask uploadTask=new UploadTask();
//                uploadTask.execute();
                List<Integer> templist=new ArrayList<>();
                List<Integer> insertList = new ArrayList<>();
//                Connection conn=MySqlConnect.getConnection();
//                Statement stmt = conn.createStatement();
                int temp=0;
                if(helper == null){
                    helper = new kyudoDBOpenHelper(getApplicationContext(), EditAIResult.this);
                }

                if(db == null){
                    db = helper.getReadableDatabase();
                }
                Cursor C=db.rawQuery("select  max(hitID)  from hit_record",null);
                C.moveToFirst();
                int maxID=C.getInt(0);

//                stmt.execute("select max(hitID) into @maxID from kyudodb.hit_record");
                for (int i=0;i<imgList.size();i++){
                    templist.add(listOfSelection.indexOf(imgList.get(i)));
                }
                for (int j=0; j<tatesiki;j+=2) {
                    for (int i = j*namel.length; i < namel.length*(j+1); i++) {
                        insertList.add(((templist.get(i))<<2)+ templist.get(i + namel.length));
                    }
                }
                int number=0;
                for (int i=0;i<insertList.size();i++){
                    db.execSQL("update hit_record set ["+result.get(temp).get(0)+"] = "+insertList.get(i)+" where hitID= "+maxID+" -"+ number);
                    temp++;
                    if (temp==(result.size())) {
                        temp=0;
                        number++;
                    }
                }
                C.close();
                dialog.dismiss();
                Intent intent = new Intent(getApplication(), SelectMenuActivity.class);
                startActivity(intent);
                finish();

            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new ProgressDialog(EditAIResult.this);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("データ削除中");
                dialog.show();
//                final EditAIResult.deleteEveryThing deleteEveryThing=new deleteEveryThing();
//                deleteEveryThing.execute();
                if(helper == null){
                    helper = new kyudoDBOpenHelper(getApplicationContext(),EditAIResult.this);
                }

                if(db == null){
                    db = helper.getReadableDatabase();
                }
                Cursor C=db.rawQuery("select  max(hitID)  from hit_record",null);
                C.moveToFirst();
                int maxID=C.getInt(0);
                for(int i=0;i<(tatesiki/2);i++) {
                    db.execSQL("delete from hit_record where hitID="+maxID+"-"+i);
                }
                dialog.dismiss();
                Intent intent=new Intent(getApplication(), CameraActivity.class);
                intent.putExtra("NUMBER",tatesiki);
                startActivity(intent);
                finish();
            }
        });
        Intent intent=getIntent();
        tatesiki=intent.getIntExtra("tatesiki",0);
        //tatesiki=4;
        dialog = new ProgressDialog(EditAIResult.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("解析結果取得中");
//        final EditAIResult.GetResult task = new GetResult();
//        task.execute();
        if(helper == null){
            helper = new kyudoDBOpenHelper(getApplicationContext(),this);
        }

        if(db == null){
            db = helper.getReadableDatabase();
        }

//                Statement smt=conn.createStatement();

        Cursor C=db.rawQuery("SELECT name_order FROM connection where messge=1;",null);
        List<String> namelist=new ArrayList<>();
        C.moveToFirst();
        String names=new String(C.getBlob(0));
        for (String name:names.split("\n")) namelist.add(name);
        namel=namelist.toArray(new String[0]);
        for (String s : namel) {
            List<Integer> result_line = new ArrayList<>();
            result_line.add(converttoInt(s));
//                    ResultSet res = smt.executeQuery("select hit_record." + converttoInt(s) + " from kyudodb.hit_record where date = current_date()");
            Cursor c1=db.rawQuery("select ["+converttoInt(s)+"] from hit_record where [date]=current_date",null);
            while (c1.moveToNext()) {
                result_line.add(c1.getInt(0));
            }
            result.add(result_line);
            Log.d("result", result_line.toString());
            c1.close();
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
        gridview.getLayoutParams().width=members.size()*50;
        namegrid.getLayoutParams().width=members.size()*50;
        // BaseAdapter を継承したGridAdapterのインスタンスを生成
        // 子要素のレイアウトファイル grid_items.xml を
        // activity_main.xml に inflate するためにGridAdapterに引数として渡す
        final GridAdapter adapter = new GridAdapter(
                EditAIResult.this.getApplicationContext(),
                R.layout.grid_items,
                imgList
        );
        NameAdapter adapter1 = new NameAdapter(
                EditAIResult.this.getApplicationContext(),
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
////                Connection conn = MySqlConnect.getConnection();
////                //Toast.makeText(MemberSet.this,"connected", Toast.LENGTH_LONG).show();
////                Statement stmt = conn.createStatement();
////                ResultSet rs = stmt.executeQuery("SELECT memberID ,family_name, first_name FROM `kyudodb`.member_list;");
//
//                if(helper == null){
//                    helper = new kyudoDBOpenHelper(getApplicationContext());
//                }
//
//                if(db == null){
//                    db = helper.getReadableDatabase();
//                }
//
////                Statement smt=conn.createStatement();
//                Cursor C=db.rawQuery("SELECT name_order FROM connection where messge=1;",null);
//                List<String> namelist=new ArrayList<>();
//                String names=new String(C.getBlob(0));
//                for (String name:names.split("\n")) namelist.add(name);
//                namel=namelist.toArray(new String[0]);
//                for (String s : namel) {
//                    List<Integer> result_line = new ArrayList<>();
//                    result_line.add(converttoInt(s));
////                    ResultSet res = smt.executeQuery("select hit_record." + converttoInt(s) + " from kyudodb.hit_record where date = current_date()");
//                    Cursor c1=db.rawQuery("select ["+converttoInt(s)+"] from hit_record where [date]=current_date",null);
//                    while (c1.moveToNext()) {
//                        result_line.add(c1.getInt(0));
//                    }
//                    result.add(result_line);
//                    Log.d("result", result_line.toString());
//                    c1.close();
//                }
//                C.close();
//
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
//                    nameOflist[i],"drawable",getPackageName()
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
//                    EditAIResult.this.getApplicationContext(),
//                    R.layout.grid_items,
//                    imgList
//            );
//            NameAdapter adapter1 = new NameAdapter(
//                    EditAIResult.this.getApplicationContext(),
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
//
//
//    }
//
//    class UploadTask extends AsyncTask<Void,Void,Void>{
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            try {
//                List<Integer> templist=new ArrayList<>();
//                List<Integer> insertList = new ArrayList<>();
////                Connection conn=MySqlConnect.getConnection();
////                Statement stmt = conn.createStatement();
//                int temp=0;
//                if(helper == null){
//                    helper = new kyudoDBOpenHelper(getApplicationContext());
//                }
//
//                if(db == null){
//                    db = helper.getReadableDatabase();
//                }
//                Cursor C=db.rawQuery("select  max(hitID)  from kyudodb.hit_record",null);
//                int maxID=C.getInt(0);
//
////                stmt.execute("select max(hitID) into @maxID from kyudodb.hit_record");
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
//                    db.execSQL("update kyudodb.hit_record set hit_record."+result.get(temp).get(0)+" = "+insertList.get(i)+" where hitID= "+maxID+" -"+ number);
//                    temp++;
//                    if (temp==(result.size())) {
//                        temp=0;
//                        number++;
//                    }
//                }
//                C.close();
//                db.close();
//                dialog.dismiss();
//                Intent intent = new Intent(getApplication(), SelectMenuActivity.class);
//                startActivity(intent);
//                finish();
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

    class deleteEveryThing extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if(helper == null){
                    helper = new kyudoDBOpenHelper(getApplicationContext(),EditAIResult.this);
                }

                if(db == null){
                    db = helper.getReadableDatabase();
                }
                Cursor C=db.rawQuery("select  max(hitID)  from kyudodb.hit_record",null);
                int maxID=C.getInt(0);
                for(int i=0;i<(tatesiki/2);i++) {
                    db.execSQL("delete from kyudodb.hit_record where hitID="+maxID+"-"+i);
                }
                dialog.dismiss();
                Intent intent=new Intent(getApplication(), CameraActivity.class);
                intent.putExtra("NUMBER",tatesiki);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dialog.dismiss();
            Intent intent=new Intent(getApplication(), CameraActivity.class);
            intent.putExtra("NUMBER",tatesiki);
            startActivity(intent);
            finish();
        }
    }
    public static String convertInputStreamToString(InputStream is) throws IOException {
        InputStreamReader reader = new InputStreamReader(is);
        StringBuilder builder = new StringBuilder();
        char[] buf = new char[1024];
        int numRead;
        while (0 <= (numRead = reader.read(buf))) {
            builder.append(buf, 0, numRead);
        }
        return builder.toString();
    }

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
        for (int i=resultTemp.get(0).size()-1;i>resultTemp.get(0).size()-tatesiki-1;i--){
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
}
