package com.leroymerlin.pandroid.plugins.utils

import groovy.util.slurpersupport.GPathResult
import groovy.xml.XmlUtil
/**
 * Created by florian on 18/02/16.
 */
public class XMLUtils {

    static public void appendToXML(File file, String xmlToAdd) {
        def parser = new XmlSlurper(false, false)
        def xml = parser.parse(file);
        def newXml = parser.parseText(xmlToAdd);

        xml = mergeXML(xml, newXml);


        def writer = new PrintWriter(new FileWriter(file))
        XmlUtil.serialize(xml, writer)
    }


    static private GPathResult mergeXML(GPathResult manifestXML, GPathResult newXML) {

        if (newXML == null)
            return manifestXML

        if (manifestXML == null)
            return newXML

        newXML.getProperties().each { key, value ->

            if (manifestXML."@$key" == null)
                manifestXML.setProperty(key, value)

        }

        newXML.children().each {
            GPathResult node ->

                GPathResult mNode = manifestXML.children().find { GPathResult oldNode ->
                    def nameEqual = oldNode.name().equals(node.name())
                    String attribute = oldNode.'@android:name'
                    String attribute1 = node.'@android:name'

                    boolean nameAttributEqual = attribute.isEmpty() || attribute1.isEmpty() || attribute.equals(attribute1)
                    return nameAttributEqual && nameEqual

                }


                if (mNode.size() > 0) {
                    mNode.replaceNode{
                        mkp.yield(mergeXML(mNode, node))
                    }
                } else {
                    manifestXML.appendNode(node)
                }
        }
        return manifestXML
    }
}
