#! /bin/ksh

# This is a symbolic link to the latest version of java (jdk1.5.0_xx)
JAVA_HOME=/apps/java/jdk1.7.0_79
export JAVA_HOME

IC_HOME=/apps/sdiapps/shared/TCC/tcc
export IC_HOME

PIP_HOME=/apps/sdiapps/shared/TCC/productintegrationpack
export PIP_HOME

IC_TMP=/apps/sdiapps/shared/TCC_tmp_space
export IC_TMP

echo $JAVA_HOME
echo $IC_HOME
echo $IC_TMP
echo $PIP_HOME

# Make sure that the JAVA_HOME variable is defined
if  [ ! "${JAVA_HOME}" ]
then
    echo '+-----------------------------------------+'
    echo '+ The JAVA_HOME variable is not defined.  +'
    echo '+-----------------------------------------+'
    exit 3    
fi

# Make sure the IC_HOME variable is defined
if  [ ! "${IC_HOME}" ]
then
    IC_HOME=.
fi

# Check if the IC_HOME points to a valid taleo Connect Client folder
if [ -e "${IC_HOME}/lib/taleo-integrationclient.jar" ]
then
    # Define the class path for the client execution
    IC_CLASSPATH="${IC_HOME}/lib/taleo-integrationclient.jar":"${IC_HOME}/log"

    # Get the Job ID (Filename of SQ without extension)
    FQSQName=${2}
    SQNameExt=${FQSQName##*/} ## Everything to the right of the rightmost slash
    SQName=${SQNameExt%.*}    ## Everything to the left of the rightmost period
    echo TCC Job Identifier is: ${SQName}
    typeset -u STRING_IN_UPPER
    STRING_IN_UPPER=${1}

    # If the request is for netchange then only execute the below commands.
    if [[ ${STRING_IN_UPPER} == *NETCHANGE* ]]
    then 
       NETCHANGE_CONFIG_FOLDER=${IC_HOME}/extensions/plugins/tcc-netchange/configuration 
       echo $NETCHANGE_CONFIG_FOLDER 
       JobName=${SQName%.*} ## Everything to the left of the rightmost underscore 
       STORAGE_PROP_FOLDER=${NETCHANGE_CONFIG_FOLDER}/${JobName} 

       # Make sure config folder for this job exists, if not then create one.
       if  [ -d ${STORAGE_PROP_FOLDER} ]
        then
 	   echo Netchange configuration folder is : ${STORAGE_PROP_FOLDER}
	else
           mkdir -p ${STORAGE_PROP_FOLDER} 
	   chmod 765 ${STORAGE_PROP_FOLDER} 
	   echo Netchange configuration folder is created : ${STORAGE_PROP_FOLDER} 


       fi
       # Make sure storage.properties file exists, if not then create one.
       if  [ -f ${STORAGE_PROP_FOLDER}/storage.properties ]
        then
 	   echo Netchange storage.property exists : ${STORAGE_PROP_FOLDER}/storage.properties
	else
	   STORAGE_PROP=${STORAGE_PROP_FOLDER}/storage.properties 
	   NETCHANGE_REPOSITORY=${IC_HOME}/system/repository/${JobName} 

           # Create the storage.properties file. 
	   echo UseCompression=true > ${STORAGE_PROP}
	   echo UEncryptionMode=2 >> ${STORAGE_PROP}
	   echo RepositoryLocation=${NETCHANGE_REPOSITORY} >> ${STORAGE_PROP} 
	   echo StorageUnitImplementation=com.taleo.integration.storage.FileStorageUnit >> ${STORAGE_PROP} 
	   echo FileStorageUnit.DefaultBlockSize=1 >> ${STORAGE_PROP} 
	   echo >> ${STORAGE_PROP} 
	   chmod 644 ${STORAGE_PROP} 
	   echo storage.properties file is created and stored at ${STORAGE_PROP}. 
        fi
        #
        # Execute the client
        echo "Calling com.taleo.integration.client.Client()..."
        ${JAVA_HOME}/bin/java -Dtcc.id=${SQName} -Djava.io.tmpdir="${IC_TMP}" -Dhttp.proxyHost=approxy.jpmchase.net -Dhttp.proxyPort=8080 -Xmx256m -Dcom.taleo.integration.client.install.dir="${IC_HOME}" -Dcom.taleo.integration.client.productpacks.dir="${PIP_HOME}" -Djava.endorsed.dirs="${IC_HOME}/lib/endorsed" -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl -Djavax.xml.transform.TransformerFactory=net.sf.saxon.TransformerFactoryImpl -Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.Log4JLogger -Djavax.xml.xpath.XPathFactory:http://java.sun.com/jaxp/xpath/dom=net.sf.saxon.xpath.XPathFactoryImpl -Dcom.taleo.integration.client.extensions.plugins.configuration.dir.plugins.tcc-netchange=${STORAGE_PROP_FOLDER} -classpath ${IC_CLASSPATH} com.taleo.integration.client.Client ${1} ${2} ${3}
    else
        #
        # Execute the client
        echo "Calling com.taleo.integration.client.Client()..."
        ${JAVA_HOME}/bin/java -Dtcc.id=${SQName} -Djava.io.tmpdir="${IC_TMP}" -Dhttp.proxyHost=approxy.jpmchase.net -Dhttp.proxyPort=8080 -Xmx256m -Dcom.taleo.integration.client.install.dir="${IC_HOME}" -Dcom.taleo.integration.client.featurepacks.dir="${PIP_HOME}" -Djava.endorsed.dirs="${IC_HOME}/lib/endorsed" -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl -Djavax.xml.transform.TransformerFactory=net.sf.saxon.TransformerFactoryImpl -Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.Log4JLogger -Djavax.xml.xpath.XPathFactory:http://java.sun.com/jaxp/xpath/dom=net.sf.saxon.xpath.XPathFactoryImpl -classpath ${IC_CLASSPATH} com.taleo.integration.client.Client ${1} ${2} ${3}
   fi
   echo "...com.taleo.integration.client.Client() returned."
else
    echo '+-----------------------------------------------------------------------------------------------+'
    echo '+ The IC_HOME variable is defined as (${IC_HOME}) but does not contain the Taleo Connect Client +'
    echo '+ The library ${IC_HOME}/lib/taleo-integrationclient.jar cannot be found.                       +'
    echo '+-----------------------------------------------------------------------------------------------+'
    exit 2
fi
