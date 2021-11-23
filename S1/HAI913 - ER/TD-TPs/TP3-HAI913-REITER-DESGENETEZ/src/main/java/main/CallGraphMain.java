package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import graphs.*;
import processors.ASTProcessor;

public class CallGraphMain extends AbstractMain {

    @Override
    protected void menu() {
        String builder = "1. Static call graph." +
                "\n2. Dynamic call graph." +
                "\n3. JDT coupling graph (Ex.1)" +
                "\n4. Spoon coupling graph (Ex.3)" +
                "\n5. Help menu." +
                "\n" + QUIT + ". To quit.";

        System.out.println(builder);
    }

    public static void main(String[] args) {

        CallGraphMain main = new CallGraphMain();
        BufferedReader inputReader;
        CallGraph callGraph = null;
        try {
            inputReader = new BufferedReader(new InputStreamReader(System.in));
            if (args.length < 1)
                setTestProjectPath(inputReader);
            else
                verifyTestProjectPath(inputReader, args[0]);
            String userInput = "";

            do {
                main.menu();
                userInput = inputReader.readLine();
                main.processUserInput(userInput, callGraph);
                Thread.sleep(3000); //TODO : delete sleep

            } while (!userInput.equals(QUIT));

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void processUserInput(String userInput, ASTProcessor processor) {
        CallGraph callGraph; //TODO : check if necessary : = (CallGraph) processor;
        CouplingGraph couplingGraph;
        SpoonGraph spoonGraph;
        try {
            switch (userInput) {
                case "1":
                    callGraph = StaticCallGraph.createCallGraph(TEST_PROJECT_PATH);
                    callGraph.log();
                    break;

                case "2":
                    System.err.println("Not implemented yet");
                    break;

                case "3":
                    couplingGraph = StaticCouplingGraph.createCouplingGraph(TEST_PROJECT_PATH);
                    couplingGraph.log();
                    return;

                case "4":
                    spoonGraph = new SpoonGraph(TEST_PROJECT_PATH);
                    spoonGraph.printCouplageAll();
                    return;

                case "5": //TODO : Implement help menu
                    return;

                case QUIT:
                    System.out.println("Bye...");
                    return;

                default:
                    System.err.println("Sorry, wrong input. Please try again.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
