<!--
  Licensed to ObjectStyle LLC under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ObjectStyle LLC licenses
  this file to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
  -->

[![Build Status](https://travis-ci.org/bootique/bootique-swagger.svg)](https://travis-ci.org/bootique/bootique-swagger)
[![Maven Central](https://img.shields.io/maven-central/v/io.bootique.swagger/bootique-swagger.svg?colorB=brightgreen)](https://search.maven.org/artifact/io.bootique.swagger/bootique-swagger/)

# bootique-swagger

Integrates [Swagger](http://swagger.io/) REST API documentation services with Bootique. Supports modern
[OpenAPI 3 specification](https://swagger.io/docs/specification/about/), as well as the legacy Swagger 2 spec. Contains
the following modules:

* `bootique-swagger-openapi`: a REST service to dynamically generate OpenAPI specifications as either
JSON or YAML. Combines metadata from annotated API resources within the application with
[static API descriptors](https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Integration-and-Configuration#known-locations)
to produce application-specific API specs.

* `bootique-swagger`: a legacy service generating Swagger 2 API descriptors.

* `bootique-swagger-ui`: embeddable Swagger web UI to visualize and interact with API specifications. Supports both
OpenAPI 3 and legacy Swagger 2 specifications.

_TODO: the examples below are based on the yet unreleased Bootique 2.0, and are only available as snapshots._

## Usage

### Prerequisites

Include ```bootique-bom```:
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.bootique.bom</groupId>
            <artifactId>bootique-bom</artifactId>
            <version>2.0-SNAPSHOTS</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### "Code-First" - exposing annotated endpoints

Swagger can generate application API specification dynamically based purely on the endpoint class/method signatures and JAX-RS
annotations (such as `@Path`, `@GET`, etc.). And then you would "enrich" the basic specification via the following mechanisms:

* [Swagger annotations](https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations)
* [Static API descriptors](https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Integration-and-Configuration#known-locations)

To expose the final specification as JSON and YAML resources, add the following dependency:
```xml
<dependency>
	<groupId>io.bootique.swagger</groupId>
	<artifactId>bootique-swagger-openapi</artifactId>
</dependency>
```
Now, when you run the app, you should be able to access the specs at the URLs similar to these:

* http://127.0.0.1:8080/openapi.json
* http://127.0.0.1:8080/openapi.yaml

### "Spec-Code-Spec"

A more complex workflow is when you have one or more API specifications, generate Java code from them (models, API
endpoints), and then need to combine them together and/or extend them with extra app-specific fragments. So essentially
going from specification(s) to code and then back to a single specification ("Spec-Code-Spec").

Everything described in the "Code-First" section above is fully applicable to the last step of this workflow
(i.e. going from code to spec), but the first step (going from spec(s) to code) is not handled by
`bootique-swagger-openapi` and requires a third-party code generator.

There are various code generators available (such as
[openapi-generator](https://github.com/OpenAPITools/openapi-generator)), but as of this writing (March 2020), most do not
support OpenAPI-compatible Swagger annotations (`io.swagger.core.v3:swagger-annotations` package). The only tool that
we found to support it is unsurprisingly coming from Swagger itself. It is
[swagger-codegen, v.3.0.x](https://github.com/swagger-api/swagger-codegen/tree/3.0.0). You can download
[the jar file](https://search.maven.org/remotecontent?filepath=io/swagger/codegen/v3/swagger-codegen-cli/3.0.18/swagger-codegen-cli-3.0.18.jar)
from Maven Central, and run the generator like this:

```
java -jar swagger-codegen-cli-3.0.18.jar generate -l jaxrs-jersey \
    -o myproject
    -i mymodel.yaml
```
There are also options to integrate this in Maven.

### Web UI

Bootique integrates Swagger browser UI to be able to view and interact with the API specs:

```xml
<dependency>
	<groupId>io.bootique.swagger</groupId>
	<artifactId>bootique-swagger-ui</artifactId>
</dependency>
```

To view the spec from the same app (e.g. the one added via `bootique-swagger-openapi` as described above), add the
relative path of the model resource to the app configuration:
```yml
swaggerui:
  default:
    specPath: openapi.json
```
When you start the application, the console will be available at `/<appcontext>/swagger-ui`. E.g.
http://127.0.0.1:8080/swagger-ui/ .

If you are running behind a proxy, make sure you pass the correct `Host` header with the `host[:port]` of the proxy,
or the browser will not be able to discover your specification endpoint and/or won't be able to invoke it properly.
E.g. for `nginx` proxy you might use the following config:

```
proxy_set_header  Host $http_host;
```

To view a spec from another app, configure specification public URL like this:
```yml
swaggerui:
  default:
    specUrl: https://example.org/path/to/openapi.json
```

