/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

/**
 * A simple Camel REST DSL route with OpenApi API documentation.
 */
@Component
public class CamelRouter extends RouteBuilder {
    
    @Autowired
    private Environment env;
    
    @Value("${camel.component.servlet.mapping.context-path}")
    private String contextPath;

    @Override
    public void configure() throws Exception {

        //String dowloadFileFromTwilioUri = "https4:www.algox.cn/cloudhub2/test/mp/api/fst/a/files/${header.folderId}/{header.fileId}"
        //        + "&scanStream=true&scanStreamDelay=1000&retry=true&fileWatcher=true&readTimeout=300000";
        String dowloadFileFromTwilioUri = "https://www.algox.cn/cloudhub2/test/mp/api/fst/a/files/ACa364d7067559f3a807922e0072af1694/14262a9e547919fa4898fadbc2b3dcdb";

        // @formatter:off
        
        // this can also be configured in application.properties
        restConfiguration()
            .component("servlet")
            .bindingMode(RestBindingMode.json)
            .dataFormatProperty("prettyPrint", "true")
            .enableCORS(true)
            .port(env.getProperty("server.port", "8080"))
            .contextPath(contextPath.substring(0, contextPath.length() - 2))
            // turn on openapi api-doc
            .apiContextPath("/api-doc")
            .apiProperty("api.title", "User API")
            .apiProperty("api.version", "1.0.0");

        rest("/users").description("User REST service")
            .consumes("application/json")
            .produces("application/json")

            .get().description("Find all users").outType(User[].class)
                .responseMessage().code(200).message("All users successfully returned").endResponseMessage()
                .to("bean:userService?method=findUsers")
        
            .get("/{id}").description("Find user by ID")
                .outType(User.class)
                .param().name("id").type(path).description("The ID of the user").dataType("integer").endParam()
                .responseMessage().code(200).message("User successfully returned").endResponseMessage()
                .to("bean:userService?method=findUser(${header.id})")

            .put("/{id}").description("Update a user").type(User.class)
                .param().name("id").type(path).description("The ID of the user to update").dataType("integer").endParam()    
                .param().name("body").type(body).description("The user to update").endParam()
                .responseMessage().code(204).message("User successfully updated").endResponseMessage()
                .to("direct:update-user");

        from("direct:update-user")
                .to("bean:userService?method=updateUser")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(204))
                .setBody(constant(""));

/*
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
                .to(dowloadFileFromTwilioUri)
                .convertBodyTo(String.class)
                .log("${body}");
*/

        // @formatter:on
    }

}
