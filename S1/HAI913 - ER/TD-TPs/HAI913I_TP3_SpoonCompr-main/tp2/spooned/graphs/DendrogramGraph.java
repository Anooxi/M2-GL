package graphs;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import processors.ProcessorClustering;
public class DendrogramGraph {
    ProcessorClustering processorClustering;

    String projectPath;

    CallGraph callgraph;

    ArrayList<ArrayList<String>> clusters;

    public DendrogramGraph(String path, CallGraph callGraph) {
        this.callgraph = callGraph;
        this.projectPath = path;
        this.processorClustering = new ProcessorClustering(path, callGraph);
        this.clusters = processorClustering.clustering();
    }

    public String getGraphAsDot() {
        StringBuilder builder = new StringBuilder("digraph G {\n");
        Integer c = 1;
        for (ArrayList<String> cluster : clusters) {
            String clusterName = "C" + c;
            // les feuilles
            if (cluster.size() == 2) {
                builder.append(('"' + cluster.get(0)) + '"').append(" -> ").append(('"' + clusterName) + '"').append(" ");
                builder.append(('"' + cluster.get(1)) + '"').append(" -> ").append(('"' + clusterName) + '"').append(" ");
            } else // On verifie si le cluster et sous ensemble d'un autre
            {
                Integer IndexPlusGrandSousEnsemble = -1;
                Integer GrandPlusGrandSousEnsemble = 0;
                for (int i = 0; i < (c - 1); i++) {
                    // cluster actuel
                    ArrayList<String> array1 = new ArrayList<String>(cluster);
                    // ancien cluster
                    ArrayList<String> array2 = new ArrayList<String>(clusters.get(i));
                    // si 2 est sous ensembles alors
                    if (array1.containsAll(array2) && (array2.size() > GrandPlusGrandSousEnsemble)) {
                        IndexPlusGrandSousEnsemble = i;
                    }
                }
                if (IndexPlusGrandSousEnsemble > (-1)) {
                    ArrayList<String> array1 = new ArrayList<String>(cluster);
                    ArrayList<String> array2 = new ArrayList<String>(clusters.get(IndexPlusGrandSousEnsemble));
                    array1.removeAll(array2);
                    builder.append((('"' + "C") + (IndexPlusGrandSousEnsemble + 1)) + '"').append(" -> ").append(('"' + clusterName) + '"').append(" ");
                    for (String s : array1) {
                        builder.append(('"' + s) + '"').append(" -> ").append(('"' + clusterName) + '"').append(" ");
                    }
                }
            }
            c++;
        }
        builder.append("\n}");
        return builder.toString();
    }

    public void saveGraph() {
        try {
            FileWriter fw = new FileWriter("DendrogramGraph.dot", false);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);
            out.println(this.getGraphAsDot());
            out.close();
            bw.close();
            fw.close();
        } catch (IOException e) {
            System.err.println("Exception ecriture fichier");
            e.printStackTrace();
        }
    }

    public void saveGraphAsPNG() throws IOException {
        MutableGraph g = new Parser().read(this.getGraphAsDot());
        Graphviz.fromGraph(g).render(Format.PNG).toFile(new File("dendrogram.png"));
    }

    public void createFiles() throws IOException {
        System.out.println("DendrogramGraph: affichage de DendrogramGraph.clusters (proviens de ProcessorClustering.clustering()) \n ");
        for (ArrayList<String> array : clusters) {
            System.out.println(array.toString());
        }
        this.saveGraph();
        this.saveGraphAsPNG();
    }
}