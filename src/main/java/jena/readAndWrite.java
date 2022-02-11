package jena;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

public class readAndWrite {
    public static void main(String[] args) {
        String strHypernym = "src/main/resources/hypernym.csv";
        // 创建scanner
        try (Scanner scanner = new Scanner(Paths.get(strHypernym).toFile())) {
            // CSV文件分隔符
            String DELIMITER = ",";
            // 设置分隔符
            scanner.useDelimiter(DELIMITER);
            // 读取
            while (scanner.hasNext()) {
                System.out.print(scanner.next() + " ");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
