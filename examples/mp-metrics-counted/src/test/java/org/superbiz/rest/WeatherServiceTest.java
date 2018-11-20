/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.superbiz.rest;

import org.apache.cxf.jaxrs.client.WebClient;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.MediaType;
import java.net.URL;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class WeatherServiceTest {

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        final WebArchive webArchive = ShrinkWrap.create(WebArchive.class, "test.war")
                .addClass(WeatherService.class)
                .addAsWebInfResource(new StringAsset("<beans/>"), "beans.xml");
        return webArchive;
    }

    @ArquillianResource
    private URL base;

    @Test
    public void testCountedPrometheus() {
        final String message = WebClient.create(base.toExternalForm())
                .path("/weather/day/status")
                .get(String.class);
        assertEquals("Hi, today is a sunny day!", message);

        final String metric = WebClient.create(base.toExternalForm())
                .path("/metrics/application/weather_day_status")
                .accept(MediaType.TEXT_PLAIN)
                .get(String.class);
        assertEquals("# TYPE application:weather_day_status counter\napplication:weather_day_status 1.0\n", metric);
    }

    @Test
    public void testCountedJson() {
        final String message = WebClient.create(base.toExternalForm())
                .path("/weather/week/status")
                .get(String.class);
        assertEquals("Hi, week will be mostly sunny!", message);

        final String metric = WebClient.create(base.toExternalForm())
                .path("/metrics/application/weather_week_status")
                .accept(MediaType.APPLICATION_JSON)
                .get(String.class);
        assertEquals("{\"weather_week_status\":{\"delegate\":{},\"unit\":\"none\",\"count\":1}}", metric);
    }
}
