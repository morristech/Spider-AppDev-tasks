package com.example.android.task2_hacker_mode;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    MediaPlayer mediaPlayer;
    Thread thread = null;
    private SensorManager sensorManager;
    private Sensor proximitySensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if (event.values[0] >= event.sensor.getMaximumRange()) {

                displayWarningMessage("You are far from the sensor");
                if (thread != null)
                    thread.interrupt();

                releaseMediaPlayer();

            } else {
                displayWarningMessage("You are near the sensor");

                thread = new Thread(new Runnable() {
                    public void run() {

                        try {
                            synchronized (this) {
                                wait(6000);
                                mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alarmtone);
                                mediaPlayer.start();
                                mediaPlayer.setLooping(true);
                            }

                        } catch (InterruptedException ex) {
                            releaseMediaPlayer();

                        }


                    }
                });
                thread.start();
            }


        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void displayWarningMessage(String message) {
        TextView warningTextView = (TextView) findViewById(R.id.warning_textview);
        warningTextView.setText(message);
    }

    public void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
