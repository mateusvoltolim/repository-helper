# repository-helper

Project created to help maven build executions and clean garbage in local repositories.

PS.: I'm sorry for any typos or grammar. English isn't my first language.

## Configurations

The configurations are made via json file. See an example:

```csv
{
	"dirBaseCheckout": "E:/Desenvolvimento/Repositorios/git/",
	"pathPomBase": "E:/Desenvolvimento/Repositorios/git/oobj-dfe-legado/pom.xml",
	"mavenHome": "",
	"goals": [
		"clean",
		"install",
		"-q",
		"-Dmaven.test.skip=true"
	],
	"localRepository": {
		"hasToClean": "false",
		"maxAgeFiles": 30,
		"path": "e:/Desenvolvimento/Ferramentas/MavenRepository/repository/br/com/oobj"
	},
	"modules": [
		"oobj-corporativo",
		"oobj-dfe-api",
		"oobj-dfe-legado",
		"oobj-dfe-core"
	],
	"throwFailure": "true"
}
```

### Parameters

* **dirBaseCheckout:** It is the path where is located your repositories.
* **pathPomBase:** It is the path where is located the pom.xml file root or parent.
* **mavenHome:** In case when Environment Variable *MAVEN_HOME* isn't defined, it is possible define the path where is the binaries of maven.
* **goals:** It is an array, and should be defined in ordered of execution. Especifies the parameters for the build.
* **localRepository:** This object has 3 members.
  * **hasToClean:** It is a boolean to define if should remove old dependencies of your versioned projects.
  * **maxAgeFiles:** Defines the max age of the files to be considered in the filter to be removed.
  * **path** The path where is located the versioned dependencies of your own projects (and not of the thrid party dependencies).
* **modules:** It is an array, and should be in ordered of execution. Defines the maven projects names located in the path defined in *dirBaseCheckout*.
* **throwFailure:** It is a boolean to define if should be thrown an exception and stop the execution.

### Considerations

* This project is made to help in my development environment, and may not work in others environments without adaptation.

### TODO list

* Use json file to configure the rules to filter old files to be removed.

