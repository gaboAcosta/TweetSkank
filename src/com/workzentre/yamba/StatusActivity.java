package com.workzentre.yamba;


import java.net.URI;

import winterwell.jtwitter.OAuthSignpostClient;
import winterwell.jtwitter.Twitter;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StatusActivity extends Activity implements OnClickListener {
	
	private static final String TAG = "StatusActivity";
	private static final String MY_TWITTER_KEY = "NoDVsbD92wxLFjQn4mKiQ";
	private static final String MY_TWITTER_SECRET = "Z1al4MUALHqcbPsBTp4HZy17EdsaRpPUTeju67g";
	private static final String MY_TWITTER_CALLBACK = "x-twit-skank-oauth-twitter://oauth_callback";
	
	String[] tokens;
	String accessTokenSecret;
	
	EditText editText;
	Button updateButton;
	Twitter twitter;
	AndroidTwitterLogin atl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status);
		
		//FindViews
		editText = (EditText) findViewById(R.id.editText);
		updateButton = (Button) findViewById(R.id.buttonUpdate);
		
		updateButton.setOnClickListener(this);
		
		conectTwitter();
		
	}
	OAuthSignpostClient client;
	
	public boolean sent = false; 
	
	public void conectTwitter()
	{
		client = new OAuthSignpostClient(MY_TWITTER_KEY, MY_TWITTER_SECRET, MY_TWITTER_CALLBACK);		

		Log.i("jtwitter","TwitterAuth run!");		
		final WebView webview = new WebView(this);
		webview.setBackgroundColor(Color.BLACK);
		webview.setVisibility(View.VISIBLE);
		final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		dialog.setContentView(webview);
		dialog.show();
		
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setWebViewClient(new WebViewClient() {
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				Log.d("jtwitter","url: "+url);				
				if ( ! url.contains(MY_TWITTER_CALLBACK)) return;
				Uri uri = Uri.parse(url);
    			String verifier= uri.getQueryParameter("oauth_verifier");
    			if (verifier==null) {
    				// denied!
    				Log.i("jtwitter","Auth-fail: "+url);  			
    				dialog.dismiss();
    				onFail(new Exception(url));
    				return;
    			}
    			if(! sent) {
    				client.setAuthorizationCode(verifier);
        			tokens = client.getAccessToken();
        			Twitter jtwitter = new Twitter(null, client);
        			Log.i("jtwitter","Authorised :)");
        			dialog.dismiss();
        			onSuccess(tokens,jtwitter);
        			sent = true;
    			}    			
			}
			
			@Override public void onPageFinished(WebView view, String url) {
				Log.i("jtwitter","url finished: "+url);
			}
		});
		// Workaround for http://code.google.com/p/android/issues/detail?id=7189
		webview.requestFocus(View.FOCUS_DOWN);
		webview.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				if (e.getAction()==MotionEvent.ACTION_DOWN
					|| e.getAction()== MotionEvent.ACTION_UP) {
					if (!v.hasFocus()) {
						v.requestFocus();
					}
		        }
		        return false;
			}
		});
		
		// getting the url to load involves a web call -- let the UI update first
		Toast.makeText(this, "redirecting", Toast.LENGTH_SHORT).show();
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				try {			
					URI authUrl =  client.authorizeUrl();
					webview.loadUrl(authUrl.toString());
				} catch (Exception e) {
					onFail(e);
				}				
			}			
		},10);
	}
	
	public void onFail(Exception e)
	{
		Log.d("fallo",e.getMessage());
	}
	
	public void onSuccess(String[] tokens, Twitter jtwitter)
	{
		this.tokens = tokens;
		this.twitter = jtwitter;
	}
	
	public void storeTokens(String[] tokens, Twitter jtwitter){
		this.tokens = tokens;
		this.twitter = jtwitter;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.status, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		
		twitter.setStatus(editText.getText().toString());
		Log.d(TAG, "onClicked");
	}

}
