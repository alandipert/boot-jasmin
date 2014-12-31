# boot-jasmin example project

This is a tiny Hello World project written in [Jasmin] assembly that
uses the [Boot] build tool.

## Continuously Compile, Run

    boot watch jasmin run -m example.HelloWorld

## Create a Jar

    boot jasmin uber jar -m example.HelloWorld

## Run the Jar

    java -jar target/project.jar

[Boot]: http://boot-clj.com/
[Jasmin]: http://jasmin.sourceforge.net/
