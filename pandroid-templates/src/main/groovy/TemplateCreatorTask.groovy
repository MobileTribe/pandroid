import groovy.xml.MarkupBuilder
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class TemplateCreatorTask extends DefaultTask {

    @Input
    String templateName;

    @Input
    String description = "";

    @Input
    String category = "Pandroid";

    @Input
    String formfactor = "Mobile";

    @InputFiles
    FileCollection javaFiles;

    @Input
    FileCollection resourceFiles;

    final NamedDomainObjectContainer<ParameterData> parametersContainer;

    TemplateCreatorTask() {
        super()
        parametersContainer = this.project.container(ParameterData);
    }


    void parameters(Action<NamedDomainObjectContainer<ParameterData>> action) {
        action.execute(parametersContainer)
    }

    @OutputDirectory
    File getOutputDir() {
        if (!templateName) {
            throw new IllegalStateException("templateName should be defined in ${this.name}")
        }
        File outputDir = project.file('build/templates/' + templateName)
        return outputDir;
    }

    LinkedHashMap replaceMap;

    @TaskAction
    public void generateTemplate() {
        File outputDir = getOutputDir();
        if (outputDir.exists()) {
            outputDir.deleteDir()
        }
        outputDir.mkdirs()

        replaceMap = new LinkedHashMap()
        parametersContainer.each {
            if (it.replace) {
                replaceMap.put(it.name, it.replace)
            }
        }
        replaceMap = replaceMap.sort { a, b -> b.value <=> a.value }

        writeGlobals()
        writeRecipe()
        writeTemplate()

        javaFiles.each { copyFile(it, new File(outputDir, "root/src/app_package/" + it.name + ".ftl"))}

        resourceFiles.each { copyFile(it, new File(outputDir, "root/src/app_package/" + it.name + ".ftl")) }
    }


    private String replaceString(String name) {
        replaceMap.each {
            name = escapePackage(name.replaceAll(it.value, '\\$\\{' + it.key + '\\}'))
        }
        return name;
    }


    private String escapePackage(String name) {
        return name.replaceAll(/import com.leroymerlin.pandroid.demo.([^;]+);/) {
            all, className ->
                '''
<#if applicationPackage??>
import ${applicationPackage}.''' + className + ''';
</#if>
'''
        }
    }

    private void copyFile(File from, File to) {
        to.parentFile.mkdirs()
        to << replaceString(from.text)
    }

    private void writeRecipe() {
        def xml = {
            recipe() {
                String openFile;
                javaFiles.each { file ->
                    def path = '${escapeXmlAttribute(srcOut)}/' + replaceString(file.name)
                    if (!openFile) {
                        openFile = path;
                    }
                    instantiate(from: 'root/src/app_package/' + file.name + ".ftl", to: path)
                }
                resourceFiles.each { file ->
                    def path = '${escapeXmlAttribute(resOut)}/' + file.parentFile.name + "/" + replaceString(file.name)
                    if (!openFile) {
                        openFile = path;
                    }
                    instantiate(from: 'root/res/' + file.parentFile.name + '/' + file.name, to: path)
                }
                if (openFile) {
                    open(file: openFile)
                }
            }
        }
        writeFile('recipe.xml.ftl', xml)
    }


    private void writeTemplate() {
        def xml = {
            template(format: '5',
                    revision: project.version,
                    name: templateName,
                    description: description,
                    minApi: project.androidMinSdkVersion,
                    minBuildApi: project.androidMinSdkVersion) {

                category(value: category)
                formfactor(value: formfactor)

                parametersContainer.findAll { it.label }.each {
                    parameter(id: it.name,
                            name: it.label,
                            type: 'string',
                            constraints: it.constraints,
                            default: it.suggest,
                            help: it.help)
                }

                globals(file: "globals.xml.ftl")
                execute(file: "recipe.xml.ftl")
            }
        }
        writeFile('template.xml', xml)
    }

    private void writeGlobals() {
        def xml = {
            globals() {
                parametersContainer.findAll { !it.label }.each {
                    item ->
                        global(id: item.name, value: item.value)
                }
            }
        }
        writeFile('globals.xml.ftl', xml)
    }


    private void writeFile(String path, Closure closure) {
        def builder = new StreamingMarkupBuilder()
        builder.encoding = "UTF-8"
        new File(getOutputDir(), path) << XmlUtil.serialize(
                builder.bind(closure << { mkp.xmlDeclaration() })
        )
    }


}


class ParameterData {
    String name;
    String label;
    String replace;
    String constraints="";
    String suggest="";
    String help="";
    String value

    ParameterData(String name) {
        this.name = name;
    }
}