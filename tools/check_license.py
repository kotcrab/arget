import fnmatch
import os

# config
main_dir = os.path.dirname(os.getcwd())
src_dir = os.path.join(main_dir, 'src', 'pl', 'kotcrab', 'arget')
header = os.path.join(main_dir, 'src', 'data', 'license_header.txt')

def contains(small, big):
    if(len(small) > len(big)):
        return False
    
    for i in xrange(len(small) - 1):
        if not small[i] == big[i]:
            return False
        
    return True

def process_file(file_path):
    global missing
    file = open(file_path)
    source = file.readlines()
    file.close()
    
    if not contains(header_lines, source):
        missing += 1
        print 'WARNING: Missing header: ' + file_path

missing = 0

f = open(header)
header_lines = f.readlines()
f.close()

for root, dirnames, filenames in os.walk(main_dir):
  for filename in fnmatch.filter(filenames, '*.java'):
      process_file(os.path.join(root, filename))
      
print 'Done, missing: ' + str(missing)
