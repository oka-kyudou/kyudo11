package jp.ac.kyudo.Report;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import jp.ac.kyudo.MainSelect.kyudoDBOpenHelper;
import jp.ac.kyudo.R;
import jp.ac.kyudo.Report.dummy.DummyContent;

import static jp.ac.kyudo.Report.ItemDetailActivity.formatResult2total;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity{

    private  RecyclerView recyclerView;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private List<String> content=new ArrayList<>();
    private static final List<String> nameList = new ArrayList<>();
    private kyudoDBOpenHelper helper;
    private SQLiteDatabase db;
    List<String> IDs=new ArrayList<>();
    List<String> namelist=new ArrayList<>();
    int dayweektoMon = 2;
    static class TotalData{
        int data;
        String name;
        float raito;
        public TotalData(String name, int data,float raito){
            this.data=data;
            this.name=name;
            this.raito=raito;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
//        getSupportLoaderManager().initLoader(0, null, this);
        DummyContent.DeleteITEMS();
        //
        // ここで非同期の処理をする
        //
        String[] namel = new String[0];
        if (helper == null) {
            helper = new kyudoDBOpenHelper(getApplicationContext(),this);
        }
        if (db == null) {
            db = helper.getReadableDatabase();
        }
            //Toast.makeText(MemberSet.this,"connected", Toast.LENGTH_LONG).show();
            Cursor rs = db.rawQuery("SELECT memberID ,family_name, first_name FROM member_list;",null);
            while (rs.moveToNext()) {
                //int id=rs.getInt(1);
                //int location=rs.getInt(2);
                String tid = rs.getString(1);
                String time = rs.getString(2);
                String id = rs.getString(0);
                //int sex = rs.getInt(4);
                //dataset.add(id+"  "+tid+" "+time);
                //global.setTestString(tid+" "+time);
                namelist.add(id+"  "+tid + " " + time);
                IDs.add((id));
            }


        namel=namelist.toArray(new String[0]);
        for (int i = 1; i <= namel.length; i++) {
            DummyContent.addItem(new DummyContent.DummyItem(String.valueOf(i), namel[i-1], DummyContent.makeDetails(i)));
        }

        TextView raitobox=findViewById(R.id.raitobox);
        StringBuilder raitotext=new StringBuilder();
        float[] range=new float[3];
        for(int i=0;i<3;i++){
            raitotext.append(i + 1).append("年生の的中率：");
            raitotext.append(creategroupdata(i)*100);
            raitotext.append("%   ");
        }
        raitobox.setText(raitotext);

        EditText editText=findViewById(R.id.editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int count=0;
                final List<TotalData> totalist=new ArrayList<>();
                Calendar calendar = Calendar.getInstance();
                final TextView ranking=findViewById((R.id.Ranking_text));

//            int today = calendar.get(Calendar.YEAR) * 10000 + (calendar.get(Calendar.MONTH)+1) * 100 + calendar.get(Calendar.DAY_OF_MONTH);


                String num=editable.toString();
                if(num.isEmpty())dayweektoMon=1;
                else dayweektoMon=Integer.parseInt(num);
                calendar.add(Calendar.DAY_OF_YEAR, -1 * (dayweektoMon));
                int mondaydate = calendar.get(Calendar.YEAR) * 10000 +( calendar.get(Calendar.MONTH)+1) * 100 + calendar.get(Calendar.DAY_OF_MONTH);
                int maxfinalvalue=0;
                for (String ID : IDs) {
                    List<int[]> exportList = new ArrayList<>();
                    Cursor C = db.rawQuery("SELECT [" + ID.split("  ")[0] + "] , date from hit_record", null);//過去データを参照
                    int prevDate = 0;
                    int[] result = new int[7];
               /*
               0-当たった数
               1-4-1本目-4本目
               5-打った数
               6-打った日付
               */
                    C.moveToFirst();

                    while (C.moveToNext()) {
                        int curDate = Integer.parseInt(C.getString(C.getColumnIndex("date")).replace("-", ""));
                        if (curDate != prevDate) {
                            if (prevDate != 0) if (result[6] != 0) exportList.add(result);
                            result = new int[7];
                            prevDate = curDate;
                        }
                        String input = C.getString(0);
                        if (input == null) input = "16";
                        formatResult2total(Integer.parseInt(input), result, curDate);
                    }
                    if (result[6] != 0) exportList.add(result);
                    int total = 0;
                    if (ID.equals("0")){
                        count++;
                        continue;
                    }
                    int maxhit=0;
                    if (exportList.size() > dayweektoMon) {
                        for (int i = exportList.size()-1 ; i >= dayweektoMon-1; i--) {
                            if (exportList.get(i)[6] < mondaydate) break;
                            total += exportList.get(i)[0];
                            maxhit+=exportList.get(i)[5];
                        }
                    } else {
                        for (int i = exportList.size()-1; i >= 0; i--) {
                            if (exportList.get(i)[6] < mondaydate) break;
                            total += exportList.get(i)[0];
                            maxhit+=exportList.get(i)[5];

                        }
                    }
                    if (maxfinalvalue<maxhit)maxfinalvalue=maxhit;
                    float raito;
                    if(maxhit!=0) {
                         raito = ((float) total / (float) maxhit) * 100;
                    }else{
                        raito=0;
                    }
                    totalist.add(new TotalData(namelist.get(count), total,raito));
                    count++;
                }
                final Switch si1=findViewById(R.id.raitoswitch);
                si1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        StringBuilder rankingbuilder=new StringBuilder();
                        if(si1.isChecked()){
                            Collections.sort(totalist,new raitoComparator());
                            rankingbuilder.append("過去"+dayweektoMon+"日間のランキング\n");
                            for(TotalData t:totalist){
                                rankingbuilder.append(t.name+" "+String.format("%.02f", t.raito)+"%\n");
                            }
                        }else{
                            Collections.sort(totalist,new resultComparator());
                            rankingbuilder.append("過去"+dayweektoMon+"日間のランキング\n");
                            for(TotalData t:totalist){
                                rankingbuilder.append(t.name+" "+t.data+"中\n");
                            }
                        }
                        ranking.setText(rankingbuilder);
                    }
                });


                si1.callOnClick();


            }
        });


        Log.d("text", Arrays.toString(namel));
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(ItemListActivity.this, DummyContent.ITEMS, mTwoPane));
    }

