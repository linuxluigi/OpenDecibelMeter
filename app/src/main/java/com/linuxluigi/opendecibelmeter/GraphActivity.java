package com.linuxluigi.opendecibelmeter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.linuxluigi.opendecibelmeter.db.DecibelDbHelper;
import com.linuxluigi.opendecibelmeter.db.DecibelsQueries;
import com.linuxluigi.opendecibelmeter.models.LogEntry;

import java.util.List;
import java.util.Objects;

/**
 * Simple graph activity, where all db records will be displayed
 */
public class GraphActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        DecibelDbHelper mDbHelper = new DecibelDbHelper(this);
        List<LogEntry> logList = DecibelsQueries.getAllRows(mDbHelper);

        GraphView graph = findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{});


        for (int i = 0; i < logList.size(); i++) {
            LogEntry logEntry = logList.get(i);
            series.appendData(new DataPoint(i, logEntry.getDecibel()), true, logList.size());
        }

        graph.addSeries(series);
    }

}
