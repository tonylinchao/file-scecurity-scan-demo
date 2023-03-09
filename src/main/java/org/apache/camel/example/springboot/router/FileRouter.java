package org.apache.camel.example.springboot.router;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.example.springboot.model.User;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import static org.apache.camel.model.rest.RestParamType.body;
import static org.apache.camel.model.rest.RestParamType.path;

@Component
public class FileRouter extends RouteBuilder {

    @Autowired
    private Environment env;

    @Value("${camel.component.servlet.mapping.context-path}")
    private String contextPath;

    @Override
    public void configure() throws Exception {

        //String dowloadFileFromTwilioUri = "https4:www.algox.cn/cloudhub2/test/mp/api/fst/a/files/${header.folderId}/{header.fileId}"
        //        + "&scanStream=true&scanStreamDelay=1000&retry=true&fileWatcher=true&readTimeout=300000";
        //String dowloadFileFromTwilioUri = "https://www.algox.cn/cloudhub2/test/mp/api/fst/a/files/ACa364d7067559f3a807922e0072af1694/14262a9e547919fa4898fadbc2b3dcdb";

        String dowloadFileFromTwilioUri = "http://www.baidu.com";
        // @formatter:off

        // File security scan POC
        rest("/files").description("File security scan POC")
                .consumes("application/json")
                .produces("application/json")
                .get("/scan/{folderId}/{fileId}").description("Single File security scan")
                .param()
                .name("folderId").type(path).description("The folderId of file").dataType("string")
                .name("fileId").type(path).description("The fileId of file").dataType("string")
                .endParam()
                .responseMessage().code(200).message("File successfully returned").endResponseMessage()
                .to("direct:download-file-from-twilio");


        from("direct:download-file-from-twilio").routeId("direct-download-file-from-twilio")
                .to("https://api.github.com/gists" +
                        "?httpMethod=POST" +
                        "&authMethod=Basic" +
                        "&authUsername=" +
                        "&authPassword=" +
                        "&authenticationPreemptive=true")
                //.toD(dowloadFileFromTwilioUri + "?httpMethod=GET")
                .convertBodyTo(String.class)
                .log("${body}");


        // @formatter:on
    }

}