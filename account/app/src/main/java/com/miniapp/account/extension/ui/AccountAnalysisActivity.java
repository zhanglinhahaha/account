package com.miniapp.account.extension.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import java.util.ArrayList;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.miniapp.account.BaseActivity;
import com.miniapp.account.FileUtil;
import com.miniapp.account.R;
import com.miniapp.account.extension.ExtensionUtil;


public class AccountAnalysisActivity extends BaseActivity {
    private static final String TAG = "AccountAnalysisActivity";
    private Context mContext;
    private Toast mToast = null;
    private PieChart mPieChart;
    private LineChart mLineChart;
    private TextView mTextView;
    ArrayList<PieEntry> mPieEntry = new ArrayList<>();
    ArrayList<Entry> mLineEntry = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_analysis);
        mContext = this;
        mTextView = (TextView) findViewById(R.id.filename);
        mPieChart = (PieChart) findViewById(R.id.pc);
        mLineChart = (LineChart) findViewById(R.id.lc);
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) actionBar.setTitle(R.string.nav_menu_analysis);
        initDataBasedSql();
    }

    private void initDataBasedSql() {
        ExtensionUtil.openSql(mContext);
        DrawChart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.other_menu, menu);
        MenuItem importFile_item = menu.findItem(R.id.importFile);
        importFile_item.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.importFile) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, 1);
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String path = null;
        Uri uri = null;
        if (resultCode == Activity.RESULT_OK) {
            uri = data.getData();
            path = FileUtil.getPath(mContext, uri);
        }
        if(path != null && ExtensionUtil.openFile(path)) {
            DrawChart();
            mTextView.setText(path);
        }else {
            Toast.makeText(mContext, "file is wrong", Toast.LENGTH_SHORT).show();
            mTextView.setText(null);
            mPieChart.clear();
            mLineChart.clear();
            initDataBasedSql();
        }
     }

    private void DrawChart() {
        mPieEntry = new ArrayList<>();
        mLineEntry = new ArrayList<>();
        for(int i = 0; i < ExtensionUtil.getDateList().size(); i++) {
            mLineEntry.add(new Entry(i, Float.valueOf(ExtensionUtil.getDateCost().get(ExtensionUtil.getDateList().get(i)).toString())));
        }
        for(String key : ExtensionUtil.getUserCost().keySet()) {
            mPieEntry.add(new PieEntry(Float.valueOf(ExtensionUtil.getUserCost().get(key).toString()), key));
        }
        if(mPieEntry.size() != 0 && mLineEntry.size() != 0) {
            DrawPieChart();
            DrawLineChart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void DrawPieChart() {
        PieDataSet pieDataSet = new PieDataSet(mPieEntry, null);
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);// set the color of pie
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(22f);//set the size of value
        pieData.setValueTextColor(Color.parseColor("#0099FF"));//set the color of value
        mPieChart.setEntryLabelTextSize(18f);//set the size of label
        mPieChart.setEntryLabelColor(Color.WHITE);//set the color of label
        mPieChart.setData(pieData);
        mPieChart.setDescription(null);
        mPieChart.isDrawEntryLabelsEnabled();

        //隐藏左下角label
        Legend legend = mPieChart.getLegend();
        legend.setForm(Legend.LegendForm.NONE);
        legend.setTextColor(Color.WHITE);

        mPieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
//                Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected() {
//                Toast.makeText(mContext, "onNothingSelected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void DrawLineChart() {
        LineDataSet lineDataSet = new LineDataSet(mLineEntry, null);
        LineData lineData = new LineData(lineDataSet);
        //set false of right and left
        mLineChart.getAxisRight().setEnabled(false);
        mLineChart.getAxisLeft().setEnabled(false);
        //settings x
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setTextColor(Color.parseColor("#333333"));
        xAxis.setTextSize(11f);
        xAxis.setAxisMinimum(0f);
        xAxis.setDrawAxisLine(true);//是否绘制轴线
        xAxis.setDrawGridLines(false);//设置x轴上每个点对应的线
        xAxis.setDrawLabels(true);//绘制标签  指x轴上的对应数值
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x轴的显示位置
        xAxis.setGranularity(1f);//禁止放大后x轴标签重绘
        //透明化图例
        Legend legend = mLineChart.getLegend();
        legend.setForm(Legend.LegendForm.NONE);
        legend.setTextColor(Color.WHITE);
        lineData.setDrawValues(false);
        mLineChart.setDescription(null);
        mLineChart.setData(lineData);

        mLineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if(mToast != null) mToast.cancel();
                String date = ExtensionUtil.getDateList().get((int)e.getX());
                mToast = Toast.makeText(mContext, date + ": " + e.getY(), Toast.LENGTH_SHORT);
                mToast.show();
            }
            @Override
            public void onNothingSelected() {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
