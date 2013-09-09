package com.workzentre.yamba;

import java.net.URI;

import winterwell.jtwitter.OAuthSignpostClient;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * A View for easily getting Twitter authorisation or doing login-by-Twitter.
 * 
 * <h3>Example</h3>
 * <code><pre>
 * AndroidTwitterLogin atl = new AndroidTwitterLogin(myApp, 
				MY_TWITTER_KEY,MY_TWITTER_SECRET,MY_TWITTER_CALLBACK) {					

	protected void onSuccess(Twitter jtwitter, String[] tokens) {
		jtwitter.setStatus("I can now post to Twitter!");
		// Recommended: store tokens in your app for future use
		// with the constructor OAuthSignpostClient(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret)
	}
 * };
 * atl.run();
 *	</pre></code>
 * @author Daniel Winterstein, John Turner
 */
public abstract class AndroidTwitterLogin {
		
	private String callbackUrl;

	private Activity context;

	/**
	 * The message that is shown to the user before
	 * directing them off to Twitter. Default is 
	 * "Please authorize with Twitter"
	 */
	public void setAuthoriseMessage(String authoriseMessage) {
		this.authoriseMessage = authoriseMessage;
	}
	
	/**
	 * @param myActivity This will have its conten view set, then reset.
	 * @param oauthAppKey 
	 * @param oauthAppSecret 
	 * @param calbackUrl Not important
	 */
	 public AndroidTwitterLogin(Activity myActivity, 
			 String oauthAppKey, String oauthAppSecret, String calbackUrl)
	 {
		this.context = myActivity;
		consumerKey =  oauthAppKey;
		consumerSecret = oauthAppSecret;
		this.callbackUrl = calbackUrl;					
		client = new OAuthSignpostClient(consumerKey, consumerSecret, callbackUrl);		
	}
	 
	OAuthSignpostClient client;

	private String authoriseMessage = "Please authorize with Twitter";

	private String consumerSecret;

	private String consumerKey;
			
	public final void run() {
	}

	protected abstract void onSuccess(String[] tokens);

	protected void onFail(Exception e) {
		Toast.makeText(context, "Twitter authorisation failed?!", Toast.LENGTH_LONG).show();
		Log.w("jtwitter", e.toString());
	}

}
