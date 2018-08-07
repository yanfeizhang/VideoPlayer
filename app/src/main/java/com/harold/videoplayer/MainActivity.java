package com.harold.videoplayer;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import com.harold.videoplayer.VideoPlayer;

public class MainActivity extends AppCompatActivity {

    //private static final String LOG_TAG = "MainActivity" ;

    //private VideoPlayer videoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //videoPlayer = findViewById(R.id.surfaceview1);
    }



    private String shellExec(String cmd){

        /*Process p = Runtime.getRuntime().exec("ls");
        String data = null;
        BufferedReader ie = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String error = null;
        while ((error = ie.readLine()) != null
                && !error.equals("null")) {
            data += error + "\n";
        }
        String line = null;
        while ((line = in.readLine()) != null
                && !line.equals("null")) {
            data += line + "\n";
        }

        Log.v("ls", data);*/
        return "";
    }
}
