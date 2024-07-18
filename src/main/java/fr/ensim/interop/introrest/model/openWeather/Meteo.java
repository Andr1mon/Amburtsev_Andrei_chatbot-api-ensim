package fr.ensim.interop.introrest.model.openWeather;

public class Meteo {
    private String meteo;
    private String details;
    private String temperature;

    public Meteo() {}

    public String getMeteo() {
        return meteo;
    }

    public void setMeteo(String meteo) {
        this.meteo = meteo;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
}
