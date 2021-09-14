package kovacs.chatlicenseapp.services;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.IOException;

public class AudioServices {
    private Context context;
    private MediaPlayer tmpMediaPlayer;
    private OnPlayCallBack onPlayCallBack;

    public AudioServices(Context context) {
        this.context = context;
    }

    public void playAudioFromUrl(String url, OnPlayCallBack onPlayCallBack) {
        if (tmpMediaPlayer != null) {
            tmpMediaPlayer.stop();
        }

        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();

            tmpMediaPlayer = mediaPlayer;
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                onPlayCallBack.onFinished();
            }
        });
    }

    public interface OnPlayCallBack {
        void onFinished();
    }
}
