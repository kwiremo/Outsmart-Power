package com.outsmart.outsmartpower.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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

/**
 * Created by Rene Moise Kwibuka
 * Edited by Rene Moise Kwibuka on 4/16/2017
 */

/**
 * This class is responsible of drawing a graph of statistics data.
 */
public class Graph extends AppCompatActivity {

    /**
     * Displaying data in the oncreate have some disadvantages. Data on the screen are ony updated
     * when this activity is started. It is not updated when the onPause and Onresume are
     * called in respectively.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        /**
         * Get x values and y values. These are passed from the previous activity.
         * This could have been accomplished by directly requesting data from the smartoutletmanager.
         * If anyone wants to improve this program this also would need better look.
         */
        DataWrapper dw1 = (DataWrapper) getIntent().getSerializableExtra("X_VALUES");
        ArrayList<String> X_Values = dw1.getData();
        DataWrapper dw2 = (DataWrapper) getIntent().getSerializableExtra("Y_VALUES");
        ArrayList<String> Y_Values = dw2.getData();

        GraphView graph = (GraphView) findViewById(R.id.graph1);
        DataPoint [] dp = new DataPoint[X_Values.size()];

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
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dp);

        SimpleDateFormat df = new SimpleDateFormat("h:mm a", Locale.US);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this, df));
        graph.addSeries(series);
    }

    /**
     * Data should be updated every time the activity resumes. (Or even instantly). That is why
     * rather than displaying data in the oncreate, data should be displayed in a different function
     * that updates often. Remember that onCreate is rarely called. Consult documentation
     * for more information. The improvement would be rather than passing data to display in
     * an intent, pass what outlet of which data will be displayed. And then, get that data
     * directly from the database or from the smatOutletManager whenever we resume or whenever
     * we update.
     */
    @Override
    protected void onResume() {
        super.onResume();
    }
}
