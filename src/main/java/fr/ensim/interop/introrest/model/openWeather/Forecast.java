package fr.ensim.interop.introrest.model.openWeather;

public class Forecast {
    private String cod;
    private Integer message;
    private Integer cnt;
    private java.util.List<List> list;
    private City city;

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public java.util.List<List> getList() {
        return list;
    }

    public void setList(java.util.List<List> list) {
        this.list = list;
    }

    public Integer getCnt() {
        return cnt;
    }

    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }

    public Integer getMessage() {
        return message;
    }

    public void setMessage(Integer message) {
        this.message = message;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }
}
