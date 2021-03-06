package de.dimajix.training.hadoop.wordcount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class WordCount extends Configured implements Tool {
    private static Logger log = LoggerFactory.getLogger(WordCount.class);

    private String inputDir = "wordcount-input";
    private String outputDir = "wordcount-output";

    public static void main(String[] args) throws Exception {
        int ret = ToolRunner.run(new WordCount(), args);
        System.exit(ret);
    }

    @Override
    public int run(String[] args) {
        // set up basic job information
        Configuration conf = getConf();

        try {
            runJob(conf);
        }
        catch(IOException|InterruptedException|ClassNotFoundException ex) {
            throw new RuntimeException(ex.getMessage());
        }

        return 0;
    }

    private boolean runJob(Configuration conf) throws IOException, InterruptedException, ClassNotFoundException {
        Job job = Job.getInstance(conf, "WordCount");
        job.setJarByClass(WordCount.class);

        // Configure input format and files
        job.setInputFormatClass(TextInputFormat.class);
        FileInputFormat.addInputPath(job, new Path(inputDir));

        // Configure output format and files
        job.setOutputFormatClass(TextOutputFormat.class);
        FileOutputFormat.setOutputPath(job, new Path(outputDir));

        // set up mapper, combiner and reducer
        job.setMapperClass(WordCountMapper.class);
        job.setReducerClass(WordCountReducer.class);
        job.setCombinerClass(WordCountReducer.class);

        // set sorting, grouping and partitioning
        // set key and value types
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        return job.waitForCompletion(true);
    }
}
