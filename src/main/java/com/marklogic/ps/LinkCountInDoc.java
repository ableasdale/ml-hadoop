/*
 * Copyright (c) 2003-2011 MarkLogic Corporation. All rights reserved.
 */
package com.marklogic.ps;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.marklogic.mapreduce.MarkLogicNode;
import com.marklogic.mapreduce.NodeInputFormat;
import com.marklogic.mapreduce.NodeOutputFormat;
import com.marklogic.mapreduce.NodePath;

/**
 * Count the number of occurrences of each link title in documents in
 * MarkLogic Server, and save the link count as a child node of each
 * referenced document. Use with the configuration file 
 * conf/marklogic-nodein-nodeout.xml.
 */
public class LinkCountInDoc {
	private static final Logger LOG = LoggerFactory.getLogger(LinkCountInDoc.class);

    public static class RefMapper 
    extends Mapper<NodePath, MarkLogicNode, Text, IntWritable> {
        
        private final static IntWritable one = new IntWritable(1);
        private Text refURI = new Text();
        
        public void map(NodePath key, MarkLogicNode value, Context context) 
        throws IOException, InterruptedException {
            if (value != null && value.get() != null) {
                Attr title = (Attr)value.get();
                String href = title.getValue();
            
                refURI.set(href);
                context.write(refURI, one);
            } 
        }
    }
    
    public static class IntSumReducer
    extends Reducer<Text, IntWritable, NodePath, MarkLogicNode> {
        private final static String TEMPLATE = "<ref-count>0</ref-count>";
        private final static String ROOT_ELEMENT_NAME = "//wp:page";
        private final static String BASE_URI_PARAM_NAME = "mapreduce.linkcount.baseuri";
        
        private NodePath nodePath = new NodePath();
        private Element element;
        private MarkLogicNode result;
        private String baseUri;
        
        protected void setup(Context context) 
        throws IOException, InterruptedException {
            try {
                DocumentBuilder docBuilder = 
                    DocumentBuilderFactory.newInstance().newDocumentBuilder();
                InputStream sbis = new ByteArrayInputStream(TEMPLATE.getBytes("UTF-8") );
                element = docBuilder.parse(sbis).getDocumentElement();
                result = new MarkLogicNode(element);
                baseUri = context.getConfiguration().get(BASE_URI_PARAM_NAME);
            } catch (ParserConfigurationException e) {
                LOG.error(e.getMessage());
            } catch (SAXException e) {
                LOG.error(e.getMessage());
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        }
        
        public void reduce(Text key, Iterable<IntWritable> values, 
                Context context
                ) throws IOException, InterruptedException {        
            int sum = 0;
            if (isInvalidName(key)) {
                return;
            }
            for (IntWritable val : values) {
                sum += val.get();
            }
            StringBuilder buf = new StringBuilder();
            buf.append(baseUri).append(key);
            nodePath.setDocumentUri(buf.toString());
            nodePath.setRelativePath(ROOT_ELEMENT_NAME);
            element.setTextContent(Integer.toString(sum));
            context.write(nodePath, result);
        }
        
        // exclude key that is invalid for a document URI
        private boolean isInvalidName(Text key) {
            String keyString = key.toString();
            return keyString == null || keyString.isEmpty() || 
                    keyString.matches("( )*");
        }
    }
    
    
   
    public void executeMapReduce(){
		Configuration conf = new Configuration();
		conf.addResource(new Path("src/main/resources/marklogic-nodein-nodeout.xml"));
        
		
		try {
			Job job = new Job(conf);
	        job.setJarByClass(LinkCountInDoc.class);
	        job.setInputFormatClass(NodeInputFormat.class);
	        job.setMapperClass(RefMapper.class);
	        job.setMapOutputKeyClass(Text.class);
	        job.setMapOutputValueClass(IntWritable.class);
	        job.setReducerClass(IntSumReducer.class);
	        job.setOutputFormatClass(NodeOutputFormat.class);
	        job.setOutputKeyClass(NodePath.class);
	        job.setOutputValueClass(MarkLogicNode.class);
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

