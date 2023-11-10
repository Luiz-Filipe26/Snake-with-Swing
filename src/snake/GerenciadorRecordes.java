package snake;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GerenciadorRecordes {
    private static GerenciadorRecordes gr;
    private final int MAX_RECORDS = 5;
    private final String DATA_FOLDER = System.getProperty("user.home") + File.separator + "snake-data";
    private final String FILE_PATH = DATA_FOLDER + File.separator + "recordes.txt";

    public static GerenciadorRecordes getInstancia() {
        if(gr == null) {
            gr = new GerenciadorRecordes();
        }
        return gr;
    }

    private GerenciadorRecordes() {}

    public boolean isNovoRecorde(int pontos) {
        List<List<String>> recordes = lerRecordes();

        if (recordes == null || recordes.size() < MAX_RECORDS) {
            return true;
        }

        int menorRecorde = Integer.parseInt(recordes.get(recordes.size() - 1).get(1));
        return pontos > menorRecorde;
    }

    public List<List<String>> lerRecordes() {
        criarArquivoSeNaoExistir();

        List<List<String>> recordes = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                int lastSpaceIndex = linha.lastIndexOf(" ");
                if (lastSpaceIndex > 0) {
                    String nome = linha.substring(0, lastSpaceIndex);
                    String pontosStr = linha.substring(lastSpaceIndex + 1);
                    if (isNumero(pontosStr)) {
                        recordes.add(List.of(nome, pontosStr));
                    }
                }
}

        } catch (IOException e) {
            return null;
        }
        return recordes;
    }
    
    private boolean isNumero(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void inserirRecorde(String nome, int pontos) {
        List<List<String>> recordes = lerRecordes();
        if (recordes == null) {
            recordes = new ArrayList<>();
        }

        recordes.add(List.of(nome, String.valueOf(pontos)));
        Collections.sort(recordes, (recorde1, recorde2) -> {
            int pontos1 = Integer.valueOf(recorde1.get(1));
            int pontos2 = Integer.valueOf(recorde2.get(1));
            return Integer.compare(pontos2, pontos1);
        });

        while (recordes.size() > MAX_RECORDS) {
            recordes.remove(recordes.size() - 1);
        }

        salvarRecordes(recordes);
    }

    private void salvarRecordes(List<List<String>> recordes) {
        criarArquivoSeNaoExistir();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (List<String> recorde : recordes) {
                bw.write(recorde.get(0) + " " + recorde.get(1));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void criarArquivoSeNaoExistir() {
        File dataFolder = new File(DATA_FOLDER);
        File recordesFile = new File(FILE_PATH);

        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        if (!recordesFile.exists()) {
            try {
                recordesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
