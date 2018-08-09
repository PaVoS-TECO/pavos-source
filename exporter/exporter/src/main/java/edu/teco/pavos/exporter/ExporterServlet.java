package edu.teco.pavos.exporter;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		PrintWriter writer = res.getWriter();
		writer.println(dID);
		writer.close();
		// Work this in a separate Thread since would be terminated otherwise?
		exporter.createFile();
	}
	
	private void status(HttpServletRequest req, HttpServletResponse res) 
			throws IOException {
		String dID = req.getParameter("downloadID");
		Boolean ready = (new DownloadState(dID)).isFileReadyForDownload();
		PrintWriter writer = res.getWriter();
		writer.println(ready.toString());
		writer.close();
	}
	
	private void download(HttpServletRequest req, HttpServletResponse res) 
			throws IOException {
		String dID = req.getParameter("downloadID");
		DownloadState ds = new DownloadState(dID);
		boolean ready = ds.isFileReadyForDownload();
		if (!ready) {
			res.sendError(HttpServletResponse.SC_CONFLICT);
		} else {
			File file = ds.getFilePath();
			String path = file.getAbsolutePath();
			//TODO find out location
			String location = "";
			res.sendRedirect(location);
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
	
}
