package jp.ac.kyudo.MainSelect;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import jp.ac.kyudo.R;

class TotalData{
     int data;
     String name;
    public TotalData(String name, int data){
        this.data=data;
        this.name=name;
    }
}
class DifTotal{
    int data;
    String name;
    int dif;
    DifTotal(String name, int data,int dif){
        this.data=data;
        this.name=name;
        this.dif=dif;
    }
}

public class TotalReport extends Fragment {

    Context context;
    BarChart barChart;
    kyudoDBOpenHelper helper;
    SQLiteDatabase db;
    TotalReport(Context context, BarChart barChart) {
        this.context=context;
        this.barChart=barChart;
        List<TotalData> totallist;

        ProgressDialog dialog;
        if (helper == null) {
            helper = new kyudoDBOpenHelper(context.getApplicationContext(), (Activity) context);
        }
        if (db == null) {
            db = helper.getReadableDatabase();
        }

        totallist=new ArrayList<>();
        dialog=new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        RunScoring runScoring = new RunScoring(context, helper, db, totallist,dialog,barChart,getActivity());
        dialog.show();
        runScoring.execute();





    }

    static class RunScoring extends AsyncTask {
        Context context;
        kyudoDBOpenHelper helper;
        SQLiteDatabase db;
        BarChart barChart;
        List<TotalData> totalist;

        FragmentActivity activity;
        ProgressDialog dialog;

        RunScoring(Context context, kyudoDBOpenHelper helper, SQLiteDatabase db, List<TotalData> totallist, ProgressDialog dialog, BarChart barChart, FragmentActivity activity) {
            this.context = context;
            this.db = db;
            this.helper = helper;
            this.totalist = totallist;
            this.dialog=dialog;
            this.barChart=barChart;
            this.activity=activity;
        }

