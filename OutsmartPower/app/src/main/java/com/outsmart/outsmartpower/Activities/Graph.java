package com.outsmart.outsmartpower.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

/**
 * To have this imports working, this line should be included in the build.gradle under your
 * app directory: compile 'com.jjoe64:graphview:4.2.1'
 * For Grahview to work a Graphview library (.jar) has to be included in the libs folder under app.
 */
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.outsmart.outsmartpower.R;
import com.outsmart.outsmartpower.helperclasses.DataWrapper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Graph extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        DataWrapper dw1 = (DataWrapper) getIntent().getSerializableExtra("X_VALUES");
        ArrayList<String> X_Values = dw1.getData();
        DataWrapper dw2 = (DataWrapper) getIntent().getSerializableExtra("Y_VALUES");
        ArrayList<String> Y_Values = dw2.getData();


        GraphView graph = (GraphView) findViewById(R.id.graph1);
        DataPoint [] dp = new DataPoint[X_Values.size()];
        //DateFormat df = new SimpleDateFormat("hh:mm:ss");
        //SimpleDateFormat df = new SimpleDateFormat("h:mm a", Locale.US);
        DateFormat df2 = new SimpleDateFormat("hh:mm");
        for(int i = 0; i < X_Values.size(); i++)
        {
            try {
                dp[i] = new DataPoint(df2.parse(X_Values.get(i)), Double.parseDouble(Y_Values.get(i)));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dp);

        SimpleDateFormat df = new SimpleDateFormat("h:mm a", Locale.US);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this, df));
        graph.addSeries(series);
    }
}
