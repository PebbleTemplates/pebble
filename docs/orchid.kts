@file:MavenRepository("kotlinx", "https://kotlin.bintray.com/kotlinx")
@file:MavenRepository("orchid", "https://dl.bintray.com/javaeden/Orchid/")
@file:MavenRepository("jitpack", "https://jitpack.io")

@file:DependsOn("com.github.holgerbrandl:kscript-annotations:1.2")

@file:DependsOn("io.github.javaeden.orchid:OrchidCore:0.12.12")
@file:DependsOn("io.github.javaeden.orchid:OrchidEditorial:0.12.12")
@file:DependsOn("io.github.javaeden.orchid:OrchidSearch:0.12.12")
@file:DependsOn("io.github.javaeden.orchid:OrchidPages:0.12.12")
@file:DependsOn("io.github.javaeden.orchid:OrchidWiki:0.12.12")
@file:DependsOn("io.github.javaeden.orchid:OrchidJavadoc:0.12.12")
@file:DependsOn("io.github.javaeden.orchid:OrchidPluginDocs:0.12.12")
@file:DependsOn("io.github.javaeden.orchid:OrchidSyntaxHighlighter:0.12.12")

import com.eden.common.util.EdenUtils
import com.eden.orchid.Orchid
import com.eden.orchid.StandardModule
import java.io.FileInputStream
import java.util.Properties

/*
To run Orchid, install kscript (https://github.com/holgerbrandl/kscript), then run the following command:

kscript orchid.kts serve
 */

val flags = HashMap<String, Any?>()

if (!EdenUtils.isEmpty(System.getenv("GITHUB_TOKEN"))) {
    flags["githubToken"] = System.getenv("GITHUB_TOKEN")
}
else {
    try {
        val gradleProperties = Properties()
        gradleProperties.load(FileInputStream(System.getProperty("user.home") + "/.gradle/gradle.properties"))
        flags["githubToken"] = gradleProperties.getProperty("github_token")
    }
    catch (e: Exception) {
    }
}

flags["theme"] = "Editorial"
flags["src"] = "src/orchid/resources"
flags["dest"] = "build/orchid"
flags["version"] = "3.0.5"
flags["baseUrl"] = "https://cjbrooks12.github.io/pebble"

val modules = listOf(StandardModule.builder()
        .args(args)
        .flags(flags)
        .build()
)
Orchid.getInstance().start(modules)