//
//    private void setupRecyclerView(@NonNull final RecyclerView recyclerView) {
//
//    }


    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ItemListActivity mParentActivity;
        private final List<DummyContent.DummyItem> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.id);
                    ItemDetailFragment fragment = new ItemDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, item.id);
                    intent.putExtra("LIST",item.content);

                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(ItemListActivity parent,
                                      List<DummyContent.DummyItem> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mContentView.setText(mValues.get(position).content);

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mContentView = view.findViewById(R.id.content);
            }
        }
    }
    public static class resultComparator implements Comparator<TotalData> {

        @Override
        public int compare(TotalData o1, TotalData o2) {
            if (o1.data!=o2.data) return (o1.data>o2.data)?-1:1;
            else return 0;
        }
    }
    public static class raitoComparator implements Comparator<TotalData> {

        @Override
        public int compare(TotalData o1, TotalData o2) {
            if (o1.raito!=o2.raito) return (o1.raito>o2.raito)?-1:1;
            else return 0;
        }
    }
    private float creategroupdata(int grade){
        int year=getYear()-grade;

        Cursor c=db.rawQuery("select memberID from member_list where memberID like '"+year+"__'",null);
        c.moveToFirst();
        List<Integer> IDlist=new ArrayList<>();
        while(c.moveToNext()){
            IDlist.add(c.getInt(0));
        }
        c.close();

        List<int[]> gradecontent=new ArrayList<>();

        for(int ID : IDlist){
            Cursor rs = db.rawQuery("SELECT [" + ID+ "] , date from hit_record",null);//過去データを参照
            int prevDate = 0;

            int[] result = new int[7];
               /*
               0-当たった数
               1-4-1本目-4本目
               5-打った数
               6-打った日付
               */
            rs.moveToFirst();

            List<int[]> exportList = new ArrayList<>();
            while (rs.moveToNext()) {
                int curDate = Integer.parseInt(rs.getString(rs.getColumnIndex("date")).replace("-",""));
                if (curDate != prevDate) {
                    if (prevDate != 0) {
                        if (result[6]!=0) exportList.add(result);
                    }
                    result = new int[7];
                    prevDate = curDate;
                }
                String input=rs.getString(0);
                if (input==null)input="16";
                formatResult2total(Integer.parseInt(input), result,curDate);
            }
            if (result[6]!=0)exportList.add(result);
            int[] total=new int[2];
            for(int[] res:exportList){
                total[0]+=res[0];
                total[1]+=res[5];
            }
            gradecontent.add(total);
        }
        float tothit=0;
        float totlnc=0;
        for(int[] col:gradecontent){
            tothit+=col[0];
            totlnc+=col[1];
        }
        return tothit/totlnc;
    }

    private int getYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(Calendar.YEAR);
    }


}
