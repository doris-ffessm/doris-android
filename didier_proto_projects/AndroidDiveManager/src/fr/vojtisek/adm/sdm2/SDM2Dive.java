/*
 * Project: JDiveLog: A Dive Logbook written in Java
 * File: SDM2Dive.java
 * 
 * @author Pascal Pellmont <jdivelog@pellmont.dyndns.org>
 * 
 * This file is part of JDiveLog.
 * JDiveLog is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * JDiveLog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with JDiveLog; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package fr.vojtisek.adm.sdm2;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
import net.sf.jdivelog.model.DiveSite;
import net.sf.jdivelog.model.Equipment;
import net.sf.jdivelog.model.JDive;
import net.sf.jdivelog.model.Masterdata;
import net.sf.jdivelog.model.Tank;
import net.sf.jdivelog.model.udcf.Dive;
import net.sf.jdivelog.model.udcf.Gas;
import net.sf.jdivelog.util.UnitConverter;
*/

public class SDM2Dive implements Comparable<SDM2Dive> {

    private static final Logger LOGGER = Logger.getLogger(SDM2Dive.class
        .getName());

    private static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat(
        "dd.MM.yyyy HH:mm:ss");

    private String date;

    private String time;

    private String depth;

    private Double duration;

    private String avgDepth;

    private String notes;

    private String location;

    private String site;

    private String temperature;

    private String partner;

    private String divemaster;

    private String tankSize;

    private String tankUnits;

    private String o2pct;

    private String o2pct2;

    private String o2pct3;

    private String pressureStart;

    private String pressureEnd;

    private String diveNumber;

    private String visibility;

    private ArrayList<SDM2Sample> samples;

    public SDM2Dive() {
        samples = new ArrayList<SDM2Sample>();
    }

    public void addSample(String time, String depth) {
        SDM2Sample sample = new SDM2Sample(time, depth);
        double t = Double.parseDouble(time);
        if (duration == null || t > duration) {
            duration = t;
        }
        samples.add(sample);
    }

    public Date getDateObject() throws ParseException {
        return DATEFORMAT.parse(date + " " + time);
    }

    public int compareTo(SDM2Dive o) {
        try {
            return getDateObject().compareTo(o.getDateObject());
        }
        catch (ParseException e) {
            LOGGER.log(Level.WARNING, "error parsing date when comparing", e);
            return -1;
        }
    }

