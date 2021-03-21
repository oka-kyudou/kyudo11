package jp.ac.kyudo.Yumi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import jp.ac.kyudo.MainSelect.kyudoDBOpenHelper;
import jp.ac.kyudo.R;

public class YumiListAdapter extends RecyclerView.Adapter<YumiMain.YumiViewHolder> {
    private Context context;
    private List<YumiMain.yumidata> listList=new ArrayList<>();
    private List<String> namelist=new ArrayList<>();
    private kyudoDBOpenHelper helper;
    private SQLiteDatabase db;


    public YumiListAdapter(Context context){
        this.context=context;
    }

    public void setListList(List<YumiMain.yumidata> listList) {
        this.listList = listList;
    }

    public void setNamelist(List<String> namelist) {
        this.namelist = namelist;
    }


    @NonNull
    @Override
    public YumiMain.YumiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate=LayoutInflater.from(parent.getContext()).inflate(R.layout.yumi_list_row,parent,false);
        return new YumiMain.YumiViewHolder(inflate,listList);
    }

    @Override
    public void onBindViewHolder(@NonNull final YumiMain.YumiViewHolder holder, final int position) {

        String[] nobiContent = {"並", "伸", "その他"};
        String[] kindcontent = { "直心", "練心", "その他"};
        ArrayAdapter<String> nobiAdapter = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, nobiContent);
        ArrayAdapter<String> kindAdapter = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, kindcontent);


        holder.IDView.setText(String.valueOf(listList.get(position).ID));
             holder.kindView.setAdapter(kindAdapter);
             holder.kindView.setSelection(listList.get(position).kind);

             holder.weightView.setText(String.valueOf(listList.get(position).weight));
             holder.nobiView.setAdapter(nobiAdapter);
             holder.nobiView.setSelection((listList.get(position).nobi));
            final ArrayAdapter<String> adapter=new ArrayAdapter<String>(context,R.layout.support_simple_spinner_dropdown_item,namelist);
             holder.userView.setAdapter(adapter);
            if (listList.get(position).user==0){
                holder.userView.setSelection(namelist.size()-1);
            }
             else holder.userView.setSelection(listList.get(position).user);

             holder.deletebutton.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     new AlertDialog.Builder(context)
                             .setTitle("弓削除")
                             .setMessage(listList.get(position).ID+"番の弓を削除しますか？")
                             .setPositiveButton("実行", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialog, int which) {
                                     if (helper == null) {
                                         helper = new kyudoDBOpenHelper(context,(Activity)context);
                                     }
                                     if (db == null) {
                                         db = helper.getReadableDatabase();
                                     }
                                     YumiMain.yumidata deletedata= listList.remove(position);
                                     db.execSQL("delete from yumi_list where yumiID="+deletedata.ID);
                                     db.execSQL("delete from yumi_user where yumiID="+deletedata.ID);
                                     adapter.notifyDataSetChanged();
                                 }
                             })
                             .setNegativeButton("キャンセル",null)
                             .show();


                 }
             });
             if (listList.get(position).cation.equals("null")) holder.cationView.setText("");
             else holder.cationView.setText(listList.get(position).cation);







    }



    @Override
    public int getItemCount() {
        return listList.size();
    }

}
