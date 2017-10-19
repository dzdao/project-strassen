import sbtassembly.Plugin._
import sbtassembly.Plugin.AssemblyKeys._

assemblySettings

name := "project-strassen"

version := "0.1"

scalaVersion := "2.12.3"

lazy val akkaVersion = "2.5.3"

resolvers += Classpaths.typesafeReleases

libraryDependencies ++= Seq(
	"com.typesafe.akka" %% "akka-actor" % akkaVersion,
	"com.typesafe.akka" %% "akka-cluster" % akkaVersion,
	"com.amazonaws" % "aws-java-sdk" % "1.11.216",
	"ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime"
	)
	
jarName in assembly := "project-strassen-ec2.jar"
