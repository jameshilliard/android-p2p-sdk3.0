package com.example.testvideo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;




public class StartActivity extends Activity  {


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		Button l  = (Button)findViewById(R.id.wan_btn);
		l.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(StartActivity.this, LoginActivity.class);
			
				startActivity(intent);
				
			}
		});
		l  = (Button)findViewById(R.id.lan_btn);
		l.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(StartActivity.this,LanDevListActivity.class);
				startActivity(intent);
			

			}
		});
		
	}
	
		

	


}
