package com.example.testvideo;

import java.nio.ByteBuffer;

import x1.Studio.Core.IVideoDataCallBack;
import x1.Studio.Core.OnlineService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;






public class LoginActivity extends Activity implements IVideoDataCallBack{


	private OnlineService ons;
	private String TAG="LoginActivity";
	 private ProgressDialog loginProgress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		ons = OnlineService.getInstance();
		ons.setCallBackData(this);
		ons.setCallBackData(this);
		final EditText userEdit = (EditText)findViewById(R.id.username_edit);
		final EditText pwdEdit = (EditText)findViewById(R.id.password_edit);
		final EditText servertEdit = (EditText)findViewById(R.id.server_edit);
		
		userEdit.setText(""); //�û������Լ���
		pwdEdit.setText("");//���룬�Լ���
		servertEdit.setText("www.scc21.com");
		
		
		Button b = (Button)findViewById(R.id.login_btn);
		b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showProgressDialog("���ڵ�¼");
				Log.v(TAG, "�û���¼......");
			    String userName=userEdit.getText().toString();
			    String password=pwdEdit.getText().toString();
			    String host=servertEdit.getText().toString();
			  
			    ons.login(userName, password, host);
			   
			    
			}
			
		});
		
		
	}
	
	private void getWanListView(){
		ShowCloseDialog();
		Log.v(TAG, "�����������б�ҳ��......");
		Intent intent = new Intent();
		intent.setClass(LoginActivity.this,WanDevListActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void OnCallbackFunForDataServer(String CallId, ByteBuffer Buf,
			int mFrameWidth, int mFrameHeight, int mEncode, int mTime) {
		
		
	}

	@Override
	public void OnCallbackFunForLanDate(String devid, String devType, int hkid,
			int channal, int stats, String audioType) {
	
		
	}
	
	@Override
	public void OnCallbackFunForComData(final int Type, final int Result,
			int AttachValueBufSize, String AttachValueBuf) {
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if(Type==0){
					if(Result==0){
						Log.v(TAG, "��¼�ɹ�......");
						Toast.makeText(LoginActivity.this, "��¼�ɹ�", Toast.LENGTH_LONG).show();
						ons.doRegWanService();//ע�ụ��������
					
					}else{
						Toast.makeText(LoginActivity.this, "��¼ʧ��", Toast.LENGTH_LONG).show();
						Log.v(TAG, "��¼ʧ��......");
					}
				}
				
			}
		});
		
		
		
		
	}

	@Override
	public void OnCallbackFunForRegionMonServer(int iFlag) {
		
		if(iFlag==1){
			Log.v(TAG, "ע�ụ��������ɹ�......");
			getWanListView();
		}else{
			Log.v(TAG, "ע�ụ��������ʧ��......");
		}
	}

   

	@Override
	public void OnCallbackFunForGetItem(byte[] byteArray, int result) {
	
		
	}

	@Override
	public void OnCallbackFunForUnDecodeDataServer(String CallId, byte[] Buf,
			int mFrameWidth, int mFrameHeight, int mEncode, int mTime,int mFream) {
		// TODO Auto-generated method stub
		
	} 
	
	private void showProgressDialog(String msg){
		ShowCloseDialog();
		loginProgress = ProgressDialog.show(this, "",msg);
		loginProgress.setCancelable(false);
		loginProgress.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					ShowCloseDialog();
					ons.quitSysm();
				}
				return true;
			}
		});
	}
	
	private void ShowCloseDialog() {
		if (this.loginProgress != null) {
			if (this.loginProgress.isShowing()){
				this.loginProgress.dismiss();
			    this.loginProgress = null;
			}
		}
	}

	@Override
	public void OnCallbackFunForIPRateData(String Ip) {
		
		
	}
}
