package de.jbossi.geolarm.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import de.jbossi.geolarm.R;
import de.jbossi.geolarm.adapter.AlarmAdapter;
import de.jbossi.geolarm.data.AlarmRepository;
import de.jbossi.geolarm.models.Alarm;


/**
 * Created by Johannes on 19.06.2015.
 */
public class AlarmList extends Activity {
    private RecyclerView mAlarmListRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    private List<Alarm> mAlarms;


    public AlarmList() {
        super();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mAlarmListRecyclerView = (RecyclerView) findViewById(R.id.alarm_list);

        mLayoutManager = new LinearLayoutManager(this);
        mAlarmListRecyclerView.setLayoutManager(mLayoutManager);

        mAlarms = AlarmRepository.getInstance(this).getmAlarms();
        mAdapter = new AlarmAdapter(mAlarms);
        mAlarmListRecyclerView.setAdapter(mAdapter);
    }


}
