# AstraLib

AstraLib is a library for the faster development of Minecraft Paper and Velocity plugins.
The name originates from a combination of the name Astralia (a Minecraft RPG server that never came to life) and the word library.
Now it is used for serveral plugins for the german Minecraft CityBuild and Skyblocm server UnknownCity.de (whose development is currently paused)
and some personal projects of TheZexquex.

## Usage
![Dynamic XML Badge](https://img.shields.io/badge/dynamic/xml?url=https%3A%2F%2Frepo.unknowncity.de%2Fsnapshots%2Fde%2Funknowncity%2Fastralib%2Fastralib-common%2Fmaven-metadata.xml&query=%2Fmetadata%2Fversioning%2Flatest&label=latest)

Repository:
```kts
maven {
    name = "unknowncitySnapshots"
    url = uri("https://repo.unknowncity.de/snapshots")
}
```
Dependency:
AstraLib is available for the platforms `paper` and `velocity`
```kts
implementation("de.unknowncity.astralib:astralib-{{PLATFORM}}:{{VERSION}}")
```
