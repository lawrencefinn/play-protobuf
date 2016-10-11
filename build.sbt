organization := "io.caboose"

name := "play-protobuf"

version := "1.0-SNAPSHOT"


scalaVersion := "2.11.7"
val reflections = "org.reflections"           %  "reflections"   % "0.9.10"

libraryDependencies ++= Seq(
  reflections,
  "com.github.spullara.mustache.java" % "compiler" % "0.9.2",
  "com.google.protobuf" % "protobuf-java" % "3.1.0",
  "com.typesafe.play" %% "play" % "2.5.9",
  "com.typesafe.play" %% "play-java" % "2.5.9",
  "com.typesafe.play" %% "play-test" % "2.5.9",
  "com.google.inject" % "guice" % "4.1.0",
  "junit" % "junit" % "4.12" % "test",
  "commons-io" % "commons-io" % "2.5" % "test"

)

javacOptions in (Compile, doc) ++= Seq("-Xdoclint:none")
