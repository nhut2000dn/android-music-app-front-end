package com.client.mymusic.fragments;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.client.mymusic.R;
import com.client.mymusic.activities.LoginActivity;
import com.client.mymusic.activities.MainActivity;
import com.client.mymusic.activities.PersonalPlaylistActivity;
import com.client.mymusic.entities.CheckLike;
import com.client.mymusic.utils.MediaPlayerService;
import com.client.mymusic.utils.TokenManager;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Thread.sleep;

public class AudioPlayFragment extends Fragment {

    @BindView(R.id.name_song_fragment_audio)
    TextView nameSongFragmentAudio;

    @BindView(R.id.name_artist_fragment_audio)
    TextView nameArtistFragmentAudio;

    @BindView(R.id.image_song_fragment_audio)
    CircularImageView imageSongFragmentAudio;

    @BindView(R.id.seek_bar_fragment_audio)
    SeekBar seekBarFragmentAudio;

    @BindView(R.id.toggle_play_audio)
    ImageView togglePlayAudio;

    @BindView(R.id.txt_current_duration)
    TextView txtCurrentDuration;

    @BindView(R.id.txt_max_duration)
    TextView txtMaxDuration;

    @BindView(R.id.views_song)
    TextView viewsSong;

    @BindView(R.id.dowloads_song)
    TextView dowloadsSong;

    @BindView(R.id.hearts_song)
    TextView heartsSong;

    @BindView(R.id.add_to_playlist)
    ImageView addToPlaylist;

    @BindView(R.id.background)
    ImageView background;

    private Animation animation;

    Call<CheckLike> callCheckLike;

    Call<Boolean> callLike;

    Call<Boolean> callDowloadsSong;

    TokenManager tokenManager;

    public static CheckLike checkLike;

    public static final String Broadcast_PLAY_NEW_AUDIO = "com.example.musicappofficial.PlayNewAudio";
    public static final String Broadcast_PLAY_SKIP_TO_NEXT = "com.example.musicappofficial.skipToNext";
    public static final String Broadcast_PLAY_SKIP_TO_PREVIOUS = "com.example.musicappofficial.skipToPrevious";

    public AudioPlayFragment() {

    }

    public static AudioPlayFragment newInstance(String param1, String param2) {
        AudioPlayFragment fragment = new AudioPlayFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_audio_play, container, false);

        ButterKnife.bind(this, view);

        animation = AnimationUtils.loadAnimation(Objects.requireNonNull(getActivity()).getApplicationContext(), R.anim.disc_rotate);