    /*
    public JDive asJDive(Masterdata masterdata) {
        JDive d = new JDive();
        d.setUnits(UnitConverter.getSystemString(UnitConverter.SYSTEM_SI));
        try {
            d.setDate(getDateObject());
        }
        catch (ParseException e) {
            LOGGER.log(Level.WARNING,
                "error parsing date when converting to JDive", e);
        }
        DecimalFormat f =
            new DecimalFormat("###########,######", new DecimalFormatSymbols() {
                private static final long serialVersionUID =
                    -8357015212143741038L;

                public char getDecimalSeparator() {
                    return ',';
                }
            });
        try {
            d.setDepth(f.parse(depth).doubleValue());
        }
        catch (ParseException pex) {
            throw new NumberFormatException("pattern: " + f.toPattern()
                + ", value: " + depth);
        }
        try {
            d.setAverageDepth(f.parse(avgDepth).doubleValue());
        }
        catch (ParseException pex) {
            throw new NumberFormatException("pattern: " + f.toPattern()
                + ", value: " + avgDepth);
        }
        d.setDuration(duration);
        StringBuffer b = new StringBuffer();
        if (partner != null && !"".equals(partner)) {
            b.append(partner);
        }
        if (partner != null && !"".equals(partner) && divemaster != null
            && !"".equals(divemaster)) {
            b.append(", ");
        }
        if (divemaster != null && !"".equals(divemaster)) {
            b.append(divemaster);
        }
        d.setBuddy(b.toString());
        d.setComment(notes);
        try {
            d.setTemperature(f.parse(temperature).doubleValue() + 273.15);
        }
        catch (ParseException pex) {
            throw new NumberFormatException("pattern: " + f.toPattern()
                + ", value: " + temperature);
        }
        Tank tank = new Tank();
        if (o2pct != null && !"".equals(o2pct) && !"0".equals(o2pct)) {
            tank.setGas(new Gas());
            try {
                tank.getGas().setTankvolume(
                    f.parse(tankSize).doubleValue() / 100000000);
            }
            catch (ParseException pex) {
                throw new NumberFormatException("pattern: " + f.toPattern()
                    + ", value: " + tankSize);
            }
            double o2 = Double.parseDouble(o2pct);
            o2 = o2 / 100;
            double n2 = 1 - o2;
            tank.getGas().setName("EAN" + o2pct);
            tank.getGas().setHelium(0.0);
            tank.getGas().setOxygen(o2);
            tank.getGas().setNitrogen(n2);
            if (pressureStart != null && !"".equals(pressureStart)) {
                tank.getGas().setPstart(
                    Double.parseDouble(pressureStart) * 100.0);
            }
            if (pressureEnd != null && !"".equals(pressureEnd)) {
                tank.getGas().setPend(Double.parseDouble(pressureEnd) * 100.0);
            }
        }
        else {
            tank.setGas(new Gas());
            try {
                tank.getGas().setTankvolume(
                    f.parse(tankSize).doubleValue() / 100000000);
            }
            catch (ParseException pex) {
                throw new NumberFormatException("pattern: " + f.toPattern()
                    + ", value: " + tankSize);
            }
            tank.getGas().setName("EAN21");
            tank.getGas().setHelium(0.0);
            tank.getGas().setOxygen(0.21);
            tank.getGas().setNitrogen(0.79);
            if (pressureStart != null && !"".equals(pressureStart)) {
                tank.getGas().setPstart(
                    Double.parseDouble(pressureStart) * 100.0);
            }
            if (pressureEnd != null && !"".equals(pressureEnd)) {
                tank.getGas().setPend(Double.parseDouble(pressureEnd) * 100.0);
            }
        }
        d.setEquipment(new Equipment());
        d.getEquipment().addTank(tank);
        d.setDiveNumber(diveNumber);
        // TODO add translation table for visibility (number -> message)
        d.setVisibility(visibility);
        d.setDiveSiteId(getDiveSitePrivateId(masterdata, site, location));
        if (samples.size() > 0) {
            Dive dive = new Dive();
            dive.setDate(d.getDate());
            dive.setTimeDepthMode();
            ArrayList<Gas> gases = new ArrayList<Gas>();
            gases.add(tank.getGas().deepClone());
            dive.setGases(gases);
            dive.setTemperature(temperature);
            Iterator<SDM2Sample> it = samples.iterator();
            dive.addSwitch(tank.getGas().getName());
            dive.addTime("0");
            dive.addDepth("0");
            String lastTime = "0";
            while (it.hasNext()) {
                SDM2Sample s = it.next();
                lastTime = s.getTime();
                dive.addTime(s.getTime());
                try {
                    dive.addDepth(f.parse(s.getDepth()).toString());
                }
                catch (ParseException pex) {
                    throw new NumberFormatException("pattern: " + f.toPattern()
                        + ", value: " + s.getDepth());
                }
            }
            dive.addTime(String.valueOf(Integer.parseInt(lastTime) + 1));
            dive.addDepth("0.0");
            d.setDive(dive);
        }
        return d;
    }
*/
    public String getAvgDepth() {
        return avgDepth;
    }

    public void setAvgDepth(String avgDepth) {
        this.avgDepth = avgDepth;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public String getDivemaster() {
        return divemaster;
    }

    public void setDivemaster(String divemaster) {
        this.divemaster = divemaster;
    }

    public String getDiveNumber() {
        return diveNumber;
    }

    public void setDiveNumber(String diveNumber) {
        this.diveNumber = diveNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getO2pct() {
        return o2pct;
    }

    public void setO2pct(String o2pct) {
        this.o2pct = o2pct;
    }

    public String getO2pct2() {
        return o2pct2;
    }

    public void setO2pct2(String o2pct2) {
        this.o2pct2 = o2pct2;
    }

    public String getO2pct3() {
        return o2pct3;
    }

    public void setO2pct3(String o2pct3) {
        this.o2pct3 = o2pct3;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getPressureEnd() {
        return pressureEnd;
    }

    public void setPressureEnd(String pressureEnd) {
        this.pressureEnd = pressureEnd;
    }

    public String getPressureStart() {
        return pressureStart;
    }

    public void setPressureStart(String pressureStart) {
        this.pressureStart = pressureStart;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getTankSize() {
        return tankSize;
    }

    public void setTankSize(String tankSize) {
        this.tankSize = tankSize;
    }

    public String getTankUnits() {
        return tankUnits;
    }

    public void setTankUnits(String tankUnits) {
        this.tankUnits = tankUnits;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    /*
    public static String getDiveSitePrivateId(
        Masterdata masterdata, String spot, String city) {
        DiveSite site = masterdata.getDiveSiteBySpotAndCity(spot, city);
        if (site == null) {
            site = new DiveSite();
            site.setSpot(spot);
            site.setCity(city);
            site.setPrivateId(String.valueOf(masterdata
                .getNextPrivateDiveSiteId()));
            masterdata.addDiveSite(site);
        }
        return site.getPrivateId();
    }
    */

    private class SDM2Sample {
        private String time;

        private String depth;

        public SDM2Sample(String time, String depth) {
            this.time = time;
            this.depth = depth;
        }

        public String getDepth() {
            return depth;
        }

        public void setDepth(String depth) {
            this.depth = depth;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }

}
