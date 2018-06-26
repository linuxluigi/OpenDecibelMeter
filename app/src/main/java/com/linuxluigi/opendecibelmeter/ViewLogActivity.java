package com.linuxluigi.opendecibelmeter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.linuxluigi.opendecibelmeter.db.DecibelDbHelper;
import com.linuxluigi.opendecibelmeter.db.DecibelsQueries;
import com.linuxluigi.opendecibelmeter.models.LogEntry;
import com.linuxluigi.opendecibelmeter.utli.LogAdapter;
import com.linuxluigi.opendecibelmeter.utli.RecyclerItemTouchHelper;

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
