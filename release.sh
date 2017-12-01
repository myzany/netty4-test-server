#!/bin/sh

# -----------------------------------------------------------------------------
# AUTHOR      : ZANY (91037677)
# DESCRIPTION : gradle build & generate release package.
# ARGUMENTS   : NONE
# CREATION    : 2017-12-01
# -----------------------------------------------------------------------------
# Copyright(c) 2015 kt corp. All rights reserved.
# -----------------------------------------------------------------------------

PRG=`cd $(dirname $0); pwd`

if [ "$PRG" != `pwd` ]; then
  echo "This script must runs at it's own directory."
  exit 1
fi

./gradlew clean build -x test


REL=$PRG/_release
LIB=$REL/lib
RES=$REL/res

if [ ! -d $LIB ]; then
  mkdir -p $LIB
fi

if [ ! -d $RES ]; then
  mkdir -p $RES
fi

\cp -R $PRG/build/libs/*.jar $LIB
\cp -R $PRG/build/resources/main/* $RES
