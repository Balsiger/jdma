#!/usr/bin/python
# merge ids and names in a dma file

import re
import shutil
import sys

filename = sys.argv[1]

f = open(filename, 'r')
lines = f.readlines()
f.close()

print "reading file...", filename

# fine the lines matching an id
for i, line in enumerate(lines):
  id_match = re.match(r"^\s*id\s+([A-Z]+)\s*(?:;|.)", line)
  if id_match:
    entry = id_match.group(1)

    # fine the line before that matching a name
    for j in range(i, -1, -1):
      name_match = re.match(r"^(\s*\w+\s+)(.*?)(\s*=.*)", lines[j])

      if name_match:
        name = name_match.group(2)
        print "replacing", entry
        lines[j] = re.sub(r"^(\s*\w+\s+)(.*?)(\s*=.*)", r"\1" + entry + r"\3",
                          lines[j])
        lines[i] = re.sub(r"^(\s*)id(?:  )?(\s+)[A-Z]+(\s*(?:;|.))",
                          r"\1base\2" + name + r"\3", lines[i])
        break;

# backup the old file, just in case
print "backing up file..."
shutil.move(filename, filename + ".rebase")

print "writing file..."
f = open(filename, 'w')
for line in lines:
  f.write(line)

print "all done!"
