import AssemblyKeys._ // put this at the top of the file

name := "Beacon"

version := "0.3"

scalaVersion := "2.10.1"

libraryDependencies += "com.codeminders" % "hidapi" % "1.1"

libraryDependencies += "net.databinder.dispatch" %% "dispatch-core" % "0.10.0"

libraryDependencies += "net.liftweb" %% "lift-json" % "2.5-RC5"

libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.7.5"

assemblySettings

seq(ProguardPlugin.proguardSettings :_*)

proguardOptions += keepMain("dk.bestbrains.beacon.Beacon")

proguardOptions += "-keep public class com.codeminders.hidapi.** { public protected *; }"

proguardOptions += "-keep public class dk.bestbrains.beacon.** { public protected *; }"

proguardOptions += "-keep public class com.ning.http.client.providers.** { public protected *; }"
