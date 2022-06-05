package com.client.mymusic.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.client.mymusic.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutMeActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_about_me);
    ButterKnife.bind(this);
  }

  @OnClick(R.id.btn_previous_page)
  void previousPage() {
    finish();
    overridePendingTransition( R.anim.no_change, R.anim.slide_out_right );
  }
}
