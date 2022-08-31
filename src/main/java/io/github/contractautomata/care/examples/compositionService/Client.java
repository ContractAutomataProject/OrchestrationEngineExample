package io.github.contractautomata.care.examples.compositionService;

import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.converters.AutDataConverter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Client {
    private final AutDataConverter<CALabel> conv;
    private Scanner scanner;
    private int bound;

    public Scanner getScanner() {
        return scanner;
    }

    public Client() {
        this.scanner = new Scanner(System.in);
        this.conv = new AutDataConverter<>(CALabel::new);
        this.bound = -1;
    }

    public Payload create(String result) {
        if (result==null){
            List<String> automata = new ArrayList<>();
            while(true) {
                if (!automata.isEmpty()) {
                    System.out.println("Do you want to add other automata to compose? (yes/no)");
                    if (!scanner.nextLine().contains("y"))
                        break;
                }
                System.out.println("Enter the path where the file is located");
                try {
                    automata.add(conv.importMSCA(scanner.nextLine()).toString());
                } catch(Exception e){
                    System.out.println("The name of the file is wrong " +e.getMessage());
                }
            }
            System.out.println("Do you want the composition to be closed under agreement? (yes/no)");
            boolean closed = scanner.nextLine().contains("y");
            System.out.println("Do you want the composition to be bounded? (yes/no)");
            if (scanner.nextLine().contains("y")){
                System.out.println("Type the desired bound");
                try {
                    this.bound = scanner.nextInt();
                }catch(InputMismatchException e){
                    System.out.println("The inserted value is not an integer, no bound will be used.");
                    this.bound=-1;
                }
            }
            return new Payload(automata,closed,bound);
        } else {
            System.out.println("The composition has been computed : "+result.toString());
            save(result);
            return null;
        }
    }

    public Integer update(String result){
        if (result==null) {
            int increment;
            System.out.println("The current bound is " + this.bound +", type the increment of the bound:");
            try {
                increment = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("The inserted value is not an integer, no increment will be used.");
                return bound;
            }
            if (increment>=0) {
                this.bound+=increment;
            }
            return bound;
        }
        else {
            System.out.println("The composition has been computed : "+result.toString());
            save(result);
            return null;
        }
    }

    public String quit(String arg){
        bound=-1;
        return "";
    }

    private void save(String result)  {
        try{
            Thread.sleep(200);
        } catch(InterruptedException e){}
        System.out.println("Do you want the save the composition? (yes/no)");
        String reply = scanner.nextLine();
        if (reply.length()==0)
          reply=scanner.nextLine();
        if (reply.contains("y")) {
            System.out.println("Enter the name of the file to store");
            String filename = scanner.nextLine();
            try {
                Files.write(Paths.get(filename), result.getBytes());
            } catch (IOException e) {
                System.out.println("Error in storing the file " + e.getMessage());
            }
        } else System.out.println("The composition will not be saved.");
    }

    public int getBound() {
        return bound;
    }
}
