package com.harold.videoplayer;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class VideoPlayer extends SurfaceView implements SurfaceHolder.Callback{

    private static final String LOG_TAG = "SurfaceView";

    private SurfaceHolder mSurfaceHolder;
    private VideoThread videoThread;

    public VideoPlayer(Context context){
        super(context);
        Log.d(LOG_TAG, "video surfaceview constructor1");
        init();
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(LOG_TAG, "video surfaceview constructor2");
        init();
    }

    public VideoPlayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.d(LOG_TAG, "video surfaceview constructor3");
        init();
    }

    private void init(){
        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(this);
        videoThread = new VideoThread(mSurfaceHolder);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){
        Log.d(LOG_TAG, "surfaceChanged");
    }

    public  void surfaceCreated(SurfaceHolder holder){
        Log.d(LOG_TAG, "surfaceCreated");
        videoThread.start();
    }

    public void surfaceDestroyed(SurfaceHolder holder){
        Log.d(LOG_TAG, "surfaceDestroyed");
    }

    private class VideoThread extends Thread {
        private SurfaceHolder mSurfaceHolder;
        public boolean mIsRunning;

        private MediaExtractor mVideoExtractor;
        private MediaFormat mMediaFormat;
        private MediaCodec mDecoder;

        public VideoThread(SurfaceHolder surfaceHolder){
            this.mSurfaceHolder = surfaceHolder;
            mIsRunning = true;
        }

        public void run(){
            Log.d(LOG_TAG, "thread is running");

            mVideoExtractor = new MediaExtractor();
            try{
                String file = getMp4FilePath();
                Log.d(LOG_TAG, "mp4 file path:"+ file);
                mVideoExtractor.setDataSource(file);

            }catch(IOException e){
                Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }

            int videoIndex = getMediaTrackIndex("video/");
            Log.d(LOG_TAG, "video track index is "+ videoIndex);
            if(videoIndex < 0){
                return ;
            }

            mMediaFormat = mVideoExtractor.getTrackFormat(videoIndex);
            int width = mMediaFormat.getInteger(MediaFormat.KEY_WIDTH);
            int height = mMediaFormat.getInteger(MediaFormat.KEY_HEIGHT);
            Log.d(LOG_TAG, mMediaFormat.toString());
            mVideoExtractor.selectTrack(videoIndex);

            try{
                mDecoder = MediaCodec.createDecoderByType(mMediaFormat.getString(MediaFormat.KEY_MIME));
                mDecoder.configure(mMediaFormat, mSurfaceHolder.getSurface(), null, 0);
            }catch (Exception e){
                e.printStackTrace();
            }

            mDecoder.start();

            MediaCodec.BufferInfo outputBufferInfo = new MediaCodec.BufferInfo();

            while(mIsRunning){
                if(putExtractorDataToCodecInput()){
                    Log.d(LOG_TAG, "end of the video file!");
                    return;
                }
                Log.d(LOG_TAG, "video is playing!");

                int outputBufferIndex = mDecoder.dequeueOutputBuffer(outputBufferInfo, 10000);
                switch (outputBufferIndex) {
                    case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                        break;
                    case MediaCodec.INFO_TRY_AGAIN_LATER:
                        break;
                    case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                        break;
                    default:
                        mDecoder.releaseOutputBuffer(outputBufferIndex, true);
                }
            }
        }

        protected String getMp4FilePath(){
            //File file = new File(Environment.getExternalStorageDirectory(), "ScreenLive");
            //return file.getAbsolutePath()+"/1.mp4";

            File file = Environment.getExternalStorageDirectory();
            return file.getAbsolutePath()+"/Screen.mp4";
            //return new String("/Storage/sdcard0/Screen.mp4");
        }
        private boolean putExtractorDataToCodecInput(){
            boolean isVideoEOS = false;

            int inputBufferIndex = mDecoder.dequeueInputBuffer(10000);
            if(inputBufferIndex > 0){
                ByteBuffer inputBuffes = mDecoder.getInputBuffer(inputBufferIndex);
                int nSampleSize = mVideoExtractor.readSampleData(inputBuffes, 0);
                if(nSampleSize < 0){
                    mDecoder.queueInputBuffer(inputBufferIndex, 0,0,0,MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    isVideoEOS = true;
                }else{
                    mDecoder.queueInputBuffer(inputBufferIndex,0,nSampleSize,mVideoExtractor.getSampleTime(),0);
                    mVideoExtractor.advance();
                }
            }
            return isVideoEOS;
        }

        private int getMediaTrackIndex(String strMediaType){
            int index = 1;
            int trackCount = mVideoExtractor.getTrackCount();
            for(int i = 0; i < trackCount; i++){
                MediaFormat mediaFormat = mVideoExtractor.getTrackFormat(i);
                String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
                Log.d(LOG_TAG, i+"\t"+mime);
                if(mime.startsWith(strMediaType)){
                    index = i;
                    break;
                }
            }

            return index;
        }


    }
}

