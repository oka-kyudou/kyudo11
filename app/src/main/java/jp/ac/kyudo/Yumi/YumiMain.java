package jp.ac.kyudo.Yumi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaRouter;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.DialogTitle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.Inflater;

import jp.ac.kyudo.MainSelect.kyudoDBOpenHelper;
import jp.ac.kyudo.R;

public class YumiMain extends AppCompatActivity {
    static List<Integer> yumiIDs = new ArrayList<>();
    static List<Integer> kinds = new ArrayList<>();
    static List<Double> weights = new ArrayList<>();
    static List<Integer> nobis = new ArrayList<>();
    static List<Integer> userIDs = new ArrayList<>();
    static List<Integer> users = new ArrayList<>();
    static List<String> namelist=new ArrayList<>();
    static List<Integer> Idlist=new ArrayList<>();
    static List<String> cationlist=new ArrayList<>();
    private kyudoDBOpenHelper helper;
    private SQLiteDatabase db;
    List<YumiMain.yumidata> listList = new ArrayList<>();
    int flag=0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yumi_main);

        if (helper == null) {
            helper = new kyudoDBOpenHelper(getApplicationContext(),this);
        }
        if (db == null) {
            db = helper.getReadableDatabase();
        }
    }




    static class yumidata {
         int ID;
         int kind;
         double weight;
         int nobi;
         int user;
         String cation;

         yumidata(int ID, int kind, double weight, int nobi, int user, String cation) {
            this.ID = ID;
            this.kind = kind;
            this.weight = weight;
            this.nobi = nobi;
            this.user = user;
            this.cation=cation;
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
            helper = new kyudoDBOpenHelper(getApplicationContext(),this);

            db = helper.getReadableDatabase();

        for (yumidata yumi :listList) {
//            ContentValues content=new ContentValues();
//            content.put("kind",String.valueOf(yumi.kind));
//            content.put("weight",yumi.weight);
//            content.put("nobi",String.valueOf(yumi.nobi));

            db.execSQL("UPDATE yumi_list SET weight=" + yumi.weight
                    +",nobi=" + yumi.nobi
                    +",kind=" + yumi.kind
                    +",cation='"+yumi.cation
                    +"' WHERE  yumiID="+yumi.ID);

            if (yumi.user>Idlist.size()-1) yumi.user=0;
            db.execSQL("update yumi_user set memberID="+Idlist.get(yumi.user)+" where yumiID="+yumi.ID+";");
        }

        db.close();


    }
     static class YumiViewHolder extends RecyclerView.ViewHolder{
         TextView IDView;
        Spinner kindView;
         EditText weightView;
        Spinner userView;
        Spinner nobiView;
        TextView cationView;
         Button deletebutton;
         List<yumidata> listList;

         YumiViewHolder(@NonNull View itemView, final List<yumidata> listList) {
            super(itemView);
            IDView=itemView.findViewById(R.id.yumiid);
            kindView=itemView.findViewById(R.id.kind);
            weightView=itemView.findViewById(R.id.weight);
            userView=itemView.findViewById(R.id.membername);
            nobiView=itemView.findViewById(R.id.nobi);
            deletebutton=itemView.findViewById(R.id.deletebuttonrow);
            cationView=itemView.findViewById(R.id.cautions);
            this.listList=listList;

            weightView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    if (weightView.hasFocus()) {
                        if (!s.toString().isEmpty()) {
                            listList.get(getAdapterPosition()).weight = Double.parseDouble(s.toString());
                        }
                    }
                }
            });
             nobiView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                 @Override
                 public void onItemSelected(AdapterView<?> parent, View view, int p, long id) {
                     listList.get(getAdapterPosition()).nobi = p;
                 }
                 @Override
                 public void onNothingSelected(AdapterView<?> parent) {

                 }
             });
             userView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                 @Override
                 public void onItemSelected(AdapterView<?> parent, View view, int p, long id) {
                     listList.get(getAdapterPosition()).user=p;
                 }

                 @Override
                 public void onNothingSelected(AdapterView<?> parent) {

                 }
             });
             cationView.addTextChangedListener(new TextWatcher() {
                 @Override
                 public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                 }

                 @Override
                 public void onTextChanged(CharSequence s, int start, int before, int count) {

                 }

                 @Override
                 public void afterTextChanged(Editable s) {
                     if (cationView.hasFocus()) {
                         listList.get(getAdapterPosition()).cation = s.toString();
                     }
                 }
             });
             kindView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                 @Override
                 public void onItemSelected(AdapterView<?> parent, View view, int p, long id) {
                     listList.get(getAdapterPosition()).kind=p;
                 }

                 @Override
                 public void onNothingSelected(AdapterView<?> parent) {

                 }
             });

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        yumiIDs = new ArrayList<>();
        kinds = new ArrayList<>();
        weights = new ArrayList<>();
        nobis = new ArrayList<>();
        userIDs = new ArrayList<>();
        users = new ArrayList<>();
        namelist=new ArrayList<>();
        Idlist=new ArrayList<>();
        cationlist=new ArrayList<>();

       listList = new ArrayList<>();
        Cursor c2 = db.rawQuery("select family_name , first_name, memberID from member_list", null);
        while (c2.moveToNext()) {
            namelist.add(c2.getString(0) + c2.getString(1));
            Idlist.add(c2.getInt(2));
        }
        namelist.add("");
        c2.close();
       Cursor C2=db.rawQuery("select count(*) from yumi_list",null);
       C2.moveToFirst();
        if (C2.getInt(0)==0){
            Toast.makeText(YumiMain.this,"YumiDataNotFound",Toast.LENGTH_LONG).show();
        }else {
            Cursor C = db.rawQuery("select yumi_list.yumiID,kind,weight,nobi, yumi_user.memberID,cation" +
                    " from yumi_list left join yumi_user on yumi_list.yumiID=yumi_user.yumiID ", null);
            C.moveToFirst();
            do {
                yumiIDs.add(C.getInt(0));
                kinds.add(C.getInt(1));
                weights.add(C.getDouble(2));
                nobis.add(C.getInt(3));
                userIDs.add(C.getInt(4));
                cationlist.add(C.getString(5));
                Cursor C1 = db.rawQuery("select family_name,first_name from member_list where memberID = " + C.getInt(4), null);
                C1.moveToFirst();
                if (C.getInt(4) == 0) {
                    users.add(namelist.size()-1);
                    continue;
                }
                users.add(namelist.indexOf(C1.getString(0) + C1.getString(1)));
                C1.close();
            } while (C.moveToNext());

            C.close();

            for (int i = 0; i < yumiIDs.size(); i++) {
                listList.add(new yumidata(yumiIDs.get(i), kinds.get(i), weights.get(i), nobis.get(i), users.get(i),cationlist.get(i)));
            }
        }
        C2.close();

        //set list adapter
        final RecyclerView listView = findViewById(R.id.yumi_list);
        final YumiListAdapter yumiListAdapter = new YumiListAdapter(YumiMain.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(YumiMain.this);
        listView.setLayoutManager(layoutManager);

        final List<yumidata> deletedlist=new ArrayList<>();
        yumiListAdapter.setListList(listList);
        yumiListAdapter.setNamelist(namelist);
        listView.setAdapter(yumiListAdapter);
        Toolbar toolbar = findViewById(R.id.yumibar);
        final Spinner spinnernobi = findViewById(R.id.spinner5nobi);
        final Spinner spinnerkind = findViewById(R.id.spinnerkind);
        final Spinner spinnerweight = findViewById(R.id.spinnerweight);
        final TextView editTextyumiID = findViewById(R.id.editTextyumiID);
        final LinearLayout bardisplay=findViewById(R.id.BarDisplayYumi);
        final Button yumiAdd=findViewById(R.id.add_yumi);
        String[] nobiContent = {"指定なし", "並", "伸", "その他"};
        final String[] kindcontent = {"指定なし", "直心", "練心", "その他"};
        String[] weightcontent = {"指定なし", "降順", "昇順"};
        ArrayAdapter<String> nobiAdapter = new ArrayAdapter<String>(YumiMain.this, R.layout.support_simple_spinner_dropdown_item, nobiContent);
        ArrayAdapter<String> kindAdapter = new ArrayAdapter<String>(YumiMain.this, R.layout.support_simple_spinner_dropdown_item, kindcontent);
        ArrayAdapter<String> weightAdapter = new ArrayAdapter<String>(YumiMain.this, R.layout.support_simple_spinner_dropdown_item, weightcontent);
        spinnerkind.setAdapter(kindAdapter);
        spinnernobi.setAdapter(nobiAdapter);
        spinnerweight.setAdapter(weightAdapter);
        Button save_button=findViewById(R.id.yumi_save);
        Button yumi_search=findViewById(R.id.yumi_search);
        bardisplay.setVisibility(View.GONE);

        yumi_search.setOnClickListener(new View.OnClickListener() {
            int flag1=0;
            @Override
            public void onClick(View v) {

                if (flag1==0) {
                    bardisplay.setVisibility(View.VISIBLE);
                    flag1=1;
                }else {
                    bardisplay.setVisibility(View.GONE);
                    flag1=0;
                }
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (yumidata yumi:listList){
                    db.execSQL("UPDATE yumi_list SET weight=" + yumi.weight
                            +",nobi=" + yumi.nobi
                            +",kind=" + yumi.kind
                            +",cation='"+yumi.cation
                            +"' WHERE  yumiID="+yumi.ID);

                    if (yumi.user>=namelist.size()-1) yumi.user=0;
                    db.execSQL("update yumi_user set memberID="+Idlist.get(yumi.user)+" where yumiID="+yumi.ID+";");
                }
                Toast.makeText(YumiMain.this,"Data Saved!",Toast.LENGTH_LONG).show();
            }
        });



        spinnerkind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {

                    if (flag==0) {
                        if (spinnernobi.getSelectedItemId() != 0) {
                            if (deletedlist != null) {
                                listList.addAll(deletedlist.stream().distinct().collect(Collectors.<yumidata>toList()));
                                flag++;
                                spinnernobi.setSelection(0);
                            }
                        } else {
                            listList.addAll(deletedlist.stream().distinct().collect(Collectors.<yumidata>toList()));
                        }
                    }else
                    {
                        flag=0;
                    }
                    Collections.sort(listList, new IDdwncompartor());

                }
                if (position==1){
                    for (int i=0;i<listList.size();i++){
                        if (listList.get(i).kind==0)continue;
                        deletedlist.add(listList.remove(i));
                        i--;
                    }
                }
                if (position==2){
                    for (int i=0;i<listList.size();i++){
                        if (listList.get(i).kind==1)continue;
                        deletedlist.add(listList.remove(i));
                        i--;
                    }
                }
                if (position==3){
                    for (int i=0;i<listList.size();i++){
                        if (listList.get(i).kind==2)continue;
                        deletedlist.add(listList.remove(i));
                        i--;
                    }
                }

                yumiListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnernobi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    if (flag==0) {
                        if (spinnerkind.getSelectedItemId() != 0) {
                            if (deletedlist != null) {
                                listList.addAll(deletedlist.stream().distinct().collect(Collectors.<yumidata>toList()));
                                flag++;
                                spinnerkind.setSelection(0);
                            }
                        } else {
                            listList.addAll(deletedlist.stream().distinct().collect(Collectors.<yumidata>toList()));
                        }
                    }
                    else{
                        flag=0;
                    }
                    Collections.sort(listList, new IDdwncompartor());

                }
                if (position==1){
                    for (int i=0;i<listList.size();i++){
                        if (listList.get(i).nobi==0)continue;
                        deletedlist.add(listList.remove(i));
                        i--;
                    }
                }
                if (position == 2) {
                    for (int i=0;i<listList.size();i++){
                        if (listList.get(i).nobi==1)continue;
                        deletedlist.add(listList.remove(i));
                        i--;
                    }
                }
                if (position==3){
                    for (int i=0;i<listList.size();i++){
                        if (listList.get(i).nobi==2)continue;
                        deletedlist.add(listList.remove(i));
                        i--;
                    }
                }
                yumiListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerweight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    Collections.sort(listList, new weightdwncompartor());
                    yumiListAdapter.notifyDataSetChanged();
                }
                if (position == 2) {
                    Collections.sort(listList, new weightupcompartor());
                    yumiListAdapter.notifyDataSetChanged();
                }
                if (position==0){
                    Collections.sort(listList,new IDdwncompartor());
                    yumiListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        editTextyumiID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void afterTextChanged(Editable s) {
                if (!editTextyumiID.getText().toString().isEmpty()) {
                    listList.addAll(new HashSet<>(deletedlist));
                    List<yumidata> tempList=new ArrayList<>(listList);
                    listList.clear();
                    listList.addAll(new HashSet<>(tempList));
//                    listList=listList.stream().distinct().collect(Collectors.<yumidata>toList());
                    for (int i = 0; i < listList.size(); i++) {
                        if (listList.get(i).ID == Integer.parseInt(editTextyumiID.getText().toString())) {
                            continue;
                        }
                        deletedlist.add(listList.remove(i));
                        i--;
                    }

                }else {
                    listList.addAll(deletedlist);
                    List<yumidata> tempList=new ArrayList<>(listList);
                    listList.clear();
                    listList.addAll(new HashSet<>(tempList));
                    Collections.sort(listList, new IDdwncompartor());

                }
                yumiListAdapter.notifyDataSetChanged();
            }
        });
        yumiAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater=(LayoutInflater) YumiMain.this.getSystemService(LAYOUT_INFLATER_SERVICE);
                final View view=inflater.inflate(R.layout.yumi_add_dialog,(ViewGroup)findViewById(R.id.dialog_layout));
                final Spinner kind= view.findViewById(R.id.KINDADD);
                final Spinner size=view.findViewById(R.id.SIZEADD);
                final Spinner user=view.findViewById(R.id.USERADD);
                final EditText ID=view.findViewById(R.id.IDADD);
                final EditText weight=view.findViewById(R.id.WEIGHTADD);

                Cursor C=db.rawQuery("select max(yumiID) from yumi_list",null);
                C.moveToFirst();
                int IDmax=C.getInt(0)+1;
                ID.setText(String.valueOf(IDmax));
                weight.setText("0.0");

                String[] kinds={"直心","練心","その他"};
                String[] sizes={"並","伸","その他"};
                ArrayAdapter<String> useradapter=new ArrayAdapter<String>(YumiMain.this,R.layout.support_simple_spinner_dropdown_item,namelist);
                ArrayAdapter<String> kindadapter=new ArrayAdapter<String>(YumiMain.this,R.layout.support_simple_spinner_dropdown_item,kinds);
                ArrayAdapter<String> sizeadapter=new ArrayAdapter<String>(YumiMain.this,R.layout.support_simple_spinner_dropdown_item,sizes);
                size.setAdapter(sizeadapter);
                kind.setAdapter(kindadapter);
                user.setAdapter(useradapter);
                new AlertDialog.Builder(YumiMain.this)
                        .setView(view)
                        .setPositiveButton("登録", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.execSQL("insert into yumi_list values("+ID.getText()+", "+kind.getSelectedItemId()+"," +
                                        weight.getText()+","+size.getSelectedItemId()+",'')");
                                if (user.getSelectedItem() !=null) {
                                    db.execSQL("insert into yumi_user values(" + Idlist.get(namelist.indexOf(user.getSelectedItem().toString())) + "," + ID.getText() + ")");
                                }
                                listList.add(new YumiMain.yumidata(Integer.parseInt(String.valueOf(ID.getText())),(int) kind.getSelectedItemId(),Double.parseDouble(weight.getText().toString()),
                                        (int) size.getSelectedItemId(), (int) user.getSelectedItemId(),""));
                                yumiListAdapter.notifyDataSetChanged();
                            }
                        }).show().getWindow().setLayout(1500,350);


            }
            int nobi2int(String nobi) {
                switch (nobi) {
                    case "並":
                        return 0;
                    case "伸":
                        return 1;
                    case "その他":
                        return 2;
                }
                return 3;
            }
            int size2int(String size){
                switch (size){
                    case "直心" :return 0;
                    case "練心": return 1;
                    case "その他": return 2;
                }
                return 3;
            }
        });
    }

    public static class weightupcompartor implements Comparator<yumidata> {

        @Override
        public int compare(yumidata o1, yumidata o2) {
            if(o1.weight!=o2.weight)return (o1.weight < o2.weight) ? -1 : 1;
            else return 0;
        }
    }

    public static class weightdwncompartor implements Comparator<yumidata> {

        @Override
        public int compare(yumidata o1, yumidata o2) {
            if(o1.weight!=o2.weight)return (o1.weight > o2.weight) ? -1 : 1;
            else return 0;
        }

    }
    public static class IDdwncompartor implements Comparator<yumidata> {

        @Override
        public int compare(yumidata o1, yumidata o2) {
            if(o1.ID!=o2.ID)return (o1.ID < o2.ID) ? -1 : 1;
            else return 0;
        }

    }

}


