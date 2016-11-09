/**
 * Created by SerP on 07.11.2016.
 */
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.stream.StreamSource;

public class XMLConcat {
    static ArrayList<HashSet<String>> listType = new ArrayList<HashSet<String>>();
    static ArrayList<String > nameFlow = new ArrayList<String>();
    public static void main(String[] args) throws Throwable {
        XMLInputFactory xmlInFactory = XMLInputFactory.newFactory();
        File dir = new File("ub_adapters");
        //File[] rootFiles = dir.listFiles();
        Writer outputWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("CacheValidate.xml"), "UTF-8"));
        XMLOutputFactory xmlOutFactory = XMLOutputFactory.newFactory();
        XMLEventWriter xmlEventWriter = xmlOutFactory.createXMLEventWriter(outputWriter);
        XMLEventFactory xmlEventFactory = XMLEventFactory.newFactory();

        //??????? ????????? ????? ?????????
        xmlEventWriter.add(xmlEventFactory.createStartDocument());
        xmlEventWriter.add(xmlEventFactory.createStartElement("", null, "ControlMessage"));
        xmlEventWriter.add(xmlEventFactory.createStartElement("", null, "ValidateMessage"));
        xmlEventWriter.add(xmlEventFactory.createStartElement("", null, "DefinitionElement"));
        xmlEventWriter.add(xmlEventFactory.createStartElement("", null, "ExecutionGroups"));
        xmlEventWriter.add(xmlEventFactory.createStartElement("", null, "ExecutionGroup"));
        xmlEventWriter.add(xmlEventFactory.createAttribute("name", dir.getName()));
        xmlEventWriter.add(xmlEventFactory.createStartElement("", null, "Flows"));

        File[] flowFiles = dir.listFiles();
        System.out.println("-------Build Cache validation for ESB UB-------");
        System.out.println();
        checkFile(flowFiles, xmlInFactory, xmlEventWriter, xmlEventFactory);

        xmlEventWriter.add(xmlEventFactory.createEndElement("", null, "Flows"));
        xmlEventWriter.add(xmlEventFactory.createEndElement("", null, "ExecutionGroup"));
        xmlEventWriter.add(xmlEventFactory.createEndElement("", null, "ExecutionGroups"));
        xmlEventWriter.add(xmlEventFactory.createEndElement("", null, "DefinitionElement"));

        //?????????? ?????
        xmlEventWriter.add(xmlEventFactory.createStartElement("", null, "DefinitionType"));
        xmlEventWriter.add(xmlEventFactory.createStartElement("", null, "ExecutionGroups"));
        xmlEventWriter.add(xmlEventFactory.createStartElement("", null, "ExecutionGroup"));
        xmlEventWriter.add(xmlEventFactory.createAttribute("name", dir.getName()));
        xmlEventWriter.add(xmlEventFactory.createStartElement("", null, "Flows"));

        addIncludeTypeInDefinitionType(flowFiles, xmlEventFactory, xmlEventWriter);

        xmlEventWriter.add(xmlEventFactory.createEndElement("", null, "Flows"));
        xmlEventWriter.add(xmlEventFactory.createEndElement("", null, "ExecutionGroup"));
        xmlEventWriter.add(xmlEventFactory.createEndElement("", null, "ExecutionGroups"));
        //----------------------------------------------------

        //?????????? ?????? ?????
        xmlEventWriter.add(xmlEventFactory.createStartElement("", null, flowFiles[flowFiles.length - 1].getName()));
        addTypeList(flowFiles[flowFiles.length - 1].listFiles(),xmlInFactory, xmlEventWriter, xmlEventFactory);
        xmlEventWriter.add(xmlEventFactory.createEndElement("", null, flowFiles[flowFiles.length - 1].getName()));
        //----------------------------------------------------
        xmlEventWriter.add(xmlEventFactory.createEndElement("", null, "DefinitionType"));
        xmlEventWriter.add(xmlEventFactory.createEndElement("", null, "ValidateMessage"));
        xmlEventWriter.add(xmlEventFactory.createEndElement("", null, "ControlMessage"));
        xmlEventWriter.add(xmlEventFactory.createEndDocument());

        xmlEventWriter.close();
        outputWriter.close();
        System.out.println();
        System.out.println("SUCCESSFULLY!!!");
    }

    public static void checkFile(File[] file, XMLInputFactory xmlInFactory, XMLEventWriter xmlEventWriter, XMLEventFactory xmlEventFactory) throws XMLStreamException {
        for (int i = 0; i < file.length - 1; i++) {
            if (!file[i].isFile()){
                xmlEventWriter.add(xmlEventFactory.createStartElement("", null, "Flow"));
                xmlEventWriter.add(xmlEventFactory.createAttribute("name", file[i].getName()));
                //????????? ???????? ?????? ? ??????
                nameFlow.add(file[i].getName());
                HashSet<String> hashSet = new HashSet<String>();
                System.out.println("Flow " + file[i].getName() + ":");
                createService(file[i].listFiles(), xmlInFactory, xmlEventWriter, xmlEventFactory, hashSet);
                listType.add(hashSet);
                xmlEventWriter.add(xmlEventFactory.createEndElement("", null, "Flow"));
                // checkFile(rootFile.listFiles(), xmlInFactory, xmlEventWriter, xmlEventFactory);
                continue;
            }
        }
    }

    public static void createService(File[] file, XMLInputFactory xmlInFactory, XMLEventWriter xmlEventWriter, XMLEventFactory xmlEventFactory, HashSet<String> hashSet) throws XMLStreamException {
        for (File rootFile : file) {
            if (!rootFile.isFile()){

                for (File rootFileVer : rootFile.listFiles()) {
                    xmlEventWriter.add(xmlEventFactory.createStartElement("", null, "Service"));
                    xmlEventWriter.add(xmlEventFactory.createAttribute("name", rootFile.getName()));
                    xmlEventWriter.add(xmlEventFactory.createAttribute("version", rootFileVer.getName()));

                    addXSD(rootFileVer.listFiles(),  xmlInFactory, xmlEventWriter, xmlEventFactory, hashSet);

                    xmlEventWriter.add(xmlEventFactory.createEndElement("", null, "Service"));
                    System.out.println("     Added schema: " + rootFile.getName()+ "_v" + rootFileVer.getName());
                }
            }
        }
    }

    public static void addXSD(File[] file, XMLInputFactory xmlInFactory, XMLEventWriter xmlEventWriter, XMLEventFactory xmlEventFactory, HashSet<String> hashSet) throws XMLStreamException {
        if (file.length != 0) {
            xmlEventWriter.add(xmlEventFactory.createStartElement("", null, "IncludeTypes"));
            xmlEventWriter.add(xmlEventFactory.createStartElement("", null, "Type"));
            String includeType = addIncludeType(file, xmlInFactory);
            hashSet.add(includeType);
            xmlEventWriter.add(xmlEventFactory.createCharacters(includeType));
            xmlEventWriter.add(xmlEventFactory.createEndElement("", null, "Type"));
            xmlEventWriter.add(xmlEventFactory.createEndElement("", null, "IncludeTypes"));

            xmlEventWriter.add(xmlEventFactory.createStartElement("", null, "Xsd"));
            for (File rootFile : file) {
                XMLEventReader xmlEventReader = xmlInFactory.createXMLEventReader(new StreamSource(rootFile));
                XMLEvent event = xmlEventReader.nextEvent();
                // Skip ahead in the input to the opening document element
                while (event.getEventType() != XMLEvent.START_ELEMENT) {
                    event = xmlEventReader.nextEvent();
                }

                do {
                    xmlEventWriter.add(event);
                    event = xmlEventReader.nextEvent();
                } while (event.getEventType() != XMLEvent.END_DOCUMENT);
                xmlEventReader.close();
            }
            xmlEventWriter.add(xmlEventFactory.createEndElement("", null, "Xsd"));
        }
    }
    public static String addIncludeType(File[] file, XMLInputFactory xmlInFactory) throws XMLStreamException {
        for (File rootFile : file) {
            XMLEventReader xmlEventReader = xmlInFactory.createXMLEventReader(new StreamSource(rootFile));
            XMLEvent event;
            for (int i = 0; i < 15; i++) {
                event = xmlEventReader.nextEvent();
                String strData = event.toString();
                if (strData.contains(".xsd") && strData.contains("/Types/")){
                    xmlEventReader.close();
                    int count = strData.split("/").length;
                    int length = strData.split("/")[count-1].length();
                    String includeType = strData.split("/")[count-1].substring(0,length-6);
                    return includeType;
                }else if (strData.contains(".xsd") && strData.contains("\\Types\\")){
                    xmlEventReader.close();
                    int count = strData.split("\\\\").length;
                    int length = strData.split("\\\\")[count-1].length();
                    String includeType = strData.split("\\\\")[count-1].substring(0,length-6);
                    return includeType;
                }
            }
        }
        return null;
    }

    public static void addIncludeTypeInDefinitionType(File[] file, XMLEventFactory xmlEventFactory,XMLEventWriter xmlEventWriter) throws XMLStreamException {
        for (int i = 0; i < file.length-1; i++) {
            xmlEventWriter.add(xmlEventFactory.createStartElement("", null, "Flow"));
            xmlEventWriter.add(xmlEventFactory.createAttribute("name", file[i].getName()));
            xmlEventWriter.add(xmlEventFactory.createStartElement("", null, "IncludeTypes"));
            for (String s : listType.get(i)) {
                xmlEventWriter.add(xmlEventFactory.createStartElement("", null, "Type"));
                xmlEventWriter.add(xmlEventFactory.createCharacters(s));
                xmlEventWriter.add(xmlEventFactory.createEndElement("", null, "Type"));
            }
            xmlEventWriter.add(xmlEventFactory.createEndElement("", null, "IncludeTypes"));
            xmlEventWriter.add(xmlEventFactory.createEndElement("", null, "Flow"));
        }
    }

    public static void addTypeList(File[] file, XMLInputFactory xmlInFactory, XMLEventWriter xmlEventWriter, XMLEventFactory xmlEventFactory) throws XMLStreamException {
        for (File rootFile : file) {
            xmlEventWriter.add(xmlEventFactory.createStartElement("", null, "Type"));
            xmlEventWriter.add(xmlEventFactory.createAttribute("name", rootFile.getName()));
            xmlEventWriter.add(xmlEventFactory.createStartElement("", null, "Xsd"));
            for (File schema : rootFile.listFiles()) {

                XMLEventReader xmlEventReader = xmlInFactory.createXMLEventReader(new StreamSource(schema));
                XMLEvent event = xmlEventReader.nextEvent();
                // Skip ahead in the input to the opening document element
                while (event.getEventType() != XMLEvent.START_ELEMENT) {
                    event = xmlEventReader.nextEvent();
                }

                do {
                    xmlEventWriter.add(event);
                    event = xmlEventReader.nextEvent();
                } while (event.getEventType() != XMLEvent.END_DOCUMENT);
                xmlEventReader.close();

            }
            xmlEventWriter.add(xmlEventFactory.createEndElement("", null, "Xsd"));
            xmlEventWriter.add(xmlEventFactory.createEndElement("", null, "Type"));
        }
    }
}