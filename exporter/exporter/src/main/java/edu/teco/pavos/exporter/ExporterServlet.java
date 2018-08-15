package edu.teco.pavos.exporter;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HttpServlet to manage a Dataexport request from the WebGUI.
 */
public class ExporterServlet extends HttpServlet {
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -1222550742086272358L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		String type = req.getParameter("requestType");
		if (type.equals("newExport")) {
			this.export(req, res);
		} else if (type.equals("getStatus")) {
			this.status(req, res);
		} else if (type.equals("tryDownload")) {
			this.download(req, res);
		} else if (type.equals("getExtensions")) {
			this.extensions(req, res);
		}
	}
	
	private void export(HttpServletRequest req, HttpServletResponse res) 
			throws IOException {
		String ext = req.getParameter("extension");
		String tf = req.getParameter("timeFrame");
		String ops = req.getParameter("observedProperties");
		String cIDs = req.getParameter("clusters");
		String sIDs = req.getParameter("sensors");
		ExportProperties props = new ExportProperties(ext, tf, ops, cIDs, sIDs);
		FileExporter exporter = new FileExporter(props);
		String dID = exporter.createFileInformation();
		
		//make sure this thread continues working, since the servlet is finished after sending back id
		Thread aThread = new Thread(new Runnable() {
            public void run() {
            	exporter.createFile();
            }
        });
        aThread.start();
        
		PrintWriter writer = res.getWriter();
		writer.println(dID);
		writer.close();
	}
	
	private void status(HttpServletRequest req, HttpServletResponse res) 
			throws IOException {
		String dID = req.getParameter("downloadID");
		String ready = (new DownloadState(dID)).isFileReadyForDownload();
		PrintWriter writer = res.getWriter();
		writer.println(ready);
		writer.close();
	}
	
	private void download(HttpServletRequest req, HttpServletResponse res) 
			throws IOException {
		String dID = req.getParameter("downloadID");
		DownloadState ds = new DownloadState(dID);
		String ready = ds.isFileReadyForDownload();
		if (!ready.equals("true")) {
			PrintWriter writer = res.getWriter();
			writer.println(ready);
			writer.close();
		} else {
			File file = ds.getFilePath();
			byte[] zip = zipFile(file);
            ServletOutputStream sos = res.getOutputStream();
            res.setContentType("application/zip");
            res.setHeader("Content-Disposition", "attachment; filename=" + ds.getID() + ".zip");
            sos.write(zip);
            sos.flush();
            sos.close();
		}
	}
	
	private void extensions(HttpServletRequest req, HttpServletResponse res) 
			throws IOException {
		Set<String> extensions = FileTypesUtility.getAllPossibleFileExtensions();
		String output = "";
		for (String extension : extensions) {
			output += extension + ",";
		}
		PrintWriter writer = res.getWriter();
		writer.println(output);
		writer.close();
	}

    /**
     * Compress the given directory with all its files.
     */
    private byte[] zipFile(File file)
    		throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        byte[] bytes = new byte[2048];

        FileInputStream fis = new FileInputStream(file.getAbsolutePath());
        BufferedInputStream bis = new BufferedInputStream(fis);
        
        zos.putNextEntry(new ZipEntry(file.getName()));

        int bytesRead;
        while ((bytesRead = bis.read(bytes)) != -1) {
            zos.write(bytes, 0, bytesRead);
        }
        zos.closeEntry();
        bis.close();
        fis.close();
        zos.flush();
        baos.flush();
        zos.close();
        baos.close();

        return baos.toByteArray();
    }
	
}
