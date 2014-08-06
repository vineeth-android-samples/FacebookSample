package com.example.samplefb;

public class FacebookUser {
	
		public String id;
		public String first_name;
		public String birthday="not available";
		public String name;
		public String link;
		public String last_name;
		public String location = "no location";
		
		@Override
		public String toString() {
		// TODO Auto-generated method stub
		return String.format("first: %s \nbirthday: %s \nlocation: %s", first_name,birthday,location);
			
		
		
		}
	
}
