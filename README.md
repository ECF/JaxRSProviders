JaxRSProviders

# Build

## Prepare for Tycho beta
We use the new pom-less tycho feature. At the time of this writing this feature is beta (Tycho 0.24). In order to use it you need to add a settings.xml file to your ~/.m2/ directory AND upgrade to Maven 3.3.3+. <br/>
[More information can be found here [1]](https://wiki.eclipse.org/Tycho/Release_Notes/0.24)

## Build
git clone https://github.com/ECF/JaxRSProviders.git
cd JaxRSProviders
mvn verify



[1] https://wiki.eclipse.org/Tycho/Release_Notes/0.24
