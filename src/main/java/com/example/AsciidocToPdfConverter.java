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
        // Définir les chemins des répertoires à parcourir
        String[] modulePaths = {
            "module1/doc",
            "module2/doc"
        };

        // Crée une instance d'Asciidoctor
        Asciidoctor asciidoctor = Factory.create();
        File tempAdocFile = new File("temp_combined.adoc"); // Fichier temporaire

        try (PrintWriter writer = new PrintWriter(tempAdocFile)) {
            // Parcours chaque répertoire spécifié
            for (String modulePath : modulePaths) {
                File moduleDir = new File(modulePath);

                // Vérifie si le répertoire existe
                if (!moduleDir.exists() || !moduleDir.isDirectory()) {
                    System.out.println("Erreur : le répertoire " + moduleDir.getAbsolutePath() + " n'existe pas ou n'est pas un répertoire.");
                    continue;
                }

                // Trouve et lit les fichiers .adoc dans le répertoire
                findAndReadAdocFiles(moduleDir, writer);
            }
        } catch (IOException e) {
            System.out.println("Une erreur est survenue lors de la création du fichier temporaire : " + e.getMessage());
            return;
        }

        try {
            // Détermine le nom du fichier PDF de sortie
            File pdfFile = new File("combined_output.pdf");

            // Configure les options pour la sortie en PDF
            Options options = OptionsBuilder.options()
                    .backend("pdf")
                    .safe(SafeMode.UNSAFE)
                    .toFile(pdfFile)
                    .get();

            // Convertit le fichier temporaire .adoc en .pdf
            asciidoctor.convertFile(tempAdocFile, options);
            System.out.println("Conversion terminée : " + pdfFile.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("Une erreur est survenue lors de la conversion : " + e.getMessage());
        } finally {
            // Supprime le fichier temporaire
            tempAdocFile.delete();
            // Libère les ressources
            asciidoctor.shutdown();
        }
    }

    // Méthode pour trouver et lire les fichiers .adoc dans un répertoire
    private static void findAndReadAdocFiles(File dir, PrintWriter writer) throws IOException {
        FilenameFilter adocFilter = (d, name) -> name.toLowerCase().endsWith(".adoc");
        File[] adocFiles = dir.listFiles(adocFilter);

        // Lire les fichiers .adoc dans le répertoire courant
        if (adocFiles != null) {
            for (File adocFile : adocFiles) {
                String content = readFile(adocFile);
                writer.println(content);
                writer.println("\n"); // Ajoute une nouvelle ligne entre les fichiers
            }
        } else {
            System.out.println("Aucun fichier .adoc trouvé dans le répertoire " + dir.getAbsolutePath());
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
