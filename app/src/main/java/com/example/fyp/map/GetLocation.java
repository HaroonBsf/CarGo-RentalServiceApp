package com.example.fyp.map;
public class GetLocation{
    public double accuracy;
    public double altitude;
    public int bearing;
    public int bearingAccuracyDegrees;
    public boolean complete;
    public long elapsedRealtimeNanos;
    public int elapsedRealtimeUncertaintyNanos;
    public boolean fromMockProvider;
    public double latitude;
    public double longitude;
    public boolean mock;
    public String provider;
    public double speed;
    public double speedAccuracyMetersPerSecond;
    public long time;
    public double verticalAccuracyMeters;

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public int getBearing() {
        return bearing;
    }

    public void setBearing(int bearing) {
        this.bearing = bearing;
    }

    public int getBearingAccuracyDegrees() {
        return bearingAccuracyDegrees;
    }

    public void setBearingAccuracyDegrees(int bearingAccuracyDegrees) {
        this.bearingAccuracyDegrees = bearingAccuracyDegrees;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public long getElapsedRealtimeNanos() {
        return elapsedRealtimeNanos;
    }

    public void setElapsedRealtimeNanos(long elapsedRealtimeNanos) {
        this.elapsedRealtimeNanos = elapsedRealtimeNanos;
    }

    public int getElapsedRealtimeUncertaintyNanos() {
        return elapsedRealtimeUncertaintyNanos;
    }

    public void setElapsedRealtimeUncertaintyNanos(int elapsedRealtimeUncertaintyNanos) {
        this.elapsedRealtimeUncertaintyNanos = elapsedRealtimeUncertaintyNanos;
    }

    public boolean isFromMockProvider() {
        return fromMockProvider;
    }

    public void setFromMockProvider(boolean fromMockProvider) {
        this.fromMockProvider = fromMockProvider;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isMock() {
        return mock;
    }

    public void setMock(boolean mock) {
        this.mock = mock;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getSpeedAccuracyMetersPerSecond() {
        return speedAccuracyMetersPerSecond;
    }

    public void setSpeedAccuracyMetersPerSecond(double speedAccuracyMetersPerSecond) {
        this.speedAccuracyMetersPerSecond = speedAccuracyMetersPerSecond;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getVerticalAccuracyMeters() {
        return verticalAccuracyMeters;
    }

    public void setVerticalAccuracyMeters(double verticalAccuracyMeters) {
        this.verticalAccuracyMeters = verticalAccuracyMeters;
    }
}
