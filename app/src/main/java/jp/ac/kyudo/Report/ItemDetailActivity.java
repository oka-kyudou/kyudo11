package jp.ac.kyudo.Report;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.NavUtils;
import androidx.preference.PreferenceManager;

import android.service.autofill.Dataset;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import jp.ac.kyudo.MainSelect.kyudoDBOpenHelper;
import jp.ac.kyudo.R;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ItemListActivity}.
 */
public class ItemDetailActivity extends AppCompatActivity {

    private LineChart mChart;
    private LineChart rChart;
    private BarChart lChart;
    private TextView av1;
    private TextView av2;
    private TextView av3;
    private TextView av4;
    private TextView av2mon;
    private TextView avall;
    int raito=1;
    static String ID;
    static String[] labels;
    static String[] llabels;
    private ProgressDialog dialog;
    private kyudoDBOpenHelper helper;
    private SQLiteDatabase db;
    private int month;
    class TaigaData{
        Float rate;
        String data;
        int number;
        TaigaData(Float rate, String date, int number){
            this.rate=rate;
            this.data=date;
            this.number=number;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        av1=findViewById(R.id.av1);
        av2=findViewById(R.id.av2);
        av3=findViewById(R.id.av3);
        av4=findViewById(R.id.av4);
        av2mon=findViewById(R.id.av2mon);
        avall=findViewById(R.id.avfull);
        setSupportActionBar(toolbar);
        dialog = new ProgressDialog(ItemDetailActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("計算中");
        dialog.show();
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(ItemDetailActivity.this);
        month=preferences.getInt("weekspan",1);
        if (month==0)month++;
        TextView textView=findViewById(R.id.textView4);
        TextView textView1=findViewById(R.id.TextView10);
        TextView textView2 =findViewById(R.id.textView11);
        textView.setText("過去"+month+"ヶ月の的中率");
        textView1.setText("過去"+month+"ヶ月の的中分布");
        textView2.setText(month+"ヶ月平均");
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        if (helper == null) {
            helper = new kyudoDBOpenHelper(getApplicationContext(),this);
        }
        if (db == null) {
            db = helper.getReadableDatabase();
        }


        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ItemDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(ItemDetailFragment.ARG_ITEM_ID));
            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();
            ID=getIntent().getStringExtra("LIST");
            Log.d("ID",ID);


        }


        if(!ID.split("  ")[0].equals("1")) createindividata(ID);
//        else {
//            float[] range=new float[3];
//            for(int i=0;i<3;i++){
//                range[i]=creategroupdata(i);
//            }





