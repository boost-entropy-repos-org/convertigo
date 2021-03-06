# What is Convertigo Mobility Platform ?

Convertigo is an open source Low Code Application Platform (LCAP) featuring MXDP (Multi eXperience Development Platform) / MBaaS (Mobile Back end as a Service) for full-stack mobile and web application development. The platform is used to build complex Cross-platform Enterprise Mobile apps in a few days. Convertigo platform is composed of several components:

1.	**Convertigo MBaaS**: The back-end MBaaS server part. Handles back-end connectors, micro-services execution, offline data device synchronization and serves Mobile Web apps. Runs as a Docker container with the `convertigo` (official) or `convertigo/convertigo` images
2.	**Convertigo Studio**: Runs on a Windows or a MacOS workstation, Eclipse based IDE, used to program MBaaS micro-services workflows and optionaly use the "Mobile Builder" edition to build Mobile apps UIs in a MXDP (Multi eXperience Development Platform) Low code mode. Can be directly downloaded from [Sourceforge.net](https://sourceforge.net/projects/convertigo/files/latest/download)
3.	**Convertigo SDKs**: Can be used with third party Mobile development tools such as Xcode (iOS) Android Studio (Android) and Visual Studio (Windows Mobile, Windows UWP and Xamarin). SDKS are available on each platform standard repository (Bintray for Android, Cocoapods for iOS and Nuget for .NET)

Convertigo Community edition brought to you by Convertigo SA (Paris & San Francisco). The platform is currently used by more than 100K developers worldwide, building enterprise class mobile apps.

> [www.convertigo.com](https://www.convertigo.com)

# How to use this image

## Quick start

	$ docker run --name C8O -d -p 28080:28080 convertigo

This will start a container running the minimum Convertigo MBaaS server. Convertigo MBaaS uses images' **/workspace** directory to store configuration file and deployed projects as an Docker volume.

You can access the Server admin console on http://[dockerhost]:28080/convertigo and login using the default credentials: admin / admin

## Link Convertigo to a CouchDB database for FullSync (Convertigo EE only)

Convertigo MBaaS FullSync module uses Apache CouchDB 2.3.1 as NoSQL repository. You can use the **[couchdb](https://hub.docker.com/_/couchdb/)** docker image and link to it convertigo this way

Launch CouchDB container and name it 'fullsync'

    docker run -d --name fullsync couchdb:2.3.1

Then launch Convertigo and link it to the running 'fullsync' container. Convertigo MBaaS sever will automatically use it as its fullsync repository.

    docker run -d --name C8O --link fullsync:couchdb -p 28080:28080 convertigo

## Link Convertigo to a Billing & Analytics database

### MySQL

MySQL is the recommended database for holding Convertigo MBaaS server analytics. You can use this command to run convertigo and link it to a running MySQL container. Change [mysql](as the container name), [data base admin user], [data base admin user] with the values for your MySQL configuration.

    docker run -d --name C8O --link [mysql]:mysql -p 28080:28080                                         \
        -e JAVA_OPTS="-Dconvertigo.engine.billing.enabled=true                                           \ 
                -Dconvertigo.engine.billing.persistence.jdbc.username=[username for the c8oAnalytics db] \
                -Dconvertigo.engine.billing.persistence.jdbc.password=[password for specified db user]   \
                -Dconvertigo.engine.billing.persistence.jdbc.url=jdbc:mysql://mysql:3306/c8oAnalytics"   \
    convertigo

## Where is Convertigo MBaaS server storing deployed projects

Projects are deployed in the Convertigo workspace, a simple file system directory. You can map the docker container **/workspace** to your physical system by using :

    docker run --name C8O -v $(pwd):/workspace -d -p 28080:28080 convertigo

You can share the same workspace by all Convertigo containers. This this case, when you deploy a project on a Convertigo container, it will be seen by others. This is the best way to build multi-instance load balanced Convertigo server farms.

## Migrate from an earlier version of Convertigo

-	Stop the container to perform a backup. And just back the workspace directory. This will backup all the projects definitions and some project data.
-	Start a new Convertigo MBaaS docker container mapping the workspace
-	All the workspace (Projects) will be automatically migrated to the new Convertigo MBaaS version

## Security

The default administration account of a Convertigo serveur is **admin** / **admin** and the **testplatform** is anonymous.

These accounts can be configured through the *administration console* and saved in the **workspace**.

### `CONVERTIGO_ADMIN_USER` and `CONVERTIGO_ADMIN_PASSWORD` variables

You can change the default administration account :

    docker run -d --name C8O -e CONVERTIGO_ADMIN_USER=administrator -e CONVERTIGO_ADMIN_PASSWORD=s3cret -p 28080:28080 convertigo

### `CONVERTIGO_TESTPLATFORM_USER` and `CONVERTIGO_TESTPLATFORM_PASSWORD` variables

You can lock the **testplatform** by setting the account :

    docker run -d --name C8O -e CONVERTIGO_TESTPLATFORM_USER=tp_user -e CONVERTIGO_TESTPLATFORM_PASSWORD=s3cret -p 28080:28080 convertigo

## `JAVA_OPTS` Environment variable

Convertigo is based on a *Java* process with some defaults *JVM* options. You can override our defaults *JVM* options with you own.

Add any *Java JVM* options such as -D[something] :

    docker run -d --name C8O -e JAVA_OPTS="-DjvmRoute=server1" -p 28080:28080 convertigo
## `JXMX` Environment variable

Convertigo relies on the container limit resources and allocate 80% of the memory limit for the heap. The heap limit can be set with the JXMX variable that set the *JVM Xmx* parameter in megabytes instead.

The default `JXMX` value is *empty* and can be defined :

    docker run -d --name C8O -e JXMX="2048" -p 28080:28080 convertigo

## `COOKIE_PATH` Environment variable

Convertigo generates a `JSESSIONID` to maintain the user session and stores in a *cookie*. The *cookie* is set for the server path `/` by default. In case of a front server with multiple services for different paths, you can set a path restriction for the *cookie* with the `JSESSIONID`. Just define the `COOKIE_PATH` environment variable with a compatible path.

The default `COOKIE_PATH` value is `/` and can be defined :

    docker run -d --name C8O -e COOKIE_PATH="/convertigo" -p 28080:28080 convertigo

## `COOKIE_SECURE` Environment variable

Convertigo use a *cookie* to maintain sessions. Requests on port `28080` are *HTTP* but we advice to use an *HTTPS* front for production (nginx, kubenetes ingress, ...).
In this case, you can secure yours cookies to be used only with secured connections by adding the `Secure` flag.
The Secure flag can be enabled by setting the `COOKIE_SECURE` environment variable to `true`.
Once enabled, cookies and sessions aren't working through an *HTTP* connection.

The default `COOKIE_SECURE` value is `false` and can be defined :

    docker run -d --name C8O -e COOKIE_SECURE="true" -p 28080:28080 convertigo

## `COOKIE_SAMESITE` Environment variable

Allow to configure the *SameSite* parameter for generated cookies. Can be empty, `none`, `lax` or `strict`.

The default `COOKIE_SAMESITE` value is *empty* and can be defined this way:

    docker run -d –name C8O -e COOKIE_SAMESITE=lax -p 28080:28080 convertigo

## `COOKIE_SAMESITE` Environment variable

Allow to configure the *SameSite* parameter for generated cookies. Can be empty, `none`, `lax` or `strict`.

The default `COOKIE_SAMESITE` value is *empty* and can be defined this way:

    docker run -d –name C8O -e COOKIE_SAMESITE=lax -p 28080:28080 convertigo

## `SESSION_TIMEOUT` Environment variable

Allow to configure the default Tomcat *session-timeout* in minutes.  This value is used for non-project calls (Administration console, Fullsync...). This value is overridden by each projects' calls (Sequence, Transaction ...).

The default `SESSION_TIMEOUT` value is *30* and can be defined this way:

    docker run -d –name C8O -e SESSION_TIMEOUT=5 -p 28080:28080 convertigo

## `DISABLE_SUDO` Environment variable

The image include *sudo* command line, configured to allow the *convertigo* user to use it without password and to perform some *root* action inside the container. This variable allow to disable this permission.

The default `DISABLE_SUDO` value is *empty* and can be defined this way:

    docker run -d –name C8O -e DISABLE_SUDO=true -p 28080:28080 convertigo


## Pre configurated Docker compose stack

You can use this [stack](https://github.com/convertigo/docker/blob/master/compose/mbaas/docker-compose.yml) to run a complete Convertigo MBaaS server with FullSync repository and MySQL analytics in a few command lines.

	mkdir c8oMBaaS
	cd c8oMBaaS
	wget https://raw.githubusercontent.com/convertigo/docker/master/compose/mbaas/docker-compose.yml
	docker-compose up -d
