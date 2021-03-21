package jp.ac.kyudo.Member;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;


import java.util.Calendar;

import jp.ac.kyudo.MainSelect.kyudoDBOpenHelper;
import jp.ac.kyudo.R;

public class MemberJoinAll extends AppCompatActivity {
    public String[] data=new String[4];
    ProgressDialog dialog;
    private kyudoDBOpenHelper helper;
    private SQLiteDatabase db;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_join_all);
        final EditText familyName=findViewById(R.id.familyname);
        final EditText firstName=findViewById(R.id.firstname);
        final Spinner sex=findViewById(R.id.sex);
        final Spinner grade=findViewById(R.id.grade);
        Button confirm=findViewById(R.id.confirmjoin);
        dialog=new ProgressDialog(MemberJoinAll.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("登録中");


        //set spinner adapter
        Integer[] gradeSpinnerItems={1,2,3};
        String[] sexSpinnerItems={"男","女","その他"};
        ArrayAdapter<Integer>gradeSpinnerAdapter=new ArrayAdapter<Integer>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                gradeSpinnerItems
        );
        ArrayAdapter<String> sexSpinnerAdapter=new ArrayAdapter<String>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                sexSpinnerItems
        );
        gradeSpinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        sexSpinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        grade.setAdapter(gradeSpinnerAdapter);
        sex.setAdapter(sexSpinnerAdapter);



        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data[0]= String.valueOf(familyName.getText());
                data[1]=String.valueOf(firstName.getText());
                data[2]=grade.getSelectedItem().toString();
                data[3]=sex.getSelectedItem().toString();
                if (data[0].isEmpty()||data[1].isEmpty()) {
                    new AlertDialog.Builder(MemberJoinAll.this)
                            .setMessage("名前を入力してください")
                            .setPositiveButton("OK", null)
                            .show();
                }else {

                    dialog.show();
                    if (helper == null) {
                        helper = new kyudoDBOpenHelper(getApplicationContext(),MemberJoinAll.this);
                    }
                    if (db == null) {
                        db = helper.getReadableDatabase();
                    }

                    int gender=0;
                    switch (data[3]){
                        case "男":gender=0;break;
                        case "女": gender=1;break;
                        case "その他":gender=2;break;
                    }
                    int year=getYear()-Integer.parseInt(data[2])+1;
                    year*=100;
                    Cursor rs=db.rawQuery("select memberID from member_list ",null);
                    //ここの条件分岐間違ってるかも　NULLじゃなかったら直してちょ
                    rs.moveToLast();
                    if (!(String.valueOf(rs.getInt(0)).startsWith(String.valueOf(year).substring(0,4)))||(rs.isNull(0))){
                        year++;
                        db.execSQL("insert into member_list values("+year+", '"+data[0]+"','"+data[1]+"',"+gender+")");
                        db.execSQL("alter table hit_record add column ["+year+"] default 16");
                    }else{
                        year=rs.getInt(0)+1;
                        db.execSQL("insert into member_list values("+year+", '"+data[0]+"','"+data[1]+"',"+gender+")");
                        db.execSQL("alter table hit_record add column ["+year+"] default 16");
                    }
                    db.close();

                dialog.dismiss();
                familyName.setText(null);
                firstName.setText(null);
                recreate();
                }


            }
        });
    }



    public static int getYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(Calendar.YEAR);
    }
}
