/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Andres Almiray
 */

includeTargets << griffonScript('_GriffonCompile')

target(name: 'avro', description: 'Compile Avro sources', prehook: null, posthook: null) {
    depends(classpath)
    gensrcDir = "${projectWorkDir}/avro"
    gensrcDirPath = new File(gensrcDir)
    gensrcDirPath.mkdirs()
    gensrcDirPath = new File(gensrcDir, 'gen-java')
    gensrcDirPath.mkdirs()

    avrosrc = "${basedir}/src/avro"
    avrosrcDir = new File(avrosrc)
    if(avrosrcDir.exists()) {
        compileAvroSources()
    } else {
        ant.echo(message: "[avro] No avro sources found at $avrosrc")
    }
}

compileAvroSources = {
    boolean uptodate1 = checkSourcesUpToDate(avrosrcDir, "**/*.avpr")
    boolean uptodate2 = checkSourcesUpToDate(avrosrcDir, "**/*.avsc") 

    ant.taskdef(name: "protocol",
                classname: "org.apache.avro.compiler.specific.ProtocolTask")
    ant.taskdef(name: "schema",
                classname: "org.apache.avro.compiler.specific.SchemaTask")
    ant.taskdef(name: "paranamer",
                classname: "com.thoughtworks.paranamer.ant.ParanamerGeneratorTask")

    if(!uptodate1 || !uptodate2) {
        ant.echo(message: "[avro] Invoking Avro protocol+schema generators on $avrosrc")
        ant.echo(message: "[avro] Generated sources will be placed in ${gensrcDirPath.absolutePath}")

        ant.protocol(destdir: gensrcDirPath) {
            fileset(dir: avrosrcDir) {
                include(name: "**/*.avpr")
            }
        }
        ant.fileset(dir: avrosrcDir, includes: "**/*.avpr").each { avrofile ->
            File markerFile = new File(gensrcDirPath.absolutePath + File.separator + '.' + (avrofile.toString() - avrosrc).substring(1))
            ant.touch(file: markerFile)
        }

        ant.schema(destdir: gensrcDirPath) {
            fileset(dir: avrosrcDir) {
                include(name: "**/*.avsc")
            }
        }
        ant.fileset(dir: avrosrcDir, includes: "**/*.avsc").each { avrofile ->
            File markerFile = new File(gensrcDirPath.absolutePath + File.separator + '.' + (avrofile.toString() - avrosrc).substring(1))
            ant.touch(file: markerFile)
        }
    } else {
        ant.echo(message: "[avro] Sources are up to date")
    }

    ant.mkdir(dir: projectMainClassesDir)
    ant.echo(message: "[avro] Compiling generated sources to $projectMainClassesDir")
    try {
        String classpathId = "griffon.compile.classpath"
        compileSources(projectMainClassesDir, classpathId) {
            src(path: gensrcDirPath)
            javac(classpathref: classpathId)
        }
        ant.paranamer(sourceDirectory: gensrcDirPath,
                      outputDirectory: projectMainClassesDir)
    } catch (Exception e) {
        event("StatusFinal", ["Compilation error: ${e.message}"])
        ant.fail(message: "[avro] Could not compile generated sources: " + e.class.simpleName + ": " + e.message)
    }
}
setDefaultTarget('avro')

checkSourcesUpToDate = { dir, pattern ->
    boolean uptodate = true
    def skipIt = new RuntimeException()
    try {
        ant.fileset(dir: dir, includes: pattern).each { avrofile ->
            File markerFile = new File(gensrcDir+File.separator+'gen-java', "." + (avrofile.toString() - avrosrc).substring(1))
            if(!markerFile.exists() || avrofile.file.lastModified() > markerFile.lastModified()) throw skipIt
        }
    } catch(x) {
       if(x == skipIt) uptodate = false
       else throw x
    }
    uptodate
}