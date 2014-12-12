package fr.ffessm.doris.android.sitedoris;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Classe utilisée pour collecter les erreurs du site et les mettre en forme pour être remontées à l'équipe
 *
 */
public class ErrorCollector {

	public HashMap<String, ArrayList<String>> errorList = new HashMap<String, ArrayList<String>>();
	
	public boolean collectErrors = false;
	
	
	protected static ErrorCollector errorCollector;
	protected ErrorCollector(){
		
	}
	
	public static ErrorCollector getInstance(){
		if(errorCollector == null) errorCollector = new ErrorCollector();
		return errorCollector;
	}
	
	public void addGroup(String group){
		if( collectErrors){
			ArrayList<String> list = errorList.get(group);
			if(list == null){
				list = new ArrayList<String>();
				errorList.put(group, list);
			}
		}
	}
	
	public void addError(String group, String errorMessage){
		if( collectErrors){
			ArrayList<String> list = errorList.get(group);
			if(list == null){
				list = new ArrayList<String>();
				errorList.put(group, list);
			}
			list.add(errorMessage);
		}
	}
	
	public void dumpErrorsAsJUnitFile(String fileName){
		File file = new File(fileName);

		try {
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
	
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
		
			BufferedWriter bw = new BufferedWriter(fw);
			//bw.write(content);
			bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			bw.write("<testsuites disabled=\"\" errors=\"\" failures=\"\" name=\"\" tests=\"\" time=\"\">\n");
									
			
			
			for (Iterator<String> iterator = errorList.keySet().iterator(); iterator.hasNext();) {
				String group =  iterator.next();
				List<String> list = errorList.get(group);
				String groupForXML = StringEscapeUtils.escapeXml(group);
				bw.write("   <testsuite errors=\""+list.size()+"\" failures=\"0\"\n");
				bw.write("              name=\""+groupForXML+"\" skipped=\"0\" tests=\""+(list.size()==0?"1":list.size())+"\" time=\"\" timestamp=\"\">\n");
				
				if(list.size()==0){
					bw.write("     <testcase assertions=\"\" classname=\"\" name=\""+groupForXML+" OK\" />\n");
				}
				for (String err : list) {
					String errForXML = StringEscapeUtils.escapeXml(err);
					bw.write("     <testcase assertions=\"\" classname=\"\" name=\""+errForXML+"\" >\n");
					bw.write("        <error message=\""+errForXML+"\">\n");
					bw.write("problem in "+groupForXML+", "+errForXML);
					bw.write("</error>\n");
					/*bw.write("        <system-out/><system-err/>\n");*/
					bw.write("     </testcase>\n");
				}
				
				bw.write("   </testsuite>\n");
			}
			
			bw.write("</testsuites>\n");
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		errorList.clear();
		
	}
	
	
}
