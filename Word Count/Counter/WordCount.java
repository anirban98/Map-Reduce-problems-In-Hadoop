import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

/*
 * Class WordCount defines the WordCountMapper & WordCountReducer classes.
 */

public class WordCount {

	/*
	 * WordCountMapper extends class Mapper which also specifies the input & output
	 * key-value format for the Mapper stage . In our case the input from the
	 * input-split file will be received line by line using RecordReader for
	 * WordCountMapper class in the form of <Object, Text> and output will be
	 * generated in the form of <Text, IntWritable> format. The Mapper is
	 * mandatory for any MapReduce program.
	 */
	
	public static class WordCountMapper extends
			Mapper<Object, Text, Text, IntWritable> {
		
		// Creating a counter to track the execution of Map tasks.
		static enum WCCounter {
			INPUT_WORDS
		}

		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();
		
		// Performing string tokenization for each line of the input. 
		// Each and every line is tokenized to multiple words.
		@Override
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString();
			StringTokenizer itr = new StringTokenizer(line);
			while (itr.hasMoreTokens()) {
				word.set(itr.nextToken());
				// setting the word to the context object. The shuffle and sort phase will act on all these words.
				context.write(word, one);
				// The counter is incremented by 1.
				Counter counter = context.getCounter(
						WCCounter.class.getName(),
						WCCounter.INPUT_WORDS.toString());
				counter.increment(1);
			}
		}
	}
	
	/*
	 * WordCountReducer extends Reducer class which also specifies the input & output
	 * key-value format for the Reducer stage.In this case the output from the 
	 * and shuffle and sort phase will be received in the form of <Text, IntWritable> and
	 * output from the Reducer stage will be generated by the RecordWriter in the
	 * <Text, IntWritable> format. The Reducer is optional for any MapReduce program.
	 */
	
	public static class WordCountReducer extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		
		private IntWritable result = new IntWritable();
		
		// Aggregating the value for each unique key.
		@Override
		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			result.set(sum);
			context.write(key, result);
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		GenericOptionsParser optionParser = new GenericOptionsParser(conf, args);
		String[] remainingArgs = optionParser.getRemainingArgs();
		Job job = new Job (conf, "WordCount");
		job.setJarByClass(WordCount.class);
		job.setMapperClass(WordCountMapper.class);
		job.setCombinerClass(WordCountReducer.class);
		job.setReducerClass(WordCountReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path(remainingArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(remainingArgs[1]));
		/*
		 *  waits for all the tasks to be completed for the respective job, 
		 *  after completion returns the appropriate response back to the  
		 *  driver which in turn returns the response back to the framework.
		 */
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}