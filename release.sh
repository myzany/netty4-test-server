#!/bin/sh

# -----------------------------------------------------------------------------
# AUTHOR      : ZANY (91037677)
# DESCRIPTION : Maven Project Build & Generate Release Pack.
# ARGUMENTS   : NONE
# CREATION    : 2015-10-01
# -----------------------------------------------------------------------------
# Copyright(c) 2015 kt corp. All rights reserved.
# -----------------------------------------------------------------------------

SYSINFO=`uname -a`
BASESYS=`uname -o`
BASEDIR=`dirname $0`

MAVEN_SETTING_XML="~/.m2/settings-apm.xml"
MAVEN_BASE_COMMAND="mvn -U -DskipTests"
MAVEN_LIFECYCLE_PHASE="clean package"
MAVEN_FINAL_COMMAND=""

RELEASE_DIR=$BASEDIR/_release

RELEASE_LIB_DIR=$RELEASE_DIR/lib
RELEASE_LOG_DIR=$RELEASE_DIR/log

RESULT_DIR=$BASEDIR/target
RESULT_JAR_SUFFIX="-provided.jar"

RESOURCE_DIR="$BASEDIR/src/main/resources"
COPY_RESOURCES="N"

printf "\n"
printf "\nCurrent System : ($BASESYS) $SYSINFO"
printf "\n"

if [ "$BASEDIR" != "." ]; then
	printf "\nPlease run this script at it's own directory."
	printf "\n"
	exit 1
fi

if [ ! -f "pom.xml" ]; then
	printf "\n'pom.xml' file does not exists."
	printf "\n"
	exit 1
fi

if [ ! -d $RELEASE_DIR ]; then
	printf "\n$RELEASE_DIR directory does not exists."
	printf "\n"
	exit 1
fi

if [ ! -d $RESOURCE_DIR ]; then
	printf "\n$RESOURCE_DIR directory does not exists."
	printf "\n"
	exit 1
fi

printf "\n==================================================="
printf "\nApplication Resource File."
printf "\n==================================================="

userInput=""
printf "\nDo you want to copy resources from source to release build directory ? [y/N] "
read userInput

if [ "$userInput" != "" ]; then
    COPY_RESOURCES=`echo $userInput | tr '[:lower:]' '[:upper:]'`
fi

printf "\n==================================================="
printf "\nMaven settings.xml file."
printf "\n==================================================="

userInput=""
printf "\nDo you redefine the maven settings.xml file ? ($MAVEN_SETTING_XML) [new path or press enter to using default]\n"
read userInput

if [ "$userInput" != "" ]; then
	MAVEN_SETTING_XML=$userInput
fi

printf "\n==================================================="
printf "\nMaven Build and Packaging."
printf "\n==================================================="
printf "\n"

if [ "$MAVEN_SETTING_XML" != "" ];
then
	MAVEN_FINAL_COMMAND="$MAVEN_BASE_COMMAND -s $MAVEN_SETTING_XML $MAVEN_LIFECYCLE_PHASE"
else
	MAVEN_FINAL_COMMAND="$MAVEN_BASE_COMMAND $MAVEN_LIFECYCLE_PHASE"
fi

sh -c "$MAVEN_FINAL_COMMAND"

BUILD_RESULT=$?

if [[ $BUILD_RESULT != 0 ]] ; then
    printf "\n"
    printf "\nBuild Failed."
    printf "\n"
    exit 1
fi

printf "\n==================================================="
printf "\nCheck result directories..."
printf "\n==================================================="

if [ ! -d $RESULT_DIR ]; then
	printf "\n$RESULT_DIR directory does not exists."
	printf "\n"
	exit 1
fi

if [ "$COPY_RESOURCES" == "Y" ]; then

    if [ -d $RELEASE_DIR ]; then

        printf "\n==================================================="
        printf "\nCopy Resources..."
        printf "\n==================================================="

        find $RELEASE_DIR -maxdepth 1 ! -path $RELEASE_DIR -type d | grep -v "\.svn" | xargs rm -rf

        cp -R $RESOURCE_DIR/* $RELEASE_DIR
    fi  
fi

printf "\n==================================================="
printf "\nCheck release directories..."
printf "\n==================================================="

if [ ! -d $RELEASE_LIB_DIR ];
then
	mkdir -p $RELEASE_LIB_DIR
else
	rm -f $RELEASE_LIB_DIR/*
fi

if [ ! -d $RELEASE_LOG_DIR ];
then
    mkdir -p $RELEASE_LOG_DIR
else
    rm -f $RELEASE_LOG_DIR/*
fi

printf "\n==================================================="
printf "\nCopy libraries..."
printf "\n==================================================="

cp $RESULT_DIR/*$RESULT_JAR_SUFFIX $RELEASE_LIB_DIR

printf "\n"

