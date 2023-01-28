# Pebble ![Continuous Integration](https://api.travis-ci.com/PebbleTemplates/pebble.svg?branch=master)

Pebble is a java templating engine inspired by [Twig](https://twig.symfony.com/). It separates itself from the crowd with its inheritance feature and its easy-to-read syntax. It ships with built-in autoescaping for security, and it includes integrated support for internationalization.

For more information please visit the [website](https://pebbletemplates.io).

# Artifact id renaming for pebble-spring-boot-starter 
As of version 3.1.0 and in order to follow this naming [recommendation](https://github.com/spring-projects/spring-boot/wiki/Building-On-Spring-Boot#naming), the artifactId of pebble-spring-boot-starter has been renamed. Please
use one of the following  artifactId according to the spring boot version that you are using 

| ArtifactId                        | spring-boot version |
|-----------------------------------|---------------------|
| pebble-legacy-spring-boot-starter | 2.x.x               |
| pebble-spring-boot-starter        | 3.x.x               |

# Breaking changes in version 3.2.x
- Rename package from `com.mitchellbosecke` to `io.pebbletemplates`
- Change default suffix to `.peb` instead of `.pebble` in spring boot autoconfiguration
- Rename method `getInstance` to `createInstance` in `BinaryOperator` interface (#521)


## License

    Copyright (c) 2013, Mitchell Bösecke
    All rights reserved.
    
    Redistribution and use in source and binary forms, with or without modification,
    are permitted provided that the following conditions are met:
    
    * Redistributions of source code must retain the above copyright notice, this
      list of conditions and the following disclaimer.
    
    * Redistributions in binary form must reproduce the above copyright notice, this
      list of conditions and the following disclaimer in the documentation and/or
      other materials provided with the distribution.
    
    * Neither the name of the {organization} nor the names of its
      contributors may be used to endorse or promote products derived from
      this software without specific prior written permission.
    
    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
    ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
    WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
    DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
    ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
    (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
    LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
    ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
    SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
