import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.TreeMap;


public class MapReduceCode {

	public static void main ( String []args ){

		try {
			// Open the file
			FileInputStream fstream = new FileInputStream("/home/training/textfile.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

			String strLine;
			Map<String,String> tm = new TreeMap<String,String>();
			boolean flag = false;

			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {

				// Print the content on the console
				// hi how hi how
				System.out.println (strLine);
				String str[] = 	strLine.split(" ");
				for ( int i =0; i < str.length ; ++i ){
					String key = str[i];
					System.out.println (key);
					flag = tm.containsKey( key );
					System.out.println (flag);
					if (flag){
						String value = tm.get(key);
						System.out.println (value);
						value = value.concat(",").concat("1");
						tm.put(key, value);// hi , { 1,1}

					} else {
						tm.put(key,"1");
					}					
				}			
			}
			customizeReduce(tm);
			//Close the input stream
			br.close();
		} catch ( Exception e){
			System.out.println (e.getMessage());
		}
	}

	public static void customizeReduce (Map<String,String> tm){	

		for( Map.Entry m:tm.entrySet() ){ 
			int count=0;
			String key=null;
			System.out.println(m.getKey()+" "+m.getValue());
			key = m.getKey().toString();
			String val[] =  m.getValue().toString().split(",");
			for ( int j=0; j<val.length; ++j ){
				count = count + Integer.parseInt(val[j]); 
			}
			System.out.println(key+" "+ count );
			String line = key +"\t"+ count;
			try{
				File fout = new File("/home/training/output.txt");
				FileOutputStream fos = new FileOutputStream(fout);
			 
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
				bw.write(line);
				bw.newLine();
			} catch(Exception e){
				System.out.println(e.getMessage());
			}
			
			
		} 


	}

}