        private void formatResult2total(int input, int[] result, int date) {
            if (input < 16) {
                switch (input & 3) {
                    case 1:
                        input = input + 1;
                        break;
                    case 2:
                        input = input - 1;
                        break;
                }
                switch (input & 12) {
                    case 4:
                        input = input + 4;
                        break;
                    case 8:
                        input = input - 4;
                        break;
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
                result[6] = date;
            }

        }
        private List<IBarDataSet> getBarData(List<DifTotal> differnce) {

            ArrayList<BarEntry> entries = new ArrayList<>();
            ArrayList<BarEntry> enrty2=new ArrayList<>();
            for (int i=0;i<differnce.size();i++){
                entries.add(new BarEntry(i+1,(int) differnce.get(i).data));
                enrty2.add(new BarEntry(i+1,differnce.get(i).dif));
            }
            List<IBarDataSet> bars = new ArrayList<>();
            BarDataSet dataSet = new BarDataSet(entries, "今週");
            BarDataSet dataSet1=new BarDataSet(enrty2,"先週");
            List<Integer> colors=new ArrayList<>();
            for (DifTotal dif:differnce){
                colors.add((dif.dif+1)*15);
            }

//        //ハイライトさせない

            dataSet.setHighlightEnabled(false);
            dataSet1.setHighlightEnabled(false);

            //Barの色をセット
        dataSet1.setColors(new int[]{Color.LTGRAY},1000);
        dataSet.setColors(new int[]{Color.DKGRAY},2000);
        if (differnce.get(0).data<differnce.get(0).dif){
            bars.add(dataSet1);
            bars.add(dataSet);
        }else {
            bars.add(dataSet);
            bars.add(dataSet1);
        }

            return bars;
        }

        private void setBarChart(BarChart chart, List<DifTotal> hitlist, int maxhit) {
            //Y軸(左)
            YAxis left = chart.getAxisLeft();
            left.setAxisMinimum(0);
            left.setAxisMaximum((int)(maxhit/10)*10+10);
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
            List<String> namelist=new ArrayList<>();
            namelist.add("");
            for (DifTotal name:hitlist){
                namelist.add(name.name);
            }
            xAxis.setValueFormatter(new IndexAxisValueFormatter(namelist.toArray(new String[0])));
            XAxis bottomAxis = chart.getXAxis();
            bottomAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            bottomAxis.setLabelCount(namelist.size());
            bottomAxis.setDrawLabels(true);
            bottomAxis.setDrawGridLines(false);
            bottomAxis.setDrawAxisLine(true);


            //グラフ上の表示
            chart.setDrawValueAboveBar(true);
            chart.getDescription().setEnabled(false);
            chart.setClickable(false);

            //凡例
//            chart.getLegend().setEnabled(false);

            chart.setScaleEnabled(false);
            //アニメーション
//
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            int maxfinalvalue=0;
            Calendar calendar = Calendar.getInstance();
//            int today = calendar.get(Calendar.YEAR) * 10000 + (calendar.get(Calendar.MONTH)+1) * 100 + calendar.get(Calendar.DAY_OF_MONTH);
            int dayweektoMon = (calendar.get(Calendar.DAY_OF_WEEK)+5)%7;
            calendar.add(Calendar.DAY_OF_YEAR, -1 * (dayweektoMon));
            int mondaydate = calendar.get(Calendar.YEAR) * 10000 +( calendar.get(Calendar.MONTH)+1) * 100 + calendar.get(Calendar.DAY_OF_MONTH);
            List<String> IDs = new ArrayList<>();
            final List<List<int[]>> resultlist = new ArrayList<>();

            Cursor C = db.rawQuery("SELECT memberID ,family_name FROM member_list;", null);
            List<String> namelist = new ArrayList<>();
            while (C.moveToNext()) {
                String tid = C.getString(1);
                IDs.add(C.getString(0));
                if (C.getInt(0)==0)continue;
                namelist.add(tid);
            }
            int count=0;
            for (String ID : IDs) {
                List<int[]> exportList = new ArrayList<>();
                Cursor rs = db.rawQuery("SELECT [" + ID.split("  ")[0] + "] , date from hit_record", null);//過去データを参照
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
                    int curDate = Integer.parseInt(rs.getString(rs.getColumnIndex("date")).replace("-", ""));
                    if (curDate != prevDate) {
                        if (prevDate != 0) if (result[6] != 0) exportList.add(result);
                        result = new int[7];
                        prevDate = curDate;
                    }
                    String input = rs.getString(0);
                    if (input == null) input = "16";
                    formatResult2total(Integer.parseInt(input), result, curDate);
                }
                if (result[6] != 0) exportList.add(result);
                resultlist.add(exportList);
                int total = 0;
                if (ID.equals("0"))continue;
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
                totalist.add(new TotalData(namelist.get(count),total));
                count++;
            }
            List<TotalData> lastweek=new ArrayList<>(lastweekformat(IDs,namelist,resultlist));
            List<DifTotal> difference=new ArrayList<>();
            for (int i=0;i<totalist.size();i++){
                difference.add(new DifTotal(totalist.get(i).name,totalist.get(i).data,lastweek.get(i).data));
            }
            Collections.sort(difference,new resultComparator());

            difference.add(0,new DifTotal("矢数",maxfinalvalue,lastweek.get(lastweek.size()-1).data));
            BarData data = new BarData(getBarData(difference));
            barChart.setData(data);
            int biggermax=maxfinalvalue;
            if (biggermax<lastweek.get(lastweek.size()-1).data)biggermax=lastweek.get(lastweek.size()-1).data;
            setBarChart(barChart, difference,biggermax);
            dialog.dismiss();
            return null;
        }
        List<TotalData> lastweekformat(List<String> IDs,List<String> namelist,List<List<int[]>> resultlist){
            List<TotalData> totalData=new ArrayList<>();
            int maxfinalvalue=0;
            Calendar calendar = Calendar.getInstance();
            int dayweektoMon = (calendar.get(Calendar.DAY_OF_WEEK)+5)%7;
            calendar.add(Calendar.DAY_OF_YEAR, -1 * (dayweektoMon)-7);
            int mondaydate = calendar.get(Calendar.YEAR) * 10000 +( calendar.get(Calendar.MONTH)+1) * 100 + calendar.get(Calendar.DAY_OF_MONTH);
            calendar.add(Calendar.DAY_OF_YEAR,6);
            int Sundaydate= calendar.get(Calendar.YEAR) * 10000 +( calendar.get(Calendar.MONTH)+1) * 100 + calendar.get(Calendar.DAY_OF_MONTH);

            int count=0;
            for (String ID : IDs) {
               /*
               0-当たった数
               1-4-1本目-4本目
               5-打った数
               6-打った日付
               */
                int total = 0;
                if (ID.equals("0")) {
                    count++;
                    continue;
                }
                int maxhit=0;
                List<int[]> exportList=new ArrayList<>(resultlist.get(count));
                if (exportList.size() > dayweektoMon) {
                    for (int i = exportList.size()-1 ; i >= dayweektoMon; i--) {
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
                if (maxfinalvalue<maxhit)maxfinalvalue=maxhit;
                totalData.add(new TotalData(namelist.get(count-1),total));
                count++;
            }
            totalData.add(new TotalData("",maxfinalvalue));

            return totalData;
        }

        @Override
        protected void onPostExecute(Object o) {
            barChart.animateY(1200, Easing.Linear);
        }

        public static class resultComparator implements Comparator<DifTotal>{

            @Override
            public int compare(DifTotal o1, DifTotal o2) {
                if (o1.data!=o2.data) return (o1.data>o2.data)?-1:1;
                else return 0;
            }
        }



    }



}
