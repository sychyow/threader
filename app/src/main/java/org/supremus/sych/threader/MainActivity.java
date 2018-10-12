package org.supremus.sych.threader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private final static int LEFT_LEG = 0;
    private final static int RIGHT_LEG = 1;
    private volatile int currentLeg = LEFT_LEG;
    private RunningLeg stepLeft;
    private RunningLeg stepRight;

    private TextView tvText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvText = findViewById(R.id.tv_text);
    }

    private void setLeg(int aLeg) {
        synchronized (this) {
            currentLeg = aLeg;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        stepLeft = new LeftLeg();
        stepRight = new RightLeg();
        new Thread(stepLeft).start();
        new Thread(stepRight).start();
    }

    @Override
    public void onStop() {
        super.onStop();
        stepLeft.stop();
        stepRight.stop();
    }

    private abstract class RunningLeg implements Runnable {
        boolean isRunning = true;

        void stop() {
            isRunning = false;
        }
    }

    private class TextSetter implements Runnable {

        private final String Message;

        TextSetter(String msg) {
            Message = msg;
        }

        @Override
        public void run() {
            tvText.setText(Message);
        }
    }

    private class LeftLeg extends RunningLeg {
        private final static String STEP_LEG = "Left step";

        @Override
        public void run() {
            while (isRunning) {
                if (currentLeg == LEFT_LEG) {
                    System.out.println(STEP_LEG);
                    runOnUiThread(new TextSetter(STEP_LEG));
                    setLeg(RIGHT_LEG);
                }
            }
        }
    }

    private class RightLeg extends RunningLeg {
        private final static String STEP_LEG = "Right step";

        @Override
        public void run() {
            while (isRunning) {
                if (currentLeg == RIGHT_LEG) {
                    System.out.println(STEP_LEG);
                    runOnUiThread(new TextSetter(STEP_LEG));
                    setLeg(LEFT_LEG);
                }
            }
        }
    }

}
