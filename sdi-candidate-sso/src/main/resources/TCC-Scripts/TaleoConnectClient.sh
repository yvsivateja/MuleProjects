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

    # Execute the client
    echo "Calling com.taleo.integration.client.Client()..."
    ${JAVA_HOME}/bin/java -Djava.io.tmpdir="${IC_TMP}" -Dhttp.proxyHost=approxy.jpmchase.net -Dhttp.proxyPort=8080 -Xmx256m -Dcom.taleo.integration.client.install.dir="${IC_HOME}" -Dcom.taleo.integration.client.productpacks.dir="${PIP_HOME}" -Djava.endorsed.dirs="${IC_HOME}/lib/endorsed" -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl -Djavax.xml.transform.TransformerFactory=net.sf.saxon.TransformerFactoryImpl -Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.Log4JLogger -Djavax.xml.xpath.XPathFactory:http://java.sun.com/jaxp/xpath/dom=net.sf.saxon.xpath.XPathFactoryImpl -classpath ${IC_CLASSPATH} com.taleo.integration.client.Client ${1} ${2} ${3}
    echo "...com.taleo.integration.client.Client() returned."

else
    echo '+-----------------------------------------------------------------------------------------------+'
    echo '+ The IC_HOME variable is defined as (${IC_HOME}) but does not contain the Taleo Connect Client +'
    echo '+ The library ${IC_HOME}/lib/taleo-integrationclient.jar cannot be found.                       +'
    echo '+-----------------------------------------------------------------------------------------------+'
    exit 2
fi
