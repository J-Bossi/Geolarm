package de.jbossi.geolarm;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Johannes on 19.06.2015.
 */
public class AlarmList extends ListActivity {

    private ListView mAlarmListView;
    private List<Alarm> mAlarms;
    @Inject
    AlarmRepository mRepository;

    public AlarmList() {
        super();


    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAlarms = mRepository.getmAlarms();
        setContentView(R.layout.activity_list);
        //  ArrayAdapter<Alarm> adapter = new ArrayAdapter<Alarm>(this,
        //          R.layout.listitem_alarm, mAlarms);
        setListAdapter(new AlarmAdapter(mAlarms, this));
        // mAlarmListView = findViewById(R.id.alarmListView);

    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }


}
