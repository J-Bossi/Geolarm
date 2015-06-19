package de.jbossi.geolarm;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by Johannes on 19.06.2015.
 */
public class AlarmList extends ListActivity {

    private ListView mAlarmListView;

    public AlarmList() {
        super();
    }

    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.listitem_alarm);
        // mAlarmListView = findViewById(R.id.alarmListView);

    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void setListAdapter(ListAdapter adapter) {
        super.setListAdapter(adapter);
    }


}
