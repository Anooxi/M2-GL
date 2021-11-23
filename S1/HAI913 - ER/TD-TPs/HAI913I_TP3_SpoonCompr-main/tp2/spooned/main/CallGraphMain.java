package main;
import graphs.AllCouplesGraph;
import graphs.CallGraph;
import graphs.CoupleGraph;
import graphs.DendrogramGraph;
import graphs.StaticCallGraph;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Scanner;
import parsers.Spoon;
import processors.ASTProcessor;
import processors.ProcessorClustering;
import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.reflect.CtModel;
public class CallGraphMain extends AbstractMain {
    @Override
    protected void menu() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n1. Static call graph.");
        builder.append("\n2. ProcessorClustering.");
        builder.append("\n3. DendrogramGraph : Cr\u00e9ation d\'un dendrogram en png (n\'envoie plus le bon)");
        builder.append("\n4. CoupleGraph : Cr\u00e9ation des fichiers CoupleGraph.dot et graphCouple.png pour un couple donn\u00e9e");
        builder.append("\n5. AllCouplesGraph : Cr\u00e9ation des fichiers CouplesGraph.dot et graphCouples.png pour un src donn\u00e9, veuillez donner une liste de classe raisonnable");
        builder.append("\n6. Static call graph (avec Spoon).");
        builder.append("\n7. Calculer la m\ufffdtrique de couplage entre deux classes A et B (avec Spoon)");
        builder.append("\n8. G\ufffdn\ufffdrez un graphe de couplage pond\ufffdr\ufffd entre les classes de l\'application. (avec Spoon)");
        builder.append("\n9. G\ufffdn\ufffdrer le regroupement hierrarchique des classes (avec Spoon)");
        builder.append("\n10. G\ufffdn\ufffdrer les groupes de classes coupl\ufffds (partitions)s (avec Spoon)");
        builder.append("\n11. Dynamic call graph.");
        builder.append("\n12. Help menu.");
        builder.append(("\n" + QUIT) + ". To quit.");
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
                Thread.sleep(3000);
            } while (!userInput.equals(QUIT) );
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void processUserInput(String userInput, ASTProcessor processor) {
        CallGraph callGraph = ((CallGraph) (processor));
        Scanner sc = new Scanner(System.in);
        Launcher ourLauncher = new Launcher();
        ourLauncher.addInputResource(TEST_PROJECT_PATH);
        // Setting the environment for Spoon
        Environment environment = ourLauncher.getEnvironment();
        environment.setCommentEnabled(true);// represent the comments from the source code in the AST

        environment.setAutoImports(true);
        ourLauncher.getEnvironment().setNoClasspath(true);
        ourLauncher.run();
        CtModel model = ourLauncher.getModel();
        Spoon analyze = new Spoon(TEST_PROJECT_PATH, model);
        try {
            switch (userInput) {
                // /home/hayaat/Desktop/Master/M2/Git/design_patterns
                case "1" :
                    callGraph = StaticCallGraph.createCallGraph(TEST_PROJECT_PATH);
                    callGraph.log();
                    break;
                case "2" :
                    long start = System.currentTimeMillis();
                    callGraph = StaticCallGraph.createCallGraph(TEST_PROJECT_PATH);
                    ProcessorClustering processorClustering = new ProcessorClustering(TEST_PROJECT_PATH, callGraph);
                    long end = System.currentTimeMillis();
                    processorClustering.log();
                    NumberFormat formatter = new DecimalFormat("#0.00000");
                    System.out.print(("Execution time is " + formatter.format((end - start) / 1000.0)) + " seconds");
                    break;
                case "3" :
                    callGraph = StaticCallGraph.createCallGraph(TEST_PROJECT_PATH);
                    DendrogramGraph dendrogramGraph = new DendrogramGraph(TEST_PROJECT_PATH, callGraph);
                    dendrogramGraph.createFiles();
                    break;
                case "4" :
                    callGraph = StaticCallGraph.createCallGraph(TEST_PROJECT_PATH);
                    CoupleGraph coupleGraph = new CoupleGraph(TEST_PROJECT_PATH, callGraph);
                    System.out.println("Inserez le nom de la première classe (avec le package au besoin : package.class)");
                    String className1 = sc.next();
                    System.out.println("Inserez le nom de la deuxième classe (avec le package au besoin : package.class)");
                    String className2 = sc.next();
                    coupleGraph.createFiles(className1, className2);
                    return;
                case "5" :
                    callGraph = StaticCallGraph.createCallGraph(TEST_PROJECT_PATH);
                    AllCouplesGraph allCouplesGraph = new AllCouplesGraph(TEST_PROJECT_PATH, callGraph);
                    allCouplesGraph.createFiles();
                    return;
                case "6" :
                    System.out.println("R?cup?ration des donn?es du model avec Spoon..");
                    analyze.getDataWithSpoon(model);
                    System.out.println("Ecriture du callGraph dans le fichier static-callGraphSpoon..");
                    analyze.log();
                    break;
                case "7" :
                    System.out.println("Inserez le nom de  la classe A");
                    String classNameA = sc.next();
                    System.out.println("Inserez le nom de la classe B");
                    String classNameB = sc.next();
                    System.out.println("Calcul de la m?trique..");
                    analyze.getDataWithSpoon(model);
                    System.out.println(analyze.getCouplingMetric(classNameA, classNameB));
                    break;
                case "8" :
                    analyze.getDataWithSpoon(model);
                    analyze.createCouplingGraph();
                    break;
                case "9" :
                    System.err.println("Not implemented yet");
                    break;
                case "10" :
                    System.err.println("Not implemented yet");
                    break;
                case QUIT :
                    System.out.println("Bye...");
                    return;
                default :
                    System.err.println("Sorry, wrong input. Please try again.");
                    return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}