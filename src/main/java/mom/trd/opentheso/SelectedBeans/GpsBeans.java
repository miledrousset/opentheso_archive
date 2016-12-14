package mom.trd.opentheso.SelectedBeans;

import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import mom.trd.opentheso.bdd.helper.AlignmentHelper;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.helper.GpsHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeAlignment;
import mom.trd.opentheso.core.alignment.AlignementSource;
import mom.trd.opentheso.core.alignment.AlignmentQuery;
import mom.trd.opentheso.core.alignment.GpsQuery;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.MapModel;

@ManagedBean(name = "gps", eager = true)
@SessionScoped
public class GpsBeans {

    private MapModel geoModel;
    private String centerGeoMap = "41.850033, -87.6500523";
    public double latitud;
    public double longitud;
    private String coordennees= null;
    private ArrayList<AlignementSource> alignementSources;
    private ArrayList<NodeAlignment> listAlignValues;
    private String selectedAlignement;
    private ArrayList<AlignementSource> listeAlignementSources;
    
    
    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
    @ManagedProperty(value = "#{user1}")
    private CurrentUser theUser;

    @PostConstruct
    public void init() {
        geoModel = new DefaultMapModel();
    }
/*
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
*/
    public boolean addCoordinates(String idConcept, String idTheso) {
        GpsHelper gpshelper = new GpsHelper();
        gpshelper.insertCoordonees(connect.getPoolConnexion(), idConcept, idTheso, latitud, longitud);
        
        return true;
    }
    public boolean getAligGps(String lexicalValue,String idC, String id_Theso, String lange)
    {
        boolean status= false;
        listAlignValues=new ArrayList<>();
        GpsQuery gpsQuery= new GpsQuery();
        String requete ="";
        listAlignValues=gpsQuery.queryGps(idC, id_Theso, lexicalValue, lange, requete);
        
        return status;
    }
    public void creerAlignAuto(String idC, String idTheso, String nom, String idlangue) {
        if(selectedAlignement == null) return;
        GpsQuery gpsQuery =new GpsQuery();
        
        listAlignValues = new ArrayList<>();
            
        for (AlignementSource alignementSource1 : alignementSources) {
            // on se positionne sur la source sélectionnée 
            if(selectedAlignement.equalsIgnoreCase(alignementSource1.getSource())) {
                // on trouve le type de filtre à appliquer
                
                if("REST".equalsIgnoreCase(alignementSource1.getTypeRequete()))
                {
                    if("xml".equals(alignementSource1.getAlignement_format()))
                    {
                        //ici il faut appeler le filtre de Wikipédia 
                        listAlignValues = gpsQuery.queryGps(idC, idTheso, nom.trim(), idlangue, alignementSource1.getRequete());
                    }
                }
           }
        }

    }

    
    public void setListeAlignementSources(String idTheso) {
        int role = theUser.getUser().getIdRole();
        if (role == 1 || role == 2) {
            AlignmentHelper alignmentHelper = new AlignmentHelper();
            listeAlignementSources = alignmentHelper.getAlignementSourceSAdmin(connect.getPoolConnexion());
        }
    }
    
    // test Géonames
/*        public void test() {
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
*/    
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

    public ArrayList<AlignementSource> getAlignementSources() {
        return alignementSources;
    }

    public void setAlignementSources(ArrayList<AlignementSource> alignementSources) {
        this.alignementSources = alignementSources;
    }
    public ArrayList<NodeAlignment> getListAlignValues() {
        return listAlignValues;
    }

    public void setListAlignValues(ArrayList<NodeAlignment> listAlignValues) {
        this.listAlignValues = listAlignValues;
    }

    public String getSelectedAlignement() {
        return selectedAlignement;
    }

    public void setSelectedAlignement(String selectedAlignement) {
        this.selectedAlignement = selectedAlignement;
    }

    public ArrayList<AlignementSource> getListeAlignementSources() {
        return listeAlignementSources;
    }

    public void setListeAlignementSources() {
        GpsHelper gpsHelper = new GpsHelper();
        alignementSources = gpsHelper.getAlignementSource(connect.getPoolConnexion());
    }

    public CurrentUser getTheUser() {
        return theUser;
    }

    public void setTheUser(CurrentUser theUser) {
        this.theUser = theUser;
    }

}
