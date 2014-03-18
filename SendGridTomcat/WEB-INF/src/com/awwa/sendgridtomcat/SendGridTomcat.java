package com.awwa.sendgridtomcat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class SendGridTomcat extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        PrintWriter pw = null;
        try {
            File file = new File("/var/tmp/log.txt");
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            System.out.println("doPost()");
            System.out.println(inputStreemToString(request.getInputStream(), "UTF-8"));
                
            pw = new PrintWriter(bw);
            pw.println("---- start parse");
        
            List<FileItem> items = new ServletFileUpload(
                    new DiskFileItemFactory()).parseRequest(request);
            for (FileItem item : items) {
                pw.println("---- parse request ----");
                if (item.isFormField()) {
                    String fieldname = item.getFieldName();
                    String fieldvalue = item.getString("UTF-8");// TODO specify appropriate encode
                    pw.println("fieldname: " + fieldname);
                    pw.println("fieldvalue: " + fieldvalue);
                } else {
                    String fieldname = item.getFieldName();
                    String filename = item.getName();
                    InputStream filecontent = item.getInputStream();
                    String content = inputStreemToString(filecontent, "UTF-8");// TODO specify appropriate encode
                    pw.println("fieldname: " + fieldname);
                    pw.println("filename: " + filename);
                    pw.println("content: " + content);
                }
            }
            pw.println("---- finish parse");
        } catch (Exception e) {
            pw.println("Fail to parse." + e);
        } finally {
            pw.close();
        }        
    }

    public static String inputStreemToString(InputStream in, String enc) throws IOException{
        
        BufferedReader reader = 
            new BufferedReader(new InputStreamReader(in, enc));
        StringBuffer buf = new StringBuffer();
        String str;
        while ((str = reader.readLine()) != null) {
                buf.append(str);
                buf.append("\n");
        }
        return buf.toString();
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException
    {
          response.setContentType("text/html");
          PrintWriter out = response.getWriter();
          out.println("<html>");
          out.println("<head>");
          out.println("<title>POST receiver</title>");
          out.println("</head>");
          out.println("<body>");
          out.println("<h1>I'm a POST receiver.</h1>");
          out.println("<body>Now I will receive POST from SendGrid Parse API.</body>");
          out.println("</html>");
    }
}
