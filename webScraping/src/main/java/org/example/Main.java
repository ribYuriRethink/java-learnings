package org.example;

import org.example.entity.Administrative;
import org.example.entity.Teacher;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Teacher> allTeachers = new ArrayList<>();
        getTeachers(allTeachers );
        getAdministrative(allTeachers);

        printAllTeachers(allTeachers);
//        printTeachersNames(filterTeacherByField("Engenharia Organizacional", allTeachers));
    }

    private static void printAllTeachers(List<Teacher> allTeachers) {
        for (Teacher teacher : allTeachers) {
            System.out.println(teacher);
        }
    }

    private static void printTeachersNames(List<Teacher> allTeachers) {
        for (Teacher teacher : allTeachers) {
            System.out.println(teacher.getName());
        }
    }

    public static List<Teacher> filterTeacherByField(String field, List<Teacher> allTeachers) {
        List<Teacher> teachersFiltered = new ArrayList<>();
        for (Teacher teacher: allTeachers) {
            if (teacher.getField().toLowerCase().contains(field.toLowerCase())) teachersFiltered.add(teacher);
        }
        return teachersFiltered;
    }

    private static void getTeachers(List<Teacher> allTeachers) {
        Document doc = getDocumentConnection("https://decsi.ufop.br/docentes");
        Elements h3Elements = doc.select("h3");

        for (Element element : h3Elements) {
            Teacher teacher = new Teacher();
            teacher.setName(element.text());
            element = element.nextElementSibling();

            if (teacher.getName().matches("Bruno Cerqueira Hott")) {
                element = element.firstElementChild();
            }

            if (element.childrenSize() > 3) {
                teacher.setEmail(element.selectFirst("a").text());
                teacher.setField(element.lastElementChild().text().replaceAll("Linha de pesquisa:", ""));
            } else {
                teacher.setEmail(element.selectFirst("a").text());
                element = element.nextElementSibling().nextElementSibling();
                teacher.setField(element.text().replaceAll("Linha de pesquisa:", ""));
            }

            allTeachers.add(teacher);
        }

        Element outlier = doc.selectXpath
                ("//*[@id=\"node-3229\"]/div/div/div/div/div[14]/div[4]/div[4]/div[7]/div[7]/div[7]/div[4]/div[4]/div[25]/div[4]/div[4]/div[7]/div[7]/div[7]/div[4]/div[4]/p")
                .get(0);
        allTeachers.add(new Teacher(outlier.text(),
                outlier.nextElementSibling().selectFirst("a").text(),
                outlier.nextElementSibling().lastElementChild().text().replaceAll("Linha de pesquisa:", "")));
    }

    private static void getAdministrative(List<Teacher> allTeachers) {
        Document doc = getDocumentConnection("https://deenp.ufop.br/corpo-docente");
        Elements trElements = doc.select("tr").next();
        for (Element element : trElements) {
            Administrative administrative = new Administrative();
            element = element.firstElementChild();
            administrative.setName(element.text());
            element = element.nextElementSibling().nextElementSibling().nextElementSibling();
            administrative.setField(element.text());
            element = element.nextElementSibling();
            administrative.setBranch_line(element.text());
            element = element.nextElementSibling();
            administrative.setRoom(element.text());
            element = element.nextElementSibling();
            administrative.setEmail(element.text());

            allTeachers.add(administrative);
        }
    }

    private static Document getDocumentConnection(String url) {
        Document doc;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return doc;
    }
}