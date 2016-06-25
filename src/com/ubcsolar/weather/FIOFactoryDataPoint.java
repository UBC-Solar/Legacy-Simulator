package com.ubcsolar.weather;

import com.ubcsolar.common.GeoCoord;

public class FIOFactoryDataPoint {
	//changeable by user
		private double latitude = 0;
		private double longitude = 0;
		private double precipProbability = 0;
		private String precipType = "rain";
		private double temperature = 0;
		private double apparentTemperature = 0;
		private double dewPoint = 0;
		private double humidity = 0;
		private double windSpeed = 0;
		private double windBearing = 0;
		private double cloudCover = 0;
		private double stormBearing =0;
		private double stormDistance =0;

		
		//fixed
		private String timezone = "AMERICA";
		private int offset = -6;
		private String summary = "TEST";
		private String icon = "cloudy";
		private int time = 0;
		private double precipIntensity = 0;
		private double visibility = 0;
		private double pressure = 0;
		private double ozone = 0;
		
		private final int NUM_HOURS_NEEDED = 4;
		
		public FIOFactoryDataPoint location(GeoCoord location){
			this.latitude = location.getLat();
			this.longitude = location.getLon();
			return this;
		}
		
		public FIOFactoryDataPoint temperature(double temperature){
			this.temperature = temperature;
			this.apparentTemperature = temperature;
			return this;
		}
		
		public FIOFactoryDataPoint cloudCover(double cloudCover){
			this.cloudCover = cloudCover / 100;
			return this;
		}
		
		public FIOFactoryDataPoint dewPoint(double dewPoint){
			this.dewPoint = dewPoint;
			return this;
		}
		
		public FIOFactoryDataPoint humidity(double humidity){
			this.humidity = humidity;
			return this;
		}
		
		public FIOFactoryDataPoint stormDistance (double stormDistance ){
			this.stormDistance = stormDistance;
			return this;
		}
		
		public FIOFactoryDataPoint stormBearing (double stormBearing ){
			this.stormBearing = stormBearing;
			return this;
		}
		
		public FIOFactoryDataPoint windSpeed(double windSpeed){
			this.windSpeed = windSpeed;
			return this;
		}
		
		public FIOFactoryDataPoint windBearing(double windBearing){
			this.windBearing = windBearing;
			return this;
		}
		
		public FIOFactoryDataPoint precipProb(double precipitationProbability){
			this.precipProbability = precipitationProbability / 100;
			return this;
		}
		
		public FIOFactoryDataPoint precipType(String precipType){
			this.precipType = precipType;
			return this;
		}
}
