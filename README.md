## Nuxeo ECM Plugin for the customizations used by the Indianapolis Museum of Art

This repository contains a plugin to the Nuxeo ECM (http://www.nuxeo.com) for all customizations made by the Indianapolis Museum of Art (IMA). It is used in production by the IMA and provided publicly for the reference of others.

### BUILDING
Run the following on the command line in order to build a jar for this project:

	% mvn clean package

You can then copy target/ima-nuxeo-custom-x.x.jar to the Nuxeo distribution, which is %nuxeo%/nxserver/plugins/ on tomcat based distributions.