        dialog.dismiss();

    }
//    private float creategroupdata(int grade){
//        int thisyear=2021;
//        Cursor c=db.rawQuery("select memberID from member_list where memberID like '2020__'",null);
//        c.moveToFirst();
//        List<Integer> IDlist=new ArrayList<>();
//        while(c.moveToNext()){
//            IDlist.add(c.getInt(0));
//        }
//        c.close();
//
//        List<int[]> gradecontent=new ArrayList<>();
//
//        for(int ID : IDlist){
//            Cursor rs = db.rawQuery("SELECT [" + ID+ "] , date from hit_record",null);//過去データを参照
//            int prevDate = 0;
//
//            int[] result = new int[7];
//               /*
//               0-当たった数
//               1-4-1本目-4本目
//               5-打った数
//               6-打った日付
//               */
//            rs.moveToFirst();
//
//            List<int[]> exportList = new ArrayList<>();
//            while (rs.moveToNext()) {
//                int curDate = Integer.parseInt(rs.getString(rs.getColumnIndex("date")).replace("-",""));
//                if (curDate != prevDate) {
//                    if (prevDate != 0) {
//                        if (result[6]!=0) exportList.add(result);
//                    }
//                    result = new int[7];
//                    prevDate = curDate;
//                }
//                String input=rs.getString(0);
//                if (input==null)input="16";
//                formatResult2total(Integer.parseInt(input), result,curDate);
//            }
//            if (result[6]!=0)exportList.add(result);
//            int[] total=new int[2];
//            for(int[] res:exportList){
//                total[0]+=res[0];
//                total[1]+=res[5];
//            }
//            gradecontent.add(total);
//        }
//        float tothit=0;
//        float totlnc=0;
//        for(int[] col:gradecontent){
//            tothit+=col[0];
//            totlnc+=col[1];
//        }
//        return tothit/totlnc;
//    }
    private void createindividata(String ID){
        List<int[]> exportList = new ArrayList<>();

        Cursor rs = db.rawQuery("SELECT [" + ID.split("  ")[0] + "] , date from hit_record",null);//過去データを参照
        Log.d("SQL","SELECT hit_record." + ID + ", date from kyudodb.hit_record");

        int prevDate = 0;

        int[] result = new int[7];
               /*
               0-当たった数
               1-4-1本目-4本目
               5-打った数
               6-打った日付
               */
        rs.moveToFirst();

        while (rs.moveToNext()) {
            int curDate = Integer.parseInt(rs.getString(rs.getColumnIndex("date")).replace("-",""));
            if (curDate != prevDate) {
                if (prevDate != 0) {
                    //result[6] = prevDate;
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
        rs.close();
        List<Float> tempResult=formatData4View(exportList);

        Float[] list=tempResult.toArray(new Float[0]);
        Log.d("result", Arrays.toString(list));

        List<String> rlabels=new ArrayList<>();
        List<Float> rlist=new ArrayList<>();
        Date AMonthBefore=getNowDate(month);

        try {
            getRecentMonth(list,labels,AMonthBefore,rlist,rlabels);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<List<Integer>> List4FourLine = formatData4View4FourLineChart(exportList);
        Integer[] data1=List4FourLine.get(0).toArray(new Integer[0]);
        Integer[] data2=List4FourLine.get(1).toArray(new Integer[0]);
        Integer[] data3=List4FourLine.get(2).toArray(new Integer[0]);
        Integer[] data4=List4FourLine.get(3).toArray(new Integer[0]);

        String[] RLabels=rlabels.toArray(new String[0]);
        Float[] RList=rlist.toArray(new Float[0]);

        mChart = findViewById(R.id.lineChart);
        rChart=findViewById(R.id.lineChartR);
        lChart=findViewById(R.id.lineChartL);

        setChart(mChart,list,labels,avall);
        setChart(rChart,RList,RLabels,av2mon);
        setChart4FourLineChart(lChart,data1,data2,data3,data4,llabels);
        setData4ScatterChart(exportList,RList,RLabels);
        Calendar calendar = Calendar.getInstance();
        List<Integer> weeklytotal=new ArrayList<>();
        List<Integer> weeklymax=new ArrayList<>();
        List<String> labels=new ArrayList<>();
        int dayweektoMon = (calendar.get(Calendar.DAY_OF_WEEK)+5)%7;
        calendar.add(Calendar.DAY_OF_YEAR, -1 * (dayweektoMon));
        int mondaydate = calendar.get(Calendar.YEAR) * 10000 +( calendar.get(Calendar.MONTH)+1) * 100 + calendar.get(Calendar.DAY_OF_MONTH);
        int[] weekdata=getweekhit(exportList,dayweektoMon,mondaydate,1000000000);
        weeklytotal.add(weekdata[0]);
        weeklymax.add(weekdata[1]);
        labels.add(String.valueOf(mondaydate).substring(4,6)+"/"+String.valueOf(mondaydate).substring(6,8));
        calendar=Calendar.getInstance();
        for (int i=0;i<10;i++) {
            dayweektoMon = (calendar.get(Calendar.DAY_OF_WEEK)+5)%7;
            calendar.add(Calendar.DAY_OF_YEAR, -1 * (dayweektoMon)-7);
            mondaydate = calendar.get(Calendar.YEAR) * 10000 + (calendar.get(Calendar.MONTH) + 1) * 100 + calendar.get(Calendar.DAY_OF_MONTH);
            calendar.add(Calendar.DAY_OF_YEAR, 6);
            int Sundaydate = calendar.get(Calendar.YEAR) * 10000 + (calendar.get(Calendar.MONTH) + 1) * 100 + calendar.get(Calendar.DAY_OF_MONTH);
            weekdata=getweekhit(exportList, dayweektoMon, mondaydate, Sundaydate);
            weeklymax.add(weekdata[1]);
            weeklytotal.add(weekdata[0]);
            labels.add(String.valueOf(mondaydate).substring(4,6)+"/"+String.valueOf(mondaydate).substring(6,8));
        }
        BarChart weeklychart=findViewById(R.id.weekly_bar);
        Collections.reverse(weeklymax);
        Collections.reverse(weeklytotal);
        Collections.reverse(labels);
        setweeklyChart(weeklychart,weeklytotal,weeklymax,labels);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, ItemListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void setweeklyChart(BarChart chart,List<Integer> weeklytotal,List<Integer> weeklymax,List<String> labels){
        List<BarEntry> entries=new ArrayList<>();
        List<BarEntry> dataentries=new ArrayList<>();
        for (int i=0;i<weeklymax.size();i++){
            entries.add(new BarEntry(i,weeklymax.get(i),null,null));
            dataentries.add(new BarEntry(i,weeklytotal.get(i),null,null));
        }
        BarDataSet maxdataset=new BarDataSet(entries,"矢数");
        BarDataSet totaldataset=new BarDataSet(dataentries,"的中数");
        ArrayList<IBarDataSet> dataSets=new ArrayList<>();
        maxdataset.setColor(Color.LTGRAY);
        totaldataset.setColor(Color.DKGRAY);
        dataSets.add(maxdataset);
        dataSets.add(totaldataset);


        //Y軸(左)
        YAxis left = chart.getAxisLeft();
        left.setAxisMinimum(0);
        left.setLabelCount(4);
        left.setDrawTopYLabelEntry(true);
        //Y軸(右)
        YAxis right = chart.getAxisRight();
        right.setDrawLabels(false);
        right.setDrawGridLines(false);
        right.setDrawZeroLine(true);
        right.setDrawTopYLabelEntry(true);

        //X軸
        XAxis xAxis = chart.getXAxis();
        //X軸に表示するLabelのリスト(最初の""は原点の位置)

        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        XAxis bottomAxis = chart.getXAxis();
        bottomAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        bottomAxis.setLabelCount(11);
        bottomAxis.setDrawLabels(true);
        bottomAxis.setDrawGridLines(false);
        bottomAxis.setDrawAxisLine(true);

        //グラフ上の表示
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);
        chart.setClickable(false);

        //凡例
        chart.getLegend().setEnabled(false);
        chart.setHighlightPerTapEnabled(false);
        chart.setHighlightPerDragEnabled(false);

        chart.setScaleEnabled(false);
        BarData barData=new BarData(dataSets) ;
        chart.setData(barData);
    }
    private void  setData(Float[] data,LineChart mChart,TextView textView) {
        // Entry()を使ってLineDataSetに設定できる形に変更してarrayを新しく作成
//        int data[] = {116, 111, 112, 121, 102, 83,
//                99, 101, 74, 105, 120, 112,
//                109, 102, 107, 93, 82, 99, 110,
//        };

        ArrayList<Entry> values;
        values = new ArrayList<>();
        double total=0;

        for (int i = 0; i < data.length; i++) {
            values.add(new Entry(i, data[i], null, null));
            total+=data[i];
        }

        LineDataSet set1;
        LineDataSet set2;

        float average= (float) (total/data.length);
        textView.setText(String.format("%.1f",average)+"%");
        ArrayList<Entry> value2=new ArrayList<>();
        value2.add(new Entry(0,average,null,null));
        value2.add(new Entry(data.length,average,null,null));

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {

            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set2=(LineDataSet) mChart.getData().getDataSetByIndex(1);
            set1.setValues(values);
            set2.setValues(value2);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values,null);
            set2=new LineDataSet(value2,null);

            set1.setDrawIcons(false);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(0f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            set1.setFillColor(Color.GRAY);


            set2.setDrawIcons(false);
            set2.setColor(Color.RED);
            set2.setCircleColor(Color.RED);
            set2.setLineWidth(1f);
            set2.setCircleRadius(3f);
            set2.setDrawCircleHole(false);
            set2.setValueTextSize(0f);
            set2.setDrawFilled(false);
            set2.setFormLineWidth(1f);
            set2.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set2.setFormSize(15.f);

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets
            dataSets.add(set2);


            // create a data object with the datasets
            LineData lineData = new LineData(dataSets);

            // set data
            mChart.setData(lineData);
        }
    }
    private void  setData4FourLineChart(Integer[] data1,Integer[] data2,Integer[] data3,Integer[] data4,BarChart mChart) {
        // Entry()を使ってLineDataSetに設定できる形に変更してarrayを新しく作成
        ArrayList<BarEntry> values1 = new ArrayList<>();
        ArrayList<BarEntry> values2 = new ArrayList<>();
        ArrayList<BarEntry> values3 = new ArrayList<>();
        ArrayList<BarEntry> values4 = new ArrayList<>();


        for (int i = 0; i < data1.length; i++) {
            values1.add(new BarEntry(i, data1[i]/4, null, null));
            values2.add(new BarEntry(i, (data2[i]+data1[i])/4, null, null));
            values3.add(new BarEntry(i, (data3[i]+data2[i]+data1[i])/4, null, null));
            values4.add(new BarEntry(i, (data4[i]+data3[i]+data2[i]+data1[i])/4, null, null));
        }


        BarDataSet set1;
        BarDataSet set2;
        BarDataSet set3;
        BarDataSet set4;



        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {

            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set2=(BarDataSet) mChart.getData().getDataSetByIndex(1);
            set3=(BarDataSet) mChart.getData().getDataSetByIndex(2);
            set4=(BarDataSet) mChart.getData().getDataSetByIndex(3);

            set1.setValues(values1);
            set2.setValues(values2);
            set3.setValues(values3);
            set4.setValues(values4);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new BarDataSet(values1,null);
            set2=new BarDataSet(values2,null);
            set3=new BarDataSet(values3,null);
            set4=new BarDataSet(values4,null);

            set1.setDrawIcons(false);
            set1.setColor(Color.LTGRAY);
//            set1.setCircleColor(Color.BLACK);
//            set1.setLineWidth(1f);
//            set1.setCircleRadius(3f);
//            set1.setDrawCircleHole(false);
            set1.setValueTextSize(0f);
//            set1.setDrawFilled(false);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);
            set1.setLabel("1本目");

            set2.setDrawIcons(false);
            set2.setColor(Color.GRAY);
//            set2.setCircleColor(Color.RED);
//            set2.setLineWidth(1f);
//            set2.setCircleRadius(3f);
//            set2.setDrawCircleHole(false);
            set2.setValueTextSize(0f);
//            set2.setDrawFilled(false);
            set2.setFormLineWidth(1f);
            set2.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set2.setFormSize(15.f);
            set2.setLabel("2本目");

            set3.setDrawIcons(false);
            set3.setColor(Color.DKGRAY);
//            set3.setCircleColor(Color.BLUE);
//            set3.setLineWidth(1f);
//            set3.setCircleRadius(3f);
//            set3.setDrawCircleHole(false);
            set3.setValueTextSize(0f);
//            set3.setDrawFilled(false);
            set3.setFormLineWidth(1f);
            set3.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set3.setFormSize(15.f);
            set3.setLabel("3本目");

            set4.setDrawIcons(false);
            set4.setColor(Color.BLACK);
//            set4.setCircleColor(Color.GREEN);
//            set4.setLineWidth(1f);
//            set4.setCircleRadius(3f);
//            set4.setDrawCircleHole(false);
            set4.setValueTextSize(0f);
//            set4.setDrawFilled(false);
            set4.setFormLineWidth(1f);
            set4.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set4.setFormSize(15.f);
            set4.setLabel("4本目");


            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set4); // add the datasets
            dataSets.add(set3);
            dataSets.add(set2);
            dataSets.add(set1);


            // create a data object with the datasets
            BarData lineData = new BarData(dataSets);

            // set data
            mChart.setData(lineData);
        }
    }
    private void setChart(LineChart mChart,Float[] list,String[] labels,TextView textView){
        // Grid背景色
        mChart.setDrawGridBackground(false);

        // no description text
        mChart.getDescription().setEnabled(false);

        mChart.getLegend().setEnabled(false);

        // Grid縦軸を破線
        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(true);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(10);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
       // xAxis.setLabelsToSkip(9);



        YAxis leftAxis = mChart.getAxisLeft();
        // Y軸最大最小設定
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
        // Grid横軸を破線
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(true);


        // 右側の目盛り
        mChart.getAxisRight().setEnabled(false);


        // add data
        setData(list,mChart,textView);
//        setData(new Integer[]{1},mChart);

        mChart.animateX(0, Easing.EaseInBack);


    }
    private void setChart4FourLineChart(BarChart mChart, Integer[] data1, Integer[] data2, Integer[] data3, Integer[] data4, String[] labels){
        // Grid背景色
        mChart.setDrawGridBackground(false);

        // no description text
        mChart.getDescription().setEnabled(false);

        mChart.getLegend().setEnabled(true);

        // Grid縦軸を破線
        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(true);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(10);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        // xAxis.setLabelsToSkip(9);



        YAxis leftAxis = mChart.getAxisLeft();
        // Y軸最大最小設定
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
        // Grid横軸を破線
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(true);


        // 右側の目盛り
        mChart.getAxisRight().setEnabled(false);


        // add data
        setData4FourLineChart(data1,data2,data3,data4,mChart);
//        setData(new Integer[]{1},mChart);


        mChart.animateX(0, Easing.EaseInBack);

        mChart.moveViewToX(xAxis.mAxisMaximum);
        mChart.zoom(8,1,1,1);
    }
    static void formatResult2total(int input, int[] result,int date){
        if (input<16) {
            switch (input&3){
                case 1:input+=1;break;
                case 2:input-=1;break;
            }
            switch (input&12){
                case 4:input+=4;break;
                case 8:input-=4;break;
            }
            if ((input & 1) == 1) {
                result[0]++;
                result[1]++;
            }
            if ((input & 2) == 2) {
                result[0]++;
                result[2]++;
            }
            if ((input & 4) == 4) {
                result[0]++;
                result[3]++;
            }
            if ((input & 8) == 8) {
                result[0]++;
                result[4]++;
            }
            result[5] += 4;
            result[6]=date;
        }
    }
    List<Float> formatData4View(List<int[]> input){
        List<Float> ViewContentmain=new ArrayList<>();
        List<Float> FinalViewContentmain=new ArrayList<>();
        List<String> labeloriginal=new ArrayList<>();
        for (int i=0;i<input.size();i++){
            int[] DayRecord=input.get(i);
            double temp1=DayRecord[0];
            double temp2=DayRecord[5];
            ViewContentmain.add((float) ((temp1/temp2)*100));
            if (i%raito==0) {
                labeloriginal.add(String.valueOf(DayRecord[6]).substring(0, 4) + "/" + String.valueOf(DayRecord[6]).substring(4, 6) + "/" + String.valueOf(DayRecord[6]).substring(6, 8));
            }
        }
        double total=0;
        for (int i=0 ;i<ViewContentmain.size();i++){
            total+=ViewContentmain.get(i);
            if (i%raito==0){
                FinalViewContentmain.add((float) (total/1));
                total=0;
            }

        }
        labels=labeloriginal.toArray(new String[0]);
        Log.d("labels", Arrays.toString(labels));
        return FinalViewContentmain;
    }
    List<List<Integer>> formatData4View4FourLineChart(List<int[]> input){
        List<List<Integer>> ViewContentmain=new ArrayList<>();

        List<Integer> Firsthit=new ArrayList<>();
        List<Integer> Secondhit=new ArrayList<>();
        List<Integer> Thridhit=new ArrayList<>();
        List<Integer> Fourthhit=new ArrayList<>();

        List<String> labeloriginal=new ArrayList<>();

        for (int i=0;i<input.size();i++){
            int[] DayRecord=input.get(i);
            double First=DayRecord[1];
            double Second=DayRecord[2];
            double Third=DayRecord[3];
            double Fourth=DayRecord[4];
            double totalEach=DayRecord[5];
            Firsthit.add((int) (First/(totalEach/4)*100));
            Secondhit.add((int) (Second/(totalEach/4)*100));
            Thridhit.add((int) (Third/(totalEach/4)*100));
            Fourthhit.add((int) (Fourth/(totalEach/4)*100));
            if (i%raito==0) {
                labeloriginal.add(String.valueOf(DayRecord[6]).substring(4, 6) + "/" + String.valueOf(DayRecord[6]).substring(6, 8));
            }
        }
        double total1=0,FinTotal1=0;
        double total2=0,FinTotal2=0;
        double total3=0,FinTotal3=0;
        double total4=0,FinTotal4=0;

        List<Integer> FinalFirsthit=new ArrayList<>();
        List<Integer> FinalSecondhit=new ArrayList<>();
        List<Integer> FinalThridhit=new ArrayList<>();
        List<Integer> FinalFourthhit=new ArrayList<>();

        for (int i =0 ; i<Firsthit.size();i++){
            total1+=Firsthit.get(i);
            FinTotal1+=Firsthit.get(i);
            total2+=Secondhit.get(i);
            FinTotal2+=Secondhit.get(i);
            total3+=Thridhit.get(i);
            FinTotal3+=Thridhit.get(i);
            total4+=Fourthhit.get(i);
            FinTotal4+=Fourthhit.get(i);
            if(i%raito==0){
                FinalFirsthit.add((int)(total1/raito));
                FinalSecondhit.add((int)(total2/raito));
                FinalThridhit.add((int)(total3/raito));
                FinalFourthhit.add((int)(total4/raito));

                total1=0;
                total2=0;
                total3=0;
                total4=0;

            }
        }
        llabels=labeloriginal.toArray(new String[0]);
        Log.d("labels", Arrays.toString(llabels));
        ViewContentmain.add(FinalFirsthit);
        ViewContentmain.add(FinalSecondhit);
        ViewContentmain.add(FinalThridhit);
        ViewContentmain.add(FinalFourthhit);
        double av1val=FinTotal1/Firsthit.size();
        double av2val=FinTotal2/Secondhit.size();
        double av3val=FinTotal3/Thridhit.size();
        double av4val=FinTotal4/Fourthhit.size();

        av1.setText(String.format("%.1f",av1val)+"%");
        av2.setText(String.format("%.1f",av2val)+"%");
        av3.setText(String.format("%.1f",av3val)+"%");
        av4.setText(String.format("%.1f",av4val)+"%");

        return ViewContentmain;
    }
    void setData4ScatterChart(List<int[]> exportlist, Float[] list,String[] datelist){
        List<TaigaData> taigadata=new ArrayList<>();
        List<Entry> yentries=new ArrayList<>();
        HashSet<Integer> xlabel=new HashSet<>();
        for (int i=1;i<=list.length;i++){
                taigadata.add(new TaigaData(list[list.length-i],datelist[datelist.length-i],exportlist.get(exportlist.size()-i)[5]));
        }
        Collections.sort(taigadata,new Compartor());
        int labelcount=0;
        for (TaigaData data:taigadata){
                yentries.add(new Entry(data.number,data.rate));
                xlabel.add(data.number);
        }
        ScatterDataSet dataSet=new ScatterDataSet(yentries,"");
        dataSet.setScatterShapeSize(50f);
        dataSet.setColor(Color.BLACK,70);
        ScatterData scatterData=new ScatterData(dataSet);
        dataSet.setDrawValues(false);
        ScatterChart scatterChart=findViewById(R.id.ScatterChart);
        XAxis xAxis=scatterChart.getXAxis();
        List<Integer> xll=new ArrayList<>(xlabel);
        Collections.sort(xll,Collections.reverseOrder());
        List<String> finalxlabels=new ArrayList<>();
        if (xll.size()==0) xll.add(0);
        for (int val=0;val<=xll.get(0);val++){
            finalxlabels.add(String.valueOf(val));
        }
        xAxis.setValueFormatter(new IndexAxisValueFormatter(finalxlabels));
//        xAxis.setLabelCount(labelcount);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(true);
        xAxis.setDrawGridLinesBehindData(true);

        YAxis right = scatterChart.getAxisRight();
        right.setDrawLabels(false);
        right.setDrawGridLines(false);
        right.setDrawZeroLine(true);
        right.setDrawTopYLabelEntry(true);
        scatterChart.setData(scatterData);
        scatterChart.getLegend().setEnabled(false);
        scatterChart.getDescription().setEnabled(false);
        scatterChart.animateX(1200);
        scatterChart.setHighlightPerTapEnabled(false);
        scatterChart.setScaleEnabled(false);
        YAxis left=scatterChart.getAxisLeft();
        left.setAxisMaximum(110);
        left.setAxisMinimum(-10);
    }
    void getRecentMonth(Float[] list, String[] label, Date AMonthBefore, List<Float> rlist, List<String> rlabels) throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        for (int i=label.length-1;i>=0;i--){
            Date currDate=dateFormat.parse(label[i]);
            if (currDate.before(AMonthBefore)) break;
            rlabels.add(label[i].substring(5));
            rlist.add(list[i]);
        }

        Collections.reverse(rlist);
        Collections.reverse(rlabels);


    }
    class Compartor implements Comparator<TaigaData> {

        @Override
        public int compare(TaigaData o1, TaigaData o2) {
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1.number == o2.number) {
                return ((o1.rate) >(o2.rate)) ? 1 : -1;
            } else {
                return (o1.number > o2.number) ? 1 : -1;
            }
        }

    }
    int[] getweekhit(List<int[]>exportList,int dayweektoMon,int mondaydate,int Sundaydate){
        int maxhit=0;
        int total=0;
        if (exportList.size() > dayweektoMon) {
            for (int i = exportList.size()-1 ; i >= dayweektoMon-1; i--) {
                if (exportList.get(i)[6] < mondaydate) break;
                if (exportList.get(i)[6]>Sundaydate)continue;
                total += exportList.get(i)[0];
                maxhit+=exportList.get(i)[5];
            }
        } else {
            for (int i = exportList.size()-1; i >= 0; i--) {
                if (exportList.get(i)[6] < mondaydate) break;
                if (exportList.get(i)[6]>Sundaydate)continue;
                total += exportList.get(i)[0];
                maxhit+=exportList.get(i)[5];

            }
        }
        return new int[]{total,maxhit};
    }
    public static Date getNowDate(int month){
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH,month*-1);
        Date date=calendar.getTime();
        Log.d("date", String.valueOf(date));
        return date;
    }
}
