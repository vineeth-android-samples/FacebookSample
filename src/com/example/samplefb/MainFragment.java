package com.example.samplefb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.Facebook;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainFragment extends Fragment {

	private static final String TAG = "MainFragment";
	private UiLifecycleHelper uiHelper;
	private Button shareButton;
	TextView tv;
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private boolean pendingPublishReauthorization = false;

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();

		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}

		uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		  outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
		    uiHelper.onSaveInstanceState(outState);
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		if (savedInstanceState != null) {
		    pendingPublishReauthorization = 
		        savedInstanceState.getBoolean(PENDING_PUBLISH_KEY, false);
		}
		
		View view = inflater.inflate(R.layout.mainpg, container, false);
		tv = (TextView) view.findViewById(R.id.textView2);
		LoginButton authButton = (LoginButton) view.findViewById(R.id.login_button);
	//	authButton.setBackgroundResource(R.drawable.fblogin);
		authButton.setBackgroundColor(getResources().getColor(R.color.red));
		
		authButton.setFragment(this);
		
		//authButton.setReadPermissions(Arrays.asList("user_location",
		//		"user_birthday", "user_likes"));
		shareButton = (Button) view.findViewById(R.id.button1);
		shareButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		        publishStory();        
		    }
		});
		return view;
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			shareButton.setVisibility(View.VISIBLE);
			if (pendingPublishReauthorization && 
			        state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
			    pendingPublishReauthorization = false;
			    publishStory();
			}
			
			
			
			
			Request.newMeRequest(session, new Request.GraphUserCallback() {

				// callback after Graph API response with user object
				@Override
				public void onCompleted(GraphUser user, Response response) {
					if (user != null) {


//						Toast.makeText(getActivity(), "welcome" + user,
//								Toast.LENGTH_LONG).show();
						System.out.println("ms>>>>>>>>>>>>>" + response);

					
							String birth = null;
							GraphObject go = response.getGraphObject();
							Gson userGson = new GsonBuilder().create();
							FacebookUser aFacebookUser = userGson.fromJson(go.getInnerJSONObject().toString(),FacebookUser.class);
							System.out.println("User details is \n "+ aFacebookUser);
							tv.setText("hi"+aFacebookUser);


					}
				}

			}).executeAsync();

			Log.i(TAG, "Logged in...");
		} else if (state.isClosed()) {
			Log.i(TAG, "Logged out...");
			shareButton.setVisibility(View.INVISIBLE);
		}
	}

	
	private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
	    for (String string : subset) {
	        if (!superset.contains(string)) {
	            return false;
	        }
	    }
	    return true;
	}
	
	void publishStory() {
	    Session session = Session.getActiveSession();

	    
	    
	    
	    
	    if (session != null){

	    	
	    	System.out.println("user>>>>>>>>>>"+session);
	    	
	    	
	        // Check for publish permissions    
	        List<String> permissions = session.getPermissions();
	        if (!isSubsetOf(PERMISSIONS, permissions)) {
	            pendingPublishReauthorization = true;
	            Session.NewPermissionsRequest newPermissionsRequest = new Session
	                    .NewPermissionsRequest(this, PERMISSIONS);
	        session.requestNewPublishPermissions(newPermissionsRequest);
	            return;
	        }

	        Bundle postParams = new Bundle();
	        postParams.putString("name", "Facebook SDK for Android");
	        postParams.putString("caption", "Build great social apps and get more installs.");
	        postParams.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
	        postParams.putString("link", "https://developers.facebook.com/android");
	        postParams.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

	        Request.Callback callback2= new Request.Callback() {
	            public void onCompleted(Response response) {
	                JSONObject graphResponse = response
	                                           .getGraphObject()
	                                           .getInnerJSONObject();
	                String postId = null;
	                try {
	                    postId = graphResponse.getString("id");
	                } catch (JSONException e) {
	                    Log.i(TAG,
	                        "JSON error "+ e.getMessage());
	                }
	                FacebookRequestError error = response.getError();
	                if (error != null) {
	                    Toast.makeText(getActivity()
	                         .getApplicationContext(),
	                         error.getErrorMessage(),
	                         Toast.LENGTH_SHORT).show();
	                    } else {
	                        Toast.makeText(getActivity()
	                             .getApplicationContext(), 
	                             postId,
	                             Toast.LENGTH_LONG).show();
	                }
	            }
	        };

	        Request request = new Request(session, "me/feed", postParams, 
	                              HttpMethod.POST, callback2);
	        
	        Toast.makeText(getActivity(), "done",Toast.LENGTH_LONG).show();

	        RequestAsyncTask task = new RequestAsyncTask(request);
	        task.execute();
	    }}

}
