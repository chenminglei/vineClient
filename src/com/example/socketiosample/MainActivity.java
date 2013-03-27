package com.example.socketiosample;

import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

public class MainActivity extends Activity implements OnErrorListener, 
OnCompletionListener {

	private TextView textView = null;
	private SocketIO socket = null;
	private VideoView videoView = null;
    private Handler handler = new Handler();
    private static String TAG = "My Tag";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Begin Here");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		textView = (TextView)findViewById(R.id.textView1);
		videoView = (VideoView)findViewById(R.id.videoView);
	    MediaController mediaController= new MediaController(this);
	    mediaController.setAnchorView(videoView);
	    videoView.setMediaController(mediaController);
	    videoView.requestFocus();
	    
	    videoView.setOnErrorListener(this);
	    videoView.setOnCompletionListener(this);
	   

		try {
			connect();
		} catch(Exception e) {
			e.printStackTrace();
		}
    }

    private void connect() throws MalformedURLException{
		socket = new SocketIO("http://vinestream.co");
		socket.connect(iocallback);
    }

	private IOCallback iocallback = new IOCallback() {

		@Override
		public void onConnect() {
		    Log.i(TAG, "onConnect");
		}

		@Override
		public void onDisconnect() {
			Log.i(TAG, "onDisconnect");
		}

		@Override
		public void onMessage(JSONObject json, IOAcknowledge ack) {
			Log.i(TAG, "onMessage");
		}

		@Override
		public void onMessage(String data, IOAcknowledge ack) {
			Log.i(TAG, "onMessage");
		}

		@Override
		public void on(String event, IOAcknowledge ack, Object... args) {
			final JSONObject message = (JSONObject)args[0];
			/*if (message != null) {
				Log.i(TAG, message.toString());
				Uri uri;
				try {
					uri = Uri.parse(message.getString("link"));
					videoView.setVideoURI(uri);
					videoView.start();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}*/
			new Thread(new Runnable() {
				public void run() {
				handler.post(new Runnable() {
					public void run() {
						try {
							if(message.getString("link") != null) {
								Uri uri = Uri.parse(message.getString("link"));
								Log.i(TAG, uri.toString());
								//videoView.setVideoURI(uri);
								textView.setText("描述:"+ message.getString("text"));
								videoView.setVideoPath(message.getString("link"));
								videoView.start();
							}

						} catch (JSONException e) {
								e.printStackTrace();
						}
					}
					});
				}
			}).start();
		}

		@Override
		public void onError(SocketIOException socketIOException) {
			Log.e(TAG, "onError");
		    socketIOException.printStackTrace();
		}
    };

   
    public void sendEvent(View view){
		if (textView.getText().toString().length() == 0) {
		    return;
		}
		try {
			JSONObject json = new JSONObject();
			json.put("message", "fuck");
			socket.emit("chat", json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	textView.setText("");
    }

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		Log.i("通知", "播放中出现错误");  
		try {
    		JSONObject json = new JSONObject();
    		json.put("message", "fuck");
    		socket.emit("chat", json);
    	} catch (JSONException e) {
    		e.printStackTrace();
    	}
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		Log.i("通知", "播放完成"); 
    	try {
			JSONObject json = new JSONObject();
			json.put("message", "fuck");
			socket.emit("chat", json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
