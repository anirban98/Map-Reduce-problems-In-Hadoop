import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Partitioner;

public class MyPartitioner<K2, V2> implements Partitioner<Text, IntWritable> {
	
	

	public void configure(JobConf job) { 
		}

    public int getPartition(Text key, IntWritable value,
                                      int numReduceTasks) {
    	int l=key.toString().length();
    	if(l==1)
    		return 0;
    	else if(l==2)
    		return 1;
		else if (l==3)
			return 2;
		else
			return 3;
    	
    	
    	    }

}
