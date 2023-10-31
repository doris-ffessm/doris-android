#!/bin/bash

# simple scripts using sqlite3  to print a markdown summary of the database


# The first argument passed to the script is the filename
dbfilename=$1

# show table list
# sqlite3 DorisAndroidPrefetch/run/database/DorisAndroid.db ".tables"
echo "| Table  | Count |"
echo "|---|---|"
echo "| fiche | `sqlite3 $dbfilename \"SELECT COUNT(*) FROM fiche;\"` |"
echo "| participant | `sqlite3 $dbfilename \"SELECT COUNT(*) FROM participant;\"` |"
echo "| zoneGeographique | `sqlite3 $dbfilename \"SELECT COUNT(*) FROM zoneGeographique;\"` |"
echo "| groupe | `sqlite3 $dbfilename \"SELECT COUNT(*) FROM groupe;\"` |"
echo "| definitionGlossaire | `sqlite3 $dbfilename \"SELECT COUNT(*) FROM definitionGlossaire;\"` |"
echo "| entreeBibliographie | `sqlite3 $dbfilename \"SELECT COUNT(*) FROM entreeBibliographie;\"` |"
echo "| photoFiche | `sqlite3 $dbfilename \"SELECT COUNT(*) FROM photoFiche;\"` |"



