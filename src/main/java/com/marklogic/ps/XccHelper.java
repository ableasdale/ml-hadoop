package com.marklogic.ps;

import java.net.URI;
import java.net.URISyntaxException;

import com.marklogic.xcc.ContentSource;
import com.marklogic.xcc.ContentSourceFactory;
import com.marklogic.xcc.Request;
import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;
import com.marklogic.xcc.exceptions.XccConfigException;

public class XccHelper {
		
	public void clean(){
		System.out.println("cleaning");
		try {
			ContentSource contentSource = ContentSourceFactory
					.newContentSource(new URI("xcc://admin:admin@localhost:9999"));
		
			 Session session = contentSource.newSession();
			 Request request = session.newAdhocQuery("import module namespace admin = \"http://marklogic.com/xdmp/admin\" at \"/MarkLogic/admin.xqy\";\nadmin:save-configuration(admin:database-delete(admin:get-configuration(), xdmp:database(\"hello-hadoop\")))");
			 session.submitRequest(request);
			 System.out.println("IT should have been deleted...");
			 session.close();
			 
			 
		} catch (XccConfigException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
