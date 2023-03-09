package org.apache.camel.example.springboot.service;

import org.apache.camel.example.springboot.model.ICAP;
import org.apache.camel.example.springboot.utils.ICAPException;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service("fileScanService")
public class FileScanService {

    public void DownloadFileFromTwilio(String folderId, String fileId){

    }

    public void UploadFileToMWG(){
        try{
            ICAP icap = new ICAP("192.168.1.5",1344,"filescan");

            String[] files = new String[]{
                    "C:\\Users\\Mads\\Downloads\\eicar.com.txt"
                    ,"C:\\Users\\Mads\\Downloads\\eicar.com2.txt"
                    ,"C:\\Users\\Mads\\Downloads\\eicar.com.txt"
                    ,"C:\\Users\\Mads\\Downloads\\eicar.com2.txt"
                    ,"C:\\Users\\Mads\\Downloads\\eicar.com.txt"
                    ,"C:\\Users\\Mads\\Downloads\\eicar.com2.txt"
                    ,"C:\\Users\\Mads\\Downloads\\Git-1.8.4-preview20130916.exe"
            };

            for(String file : files) {
                try {
                    System.out.print(file + ": ");
                    boolean result = icap.scanFile(file);
                    System.out.println(result == true ? "Clean" : "Infected");
                } catch (ICAPException ex) {
                    System.err.println("Could not scan file " + file + ": " + ex.getMessage());
                } catch (IOException ex) {
                    System.err.println("IO error occurred when scanning file " + file + ": " + ex.getMessage());
                }
            }
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
        catch(ICAPException e){
            System.out.println(e.getMessage());
        }
    }
}
