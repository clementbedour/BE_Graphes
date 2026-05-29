package org.insa.graphs.algorithm.shortestpath;
import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.insa.graphs.algorithm.ArcInspector;
import org.insa.graphs.algorithm.ArcInspectorFactory;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;

import jdk.jfr.Frequency;


public class Ecart extends ShortestPathAlgorithm {

    public Ecart(ShortestPathData data) {
        super(data);
    }

    @Override
    protected ShortestPathSolution doRun() {
        // retrieve data from the input problem (getInputData() is inherited from the
        // parent class ShortestPathAlgorithm)
        final ShortestPathData data = getInputData();
        ShortestPathSolution solution = null;
        List<Node> tabNodes = data.getGraph().getNodes();
        int taille = tabNodes.size();
        Frequentation[] tabfreq = new Frequentation[taille];
        for(int i = 0; i < taille; i++){
            Node node = tabNodes.get(i);
            Frequentation frequ = new Frequentation(node);
            tabfreq[i] = frequ;
        }
        Arrays.sort(tabfreq, Comparator.comparingInt(Frequentation::getFrequentation));
        int seuil = 1; // A changer par une valeur qui sera donnée par l'utilisateur
        Path pathDij;
        List<ArcInspector> arcInspectors = ArcInspectorFactory.getAllFilters();
        ShortestPathData sPData = new ShortestPathData(data.getGraph(), data.getOrigin(), data.getDestination(), arcInspectors.get(0));
        DijkstraAlgorithm graphDij = new DijkstraAlgorithm(sPData);
        ShortestPathSolution dijSol = graphDij.run();
        Duration temps = dijSol.getSolvingTime();
        pathDij = dijSol.getPath();
        int milieu = taille/2;
        Graph graphe_prov = data.getGraph();
        while (tabNodes.size() != 0) {
            for(int i = milieu +1; i < taille; i++){
            Node node = tabfreq[i].getSommet();
            graphe_prov.
            tabfreq[i] = frequ;
        }
            
        }
        // when the algorithm terminates, return the solution that has been found
        return solution;

    }

}


