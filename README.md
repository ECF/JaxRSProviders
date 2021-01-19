OSGi Remote Services JaxRS Distribution Providers
===========================

ECF Distribution Providers based upon the JaxRS specification that supports [OSGi R7 Remote Services](https://osgi.org/specification/osgi.cmpn/7.0.0/service.remoteservices.html).   Implementations based on [Apache CXF](http://cxf.apache.org/) or [Jersey](https://jersey.github.io/) are provided.  

## NEW:  Tutorial for Extending the JaxRS Jersey Provider by adding customized request-level Basic Authentication
[See the documentation on the ECF Wiki](https://wiki.eclipse.org/Tutorial:_Extending_the_JaxRS_Remote_Services_Provider)

## NEW:  Support for Endpoint Description Extender Format (EDEF) Properties substitution
This makes it much easier and more flexible to use EDEF files for importing Endpoint Descriptions.  [See the documentation on the ECF Wiki](https://wiki.eclipse.org/Using_Properties_to_Import_Endpoint_Descriptions)

## Support for OSGi R7 Async Remote Services
OSGi R7 Remote Services includes support for [Asynchronous Remote Services](https://osgi.org/specification/osgi.cmpn/7.0.0/service.remoteservices.html#d0e1407) supporting Remote Services with return values of CompletableFuture, Future, or OSGi's Promise (Java) that will be executed asynchronously.

## JaxRS Jersey and CXF Distribution Provider Configuration Properties

The Jersey and CXF distribution provider configuration properties are [documented on this wiki page](https://github.com/ECF/JaxRSProviders/wiki/JaxRS-Distribution-Provider-Configuration-Properties).

## Tutorial Using JaxRS Student Example on Karaf

See [JaxRS Remote Services Tutorial](https://wiki.eclipse.org/Tutorial:_JaxRS_Remote_Services_on_Karaf)

## Apache Karaf Download and Install

See [this wiki page](https://wiki.eclipse.org/Tutorial:_JaxRS_Remote_Services_on_Karaf) for a description of how to install and run in [Apache Karaf](https://karaf.apache.org/).

LICENSE
=======

JaxRS OSGi Remote Services is distributed with the Apache2 license. See LICENSE in this directory for more
information.

