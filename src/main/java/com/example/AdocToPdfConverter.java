package com.example;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.AttributesBuilder;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AdocToPdfConverter {
    public static void main(String[] args) {
        String[] inputFiles = {"document1.adoc", "document2.adoc"};

        try (Asciidoctor asciidoctor = Asciidoctor.Factory.create()) {
            for (String inputFile : inputFiles) {
                String outputFile = inputFile.replace(".adoc", ".pdf");

                Map<String, Object> attributes = AttributesBuilder.attributes()
                        .tableOfContents(true)
                        .tableOfContents("left")
                        .asMap();

                OptionsBuilder options = OptionsBuilder.options()
                        .safe(SafeMode.UNSAFE)
                        .backend("pdf")
                        .attributes(attributes)
                        .inPlace(false)
                        .toFile(new File(outputFile));

                asciidoctor.convertFile(new File(inputFile), options);
                System.out.println("Converted " + inputFile + " to " + outputFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}