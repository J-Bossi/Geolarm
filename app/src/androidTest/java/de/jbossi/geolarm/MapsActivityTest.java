package de.jbossi.geolarm;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;


public class MapsActivityTest extends ActivityInstrumentationTestCase2 {
    private Solo solo;


    @SuppressWarnings("unchecked")
    public MapsActivityTest() throws ClassNotFoundException {
        super(MapsActivity.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation());
        getActivity();
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        super.tearDown();
    }

    public void testRun() {
        //Wait for activity: 'com.example.ExampleActivty'
        solo.waitForActivity("MapsActivity");
        solo.clickOnImageButton(0);

    }
}