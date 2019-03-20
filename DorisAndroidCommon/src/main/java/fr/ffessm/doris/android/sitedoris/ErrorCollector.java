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

	public class ErrorMessage{
		public String shortMessage;
		public String longMessage;
		public ErrorMessage(String msg){
			shortMessage =msg;
			longMessage = msg;
		}
		public ErrorMessage(String shortMessage, String longMessage){
			this.shortMessage = shortMessage;
			this.longMessage = longMessage;
		}
	}
	
	public HashMap<String, ArrayList<ErrorMessage>> errorList = new HashMap<String, ArrayList<ErrorMessage>>();
	
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
			ArrayList<ErrorMessage> list = errorList.get(group);
			if(list == null){
				list = new ArrayList<ErrorMessage>();
				errorList.put(group, list);
			}
		}
	}
	
	public void addError(String group, String shortErrorMessage, String longErrorMessage){
		if( collectErrors){
			ArrayList<ErrorMessage> list = errorList.get(group);
			if(list == null){
				list = new ArrayList<ErrorMessage>();
				errorList.put(group, list);
			}
			list.add(new ErrorMessage(shortErrorMessage, longErrorMessage));
		}
	}
	public void addError(String group, String shortErrorMessage){
		addError(group, shortErrorMessage, shortErrorMessage);
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
				List<ErrorMessage> list = errorList.get(group);
				String classNameForGroup = group.replaceAll(" ", ".");
				String groupForXML = StringEscapeUtils.escapeXml(classNameForGroup);
				bw.write("   <testsuite errors=\""+list.size()+"\" failures=\"0\"\n");
				bw.write("              name=\""+groupForXML+"\" skipped=\"0\" tests=\""+(list.size()==0?"1":list.size())+"\" time=\"\" timestamp=\"\">\n");
				
				if(list.size()==0){
					bw.write("     <testcase assertions=\"\" classname=\""+groupForXML+"\" name=\""+groupForXML+" OK\" />\n");
				}
				for (ErrorMessage err : list) {
					String errForXML = StringEscapeUtils.escapeXml(err.shortMessage);
					bw.write("     <testcase assertions=\"\" classname=\""+groupForXML+"\" name=\""+errForXML+"\" >\n");
					bw.write("        <error message=\""+errForXML+"\">\n");
					bw.write(StringEscapeUtils.escapeXml(err.longMessage));
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
