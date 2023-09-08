package fr.ffessm.doris.android.tools;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.ffessm.doris.android.datamodel.associations.Fiches_ZonesGeographiques;

public class Zones_Outils {
    public static int getZoneLevel(ZoneGeographique zone) {

        ZoneGeographique parent = zone.getParentZoneGeographique();
        if( parent == null){
            return 0;
        } else {
            return getZoneLevel(parent) + 1;
        }
    }
    public static boolean isLastChild(ZoneGeographique zone) throws SQLException {

        ZoneGeographique parent = zone.getParentZoneGeographique();
        if( parent == null){
            // actually not true, need to look into the DB for relative position wrt other root zones
            return true;
        } else {
            if(parent.getContextDB() == null) {
                parent.setContextDB(zone.getContextDB());
            }
            if(parent.getContextDB() != null) {
                parent.getContextDB().zoneGeographiqueDao.refresh(parent);
            }
            List<ZoneGeographique> list =parent.getZoneGeographicChilds();
            Iterator<ZoneGeographique> it = list.iterator();
            ZoneGeographique last = null;
            while(it.hasNext()){
                last = it.next();
            }
            return last != null && zone.getId() == last.getId();
        }
    }
}
