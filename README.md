# Dependency-track bom publish plugin

## A gradle communinity plugin
The latest stable version of the plugin can be found under plugins.gradle.org.

Link: https://plugins.gradle.org/plugin/com.github.mkep-dev.dependency-track-bom-publish 

## Motivation
Easy way to publish the generated dependency bom to your dependency track server.

I suggest to use cyclonedx.

## Installation
This plugin depends on a bom generation task. E.g. the [cycloneDx plugin](https://github.com/CycloneDX/cyclonedx-gradle-plugin).

### Add the plugins
    
    plugins{
        id "org.cyclonedx.bom" version "1.7.3"
        id 'com.github.mkep-dev.dependency-track-bom-publish' version "0.1.3"
        ...
    }
    
### Minimal configuration
Note: The `dependsOn` is extremely important!!!

Https is used as default protocol (Use `useHttps` option to change this).

    ...
    dtrackPublishBom{
        host "dtrackhostname"
        apiKey "EDrfTh1NrwqDtxJYKqEY206yu0sf2j1w" // Replace with your api key (Administration > Teams> Select Team > API KEY copy
        projectUuid "aa581df3-e48c-464b-bd61-8fe359af3d8a" // Projekt uuid the uuid of your dtrack project ( Visible in the address bar of your if you open the project in dtrack)
    }
    publishBom.dependsOn(cyclonedxBom)   
    ...

### Full configuration
    dtrackPublishBom{
        useHttps true  // whether https should be used, defualts to true
        host "dtrackhostname"
        apiKey "EDrfTh1NrwqDtxJYKqEY206yu0sf2j1w"
        projectUuid "aa581df3-e48c-464b-bd61-8fe359af3d8a"
        realm "the/realm/of/the/restapi" // Default "api/v1/bom"
        bomFile file("path to your bom.xml file relative to project root)" // Default file("reports/bom.xml") (given by the cycloneDx plugin
    }
    publishBom.dependsOn(cyclonedxBom)
    
## Usage
Simply run the `publishBom` task.
    
    
 
    
