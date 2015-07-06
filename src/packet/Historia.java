package packet;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Twitter: @d0tplist
 * Created by Alejandro Covarrubias on 7/5/15.
 */
public class Historia {
    String url;
    String nombre;
    LocalTime date;

    public Historia(String url, String nombre, LocalTime date) {
        this.url = url;
        this.nombre = nombre;
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalTime getDate() {
        return date;
    }

    public void setDate(LocalTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return nombre +" Time: "+date.getHour()+":"+date.getMinute()+":"+date.getSecond();
    }
}
