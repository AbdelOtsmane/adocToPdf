package com.example;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Asciidoctor.Factory;
import org.asciidoctor.Options;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;

public class AsciidocToPdfConverter {

    public static void main(String[] args) {
        // Vérifie si le chemin du répertoire source est fourni
        if (args.length < 1) {
            System.out.println("Utilisation : java -jar asciidoc-to-pdf.jar <chemin_du_repertoire_source>");
            return;
        }

        // Récupère le répertoire source depuis les arguments
        File sourceDir = new File(args[0]);

        // Vérifie si le répertoire existe
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            System.out.println("Erreur : le répertoire source " + sourceDir.getAbsolutePath() + " n'existe pas ou n'est pas un répertoire.");
            return;
        }

        // Crée une instance d'Asciidoctor
        Asciidoctor asciidoctor = Factory.create();

        try {
            // Filtre pour ne traiter que les fichiers .adoc
            FilenameFilter adocFilter = (dir, name) -> name.toLowerCase().endsWith(".adoc");
            File[] adocFiles = sourceDir.listFiles(adocFilter);

            if (adocFiles != null && adocFiles.length > 0) {
                // Crée un fichier temporaire pour concaténer le contenu
                File tempAdocFile = new File(sourceDir, "temp_combined.adoc");
                try (PrintWriter writer = new PrintWriter(tempAdocFile)) {
                    for (File adocFile : adocFiles) {
                        // Lit le contenu de chaque fichier .adoc et l'écrit dans le fichier temporaire
                        String content = readFile(adocFile);
                        writer.println(content);
                        writer.println("\n"); // Ajoute une nouvelle ligne entre les fichiers
                    }
                }

                // Détermine le nom du fichier PDF de sortie
                File pdfFile = new File(sourceDir, "combined_output.pdf");

                // Configure les options pour la sortie en PDF
                Options options = OptionsBuilder.options()
                        .backend("pdf")
                        .safe(SafeMode.UNSAFE)
                        .toFile(pdfFile)
                        .get();

                // Convertit le fichier temporaire .adoc en .pdf
                asciidoctor.convertFile(tempAdocFile, options);
                System.out.println("Conversion terminée : " + pdfFile.getAbsolutePath());

                // Supprime le fichier temporaire
                tempAdocFile.delete();
            } else {
                System.out.println("Aucun fichier .adoc trouvé dans le répertoire " + sourceDir.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("Une erreur est survenue lors de la conversion : " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Libère les ressources
            asciidoctor.shutdown();
        }
    }

    // Méthode pour lire le contenu d'un fichier
    private static String readFile(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
}
