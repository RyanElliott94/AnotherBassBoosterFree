package com.simplistic.anotherbassbooster;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements OnSeekBarChangeListener {

	private SeekBar progressSeek;
	private SeekBar bassSeek;
	private BassBoost bb;
	private SeekBar volumeSeek;
	private TextView volumeText;
	private AudioManager audioManager = null;
	private TextView bassText;
	private TextView volText;
	private NotificationUtils mNotificationUtils;
	RelativeLayout mainLayout;
	private int currentVol;
	private AdView mAdView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mNotificationUtils = new NotificationUtils(this);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		bb = new BassBoost(100 , 0);
		bb.setEnabled(true); 
		
		progressSeek = findViewById(R.id.progress);
		
		bassSeek = findViewById(R.id.bassSeek);
		
		bassText = findViewById(R.id.bassText);
		
		volumeSeek = findViewById(R.id.volumeSeek);
		
		volumeText = findViewById(R.id.volumeText);
		
		volText = findViewById(R.id.volText);

		mainLayout = findViewById(R.id.mainLayout);
		int[] androidColors = getResources().getIntArray(R.array.colours);
		int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
		mainLayout.setBackgroundColor(randomAndroidColor);

		progressSeek.setOnSeekBarChangeListener(this);

		bassSeek.setOnSeekBarChangeListener(this);
		
//		Typeface tf0 = Typeface.createFromAsset(getAssets(), "RobotoThin.ttf");
//		volumeText.setTypeface(tf0);
		
//		Typeface tf1 = Typeface.createFromAsset(getAssets(), "RobotoThin.ttf");
//		volText.setTypeface(tf1);
//
//		Typeface tf2 = Typeface.createFromAsset(getAssets(), "RobotoThin.ttf");
//		bassText.setTypeface(tf2);
		
		initControls();
		updateBassBoost();
		getProgress();

		MobileAds.initialize(getApplicationContext(), "ca-app-pub-7041676438054720~7585093298");
//
		mAdView = findViewById(R.id.adView);

		AdRequest adRequest = new AdRequest.Builder()
				.build();
		mAdView.loadAd(adRequest);
		mAdView.bringToFront();
		}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		if (menuItem.getItemId() == R.id.rate) {
			try {
			    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.simplistic.anotherbassboosterpro")));
			} catch (android.content.ActivityNotFoundException anfe) {
			    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.simplistic.anotherbassboosterpro")));
			}
        	}
		return super.onOptionsItemSelected(menuItem);
	}
	
	 private void initControls()
	    {
	        try
	        {
	            volumeSeek = findViewById(R.id.volumeSeek);
	            audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
	            volumeSeek.setMax(audioManager
	                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
	            volumeSeek.setProgress(audioManager
	                    .getStreamVolume(AudioManager.STREAM_MUSIC));


	            volumeSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() 
	            {
	                @Override
	                public void onStopTrackingTouch(SeekBar arg0) 
	                {
	                	currentVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	                	if(currentVol >= 15){
							volumeText.setText("Volume: Max");
						}else {
							volumeText.setText("Volume: " + currentVol);
						}
	                }

	                @Override
	                public void onStartTrackingTouch(SeekBar arg0) 
	                {
	                }

	                @SuppressLint("SetTextI18n")
					@Override
	                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) 
	                {
						volumeText.setVisibility(View.VISIBLE);
	                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
	                            progress, 0);
	                    volumeText.setText("Volume: " + progress);
	                }
	            });
	        }
	        catch (Exception e) 
	        {
	            e.printStackTrace();
	        }
	    }

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		switch(seekBar.getId()){
		case R.id.progress:
			progressSeek.getProgress();
			bassSeek.setProgress(progress);
			bb.setStrength ((short)progress);
			break;
		case R.id.bassSeek:
			progressSeek.setProgress(progress);
			bb.setStrength ((short)progress);
			break;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
	
	public void Choose() {
	    androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder((this));
	    builder.setTitle("Please Choose:");
	    builder.setMessage("Keep Running: This will keep Bass Booster running in the background"
	    		+ "\n"
	    		+ "\n"
	    		+ "Exit: This will permanently close Bass Booster");
	    builder.setCancelable(true);
	    builder.setIcon(R.drawable.ic_launcher);
	    builder.setNegativeButton("Exit", new OnClickListener(){

	        @Override
			public void onClick(DialogInterface dialogInterface2, int dialogInterface) {
	        	saveProgress();
	        	releaseBassBoost();
				mNotificationUtils.getManager().cancelAll();
        		finish();
	       }
	    });
	    
	    builder.setNeutralButton("Keep Running", new OnClickListener(){

	        @Override
			public void onClick(DialogInterface dialogInterface2, int dialogInterface) {
	        	saveProgress();
				NotificationCompat.Builder nb = mNotificationUtils.
						getAndroidChannelNotification("Bass Booster", "Running");

				mNotificationUtils.getManager().notify(101, nb.build());
	        	finish();
	        }
	    });
	    builder.create().show();
	}

	public void getProgress(){
        SharedPreferences sp = getSharedPreferences("Bass", MODE_PRIVATE);
        bb.setStrength((short) sp.getInt("bass_boost", 0));
        bassSeek.setProgress(sp.getInt("bass_boost", 0));

		currentVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		if(currentVol >= 15){
			volumeText.setText("Volume: Max");
		}else {
			volumeText.setText("Volume: " + currentVol);
		}
	}
	
	public void saveProgress(){
		SharedPreferences sp = getSharedPreferences("Bass", MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt("bass_boost", bb.getRoundedStrength());
		editor.apply();
		}
	
	  public void updateBassBoost ()
	    {
	    
	    	getProgress();
	   
	    } 
	  
	  public void releaseBassBoost(){
		  if(bb != null){
			  bb.setEnabled(false);
			  bb.release();
		  }
	  }
	  @Override
	  public void onBackPressed(){
		  Choose();
	  }


}
