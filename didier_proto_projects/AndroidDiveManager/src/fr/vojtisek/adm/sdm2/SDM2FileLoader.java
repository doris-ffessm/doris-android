package fr.vojtisek.adm.sdm2;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Stack;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

//import org.apache.commons.digester.Digester;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class SDM2FileLoader extends DefaultHandler {

    private TreeSet<SDM2Dive> dives;

    public SDM2FileLoader(File f) throws ZipException, IOException, SAXException, ParserConfigurationException{
    	dives = new TreeSet<SDM2Dive>();
    	
    	ZipFile zf;
		zf = new ZipFile(f);
		SAXParserFactory factory = SAXParserFactory.newInstance();
        Enumeration<? extends ZipEntry> en = zf.entries();
        while (en.hasMoreElements()) {
            ZipEntry entry = en.nextElement();
            BufferedInputStream bis = new BufferedInputStream(zf.getInputStream(entry));
            
            
            SAXParser parser = factory.newSAXParser();
            
            Reader isr = new InputStreamReader(bis);
            InputSource is = new InputSource();
            is.setCharacterStream(isr);
            is.setEncoding("ISO-8859-15");
            parser.parse(is, this);
            
        }
    }
    
    
    SDM2Dive currentDive;
    
    Stack<String> fullQualifiedName = new Stack<String>();
    
    public String getCurrenFullQualifiedName(){
    	StringBuffer sb = new StringBuffer();
    	for (String localName : fullQualifiedName) {
			sb.append(localName+"/");
		}
    	return sb.toString();
    }
    
    public void startElement (String uri, String name,
		      String qName, Attributes atts)
	{
    	fullQualifiedName.push(name);
    	String currentFullQName = getCurrenFullQualifiedName();
    	if(currentFullQName.equals("SUUNTO/MSG/")){
    		currentDive = new SDM2Dive();
    		dives.add(currentDive);
    	} else if(currentFullQName.equals("SUUNTO/MSG/SAMPLE/")){
    		// TODO
    	}
	}
	
	
	public void endElement (String uri, String name, String qName)
	{
		fullQualifiedName.pop();
	}
	public void characters (char ch[], int start, int length)
    {
		String currentFullQName = getCurrenFullQualifiedName();
		StringBuffer sb = new StringBuffer();
		for (int i = start; i < start + length; i++) {
			sb.append(ch[i]);
		}
		if(currentFullQName.equals("SUUNTO/MSG/DATE/")){
			currentDive.setDate(sb.toString());
		}
		else if(currentFullQName.equals("SUUNTO/MSG/TIME/")){
			currentDive.setTime(sb.toString());
		}
		else if(currentFullQName.equals("SUUNTO/MSG/MAXDEPTH/")){
			currentDive.setDepth(sb.toString());
		}
		else if(currentFullQName.equals("SUUNTO/MSG/MEANDEPTH/")){
			currentDive.setAvgDepth(sb.toString());
		}
		else if(currentFullQName.equals("SUUNTO/MSG/LOGNOTES/")){
			currentDive.setNotes(sb.toString());
		}
		else if(currentFullQName.equals("SUUNTO/MSG/LOCATION/")){
			currentDive.setLocation(sb.toString());
		}
		else if(currentFullQName.equals("SUUNTO/MSG/SITE/")){
			currentDive.setSite(sb.toString());
		}
		else if(currentFullQName.equals("SUUNTO/MSG/WATERTEMPMAXDEPTH/")){
			currentDive.setTemperature(sb.toString());
		}
		else if(currentFullQName.equals("SUUNTO/MSG/PARTNER/")){
			currentDive.setPartner(sb.toString());
		}
		else if(currentFullQName.equals("SUUNTO/MSG/DIVEMASTER/")){
			currentDive.setDivemaster(sb.toString());
		}
		else if(currentFullQName.equals("SUUNTO/MSG/CYLINDERSIZE/")){
			currentDive.setTankSize(sb.toString());
		}
		else if(currentFullQName.equals("SUUNTO/MSG/CYLINDERUNITS/")){
			currentDive.setTankUnits(sb.toString());
		}
		else if(currentFullQName.equals("SUUNTO/MSG/CYLINDERSTARTPRESSURE/")){
			currentDive.setPressureStart(sb.toString());
		}
		else if(currentFullQName.equals("SUUNTO/MSG/CYLINDERENDPRESSURE/")){
			currentDive.setPressureEnd(sb.toString());
		}
		else if(currentFullQName.equals("SUUNTO/MSG/O2PCT/")){
			currentDive.setO2pct(sb.toString());
		}
		else if(currentFullQName.equals("SUUNTO/MSG/O2PCT_2/")){
			currentDive.setO2pct2(sb.toString());
		}
		else if(currentFullQName.equals("SUUNTO/MSG/O2PCT_3/")){
			currentDive.setO2pct3(sb.toString());
		}
		
    }
    
    
  /*  public SDM2FileLoader(File f) throws SAXException, IOException {
        dives = new TreeSet<SDM2Dive>();
        Digester d = new Digester();
        d.setValidating(false);
        d.addObjectCreate("SUUNTO/MSG", SDM2Dive.class);
        d.addBeanPropertySetter("SUUNTO/MSG/DATE", "date");
        d.addBeanPropertySetter("SUUNTO/MSG/TIME", "time");
        d.addBeanPropertySetter("SUUNTO/MSG/MAXDEPTH", "depth");
        d.addBeanPropertySetter("SUUNTO/MSG/MEANDEPTH", "avgDepth");
        d.addBeanPropertySetter("SUUNTO/MSG/LOGNOTES", "notes");
        d.addBeanPropertySetter("SUUNTO/MSG/LOCATION", "location");
        d.addBeanPropertySetter("SUUNTO/MSG/SITE", "site");
        d.addBeanPropertySetter("SUUNTO/MSG/WATERTEMPMAXDEPTH", "temperature");
        d.addBeanPropertySetter("SUUNTO/MSG/PARTNER", "partner");
        d.addBeanPropertySetter("SUUNTO/MSG/DIVEMASTER", "divemaster");
        d.addBeanPropertySetter("SUUNTO/MSG/CYLINDERSIZE", "tankSize");
        d.addBeanPropertySetter("SUUNTO/MSG/CYLINDERUNITS", "tankUnits");
        d.addBeanPropertySetter("SUUNTO/MSG/CYLINDERSTARTPRESSURE", "pressureStart");
        d.addBeanPropertySetter("SUUNTO/MSG/CYLINDERENDPRESSURE", "pressureEnd");
        d.addBeanPropertySetter("SUUNTO/MSG/O2PCT", "o2pct");
        d.addBeanPropertySetter("SUUNTO/MSG/O2PCT_2", "o2pct2");
        d.addBeanPropertySetter("SUUNTO/MSG/O2PCT_3", "o2pct3");
        d.addBeanPropertySetter("SUUNTO/MSG/DIVENUMBER", "diveNumber");
        d.addBeanPropertySetter("SUUNTO/MSG/WATERVISIBILITYDESC", "visibility");
        d.addCallMethod("SUUNTO/MSG/SAMPLE", "addSample", 2);
        d.addCallParam("SUUNTO/MSG/SAMPLE/SAMPLETIME", 0);
        d.addCallParam("SUUNTO/MSG/SAMPLE/DEPTH", 1);
        ZipFile zf = new ZipFile(f);
        Enumeration<? extends ZipEntry> en = zf.entries();
        while (en.hasMoreElements()) {
            ZipEntry entry = en.nextElement();
            BufferedInputStream is =
                new BufferedInputStream(zf.getInputStream(entry));
            SDM2Dive dive = (SDM2Dive)d.parse(is);
            dives.add(dive);
        }
    } */
    
    public TreeSet<SDM2Dive> getDives() {
        return dives;
    }
}

