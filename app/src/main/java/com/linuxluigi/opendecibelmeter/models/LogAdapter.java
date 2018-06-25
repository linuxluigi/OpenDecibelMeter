package com.linuxluigi.opendecibelmeter.models;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.linuxluigi.opendecibelmeter.R;
import com.linuxluigi.opendecibelmeter.db.DecibelContract;
import com.linuxluigi.opendecibelmeter.db.DecibelDbHelper;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.MyViewHolder> {

    private List<LogEntry> logList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView decibel, position, timestamp;
        public RelativeLayout viewBackground, viewForeground;

        public MyViewHolder(View view) {
            super(view);
            decibel = (TextView) view.findViewById(R.id.decibel);
            position = (TextView) view.findViewById(R.id.position);
            timestamp = (TextView) view.findViewById(R.id.timestamp);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
        }
    }

    public LogAdapter(List<LogEntry> logList) {
        this.logList = logList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.log_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        LogEntry logEntry = logList.get(position);
        holder.decibel.setText(String.format(Locale.GERMANY, "%d: %f", logEntry.getId(), logEntry.getDecibel()));
        holder.position.setText(logEntry.getPosition());

        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN);
        String timestamp = formatter.format(logEntry.getTimestamp());

        holder.timestamp.setText(timestamp);
    }

    @Override
    public int getItemCount() {
        return logList.size();
    }

    public void removeItem(int position, DecibelDbHelper mDbHelper) {
        Log.d("sql", logList.get(position).toString());

        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // delete entry from sql
        String id = Integer.toString(logList.get(position).getId());
        db.execSQL("DELETE FROM " + DecibelContract.LogEntry.TABLE_NAME + " WHERE " + DecibelContract.LogEntry._ID + "='" + id + "'");
        db.close();

        logList.remove(position);

        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

}
