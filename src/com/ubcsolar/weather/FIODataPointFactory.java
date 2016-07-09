package com.ubcsolar.weather;

import com.eclipsesource.json.JsonObject;
import com.ubcsolar.common.GeoCoord;

public class FIODataPointFactory {
	//changeable by user
		
		private double precipProbability = 0;
		private String precipType = "rain";
		private double temperature = 0;
		private double apparentTemperature = 0;
		private double dewPoint = 0;
		private double humidity = 0;
		private double windSpeed = 0;
		private double windBearing = 0;
		private double cloudCover = 0;
		private double stormBearing = 0;
		private double stormDistance = 0;
		private int time = 0;

		
		//fixed
		
		private String summary = "TEST";
		private String icon;
		private double precipIntensity = 0;
		private double visibility = 0;
		private double pressure = 0;
		private double ozone = 0;
		
		private JsonObject data;
		
		public FIODataPointFactory temperature(double temperature){
			this.temperature = temperature;
			this.apparentTemperature = temperature;
			return this;
		}
		
		/**
		 * Sets the time of the datapoint
		 * @param time: given in seconds from 1970 (I think)
		 * @return the same factory
		 */
		public FIODataPointFactory time(int time){
			this.time = time;
			return this;
		}
		
		/**
		 * 
		 * @param cloudCover: must be a value between 0 and 1 inclusive
		 * @return
		 */
		public FIODataPointFactory cloudCover(double cloudCover){
			this.cloudCover = cloudCover;
			return this;
		}
		
		public FIODataPointFactory dewPoint(double dewPoint){
			this.dewPoint = dewPoint;
			return this;
		}
		
		public FIODataPointFactory humidity(double humidity){
			this.humidity = humidity;
			return this;
		}
		
		public FIODataPointFactory stormDistance (double stormDistance ){
			this.stormDistance = stormDistance;
			return this;
		}
		
		public FIODataPointFactory stormBearing (double stormBearing ){
			this.stormBearing = stormBearing;
			return this;
		}
		
		public FIODataPointFactory windSpeed(double windSpeed){
			this.windSpeed = windSpeed;
			return this;
		}
		
		public FIODataPointFactory windBearing(double windBearing){
			this.windBearing = windBearing;
			return this;
		}
		
		/**
		 * 
		 * @param precipitationProbability: must be a value between 0 and 1 inclusive
		 * @return
		 */
		public FIODataPointFactory precipProb(double precipitationProbability){
			this.precipProbability = precipitationProbability;
			return this;
		}
		
		public FIODataPointFactory precipType(String precipType){
			this.precipType = precipType;
			return this;
		}
		
		/**
		 * creates a JsonObject with all of the data given to the dataPoint. If
		 * data is missing, it will be entered as 0
		 * @return the JsonObject with all of the data
		 */
		
		public JsonObject build(){
			chooseIcon();
			
			data = new JsonObject();
			data.add("time", time);
			data.add("summary", summary);
			data.add("icon", icon);
			data.add("precipIntensity", precipIntensity);
			data.add("precipProbability", precipProbability);
			data.add("precipType", precipType);
			data.add("temperature", temperature);
			data.add("apparentTemperature", apparentTemperature);
			data.add("dewPoint", dewPoint);
			data.add("humidity", humidity);
			data.add("windSpeed", windSpeed);
			data.add("windBearing", windBearing);
			data.add("nearestStormDistance", stormDistance);
			data.add("nearestStormBearing", stormBearing);
			data.add("visibility", visibility);
			data.add("cloudCover", cloudCover);
			data.add("pressure", pressure);
			data.add("ozone", ozone);
			
			return data;
		}
		
		public void chooseIcon(){
			if(precipProbability >= 80){
				icon = "hella rainy";
			}else if(precipProbability >= 40){
				icon = "rainy";
			}else if(precipProbability >= 10){
				icon = "slightly rainy";
			}else if(cloudCover >= 50){
				icon = "cloudy";
			}else if(cloudCover >= 20){
				icon = "partly cloudy";
			}else{
				icon = "clear";
			}
		}
}
