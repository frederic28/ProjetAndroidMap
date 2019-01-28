package kristof.ccm.ccm2018goooglemap;

import java.util.Date;

public class Localisation {
    private String id_user;
    private double latitude;
    private double longitude;
    private Date updated_at;

    public Localisation(String id_user, double latitude, double longitude) {
        this.id_user = id_user;
        this.latitude = latitude;
        this.longitude = longitude;
        this.updated_at = new Date();
    }


    public String getId_user() { return id_user; }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public Date getUpdated_at(){return updated_at;}
}
