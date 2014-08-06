package com.example.samplefb;

import java.util.Arrays;

import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;




public class SuccessLogin extends Activity{
	public boolean isFetching=false;	
	Button b;
	TextView tv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		
		setContentView(R.layout.login);
		b=(Button) findViewById(R.id.button1);
		tv=(TextView) findViewById(R.id.textView1);
		b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				tv.setText("working.........");
				performFacebookLogin();	
				
				
				
				
				
				
				
				
			}
		});
		
		
		
	
		
		
	}
	
	private void performFacebookLogin()
	{
		

		
	
	    Log.d("FACEBOOK", "performFacebookLogin");
	    final Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, Arrays.asList("email"));
	    Session openActiveSession = Session.openActiveSession(this, true, new Session.StatusCallback()
	    {
	        @Override
	        public void call(Session session, SessionState state, Exception exception)
	        {
	            Log.d("FACEBOOK", "call");
	            if (session.isOpened())
	            {
	                Log.d("FACEBOOK", "if (session.isOpened() && !isFetching)");
	                isFetching=true;
	                session.requestNewReadPermissions(newPermissionsRequest);
	                Request getMe = Request.newMeRequest(session, new GraphUserCallback()
	                {
	                    @Override
	                    public void onCompleted(GraphUser user, Response response)
	                    {
	                        Log.d("FACEBOOK", "onCompleted"+user);
	                        if (user != null)
	                        {
	                        	
	                        	tv.setText("inside user check");
	                            Log.d("FACEBOOK", "user != null");
	                            org.json.JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
	                            String email = graphResponse.optString("email");
	                            String id = graphResponse.optString("id");
	                            String facebookName = user.getUsername();
	                            
	                            Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();;
	                            if (email == null || email.length() < 0)
	                            {
//	                                Logic.showAlert(
//	                                        ActivityLogin.this,
//	                                        "Facebook Login",
//	                                        "An email address is required for your account, we could not find an email associated with this Facebook account. Please associate a email with this account or login the oldskool way.");
//	                                return;
	                            }
	                        }
	                    }
	                });
	                getMe.executeAsync();
	            }
	            else
	            {
	            	tv.setText("else");
	            	
	                if (!session.isOpened())
	                    Log.d("FACEBOOK", "!session.isOpened()");
	                else
	                    Log.d("FACEBOOK", "isFetching");

	            }
	        }
	    });
	
	}
	

}
