package com.sori.touchsori.utill;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

/**
 * Created by Dongnam on 2017. 5. 24..
 */

public class MediaUtil {
    public interface MediaInfoListener{
        public void mediaCompleted();
    }

    public static final String TAG = MediaUtil.class.getSimpleName();
    private MediaPlayer mediaPlayer;
    private static Context mContext;

    private static class Singleton {
        private static final MediaUtil instance = new MediaUtil();
    }

    public static MediaUtil getInstance(Context context) {
        mContext = context;
        return Singleton.instance;
    }

    /**
     * 사운드 시작
     *
     * @param fileName
     * @param looping
     */
    public void soundStartFromFilePath(String fileName, boolean looping) {
        LogUtil.i(TAG, "soundPlay() -> Start !!!");

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        } else {
            mediaPlayer.reset();
        }

        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = new MediaPlayer();
            }

            AssetFileDescriptor descriptor = mContext.getAssets().openFd(fileName);
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            mediaPlayer.prepare();
            mediaPlayer.setVolume(1f, 1f);
            mediaPlayer.setLooping(looping);
            mediaPlayer.start();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
        }
    }

    public void soundStartFromFileResource(int resource, boolean looping, final MediaInfoListener listener) {
        LogUtil.i(TAG, "soundPlay() -> Start !!!");

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        } else {
            mediaPlayer.reset();
            if(null == listener) {
                mediaPlayer.setOnCompletionListener(null);
            }
        }

        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = new MediaPlayer();
            }

            AssetFileDescriptor descriptor = mContext.getResources().openRawResourceFd(resource);
            if (descriptor == null) return;
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            mediaPlayer.prepare();
            mediaPlayer.setLooping(looping);
            mediaPlayer.start();
            if(null != listener) {
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mediaPlayer.release();
                        mediaPlayer = null;
                        listener.mediaCompleted();
                    }
                });
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
        }
    }

    /**
     * 사운드 중지
     */
    public void soundStop() {
        LogUtil.d(TAG, "soundStop() -> Start !!!");
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                LogUtil.e(TAG, "soundStop() -> " + e.getMessage());
            }
        }
    }
}
