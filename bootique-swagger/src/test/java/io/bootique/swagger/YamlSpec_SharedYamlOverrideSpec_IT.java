/*
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.bootique.swagger;

import io.bootique.test.junit5.BQTestClassFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class YamlSpec_SharedYamlOverrideSpec_IT {

    @RegisterExtension
    public static final BQTestClassFactory testFactory = new BQTestClassFactory();
    private static final WebTarget target = ClientBuilder.newClient().target("http://127.0.0.1:8080/");

    @BeforeAll
    public static void beforeClass() {
        testFactory.app("-s", "-c", "classpath:config4/startup.yml")
                .autoLoadModules()
                .run();
    }

    @Test
    public void testJson() {
        Response r1 = target.path("/c1/model.json").request().get();
        assertEquals(200, r1.getStatus());
        SwaggerAsserts.assertEqualsToResource(r1.readEntity(String.class), "config4/response1.json");

        Response r2 = target.path("/c2/model.json").request().get();
        assertEquals(200, r2.getStatus());
        SwaggerAsserts.assertEqualsToResource(r2.readEntity(String.class), "config4/response2.json");
    }
}
