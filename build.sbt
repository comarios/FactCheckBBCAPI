name := "factcheckerapi"

version := "1.0-SNAPSHOT"

resolvers ++= Seq(
     "TypeSafe" at "http://repo.typesafe.com/typesafe/releases/",
     "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
     "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
)


libraryDependencies ++= Seq(
	"edu.stanford.nlp" % "stanford-corenlp" % "1.2.0",
	"edu.stanford.nlp" % "stanford-corenlp" % "1.2.0" classifier "models",
	"org.json" % "json" % "20140107",
	"org.apache.httpcomponents" % "httpclient" % "4.3.3",
	"commons-logging" % "commons-logging" % "1.1.3",
	"commons-codec" % "commons-codec" % "1.9",  
  javaJdbc,
  javaEbean,
  cache
)     

play.Project.playJavaSettings
