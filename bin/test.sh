#!/bin/bash

groovy -cp src:lib/junit-4.11.jar:lib/serializer.jar:lib/xalan.jar:lib/xmlunit-core-2.0alpha.jar:lib/xmlunit-hamcrest-2.0alpha.jar:lib/xmlunit-legacy-2.0alpha.jar:lib/xmlunit-sumo-2.0alpha.jar test/RanorexToJunitConverterTest
