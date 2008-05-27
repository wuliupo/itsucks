#!/bin/bash

TARGET_DIR=$1
echo "Target directory: $TARGET_DIR"

cp ../itsucks-base/target/itsucks-base*.jar $TARGET_DIR
cp ../itsucks-core/target/itsucks-core*.jar $TARGET_DIR
cp ../itsucks-gui/target/itsucks-gui*.jar $TARGET_DIR
cp ../itsucks-console/target/itsucks-console*.jar $TARGET_DIR
cp ../itsucks-vmcheck/target/itsucks-vmcheck*.jar $TARGET_DIR

cp ../itsucks-help/itsucks-help.jar $TARGET_DIR
