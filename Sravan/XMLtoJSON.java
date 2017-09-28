package Json;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.XML;

public class XMLtoJSON 
{
	public static void main(String[] args) throws FileNotFoundException, IOException, JSONException
	{
		StringBuilder sb = new StringBuilder(); 
        InputStream in = new FileInputStream("./xmlinput.xml"); 
        Charset encoding = Charset.defaultCharset(); 
        Reader reader = new InputStreamReader(in, encoding);
        int r = 0; 
        while ((r = reader.read()) != -1) 
        { 
            char ch = (char) r; 
            sb.append(ch); 
        }  
        in.close(); 
        reader.close(); 
        String xml = sb.toString();
        
        String json = XML.toJSONObject(xml).toString(); 
        FileWriter ofstream = new FileWriter("./jsonoutput.json"); 
        try (BufferedWriter out = new BufferedWriter(ofstream)) 
        { 
            out.write(json);
            System.out.println("JSON file created");
        }
	}
}
