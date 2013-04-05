/*
 * Copyright (c) 2011 MarkLogic Corporation. All rights reserved.
 */
package com.marklogic.ps;

import java.io.IOException;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import com.marklogic.mapreduce.ContentOutputFormat;
import com.marklogic.mapreduce.DocumentInputFormat;
import com.marklogic.mapreduce.DocumentURI;
import com.marklogic.mapreduce.MarkLogicNode;

/**
 * Read the first word from each word in the input documents, then produce a
 * single output document containing the words, sorted, and concatenated into a
 * single string. Only XML documents with text nodes contribute to the final
 * result.
 * 
 * <p>
 * This sample uses the marklogic-hello-world.xml config file.
 * </p>
 * <p>
 * The config file name is hard-coded into the sample for simplicity, so not
 * additional command line options are required by the sample.
 * </p>
 * <p>
 * The mapper create key-value pairs where the key is always the same constant
 * and the value is the first word from the document. The reducer sorts the
 * words, concatenates them together, and writes them to an output document.
 * Since all key-value pairs produced by the mapper have same key, there's only
 * one input pair to the reducer, producing only a single output document.
 * </p>
 * <p>
 * For example, given 2 input documents whose first words are "hello" and
 * "world", the mapper produces: (1, "hello") and (1, "world"). The reducer
 * receives (1, ("hello", "world")) as input and inserts HelloWorld.txt in the
 * database, containing "hello world".
 * </p>
 */
public class HelloWorld {
	
	private static final Logger LOG = LoggerFactory.getLogger(HelloWorld.class);
	
	public static class MyMapper extends
			Mapper<DocumentURI, MarkLogicNode, IntWritable, Text> {
		private static final Logger LOG = LoggerFactory.getLogger(MyMapper.class);
		private final static IntWritable one = new IntWritable(1);
		private Text firstWord = new Text();

		public void map(DocumentURI key, MarkLogicNode value, Context context)
				throws IOException, InterruptedException {
			if (key != null && value != null && value.get() != null) {
				// grab the first word from the document text
				Document doc = (Document) value.get();
				String text = doc.getDocumentElement().getTextContent();
				firstWord.set(text.split(" ", 2)[0]);
				context.write(one, firstWord);
			} else {
				LOG.error("key: " + key + ", value: " + value);
			}
		}
	}

	public static class MyReducer extends
			Reducer<IntWritable, Text, DocumentURI, Text> {
		private static final Logger LOG = LoggerFactory.getLogger(MyReducer.class);
		private Text result = new Text();
		private static final DocumentURI outputURI = new DocumentURI(
				"HelloWorld.txt");
		private String allWords = new String();

		public void reduce(IntWritable key, Iterable<Text> values,
				Context context) throws IOException, InterruptedException {
			// Sort the words
			ArrayList<String> words = new ArrayList<String>();
			for (Text val : values) {
				words.add(val.toString());
			}
			Collections.sort(words);

			// concatenate the sorted words into a single string
			allWords = "";
			Iterator<String> iter = words.iterator();
			while (iter.hasNext()) {
				allWords += iter.next() + " ";
			}

			// save the final result
			result.set(allWords.trim());
			context.write(outputURI, result);

		}
	}

	public void executeMapReduce() {
		LOG.info("Starting MapReduce Job");
		try {
			Configuration conf = new Configuration();
			
			Job job = new Job(conf);
			job.setJarByClass(HelloWorld.class);

			// Map related configuration
			job.setInputFormatClass(DocumentInputFormat.class);
			job.setMapperClass(MyMapper.class);
			job.setMapOutputKeyClass(IntWritable.class);
			job.setMapOutputValueClass(Text.class);

			// Reduce related configuration
			job.setReducerClass(MyReducer.class);
			job.setOutputFormatClass(ContentOutputFormat.class);
			job.setOutputKeyClass(DocumentURI.class);
			job.setOutputValueClass(Text.class);

			conf = job.getConfiguration();
            conf.addResource(new Path("src/main/resources/marklogic-hello-world.xml"));

			job.waitForCompletion(true);
			
		} catch (IOException e) {
			LOG.error(e.getMessage());
		} catch (InterruptedException e) {
			LOG.error(e.getMessage());
		} catch (ClassNotFoundException e) {
			LOG.error(e.getMessage());
		}

	}

}