        seekBarFragmentAudio.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
        seekBarFragmentAudio.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        NewAsynTask asynTask = new NewAsynTask();
        asynTask.execute();
        return view;
    }

    @OnClick(R.id.btn_previous_song)
    void previousSong() {
        if (MediaPlayerService.mediaPlayer != null) {
            Intent broadcastIntent = new Intent(Broadcast_PLAY_SKIP_TO_PREVIOUS);
            Objects.requireNonNull(getActivity()).sendBroadcast(broadcastIntent);
            NewAsynTask asynTask = new NewAsynTask();
            asynTask.execute();
        }
    }

    @OnClick(R.id.toggle_play_audio)
    void togglePlayAudio() {
        if (MediaPlayerService.mediaPlayer != null) {
            if (MediaPlayerService.mediaPlayer.isPlaying()) {
                MediaPlayerService.pauseMedia();
                togglePlayAudio.setImageResource(R.drawable.ic_play_arrow);
            } else {
                MediaPlayerService.resumeMedia();
                togglePlayAudio.setImageResource(R.drawable.ic_pause_circle_outline);
            }
        }
    }

    @OnClick(R.id.btn_next_song)
    void nextSong() {
        if (MediaPlayerService.mediaPlayer != null) {
            Intent broadcastIntent = new Intent(Broadcast_PLAY_SKIP_TO_NEXT);
            Objects.requireNonNull(getActivity()).sendBroadcast(broadcastIntent);
            NewAsynTask asynTask = new NewAsynTask();
            asynTask.execute();
        }
    }

    @OnClick(R.id.dowloads_song)
    void dowloadSong() {
        Toast.makeText(getActivity(), "Dowload", Toast.LENGTH_SHORT).show();
//        DownloadManager downloadManager = (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
//        DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://192.168.1.117/music_offical_2/storage/app/public/" + MediaPlayerService.activeAudio.getData()));
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
//        Long reference = downloadManager.enqueue(request);

        DownloadManager downloadmanager = (DownloadManager) Objects.requireNonNull(getActivity()).getSystemService(Context.DOWNLOAD_SERVICE);
//        Uri uri = Uri.parse(MediaPlayerService.activeAudio.getData());
        Uri uri = Uri.parse("http://192.168.1.117/music_offical_2/storage/app/public/" + MediaPlayerService.activeAudio.getData());
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle("My File");
        request.setDescription("Downloading");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        request.setVisibleInDownloadsUi(false);
//        request.setDestinationUri(Uri.parse("file://" + "storage/emulated/0/Download" + "/myfile.mp3"));
        downloadmanager.enqueue(request);
        callDowloadsSong = MainActivity.service.updateDowloadsSong(MediaPlayerService.activeAudio.getId());
        callDowloadsSong.enqueue(new Callback<Boolean>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body()) {
                        Toast.makeText(getContext(), "::True", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "::Fail", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(getContext(), "::Failssssss", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @OnClick(R.id.hearts_song)
    void LikeAction() {
        String accessToken = TokenManager.getToken().getAccessToken();
        if (accessToken != null) {
            callLike = MainActivity.serviceWithAuth.like(MediaPlayerService.activeAudio.getId());
            callLike.enqueue(new Callback<Boolean>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        if (response.body()) {
                            MediaPlayerService.activeAudio.setLikes(MediaPlayerService.activeAudio.getLikes() + 1);
                            heartsSong.setText((MediaPlayerService.activeAudio.getLikes()) + "");
                            heartsSong.setTextColor(Color.parseColor("#ea293b"));
                            setTextViewDrawableColor(heartsSong, Color.parseColor("#ea293b"));
                        } else {
                            MediaPlayerService.activeAudio.setLikes(MediaPlayerService.activeAudio.getLikes() - 1);
                            heartsSong.setText((MediaPlayerService.activeAudio.getLikes()) + "");
                            heartsSong.setTextColor(Color.parseColor("#FFFFFF"));
                            setTextViewDrawableColor(heartsSong, Color.parseColor("#FFFFFF"));
                        }
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                }
            });
        } else {
            startActivity(new Intent(getContext(), LoginActivity.class));
            Objects.requireNonNull(getActivity()).overridePendingTransition( R.anim.slide_in_up, R.anim.no_change );
        }
    }

    @OnClick(R.id.add_to_playlist)
    void addToPlaylist() {
        String accessToken = TokenManager.getToken().getAccessToken();
        if (accessToken != null) {
            Intent intent = new Intent(getActivity(), PersonalPlaylistActivity.class);
            intent.putExtra("action", "add_to_playlist");
            intent.putExtra("id", MediaPlayerService.activeAudio.getId());
            intent.putExtra("name", MediaPlayerService.activeAudio.getTitle());
            startActivityForResult(intent, 1);
            Objects.requireNonNull(getActivity()).overridePendingTransition( R.anim.slide_in_left, R.anim.no_change );
        } else {
            startActivity(new Intent(getContext(), LoginActivity.class));
            Objects.requireNonNull(getActivity()).overridePendingTransition( R.anim.slide_in_up, R.anim.no_change );
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class NewAsynTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            refreshFragment();
        }

    }

    @SuppressLint("SetTextI18n")
    private void refreshFragment() {

        if (MediaPlayerService.mediaPlayer != null) {

            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
            nameSongFragmentAudio.setText(MediaPlayerService.activeAudio.getTitle());
            nameArtistFragmentAudio.setText(MediaPlayerService.activeAudio.getArtist());

            viewsSong.setText(MediaPlayerService.activeAudio.getViews() + "");
            dowloadsSong.setText(MediaPlayerService.activeAudio.getDowloads() + "");
            heartsSong.setText(MediaPlayerService.activeAudio.getLikes() + "");

            checkLike();

            if (!MediaPlayerService.activeAudio.getImage().equals("")) {
//                Picasso.get().load(MediaPlayerService.activeAudio.getImage())
//                        .fit().placeholder(R.drawable.place_holder_profile).into(imageSongFragmentAudio);
                Picasso.get().load("http://192.168.1.117/music_offical/storage/app/public/" + MediaPlayerService.activeAudio.getImage())
                        .fit().placeholder(R.drawable.place_holder_profile).into(imageSongFragmentAudio);
            }

            txtMaxDuration.setText(simpleDateFormat.format(MediaPlayerService.mediaPlayer.getDuration()));
            seekBarFragmentAudio.setMax(MediaPlayerService.mediaPlayer.getDuration());
            imageSongFragmentAudio.setAnimation(animation);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (MediaPlayerService.mediaPlayer.isPlaying()) {
                        txtCurrentDuration.setText(simpleDateFormat.format(MediaPlayerService.mediaPlayer.getCurrentPosition()));
                        seekBarFragmentAudio.setProgress(MediaPlayerService.mediaPlayer.getCurrentPosition());
                    }
                    handler.postDelayed(this, 500);
                }
            }, 100);

            seekBarFragmentAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    MediaPlayerService.mediaPlayer.seekTo(seekBar.getProgress());
                }
            });

        }

    }

    private void checkLike() {
        String accessToken = TokenManager.getToken().getAccessToken();
        if (accessToken != null) {
            callCheckLike = MainActivity.serviceWithAuth.checkLike(MediaPlayerService.activeAudio.getId());
            callCheckLike.enqueue(new Callback<CheckLike>() {
                @Override
                public void onResponse(Call<CheckLike> call, Response<CheckLike> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        checkLike = response.body();
                        if (checkLike.getCheck()) {
                            heartsSong.setTextColor(Color.parseColor("#ea293b"));
                            setTextViewDrawableColor(heartsSong, Color.parseColor("#ea293b"));
                        } else {
                            heartsSong.setTextColor(Color.parseColor("#FFFFFF"));
                            setTextViewDrawableColor(heartsSong, Color.parseColor("#FFFFFF"));
                        }
                        Toast.makeText(getContext(), response.body().getCheck() + ": Da Check", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<CheckLike> call, Throwable t) {
                    Toast.makeText(getContext(), "::Failssssss", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            }
        }
    }

}
