import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    static ArrayList<ArrayList<String>> rows = new ArrayList<>();

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        //MENU
        printMenu();
        boolean exit = false;
        while (!exit) {
            String action = scanner.nextLine();
            if (action.equals("1")) {            //MANUALLY
                String command = "";
                while (true) {
                    try {
                        rows.clear();
                        System.out.println("Enter the desired command:");
                        command = scanner.nextLine();
                        if (command.equalsIgnoreCase("exit")) {
                            break;
                        }
                        Pattern mainPattern = Pattern.compile("^\s*(CREATE\s+Table|DROP\s+Table|SELECT|INSERT\s+INTO|UPDATE|DELETE\s+FROM)\s+(.*)", Pattern.CASE_INSENSITIVE);
                        Matcher mainMacher = mainPattern.matcher(command);
                        boolean mainFound = mainMacher.find();

                        if (mainFound) {
                            String functionName = mainMacher.group(1).replaceAll("\s+", " ").toUpperCase();
                            switch (functionName) {
                                case "CREATE TABLE":
                                    create_table(mainMacher.group(2));
                                    break;
                                case "DROP TABLE":
                                    drop_table(mainMacher.group(2));
                                    break;
                                case "SELECT":
                                    select(mainMacher.group(2));
                                    break;
                                case "INSERT INTO":
                                    insert_into(mainMacher.group(2));
                                    break;
                                case "UPDATE":
                                    update(mainMacher.group(2));
                                    break;
                                case "DELETE FROM":
                                    delete_from(mainMacher.group(2));
                                    break;
                            }
                        } else {
                            System.out.println("syntax Function_Name founded Error");
                        }
                        System.out.println("(Enter \"exit\" to exit the command field)");
                    } catch (Exception e) {
                        System.out.println("There was an error while entering the command.");
                        System.out.println("The ERROR is: " + e.getMessage());
                    }
                }
                System.out.print("\t\t\t   Want to do something else?");
                String YESorNO = scanner.next();
                if (YESorNO.equalsIgnoreCase("no") || YESorNO.equalsIgnoreCase("n")) {
                    exit = true;
                } else {
                    printMenu();
                }

            } else if (action.equals("2")) {               //FILE
                System.out.println("Enter FilePath: ");
                String filePath = scanner.nextLine();
                filePath = filePath.replaceFirst("\"", "");
                filePath = replaceLast(filePath, "\"", "");
                ArrayList<String> commands = new ArrayList<>();
                commands.addAll(List.of(readFromCommandFile(filePath)));
                for (String command : commands) {
                    try {
                        rows.clear();
                        Pattern mainPattern = Pattern.compile("^\s*(CREATE\s+Table|DROP\s+Table|SELECT|INSERT\s+INTO|UPDATE|DELETE\s+FROM)\s+(.*)", Pattern.CASE_INSENSITIVE);
                        Matcher mainMacher = mainPattern.matcher(command);
                        boolean mainFound = mainMacher.find();

                        if (mainFound) {
                            String functionName = mainMacher.group(1).replaceAll("\s+", " ").toUpperCase();
                            switch (functionName) {
                                case "CREATE TABLE":
                                    create_table(mainMacher.group(2));
                                    break;
                                case "DROP TABLE":
                                    drop_table(mainMacher.group(2));
                                    break;
                                case "SELECT":
                                    select(mainMacher.group(2));
                                    break;
                                case "INSERT INTO":
                                    insert_into(mainMacher.group(2));
                                    break;
                                case "UPDATE":
                                    update(mainMacher.group(2));
                                    break;
                                case "DELETE FROM":
                                    delete_from(mainMacher.group(2));
                                    break;
                            }
                        } else {
                            System.out.println("syntax functionName founded Error");
                        }
                    }catch (Exception e){
                        System.out.println("There was an error while entering the command.");
                        System.out.println("The ERROR is: " + e.getMessage());
                    }
                }
                printMenu();
            } else if (action.equals("3")) {
                break;
            }
        }
        System.out.println("THANK YOU for using the Program.");


    }

    //command methods:
    static void create_table(String command) throws IOException {
        Pattern pattern = Pattern.compile("^\s*([^\s/\\:*?\"<>|](?:\s*[^\s/\\:*?\"<>|])*?)\s*\\((.*?)\\)(\s*$|\s*;\s*$)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);
        boolean matchFound = matcher.find();
        if (matchFound) {
            String fileName = matcher.group(1) + ".txt";
            String[] columns = matcher.group(2).split(",");
            for (int i = 0; i < columns.length; i++) {
                columns[i] = columns[i].strip();
            }
            File file = new File(fileName);
            String box = ("|" + "-".repeat(50)).repeat(columns.length) + "|";

            if (file.createNewFile()) {
                FileWriter fileWriter = new FileWriter(fileName);
                fileWriter.write(box + "\n" + getOutput(columns) + box + "\n");
                System.out.println("File Created");
                fileWriter.close();
            } else {
                System.out.println("File already Exists");
            }

        } else {
            System.out.println("Incorrect syntax for Create Table has been entered! Please re-enter");
            System.out.println("The correct syntax of this command is as follows:");
            System.out.println(" CREATE TABLE table_name (column1,column2,column3,...); ");
        }
    }

    static void drop_table(String command) {
        Pattern pattern = Pattern.compile("^\\s*([^\\s/\\\\:*?\\\"<>|](?:\\s*[^\\s/\\\\:*?\\\"<>|;])*?)(\\s*$|\\s*;\\s*$)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);
        boolean matchFound = matcher.find();
        if (matchFound) {
            String fileName = matcher.group(1);
            fileName += ".txt";
            File file = new File(fileName);
            if (file.delete()) {
                System.out.println("Deleted the file: " + file.getName());
            } else {
                System.out.println("Failed to delete the file.");
            }
        } else {
            System.out.println("Incorrect syntax for Drop Table has been entered! Please re-enter");
            System.out.println("The correct syntax of this command is as follows:");
            System.out.println(" DROP TABLE table_name ");
        }

    }

    static void select(String command) throws FileNotFoundException {
        Pattern pattern = Pattern.compile("^\\s*(.*)\\s+from\\s+([^\\s/\\:*?\"<>|](?:\\s*[^\\s/\\:*?\"<>|;]*))\\s*(?:\\s+((WHERE|ORDER\\s+BY)\\s+([^;]+))?)?(\\s*;\\s*)?$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);
        boolean matchFound = matcher.find();
        if (matchFound) {
            String before_from = matcher.group(1).strip();      //columns  / *  / Aggregate
            String table_name = matcher.group(2).strip() + ".txt";
            String after_WHEREorORDERForWhere = "";
            String after_WHEREorORDERForOrder = "";
            fileReader(table_name);
            ArrayList<Integer> index = new ArrayList<>();               //?
            ArrayList<Integer> columnsToShow = new ArrayList<>();       //sotooon javab
            //Aggregate Functions:
            boolean aggregate_flag = false;
            double result;
            Pattern aggregatePattern = Pattern.compile("^\\s*(.*)\\s*\\((.*)\\)\\s*$", Pattern.CASE_INSENSITIVE);
            Matcher aggergateMatcher = aggregatePattern.matcher(before_from);
            boolean aggergatMatchFound = aggergateMatcher.find();

            if (before_from.equals("*")) {
                for (int i = 0; i < rows.size(); i++) {
                    columnsToShow.add(i);
                }
            } else if (aggergatMatchFound) {
                aggregate_flag = true;
                String aggergatFunctionName = aggergateMatcher.group(1).strip();
                boolean withWhere = false;
                if (matcher.group(4) != null) {
                    withWhere = matcher.group(4).equalsIgnoreCase("where");
                    String group4 = matcher.group(4).strip().replaceAll("\\s+", " ");
                    if (group4.equalsIgnoreCase("where")) {
                        after_WHEREorORDERForWhere = matcher.group(5).strip();
                    }
                }
                if (aggergatFunctionName.equalsIgnoreCase("count")) {
                    result = count(aggergateMatcher.group(2), withWhere, after_WHEREorORDERForWhere);
                    System.out.println("Count (" + aggergateMatcher.group(2) + "):");
                    System.out.println(result);
                } else if (aggergatFunctionName.equalsIgnoreCase("sum")) {
                    result = sum(aggergateMatcher.group(2), withWhere, after_WHEREorORDERForWhere);
                    System.out.println("Sum (" + aggergateMatcher.group(2) + "):");
                    System.out.println(result);
                } else if (aggergatFunctionName.equalsIgnoreCase("avg")) {
                    result = Double.parseDouble(String.format("%.3f", avg(aggergateMatcher.group(2), withWhere, after_WHEREorORDERForWhere)));
                    System.out.println("Average (" + aggergateMatcher.group(2) + "):");
                    System.out.println(result);
                } else if (aggergatFunctionName.equalsIgnoreCase("min")) {
                    result = min(aggergateMatcher.group(2), withWhere, after_WHEREorORDERForWhere);
                    System.out.println("Min (" + aggergateMatcher.group(2) + "):");
                    System.out.println(result);
                } else if (aggergatFunctionName.equalsIgnoreCase("max")) {
                    result = max(aggergateMatcher.group(2), withWhere, after_WHEREorORDERForWhere);
                    System.out.println("Max (" + aggergateMatcher.group(2) + "):");
                    System.out.println(result);
                } else {
                    System.out.println("This ( " + aggergateMatcher.group(1) + " ) Aggregate Functions could not be found");
                }
            } else {
                String[] columns = before_from.split(",");
                for (int i = 0; i < columns.length; i++) {
                    columns[i] = columns[i].strip();
                }

                ////////////////////        AS:
                for (int i = 0; i < columns.length; i++) {
                    if (columns[i].toUpperCase().contains(" AS ")) {
                        String[] columnParts = columns[i].split("\s+[Aa][Ss]\s+");
                        for (int j = 0; j < rows.get(0).size(); j++) {
                            if (rows.get(0).get(j).equalsIgnoreCase(columnParts[0])) {
                                rows.get(0).set(j, columnParts[1]);
                                columns[i] = columnParts[1];
                            }
                        }
                    }
                }
                //////////////////////
                boolean find = false;
                for (String columnName : columns) {
                    find = false;
                    for (int i = 0; i < rows.get(0).size(); i++) {
                        if (rows.get(0).get(i).equalsIgnoreCase(columnName)) {
                            columnsToShow.add(i);
                            find = true;
                            break;
                        }
                    }
                    if (!find) {
                        System.out.println(columnName + " does not exist in the table!");
                    }
                }
            }
            boolean where_flag = false;
            boolean order_flag = false;
            ArrayList<Integer> rowsToShow = new ArrayList<>();
            if (matcher.group(4) != null) {
                where_flag = true;
                String group4 = matcher.group(4).strip().replaceAll("\\s+", " ");
                if (group4.equalsIgnoreCase("where") || group4.equalsIgnoreCase("order by")) {
                    after_WHEREorORDERForWhere = matcher.group(5).strip();
                    after_WHEREorORDERForOrder = matcher.group(3).strip();
                }

                String[] strForOrder = after_WHEREorORDERForOrder.split("[Oo][Rr][Dd][Ee][Rr]\\s+[Bb][Yy]");

                if (strForOrder.length == 1) {                  //just where
                    rowsToShow.addAll(whereCondition(after_WHEREorORDERForWhere));
                } else if (strForOrder.length == 2) {       // order by ID          //where NAME='ali' order by ID

                    if (strForOrder[0].isEmpty()) {      //just order by
                        order_flag = true;

                        String[] partsOfOrder = after_WHEREorORDERForWhere.split("\\s+");       //0:column name  1:ASC|DESC

                        for (int i = 0; i < partsOfOrder.length; i++) {
                            partsOfOrder[i] = partsOfOrder[i].strip();
                        }
                        ArrayList<ArrayList<String>> table = new ArrayList<>();
                        table = order_by(rows, partsOfOrder[0]);
                        if (partsOfOrder.length == 2) {
                            if (partsOfOrder[1].equalsIgnoreCase("desc")) {
                                printTableDESC(table, columnsToShow);
                            } else if (partsOfOrder[1].equalsIgnoreCase("asc")) {
                                printTableASC(table, columnsToShow);
                            }
                        } else {
                            printTableDESC(table, columnsToShow);
                        }
                    } else {                      //where and order by
                        String[] elsesPart = after_WHEREorORDERForWhere.split("\\s*[Oo][Rr][Dd][Ee][Rr]\\s+[Bb][Yy]\\s*");
                        ArrayList<ArrayList<String>> whereRows = new ArrayList<>();
                        for (int i = 0; i < 2; i++) {
                            elsesPart[i] = elsesPart[i].strip();
                        }
                        ArrayList<Integer> numOfWhereRows = new ArrayList<>();
                        numOfWhereRows.addAll(whereCondition(elsesPart[0]));
                        whereRows.add(rows.get(0));
                        for (int num : numOfWhereRows) {
                            whereRows.add(rows.get(num));
                        }

                        order_flag = true;

                        String[] partsOfOrder = elsesPart[1].split("\\s+");       //0:column name  1:ASC|DESC

                        for (int i = 0; i < partsOfOrder.length; i++) {
                            partsOfOrder[i] = partsOfOrder[i].strip();
                        }
                        ArrayList<ArrayList<String>> table = new ArrayList<>();
                        table = order_by(whereRows, partsOfOrder[0]);
                        if (partsOfOrder.length == 2) {
                            if (partsOfOrder[1].equalsIgnoreCase("desc")) {
                                printTableDESC(table, columnsToShow);
                            } else if (partsOfOrder[1].equalsIgnoreCase("asc")) {
                                printTableASC(table, columnsToShow);
                            }
                        } else {
                            printTableDESC(table, columnsToShow);
                        }
                    }
                } else {
                    System.out.println("Error in syntax in WHERE and ORDER BY");
                    System.out.println("The correct syntax of this command is as follows:");
                    System.out.println("    For WHERE syntax:");
                    System.out.println("        SELECT column1, column2, ... FROM table_name WHERE condition1 (AND/OR)? ...");
                    System.out.println("            For condition syntax:  (value/pattern must be inside '...' (single quotation).)");
                    System.out.println("                column_name = 'value'");
                    System.out.println("                column_name LIKE 'pattern'");
                    System.out.println("                column_name REGEXP 'pattern'");
                    System.out.println("    For ORDER BY syntax:");
                    System.out.println("        SELECT column1, column2, ... FROM table_name ORDER BY column1, column2, ... ASC|DESC;\n");

                }
            }
            //print:
            if (!order_flag && !aggregate_flag) {
                if (columnsToShow.size() > 0) {
                    if (where_flag) {
                        printTable(columnsToShow, rowsToShow);
                    } else {
                        printTable(columnsToShow);
                    }
                } else {
                    System.out.println("There is Nothing To Show");
                }

            }
        } else {
            System.out.println("Incorrect syntax for Select has been entered! Please re-enter");
            System.out.println("The correct syntax of this command is as follows:");
            System.out.println("    If you want to see the whole file: ");
            System.out.println("        SELECT column1,column2,... FROM table_name; ");
            System.out.println("    If you want to view the file with different column header names:");
            System.out.println("        SELECT column1_name AS new_column1_name,column2_name AS new_column2_name,... FROM table_name;");
            System.out.println("    If you want to have a condition in viewing the file:");
            System.out.println("        SELECT column1, column2, ... FROM table_name WHERE condition (AND/OR)?;");
            System.out.println("            For condition syntax:  (value/pattern must be inside '...' (single quotation).)");
            System.out.println("                column_name = 'value'");
            System.out.println("                column_name LIKE 'pattern'");
            System.out.println("                column_name REGEXP 'pattern'");
            System.out.println("    If you want to use Aggregate Functions:");
            System.out.println("        SELECT COUNT/SUM/AVG/MIN/MAX(column_name) FROM table_name ((WHERE condition)? This part may not exist.);");
        }
    }

    static double count(String columnName, boolean haveWhere, String condition) {
        double result = 0;
        if (haveWhere) {

            int indexColumn = -1;
            for (int i = 0; i < rows.get(0).size(); i++) {
                if (rows.get(0).get(i).equalsIgnoreCase(columnName)) {
                    indexColumn = i;
                    break;
                }
            }
            if (indexColumn != -1) {

                for (int num : whereCondition(condition)) {
                    if (!rows.get(num).get(indexColumn).isBlank()) {
                        result++;
                    }
                }
            } else {
                System.out.println("There is no such column ( " + columnName + " ).");
            }
        } else {
            int indexColumn = -1;
            for (int i = 0; i < rows.get(0).size(); i++) {
                if (rows.get(0).get(i).equalsIgnoreCase(columnName)) {
                    indexColumn = i;
                    break;
                }
            }
            if (indexColumn != -1) {
                for (int i = 1; i < rows.size(); i++) {
                    if (!rows.get(i).get(indexColumn).isBlank()) {
                        result++;
                    }
                }
            } else {
                System.out.println("There is no such column ( " + columnName + " ).");
            }
        }
        return result;
    }

    static double sum(String columnName, boolean haveWhere, String condition) {
        double result = 0;
        if (haveWhere) {

            int indexColumn = -1;
            for (int i = 0; i < rows.get(0).size(); i++) {
                if (rows.get(0).get(i).equalsIgnoreCase(columnName)) {
                    indexColumn = i;
                    break;
                }
            }
            if (indexColumn != -1) {
                for (int num : whereCondition(condition)) {
                    if (!rows.get(num).get(indexColumn).isBlank() && isNumeric(rows.get(num).get(indexColumn).strip())) {
                        result += Double.parseDouble(rows.get(num).get(indexColumn).strip());
                    }
                }
            } else {
                System.out.println("There is no such column ( " + columnName + " ).");
            }
        } else {
            int indexColumn = -1;
            for (int i = 0; i < rows.get(0).size(); i++) {
                if (rows.get(0).get(i).equalsIgnoreCase(columnName)) {
                    indexColumn = i;
                    break;
                }
            }
            if (indexColumn != -1) {
                for (int i = 1; i < rows.size(); i++) {
                    if (!rows.get(i).get(indexColumn).isBlank() && isNumeric(rows.get(i).get(indexColumn).strip())) {
                        result += Double.parseDouble(rows.get(i).get(indexColumn).strip());
                    }
                }
            } else {
                System.out.println("There is no such column ( " + columnName + " ).");
            }
        }
        return result;
    }

    static double avg(String columnName, boolean haveWhere, String condition) {
        double result = sum(columnName, haveWhere, condition) / count(columnName, haveWhere, condition);
        return result;
    }

    static double min(String columnName, boolean haveWhere, String condition) {
        double result = 0;
        boolean findFirstMin = false;
        if (haveWhere) {

            int indexColumn = -1;
            for (int i = 0; i < rows.get(0).size(); i++) {
                if (rows.get(0).get(i).equalsIgnoreCase(columnName)) {
                    indexColumn = i;
                    break;
                }
            }
            if (indexColumn != -1) {
                for (int i = 0; i < whereCondition(condition).size(); i++) {
                    if (!rows.get(whereCondition(condition).get(i)).get(indexColumn).isBlank() && isNumeric(rows.get(whereCondition(condition).get(i)).get(indexColumn).strip())) {
                        result = Double.parseDouble(rows.get(whereCondition(condition).get(i)).get(indexColumn).strip());
                        findFirstMin = true;
                        break;
                    }
                }
                if (findFirstMin) {
                    for (int num : whereCondition(condition)) {
                        if (!rows.get(num).get(indexColumn).isBlank() && isNumeric(rows.get(num).get(indexColumn).strip())) {
                            if (Double.parseDouble(rows.get(num).get(indexColumn).strip()) < result) {
                                result = Double.parseDouble(rows.get(num).get(indexColumn).strip());
                            }
                        }
                    }
                } else {
                    System.out.println("With this given condition there is no numeric data in this column.");
                }
            } else {
                System.out.println("There is no such column ( " + columnName + " ).");
            }
        } else {
            int indexColumn = -1;
            for (int i = 0; i < rows.get(0).size(); i++) {
                if (rows.get(0).get(i).equalsIgnoreCase(columnName)) {
                    indexColumn = i;
                    break;
                }
            }
            if (indexColumn != -1) {
                for (int i = 0; i < rows.size(); i++) {
                    if (!rows.get(i).get(indexColumn).strip().isBlank() && isNumeric(rows.get(i).get(indexColumn).strip())) {
                        result = Double.parseDouble(rows.get(i).get(indexColumn).strip());
                        findFirstMin = true;
                        break;
                    }
                }
                if (findFirstMin) {
                    for (int i = 1; i < rows.size(); i++) {
                        if (!rows.get(i).get(indexColumn).isBlank() && isNumeric(rows.get(i).get(indexColumn).strip())) {
                            if (Double.parseDouble(rows.get(i).get(indexColumn).strip()) < result) {
                                result = Double.parseDouble(rows.get(i).get(indexColumn).strip());
                            }
                        }
                    }
                } else {
                    System.out.println("There is no numerical data in this column.");
                }
            } else {
                System.out.println("There is no such column ( " + columnName + " ).");
            }
        }
        return result;
    }

    static double max(String columnName, boolean haveWhere, String condition) {
        double result = 0;
        boolean findFirstMin = false;
        if (haveWhere) {

            int indexColumn = -1;
            for (int i = 0; i < rows.get(0).size(); i++) {
                if (rows.get(0).get(i).equalsIgnoreCase(columnName)) {
                    indexColumn = i;
                    break;
                }
            }
            if (indexColumn != -1) {
                for (int i = 0; i < whereCondition(condition).size(); i++) {
                    if (!rows.get(whereCondition(condition).get(i)).get(indexColumn).isBlank() && isNumeric(rows.get(whereCondition(condition).get(i)).get(indexColumn).strip())) {
                        result = Double.parseDouble(rows.get(whereCondition(condition).get(i)).get(indexColumn).strip());
                        findFirstMin = true;
                        break;
                    }
                }
                if (findFirstMin) {
                    for (int num : whereCondition(condition)) {
                        if (!rows.get(num).get(indexColumn).isBlank() && isNumeric(rows.get(num).get(indexColumn).strip())) {
                            if (Double.parseDouble(rows.get(num).get(indexColumn).strip()) > result) {
                                result = Double.parseDouble(rows.get(num).get(indexColumn).strip());
                            }
                        }
                    }
                } else {
                    System.out.println("With this given condition there is no numeric data in this column.");
                }
            } else {
                System.out.println("There is no such column ( " + columnName + " ).");
            }
        } else {
            int indexColumn = -1;
            for (int i = 0; i < rows.get(0).size(); i++) {
                if (rows.get(0).get(i).equalsIgnoreCase(columnName)) {
                    indexColumn = i;
                    break;
                }
            }
            if (indexColumn != -1) {
                for (int i = 0; i < rows.size(); i++) {
                    if (!rows.get(i).get(indexColumn).strip().isBlank() && isNumeric(rows.get(i).get(indexColumn).strip())) {
                        result = Double.parseDouble(rows.get(i).get(indexColumn).strip());
                        findFirstMin = true;
                        break;
                    }
                }
                if (findFirstMin) {
                    for (int i = 1; i < rows.size(); i++) {
                        if (!rows.get(i).get(indexColumn).isBlank() && isNumeric(rows.get(i).get(indexColumn).strip())) {
                            if (Double.parseDouble(rows.get(i).get(indexColumn).strip()) > result) {
                                result = Double.parseDouble(rows.get(i).get(indexColumn).strip());
                            }
                        }
                    }
                } else {
                    System.out.println("There is no numerical data in this column.");
                }
            } else {
                System.out.println("There is no such column ( " + columnName + " ).");
            }
        }
        return result;
    }

    public static ArrayList<ArrayList<String>> order_by(ArrayList<ArrayList<String>> data, String columnName) {
        try {
            int columnIndex = -1;
            for (int i = 0; i < data.get(0).size(); i++) {
                if (data.get(0).get(i).equalsIgnoreCase(columnName)) {
                    columnIndex = i;
                    break;
                }
            }
            if (columnIndex == -1) {
                throw new IllegalArgumentException("Column " + columnName + " not found\n" +
                        "The table is unsorted as above. If you want to sort it, select one of the columns in the first row.");
            }

            int finalColumnIndex = columnIndex;
            Collections.sort(data.subList(1, data.size()), new Comparator<ArrayList<String>>() {
                @Override
                public int compare(ArrayList<String> row1, ArrayList<String> row2) {
                    String s1 = row1.get(finalColumnIndex);
                    String s2 = row2.get(finalColumnIndex);

                    if (s1.matches("\\d+") && s2.matches("\\d+")) {
                        return Integer.compare(Integer.parseInt(s1), Integer.parseInt(s2));
                    } else if (s1.matches("\\d+")) {
                        return -1;
                    } else if (s2.matches("\\d+")) {
                        return 1;
                    } else {
                        return s1.compareTo(s2);
                    }
                }
            });

            return data;
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
        return data;
    }

    static void insert_into(String command) throws IOException {
        Pattern pattern1 = Pattern.compile("^\s*([^\s/\\:*?\"<>|](?:\s*[^\s/\\:*?\"<>|]*?))\s*\\((.*?)\\)\s*VALUES\s*((\s*\\(((\s*'[^']*'\s*,?\s*)+\s*)\\)\s*,?\s*)+\s*)(\s*$|\s*;\s*$)", Pattern.CASE_INSENSITIVE);
        Pattern pattern2 = Pattern.compile("^\s*([^\s/\\:*?\"<>|](?:\s*[^\s/\\:*?\"<>|]*?))\s+VALUES\s*((\s*\\(((\s*'[^']*'\s*,?\s*)+\s*)\\)\s*,?\s*)+)\s*(\s*$|\s*;\s*$)", Pattern.CASE_INSENSITIVE);
        Matcher matcher1 = pattern1.matcher(command);
        Matcher matcher2 = pattern2.matcher(command);
        boolean matchFound1 = matcher1.find();
        boolean matchFound2 = matcher2.find();
        if (matchFound1) {
            String fileName = matcher1.group(1) + ".txt";
            fileReader(fileName);
            String[] columns = matcher1.group(2).split(",");
            for (int i = 0; i < columns.length; i++) {
                columns[i] = columns[i].strip();
            }
            String str = replaceLast(matcher1.group(3), ")", "").replaceFirst("\\(", "").strip();
            String[] valuesGroups = str.strip().split("\\)[,\s]*\\(");

            for (String group : valuesGroups) {
                String[] values = group.replaceAll("'", "").split(",");
                for (int i = 0; i < values.length; i++) {
                    values[i] = values[i].strip();
                }

                boolean find = false;
                if (columns.length == values.length) {
                    ArrayList<String> row = new ArrayList<>();
                    for (int i = 0; i < rows.get(0).size(); i++) {
                        find = false;
                        for (int j = 0; j < columns.length; j++) {
                            if (rows.get(0).get(i).equalsIgnoreCase(columns[j])) {
                                row.add(i, values[j]);
                                find = true;
                                break;
                            }
                        }
                        if (!find) {
                            row.add(i, "");
                        }
                    }
                    rows.add(row);
                    FileWriter fileWriter = new FileWriter(fileName, true);
                    fileWriter.write(getOutput(row));
                    fileWriter.close();
                } else {
                    System.out.println("Syntx Error , The number of values is not the same as the number of columns");
                }
            }
        } else if (matchFound2) {
            String fileName = matcher2.group(1) + ".txt";
            fileReader(fileName);
            String str = replaceLast(matcher2.group(2), ")", "").replaceFirst("\\(", "").strip();
            String[] valuesGroups = str.strip().split("\\)[,\s]*\\(");
            for (String group : valuesGroups) {
                String[] values = group.replaceAll("'", "").split(",");
                for (int i = 0; i < values.length; i++) {
                    values[i] = values[i].strip();
                }
                ArrayList<String> row = new ArrayList<>();
                for (int i = 0; i < rows.get(0).size(); i++) {
                    if (i < values.length) {
                        row.add(i, values[i]);
                    } else {
                        row.add(i, "");
                    }
                }
                rows.add(row);
                FileWriter fileWriter = new FileWriter(fileName, true);
                fileWriter.write(getOutput(row));
                fileWriter.close();
            }
        } else {
            System.out.println("Incorrect syntax for INSERT INTO has been entered! Please re-enter");
            System.out.println("The correct syntax of this command is as follows:");
            System.out.println("    INSERT INTO table_name (column1,column2,column3,...) VALUES ('value1','value2','value3',...)");
            System.out.println("    or");
            System.out.println("    INSERT INTO table_name VALUES ('value1','value2','value3',...)\n");
        }

    }

    static void update(String commmand) throws IOException {
        Pattern updatePattern = Pattern.compile("^\s*([^\s/\\:*?\"<>|](?:\s*[^\s/\\:*?\"<>|;]*))\s+set\s+(.*?)(?:\s*((WHERE)\s+([^;]+))?)?(\s*;\s*)?$", Pattern.CASE_INSENSITIVE);
        Matcher updateMatcher = updatePattern.matcher(commmand);
        boolean updateMatchFound = updateMatcher.find();
        if (updateMatchFound) {
            String fileName = updateMatcher.group(1).strip() + ".txt";
            fileReader(fileName);
            String afterSet = updateMatcher.group(2).strip();           //ContactName = 'Alfred Schmidt', City = 'Frankfurt'
            String[] updatesParts = afterSet.split(",");        //[ContactName = 'Alfred Schmidt', City = 'Frankfurt']
            for (int i = 0; i < updatesParts.length; i++) {
                updatesParts[i] = updatesParts[i].strip();
            }
            for (String part : updatesParts) {
                String[] partValues = part.split("=");
                for (int i = 0; i < partValues.length; i++) {
                    partValues[i] = partValues[i].strip();
                }
                partValues[1] = partValues[1].replaceFirst("'", " ");
                partValues[1] = replaceLast(partValues[1], "'", "");
                int indexcolumn = -1;
                for (int i = 0; i < rows.get(0).size(); i++) {
                    if (rows.get(0).get(i).equalsIgnoreCase(partValues[0])) {
                        indexcolumn = i;

                        if (updateMatcher.group(4) == null || updateMatcher.group(4).isEmpty()) {        //without where
                            for (int j = 1; j < rows.size(); j++) {
                                rows.get(j).set(indexcolumn, partValues[1]);
                            }
                        } else {                                                                          //with where
                            String afterWhere = updateMatcher.group(5).strip();
                            for (int numOfRows : whereCondition(afterWhere)) {
                                rows.get(numOfRows).set(indexcolumn, partValues[1]);
                            }
                        }

                        String box = ("|" + "-".repeat(50)).repeat(rows.get(0).size()) + "|";
                        FileWriter fileWriter = new FileWriter(fileName);
                        fileWriter.write(box + "\n");
                        fileWriter.write(getOutput(rows.get(0)));
                        fileWriter.write(box + "\n");
                        for (int j = 1; j < rows.size(); j++) {
                            fileWriter.write(getOutput(rows.get(j)));
                        }
                        fileWriter.close();

                        System.out.println("Update was done successfully");
                        break;
                    }
                }
                if (indexcolumn == -1) {
                    System.out.println("This column ( " + partValues[0] + " ) was not found!");
                }
            }
        } else {
            System.out.println("Incorrect syntax for UPDATE has been entered! Please re-enter");
            System.out.println("The correct syntax of this command is as follows:");
            System.out.println("    If you want to update a specific part of the column:");
            System.out.println("        UPDATE table_name SET column1 = 'value1', column2 = 'value2' ... WHERE condition ;");
            System.out.println("    If you want to update all of that column:");
            System.out.println("        UPDATE table_name SET column1 = 'value1', column2 = 'value2' ... ;");
        }
    }

    static void delete_from(String comamand) throws IOException {
        comamand = comamand.strip();
        Pattern deletePattern = Pattern.compile("^\s*([^\s/\\:*?\"<>|](?:\s*[^\s/\\:*?\"<>|;]*))\s*(?:\s+(where)\s+([^\s][^;]+))?(\s*;\s*)?$", Pattern.CASE_INSENSITIVE);
        Matcher deleteMatcher = deletePattern.matcher(comamand);
        boolean deletematchFound = deleteMatcher.find();
        if (deletematchFound) {
            String fileName = deleteMatcher.group(1) + ".txt";
            fileReader(fileName);
            if (deleteMatcher.group(2) == null || deleteMatcher.group(2).isEmpty()) {     //without where
                for (int j = 1; j < rows.size(); j++) {
                    rows.remove(j);
                    j--;
                }
            } else {          //with  where
                String afterWhere = deleteMatcher.group(3).strip();
                for (int j = whereCondition(afterWhere).size() - 1; j >= 0; j--) {
                    int index = whereCondition(afterWhere).get(j);
                    rows.remove(index);
                }
            }

            String box = ("|" + "-".repeat(50)).repeat(rows.get(0).size()) + "|";
            FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.write(box + "\n");
            fileWriter.write(getOutput(rows.get(0)));
            fileWriter.write(box + "\n");
            for (int j = 1; j < rows.size(); j++) {
                System.out.println(getOutput(rows.get(j)));
                fileWriter.write(getOutput(rows.get(j)));
            }
            fileWriter.close();

            System.out.println("Delete was done successfully");
        }
        else {
            System.out.println("Incorrect syntax for DELETE has been entered! Please re-enter");
            System.out.println("The correct syntax of this command is as follows:");
            System.out.println("    If you want to delete a specific row:");
            System.out.println("        DELETE FROM table_name WHERE condition;");
            System.out.println("    If you want to delete all rows:");
            System.out.println("        DELETE FROM table_name;");
        }
    }

    //Performer methods:
    static String getOutput(ArrayList<String> datas) {
        String output = "";
        for (String data : datas) {
            int padding = 50 - data.length();
            int leftPadding = padding / 2;
            int rightPadding = padding - leftPadding;
            output += "|" + " ".repeat(leftPadding) + data + " ".repeat(rightPadding);
        }
        output += "|\n";
        return output;
    }

    static String getOutput(String[] datas) {
        String output = "";
        for (String data : datas) {
            int padding = 50 - data.length();
            int leftPadding = padding / 2;
            int rightPadding = padding - leftPadding;
            output += "|" + " ".repeat(leftPadding) + data + " ".repeat(rightPadding);
        }
        output += "|\n";
        return output;
    }       //for print when crate file

    static void fileReader(String fileName) throws FileNotFoundException {
        ArrayList<ArrayList<String>> tableDatas = new ArrayList<>();
        File file = new File(fileName);
        if (file.exists()) {
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                ArrayList<String> rowDatas = new ArrayList<>(Arrays.asList(reader.nextLine().replaceFirst("\\|", "").split("\\|")));
                for (String data : rowDatas) {
                    rowDatas.set(rowDatas.indexOf(data), data.strip());
                }
                tableDatas.add(rowDatas);
            }
        } else {
            System.out.println("The file does not exist.");
        }

        for (int i = 0; i < tableDatas.size(); i++) {
            if (i != 0 && i != 2) {
                rows.add(tableDatas.get(i));
            }
        }
    }

    public static String replaceLast(String str, String regex, String replacement) {
        int lastIndex = str.lastIndexOf(regex);
        if (lastIndex == -1) {
            return str;
        }
        String substring = str.substring(0, lastIndex);
        return substring + replacement + str.substring(lastIndex + regex.length());
    }

    static boolean parenthesCheck(String str) {
        int open = 0;
        int close = 0;
        for (char c : str.toCharArray()) {
            if (c == '(') {
                open++;
            } else if (c == ')') {
                close++;
            }
        }
        if (open == close) {
            return true;
        } else {
            return false;
        }
    }

    static ArrayList<Integer> whereCondition(String condition) {
        condition = condition.strip();
        condition = "(" + condition + ")";
        ArrayList<Integer> nums = new ArrayList<>();
        if (parenthesCheck(condition)) {

            String output = "";
            Stack<Character> stack = new Stack<>();
            int i = 1;
            for (char character : condition.toCharArray()) {
                output = "";
                if (character != ')') {
                    stack.push(character);
                } else {
                    while (stack.peek() != '(') {
                        output = stack.pop() + output;
                    }
                    stack.pop();
                    for (char c : check(output).toCharArray()) {
                        stack.push(c);
                    }
                }
            }
            output = "";
            while (!stack.isEmpty()) {
                output = stack.pop() + output;
            }

            String[] outputAsArray = (output).split("\\s*,\\s*");
            for (String num : outputAsArray) {
                if (!num.isEmpty())
                    nums.add(Integer.parseInt(num));
            }
        } else {
            System.out.println("The number of parentheses is not entered correctly");
        }
        return nums;

    }

    public static String check(String str) {
        String output = "";

        ArrayList<Integer> finalAnswer = new ArrayList<>();
        Pattern pattern = Pattern.compile("^\s*(\\w[\\w\s]*\s*=\s*'[^']+'|\\w[\\w\s]*\s+(regexp|like)\s*'[^']+'|\\d(,\\d+)*)(\s+(and|or)\s+(\\w[\\w\s]*\s*=\s*'[^']+'|\\w[\\w\s]*\s+(regexp|like)\s*'[^']+'|\\d(,\\d+)*))*\s*$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        boolean matchFound = matcher.find();                //parenthesis: age='18' and name='reza' or 1,2,3,4,5
        if (matchFound) {
            Pattern a0 = Pattern.compile("((\\w*)\\s*(=|\\s+like|\\s+regexp)\\s*'([^']+)')", Pattern.CASE_INSENSITIVE);                 ///if i have bug return back it
            Matcher a01 = a0.matcher(str);
            while (a01.find()) {
                String solve = "";
                for (int i = 0; i < rows.size(); i++) {
                    if (a01.group(3).strip().equals("=")) {
                        if (rows.get(i).get(rows.get(0).indexOf(a01.group(2))).equalsIgnoreCase(a01.group(4))) {
                            solve += (i + ",");
                        }
                    } else if (a01.group(3).strip().equalsIgnoreCase("regexp")) {
                        Pattern regexPattern = Pattern.compile(a01.group(4));
                        Matcher regexMatcher = regexPattern.matcher(rows.get(i).get(rows.get(0).indexOf(a01.group(2))));
                        boolean regexFound = regexMatcher.find();
                        if (regexFound) {
                            solve += (i + ",");
                        }
                    } else if (a01.group(3).strip().equalsIgnoreCase("like")) {
                        int numOfPersents = a01.group(4).strip().length() - a01.group(4).strip().replaceAll("%", "").length();
                        if (a01.group(4).strip().contains("%")) {
                            if (numOfPersents == 1) {
                                if (a01.group(4).strip().startsWith("%")) {                              //a%
                                    String likeShape = a01.group(4).replaceAll("%", "");
                                    if (rows.get(i).get(rows.get(0).indexOf(a01.group(2))).endsWith(likeShape)) {
                                        solve += (i + ",");
                                    }
                                } else if (a01.group(4).strip().endsWith("%")) {         //%a
                                    String likeShape = a01.group(4).replaceAll("%", "");
                                    if (rows.get(i).get(rows.get(0).indexOf(a01.group(2))).startsWith(likeShape)) {
                                        solve += (i + ",");
                                    }
                                } else {        //a%b
                                    String likeShape = a01.group(4);
                                    String[] parts = likeShape.split("%");
                                    if (rows.get(i).get(rows.get(0).indexOf(a01.group(2))).startsWith(parts[0]) && rows.get(i).get(rows.get(0).indexOf(a01.group(2))).endsWith(parts[1])) {
                                        solve += (i + ",");
                                    }
                                }
                            } else if (numOfPersents == 2) {
                                String likeShape = a01.group(4).replaceAll("%", "");
                                if (a01.group(4).strip().endsWith("%") && a01.group(4).strip().startsWith("%")) {          //%a%
                                    if (rows.get(i).get(rows.get(0).indexOf(a01.group(2))).contains(likeShape)) {
                                        solve += (i + ",");
                                    }
                                }
                            } else {
                                System.out.println("The number of % entered is wrong!");
                            }
                        } else {
                            System.out.println("There is no % sign!");
                        }
                    }
                }

                solve = replaceLast(solve, ",", "");

                str = str.replace(a01.group(1), solve);
            }
        }else {
            System.out.println("Incorrect syntax for WHERE has been entered! Please re-enter");
            System.out.println("The correct syntax of this command is as follows:");
            System.out.println("    If you want to have a condition in command:");
            System.out.println("        SELECT column1, column2, ... FROM table_name WHERE condition (AND/OR)?;");
            System.out.println("            For condition syntax:  (value/pattern must be inside '...' (single quotation).)");
            System.out.println("                column_name = 'value'");
            System.out.println("                column_name LIKE 'pattern'");
            System.out.println("                column_name REGEXP 'pattern'");
        }

        //and  /  or
        String[] str_split = str.split("[Aa][Nn][Dd]|[Oo][Rr]");
        ArrayList<ArrayList<Integer>> resultOfPhrases = new ArrayList<>();

        for (String part : str_split) {
            ArrayList<Integer> allRows = new ArrayList<>();
            for (int i = 1; i < rows.size(); i++) {                     //allRows:[1,2,3,...,rowz.size()] <- for using in (not) part
                allRows.add(i);
            }
            boolean notFind = false;
            if (part.toLowerCase().contains("not ")) {
                notFind = true;
                part = part.replace("not", "").replace(" ", "");
            }

            String[] strArray = part.strip().split(",");
            ArrayList<Integer> arrayList = new ArrayList<>();
            for (String s : strArray) {
                if (!s.isEmpty()) {
                    s = s.strip();
                    int i = Integer.parseInt(s);                    //BAD BUG!!!****************************************
                    arrayList.add(i);
                }
            }
            if (notFind) {
                allRows.removeAll(arrayList);
                resultOfPhrases.add(allRows);
            } else {
                resultOfPhrases.add(arrayList);
            }


        }
        ArrayList<String> operators = new ArrayList<>();//[and , or]
        Pattern pattern1 = Pattern.compile("(and|or)", Pattern.CASE_INSENSITIVE);
        Matcher matcher1 = pattern1.matcher(str);
        while (matcher1.find()) {
            operators.add(matcher1.group(1));                        //add->and  //add->or
        }
        for (int i = 0; i < operators.size(); i++) {
            if (operators.get(i).equalsIgnoreCase("or")) {
                finalAnswer.addAll(resultOfPhrases.get(i));
                finalAnswer.addAll(resultOfPhrases.get(i + 1));

            } else if (operators.get(i).equalsIgnoreCase("and")) {
                finalAnswer.addAll(resultOfPhrases.get(i));
                finalAnswer.retainAll(resultOfPhrases.get(i + 1));

            } else {
                System.out.println("We don't have this Operator");
            }
        }
        if (operators.size() == 0) {
            finalAnswer.addAll(resultOfPhrases.get(0));
        }
        HashSet<Integer> set = new HashSet<Integer>();
        for (int i : finalAnswer) {
            set.add(i);
        }
        finalAnswer.clear();
        for (Integer i : set) {
            finalAnswer.add(i);
        }
        //finish
        output = "";
        for (int i = 0; i < finalAnswer.size(); i++) {
            if (i == finalAnswer.size() - 1) {
                output += finalAnswer.get(i);
            } else {
                output += finalAnswer.get(i) + ",";
            }
        }
        return output;

    }

    static void printTable(ArrayList<Integer> printColumns) {
        for (int i = 0; i < rows.size(); i++) {
            String output = "";
            for (int numOfColumn : printColumns) {
                for (int j = 0; j < rows.get(i).size(); j++) {
                    if (j == numOfColumn) {
                        int padding = 50 - rows.get(i).get(j).length();
                        int leftPadding = padding / 2;
                        int rightPadding = padding - leftPadding;
                        output += "|" + " ".repeat(leftPadding) + rows.get(i).get(j) + " ".repeat(rightPadding);
                    }
                }
            }
            output += "|\n";
            System.out.println(output);
        }
    }

    static void printTable(ArrayList<Integer> printColumns, ArrayList<Integer> printRows) {
        for (int i = 0; i < rows.size(); i++) {
            if (printRows.contains(i) || i == 0) {
                String output = "";
                for (int numOfColumn : printColumns) {
                    for (int j = 0; j < rows.get(i).size(); j++) {
                        //for (int numOfRow : printRows) {
                        if (j == numOfColumn) {
                            int padding = 50 - rows.get(i).get(j).length();
                            int leftPadding = padding / 2;
                            int rightPadding = padding - leftPadding;
                            output += "|" + " ".repeat(leftPadding) + rows.get(i).get(j) + " ".repeat(rightPadding);
                        }
                    }
                }
                output += "|\n";
                System.out.println(output);
            }
        }
    }

    static void printTableDESC(ArrayList<ArrayList<String>> arrayLists, ArrayList<Integer> printColumns) {
        for (ArrayList<String> al : arrayLists) {
            String output = "";
            for (int numOfColumn : printColumns) {
                for (int j = 0; j < al.size(); j++) {
                    if (j == numOfColumn) {
                        int padding = 50 - al.get(j).length();
                        int leftPadding = padding / 2;
                        int rightPadding = padding - leftPadding;
                        output += "|" + " ".repeat(leftPadding) + al.get(j) + " ".repeat(rightPadding);
                    }
                }
            }
            output += "|\n";
            System.out.println(output);
        }
    }

    static void printTableASC(ArrayList<ArrayList<String>> arrayLists, ArrayList<Integer> printColumns) {
        {
            String output = "";
            for (int numOfColumn : printColumns) {
                for (int j = 0; j < arrayLists.get(0).size(); j++) {
                    if (j == numOfColumn) {
                        int padding = 50 - rows.get(0).get(j).length();
                        int leftPadding = padding / 2;
                        int rightPadding = padding - leftPadding;
                        output += "|" + " ".repeat(leftPadding) + rows.get(0).get(j) + " ".repeat(rightPadding);
                    }
                }
            }
            output += "|\n";
            System.out.println(output);
        }
        for (int i = arrayLists.size() - 1; i >= 1; i--) {
            String output = "";
            for (int numOfColumn : printColumns) {
                for (int j = 0; j < rows.get(i).size(); j++) {
                    if (j == numOfColumn) {
                        int padding = 50 - rows.get(i).get(j).length();
                        int leftPadding = padding / 2;
                        int rightPadding = padding - leftPadding;
                        output += "|" + " ".repeat(leftPadding) + rows.get(i).get(j) + " ".repeat(rightPadding);
                    }
                }
            }
            output += "|\n";
            System.out.println(output);
        }
    }

    static boolean isNumeric(String str) {
        return str.matches("^-?[0-9]+\\.?[0-9]*$");
    }

    public static String[] readFromCommandFile(String filePath) {
        ArrayList<String> lines = new ArrayList<>();
        Scanner reader = null;
        try {
            reader = new Scanner(new File(filePath));
            reader.useDelimiter(";");
            while (reader.hasNext()) {
                String line = reader.next().trim();
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("!!! File dose NOT EXIST !!!");
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return lines.toArray(new String[0]);
    }

    static void printMenu() {
        System.out.println("\nWelcome to the Database Management Program".indent(10));
        System.out.println("1.Enter commands MANUALLY".indent(15));
        System.out.println("2.Enter commands as a FILE".indent(15));
        System.out.println("3.Exit".indent(15));
        System.out.print("\t\t\t\t   Action :");
    }
}





