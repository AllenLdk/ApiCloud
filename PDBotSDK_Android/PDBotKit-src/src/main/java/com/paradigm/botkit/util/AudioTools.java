package com.paradigm.botkit.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;

import java.io.File;

/**
 * Created by wuyifan on 2018/1/26.
 */

public class AudioTools {

    private Context context;
    private MediaPlayer mediaPlayer;
    private MediaRecorder mediaRecorder;
    private File playingFile;
    private File recodeFile;
    private long recordStartTime;

    public AudioTools(Context context) {

        this.context = context;
    }

    public void startRecord() {

        stopAllAudio();
        if (mediaRecorder == null) mediaRecorder = new MediaRecorder();

        try {
            recodeFile = File.createTempFile("record", ".amr", context.getCacheDir());
            recodeFile.deleteOnExit();

            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(recodeFile.getPath());
            mediaRecorder.setAudioChannels(1);
            mediaRecorder.prepare();
            mediaRecorder.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

        recordStartTime = System.currentTimeMillis();
    }

    public File finishRecord() {
        if (mediaRecorder != null) mediaRecorder.reset();
        if (getRecordDuration() < 1000) return null;
        recordStartTime = 0;
        return recodeFile;
    }

    public void cancelRecord() {
        if (mediaRecorder != null) mediaRecorder.reset();
    }

    public float getRecordVolume() {
        if (mediaRecorder == null) return 0.0f;
        float volume = mediaRecorder.getMaxAmplitude() / 32768.0f;
        if (volume < 0.0f) volume = 0.0f;
        else if (volume > 1.0f) volume = 1.0f;
        return volume;
    }

    public long getRecordDuration() {
        return recordStartTime > 0 ? System.currentTimeMillis() - recordStartTime : 0;
    }

    public void startPlay(File file) {

        stopAllAudio();
        if (mediaPlayer == null) mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(file.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.playingFile = file;
    }

    public void stopPlay() {
        if (mediaPlayer != null) mediaPlayer.reset();
    }

    public File getPlayingFile() {
        return mediaPlayer != null && mediaPlayer.isPlaying() ? playingFile : null;
    }

    private void stopAllAudio() {
        if (mediaPlayer != null) mediaPlayer.reset();
        if (mediaRecorder != null) mediaRecorder.reset();
    }

    public static long getAudioDuration(File file) {
        long duration = 0;
        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(file.getPath());
            player.prepare();
            duration = player.getDuration();
        } catch (Exception e) {
            e.printStackTrace();
        }
        player.release();
        return duration;
    }
}
