package mom.trd.opentheso.SelectedBeans;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.helper.GpsHelper;
import org.geonames.Toponym;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;
import org.primefaces.event.map.GeocodeEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.GeocodeResult;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

@ManagedBean(name = "gps", eager = true)
@SessionScoped
public class GpsBeans {

    private MapModel geoModel;
    private String centerGeoMap = "41.850033, -87.6500523";
    private double latitud;
    private double longitud;
    private String coordennees= null;
    
    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;

    @PostConstruct
    public void init() {
        geoModel = new DefaultMapModel();
    }

    public void onGeocode(GeocodeEvent event) {
        boolean premiere = true;
        List<GeocodeResult> results = event.getResults();
        geoModel = new DefaultMapModel();
        if (results != null && !results.isEmpty()) {
            LatLng center = results.get(0).getLatLng();
            centerGeoMap = center.getLat() + "," + center.getLng();

            for (int i = 0; i < results.size(); i++) {
                GeocodeResult result = results.get(i);
                geoModel.addOverlay(new Marker(result.getLatLng(), result.getAddress()));
                if (premiere) {
                    latitud = result.getLatLng().getLat();
                    longitud = result.getLatLng().getLng();
                    premiere = false;
                }
            }
        }
    }

    public boolean addCoordinates(String idConcept, String idTheso) {
        GpsHelper gpshelper = new GpsHelper();
        gpshelper.insertCoordonees(connect.getPoolConnexion(), idConcept, idTheso, latitud, longitud);
        
        return true;
    }

        public void test() {
        try {
            WebService.setUserName("demo"); // add your username here
            
            ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
            searchCriteria.setQ("zurich");
            ToponymSearchResult searchResult = WebService.search(searchCriteria);
            for (Toponym toponym : searchResult.getToponyms()) {
                System.out.println(toponym.getName()+" "+ toponym.getCountryName());
            }
        } catch (Exception ex) {
            Logger.getLogger(GpsHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public MapModel getGeoModel() {
        return geoModel;
    }

    public void setGeoModel(MapModel geoModel) {
        this.geoModel = geoModel;
    }

    public String getCenterGeoMap() {
        return centerGeoMap;
    }

    public void setCenterGeoMap(String centerGeoMap) {
        this.centerGeoMap = centerGeoMap;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public String getCoordennees() {
        return coordennees;
    }

    public void setCoordennees(String coordennees) {
        this.coordennees = coordennees;
    }

}
