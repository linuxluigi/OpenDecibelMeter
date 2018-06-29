package com.linuxluigi.opendecibelmeter;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;

import com.linuxluigi.opendecibelmeter.db.DecibelDbHelper;
import com.linuxluigi.opendecibelmeter.db.DecibelsQueries;
import com.linuxluigi.opendecibelmeter.models.LogEntry;
import com.linuxluigi.opendecibelmeter.utli.LogAdapter;
import com.linuxluigi.opendecibelmeter.utli.RecyclerItemTouchHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewLogActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    /**
     * Database helper that will provide us access to the database
     */
    private DecibelDbHelper mDbHelper;

    private LogAdapter mAdapter;

    private List<LogEntry> logList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_log);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new DecibelDbHelper(this);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        mAdapter = new LogAdapter(logList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        // set the adapter
        recyclerView.setAdapter(mAdapter);

        prepaireLogData();

        // export logList to a csv file in the downloads folder
        final Button exportBtn = findViewById(R.id.exportData);
        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/decibel.csv");

                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        throw new RuntimeException("Unable to create File " + e);
                    }
                }
                try {
                    FileWriter writer = new FileWriter(file);
                    for (int i = 0; i < logList.size(); i++) {
                        LogEntry logEntry = logList.get(i);

                        // id
                        writer.write(logEntry.getId());
                        writer.write(',');

                        // decibel
                        writer.write(logEntry.getDecibel().toString());
                        writer.write(',');

                        // timestamp
                        writer.write(logEntry.getTimestamp().toString());
                        writer.write('\n');

                        // id
                        writer.write(logEntry.getId());
                        writer.write(',');
                    }
                    writer.flush();
                    writer.close();

                    Snackbar.make(view, getResources().getString(R.string.log_export_success), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } catch (IOException e) {
                    Snackbar.make(view, getResources().getString(R.string.log_export_error), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    throw new RuntimeException("Unable to write to File " + e);
                }
            }
        });
    }

    private void prepaireLogData() {
        List<LogEntry> logListTmp = DecibelsQueries.getAllRows(mDbHelper);

        logList.addAll(logListTmp);

        mAdapter.notifyDataSetChanged();
    }

    /**
     * callback when recycler view is swiped
     * item will be removed on swiped
     * undo option will be provided in snackbar to restore the item
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof LogAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar

            // remove the item from recycler view
            mAdapter.removeItem(viewHolder.getAdapterPosition(), mDbHelper);
        }
    }

}
