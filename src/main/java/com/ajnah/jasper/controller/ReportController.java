package com.ajnah.jasper.controller;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.FileResolver;
import net.sf.jasperreports.engine.util.JRSaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping(value="/v1")
public class ReportController {

    @Autowired
    ResourceLoader resourceLoader;

    @GetMapping("/generate")
    public HttpEntity<byte[]> generate() throws JRException, IOException {


        FileResolver fileResolver = new FileResolver() {

            @Override
            public File resolveFile(String fileName) {
                URI uri;
                try {
                    uri = new URI(this.getClass().getResource(fileName).getPath());
                    return new File(uri.getPath());
                } catch (URISyntaxException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return null;
                }
            }
        };

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("title", "Employee Report");
        parameters.put("minSalary", 15000.0);
        InputStream logo= resourceLoader.getResource("classpath:/wood.jpg").getInputStream();
        parameters.put("logo",  logo);

        parameters.put("condition", " LAST_N<AME ='Smith' ORDER BY FIRST_NAME");

        InputStream employeeReportStream
                = getClass().getResourceAsStream("/test3.jrxml");
        JasperReport jasperReport
                = JasperCompileManager.compileReport(employeeReportStream);

        JRSaver.saveObject(jasperReport, "test3.jasper");

        JasperPrint jasperPrint
                = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

        byte[] documentBody=JasperExportManager.exportReportToPdf(jasperPrint);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_PDF);
        header.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"file.pdf\"");
        header.setContentLength(documentBody.length);
        return new HttpEntity<byte[]>(documentBody, header);


    }


//    Public void CreateReport(File reportFile, List<Map<String, Object>> ParamList,  List<JRDataSource> SourceList){
//
//        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportFile.getPath());
//        Map<String, Object> parameters = paramList.get(0);
//        JRDataSource datasource = datasourceList.get(0);
//        jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, datasource);
//
//        if(paramList.size()>1){
//            for(int i=1; i < paramList.size(); i++)
//            {
//                JasperPrint jasperPrint_next = JasperFillManager.fillReport(jasperReport, paramList.get(i), datasourceList.get(i));
//                List pages = jasperPrint_next.getPages();
//                for (int j = 0; j < pages.size(); j++) {
//                    JRPrintPage object = (JRPrintPage) pages.get(j);
//                    jasperPrint.addPage(object);
//                }
//            }
//        }
//
//    }


}
