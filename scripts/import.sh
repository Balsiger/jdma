#!/bin/sh

curl "http://www.ixitxachitls.net/$1.dma?deep&body" -o build/war/WEB-INF/classes/import.dma
java net.ixitxachitls.dma.server.Importer -i -n import.dma
