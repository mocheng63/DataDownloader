import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.*;
import javax.net.ssl.HttpsURLConnection;
import org.json.*;

public class DataDownloader {

    public static void main(String[] args) throws Exception {
    	for ( int m=1 ; m<=12 ; m++ ) {
    		for ( int d=1 ; d<=31 ; d++ ) {
    			String date = "2018"+String.format("%02d",m)+String.format("%02d",d);
    	    	downloadOneDay("fb",date);
    		}
    	}
    }

    private static void downloadOneDay(String Stock, String date) throws Exception {

    	String stock = Stock.toLowerCase();
    	String STOCK = Stock.toUpperCase();
    	String httpsURL = "https://api.iextrading.com/1.0/stock/"+stock+"/chart/date/"+date;
        URL myUrl = new URL(httpsURL);

        HttpsURLConnection conn = (HttpsURLConnection)myUrl.openConnection();
        InputStream is = conn.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        String data = "";
        String inputLine ;

        while ((inputLine = br.readLine()) != null) {
        	data = data+inputLine;
        }

        JSONObject obj = new JSONObject("{data:"+data+"}");
        JSONArray array = obj.getJSONArray("data");
        
        if ( array.length() == 0 ) return;
        
        Files.createDirectories(Paths.get("c:\\temp\\stock-data\\"+STOCK));
        BufferedWriter writer = new BufferedWriter(new FileWriter("c:\\temp\\stock-data\\"+STOCK+"\\"+STOCK+"_"+date+".csv"));
        writer.write("Symbol,Time,Price\r\n");

        for ( int i=0 ; i<array.length() ; i++ ) {
        	JSONObject one = array.getJSONObject(i);
        	try {
        		String thisdate = one.getString("date");
        		String thistime = one.getString("minute");
        		String price = one.getString("close");
            	writer.write(STOCK+","+thisdate+" "+thistime+","+price+"\r\n");
        	} catch ( Exception ex ) {
                System.out.println(date + ": error on item " + i + "\r\n");
        	}
        }
        
        System.out.println(date+": downloaded\r\n");

        br.close();
        writer.close();
    }

}
