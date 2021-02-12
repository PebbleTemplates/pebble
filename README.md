# Pebble ![Continuous Integration](https://api.travis-ci.com/PebbleTemplates/pebble.svg?branch=master)

Pebble is a java templating engine inspired by [Twig](http://twig.sensiolabs.org/). It separates itself from the crowd with its inheritance feature and its easy-to-read syntax. It ships with built-in autoescaping for security, and it includes integrated support for internationalization.

For more information please visit the [website](https://pebbletemplates.io).

# Artifact id renaming for pebble-spring-boot-starter 
As of version 3.1.0 and in order to follow this naming [recommendation](https://github.com/spring-projects/spring-boot/wiki/Building-On-Spring-Boot#naming), the artifactId of pebble-spring-boot-starter has been renamed as is:

| Old artifactId | New artifactId | spring-boot version |
| --- | --- | --- |
| pebble-spring-boot-starter | pebble-legacy-spring-boot-starter | 1.5.x |
| pebble-spring-boot-2-starter | pebble-spring-boot-starter | 2.x.x |

# New group id
Please note that the pebble's groupId has been updated as of version 2.5.0
```
<dependency>
	<groupId>io.pebbletemplates</groupId>
	<artifactId>pebble</artifactId>
	<version>3.1.5</version>
</dependency>
```

## License

    Copyright (c) 2013 by Mitchell Bösecke

    Some rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are
    met:
    
        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
    
        * Redistributions in binary form must reproduce the above
          copyright notice, this list of conditions and the following
          disclaimer in the documentation and/or other materials provided
          with the distribution.
    
        * The names of the contributors may not be used to endorse or
          promote products derived from this software without specific
          prior written permission.
    
    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
    "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
    LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
    A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
    OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
    SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
    LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
    DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
    THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
    OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
