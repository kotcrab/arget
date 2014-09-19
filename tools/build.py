import os
import re
import sys
import shutil
from subprocess import call

import check_license

# TODO check for license headres befroe build

# config
JET_FOLDER = 'F:\Arget\JET'
# config end

main_dir = os.path.dirname(os.getcwd())

pom = os.path.join(main_dir, 'pom.xml')
maven_out = os.path.join(main_dir, 'target')

out = os.path.join(os.getcwd(), 'build')
jar_out = os.path.join(out, 'jar')

def get_pom_version():
    with open(pom, 'r') as file:
        data = file.read().replace('\n', '')

        version = re.search('<version>(.+?)</version>', data)

        if version:
            return version.groups()[0]
        else:
            print 'Failed to read version from pom file!'
            exit(-1)

def build_maven():
    if os.path.exists(out):
        shutil.rmtree(out)
    
    os.mkdir(out)
    os.mkdir(jar_out)
    
    maven_return = call(['mvn', '-f', pom, 'clean', 'package', 'dependency:copy-dependencies'], shell=True)

    if maven_return != 0:
        print 'Maven build failed!'
        exit(-1)
        
    print 'Maven build finished, copying files...'
    
    shutil.copy(os.path.join(maven_out, 'arget-' + version + '.jar'), jar_out)
    shutil.copytree(os.path.join(maven_out, 'lib'), os.path.join(jar_out, 'lib'))
    
    print 'Done.'


version = get_pom_version()
missing_headers_count = check_license.count_missing_headers()

print 'Arget Build Tool v1.0 (building: ' + version + ')'
print ''

if missing_headers_count > 0:
    print 'WARNING: Missing license headers! (' + str(missing_headers_count) + ')'
    print ''

print 'Select build:'
print '1: Cleanup and Maven build'
print '2: JET native build'
print '3: Windows installer'


option = raw_input('Build:')

if option == '1':
    build_maven()
elif option == '2':
    print 'Not implemented yet!'
elif option == '3':
    print 'Not implemented yet!'
else:
    print 'Invalid build'
