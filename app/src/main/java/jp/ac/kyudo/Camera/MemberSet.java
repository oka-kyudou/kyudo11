package jp.ac.kyudo.Camera;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jp.ac.kyudo.MainSelect.kyudoDBOpenHelper;
import jp.ac.kyudo.Member.MemberLeave;
import jp.ac.kyudo.R;

public class MemberSet extends AppCompatActivity {
    private RecyclerView.Adapter adapter;
    private List<String> dataset = new ArrayList<>();
    private List<String> namelist=new ArrayList<>();
    private kyudoDBOpenHelper helper;
    private SQLiteDatabase db;
    List<String> spinnerItems= new ArrayList<>();



    private ProgressDialog dialog;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_set);

        //ids
        Button add = findViewById(R.id.add);
        Button confirm = findViewById(R.id.confirm);
        Button clear=findViewById(R.id.delete);
        RecyclerView recyclerView = findViewById(R.id.my_recycler_view);
        final Spinner spinner=findViewById(R.id.spinner);

        // くるくるを表示
        dialog = new ProgressDialog(MemberSet.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("接続中");
        dialog.show();
        spinnerItems.add("");
        SharedPreferences preferenceManager= PreferenceManager.getDefaultSharedPreferences(MemberSet.this);
        int tatesiki=Integer.parseInt(preferenceManager.getString("tatemasu","10"));
        for (int i=2;i<=tatesiki;i+=2) spinnerItems.add(String.valueOf(i));

        ArrayAdapter<String> spinneradapter = new ArrayAdapter<String>(
                this,
                R.layout.spinner_item,
                spinnerItems
        );

        spinneradapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinneradapter);

        //OnClickListner
        confirm.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                saveList(MemberSet.this,"1", (ArrayList<String>) dataset);
                FileWriter writer = null;
//                String dirArr= Objects.requireNonNull(MemberSet.this.getExternalFilesDir(null)).getAbsolutePath();
                String dirArr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();

                File dir=new File(dirArr+"/deeplab");
                if(!dir.exists()){
                    dir.mkdirs();
                }
                try {
                    writer = new FileWriter(dirArr+"/deeplab/result.txt");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for(String str: dataset) {
                    try {
                        writer.write(str+"\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String SpinnerResult=spinner.getSelectedItem().toString();
                if (SpinnerResult.isEmpty()){
                    AlertDialog.Builder builder=new AlertDialog.Builder(MemberSet.this);
                    builder.setMessage("縦マスを設定してください");
                    builder.show();
                }else {
                    int data = Integer.parseInt(SpinnerResult);

                    Intent intent = new Intent(getApplication(), CameraActivity.class);
                    intent.putExtra("NUMBER", data);

                    startActivity(intent);
                    finish();
                }
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                additem();
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(MemberSet.this);
                builder.setMessage("立順を削除しますか？");
                builder.setPositiveButton("削除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dataset.clear();
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.show();

            }
        });



        //set View
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);





//        final SampleTask task = new SampleTask();
//        task.execute();
        try{

//                Connection conn= MySqlConnect.getConnection();
//                //Toast.makeText(MemberSet.this,"connected", Toast.LENGTH_LONG).show();
//                Statement stmt=conn.createStatement();
//                ResultSet rs=stmt.executeQuery("SELECT memberID ,family_name, first_name FROM `kyudodb`.member_list;");
            if(helper == null){
                helper = new kyudoDBOpenHelper(getApplicationContext(),this);
            }

            if(db == null){
                db = helper.getWritableDatabase();
            }
            Cursor C = db.rawQuery("SELECT memberID ,family_name, first_name FROM member_list;",null);

            while(C.moveToNext()){
                //int id=rs.getInt(1);
                //int location=rs.getInt(2);
                String tid=C.getString(1);
                String time=C.getString(2);
                String id =C.getString(0);
                //int sex = rs.getInt(4);
                //dataset.add(id+"  "+tid+" "+time);
                //global.setTestString(tid+" "+time);
                namelist.add(id+"  "+tid+" "+time);

            }
            C.close();
            db.close();

        }catch(Exception e){
            e.printStackTrace();
        }
        dialog.dismiss();
        dataset=loadList(MemberSet.this,"1");



        //set adapter
        adapter = new MemberSetAdapter(dataset);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(MemberSet.this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL); // ここで横方向に設定
        recyclerView.setLayoutManager(manager);




        ItemTouchHelper mIth = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.LEFT) {

                    @Override
                    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                        final int swipeFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                        final int dragFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                        return makeMovementFlags(dragFlags, swipeFlags);
                    }

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {

                        final int fromPos = viewHolder.getAdapterPosition();
                        final int toPos = target.getAdapterPosition();
                        adapter.notifyItemMoved(fromPos, toPos);
                        String temp;
                        temp=dataset.get(fromPos);
                        dataset.remove(fromPos);
                        dataset.add(toPos,temp);
                        //adapter.notifyDataSetChanged();
                        return true;// true if moved, false otherwise
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        final int fromPos = viewHolder.getAdapterPosition();
                        dataset.remove(fromPos);
                        adapter.notifyItemRemoved(fromPos);
                    }
                });


        mIth.attachToRecyclerView(recyclerView);
    }
    private void additem(){
        //dataset.add("A");
        final String[] items = namelist.toArray(new String[0]);
        new AlertDialog.Builder(MemberSet.this)
                .setTitle("追加する部員を選択")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // item_which pressed
                        dataset.add(items[which]);
                        adapter.notifyDataSetChanged();
                    }
                })
                .show();

    }
    public static void saveList(Context ctx, String key, ArrayList<String> list) {
        JSONArray jsonAry = new JSONArray();
        for(int i=0; i<list.size(); i++) {
            jsonAry.put(list.get(i));
        }
        SharedPreferences prefs = ctx.getSharedPreferences("pref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, jsonAry.toString());
        editor.apply();
    }

    // 設定値 ArrayList<String> を取得（Context は Activity や Application や Service）
    public static ArrayList<String> loadList(Context ctx, String key) {
        ArrayList<String> list = new ArrayList<String>();
        SharedPreferences prefs = ctx.getSharedPreferences("pref", Context.MODE_PRIVATE);
        String strJson = prefs.getString(key, ""); // 第２引数はkeyが存在しない時に返す初期値
        if(!strJson.equals("")) {
            try {
                JSONArray jsonAry = new JSONArray(strJson);
                for(int i=0; i<jsonAry.length(); i++) {
                    list.add(jsonAry.getString(i));
                }
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        return list;
    }







}
