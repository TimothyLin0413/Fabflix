import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class log_processing {

    public static void readFile(String fileName) {
        ArrayList<String> tjList = new ArrayList<>();
        ArrayList<String> tsList = new ArrayList<>();
        long totalTj = 0;
        long totalTs = 0;
        long totalLines = 0;

        try {
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String nextline = scanner.nextLine();
                if (nextline.substring(0,2).equals("ts")) {
                    tsList.add(nextline);
                } else if (nextline.substring(0,2).equals("tj")) {
                    tjList.add(nextline);
                }
            }

            for (int i = 0; i < tsList.size(); i++) {
                totalTs += Long.parseLong(tsList.get(i).substring(3));
            }

            for (int j = 0; j < tjList.size(); j++) {
                totalTj += Long.parseLong(tjList.get(j).substring(3));
            }

            totalLines = tjList.size() + tsList.size();
            System.out.println("TotalLine: "+ totalLines);
            System.out.println("Average Search Servlet Time:" + (totalTs / tsList.size() / 10000000));
            System.out.println("Average JDBC Time:" + (totalTj / tjList.size() / 10000000));
            scanner.close();
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        String single_instance_log1 = "/home/ubuntu/cs122b-fall-team-18/WebContent/single_instance_log1";
       String log = "C:\\Users\\timot\\gitclones\\cs122b-fall-team-18\\WebContent\\scale_version_log1";
//        String single_instance_log1 = "C:\\Users\\Dave PC\\demo\\cs122b-fall-team-18\\WebContent\\single_instance_log1"; // replace this file with the single_instance_log1 file location
        readFile(log);
    }
}
