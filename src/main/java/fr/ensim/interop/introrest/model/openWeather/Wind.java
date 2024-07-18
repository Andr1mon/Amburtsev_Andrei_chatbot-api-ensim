package fr.ensim.interop.introrest.model.openWeather;


public class Wind{
    public double speed;
    public int deg;
    public double gust;

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getGust() {
        return gust;
    }

    public void setGust(double gust) {
        this.gust = gust;
    }

    public int getDeg() {
        return deg;
    }

    public void setDeg(int deg) {
        this.deg = deg;
    }
}