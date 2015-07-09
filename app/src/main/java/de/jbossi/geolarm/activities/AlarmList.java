package de.jbossi.geolarm.activities;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.List;

import de.jbossi.geolarm.models.Alarm;
import de.jbossi.geolarm.adapter.AlarmAdapter;
import de.jbossi.geolarm.data.AlarmRepository;
import de.jbossi.geolarm.R;


/**
 * Created by Johannes on 19.06.2015.
 */
public class AlarmList extends ListActivity {

    private ListView mAlarmListView;
    private List<Alarm> mAlarms;



    public AlarmList() {
        super();


    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAlarms = AlarmRepository.getInstance(this).getmAlarms();
        setContentView(R.layout.activity_list);
        setListAdapter(new AlarmAdapter(mAlarms, this));


    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }


}