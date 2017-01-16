#!/bin/bash

if [ $# == 0 ]
then
  echo "Need the url to the entry as parameter"
  exit 1
fi

name=$1
file=build/imports/$1.ascii

dir=dirname "$file"
echo "creating directory $dir"
mkdir -p $dir

url=http://www.ixitxachitls.net/$1.proto?body
url=`echo $url | sed -e 's/ /%20/g'`
echo "requesting url $url to $file"
curl "$url" -o "$file"

# delete two first and two last line (html body and pre)
sed -e '$ d' -i=tmp "$file"
sed -e '$ d' -i=tmp "$file"
sed -e '1,1d' -i=tmp "$file"
sed -e '1,1d' -i=tmp "$file"

source .classpath
java net.ixitxachitls.dma.server.Importer -i --dev -a "$file"
