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
import org.json.JSONObject;
import org.json.XML;

public class JSONtoXML  
{ 
    public static void main(String[] args) throws FileNotFoundException, IOException, JSONException 
    { 
    	StringBuilder sb = new StringBuilder(); 
        InputStream in = new FileInputStream("./file.json"); 
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
        String json = sb.toString();
        JSONObject jsonFileObject = new JSONObject(json);
        String xml = XML.toString(jsonFileObject);
        FileWriter ofstream = new FileWriter("./file.xml"); 
        try (BufferedWriter out = new BufferedWriter(ofstream)) 
        { 
            out.write(xml);
            System.out.println("XML file created");
        } 
    }   
} 
