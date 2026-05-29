package org.insa.graphs.algorithm.shortestpath;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.insa.graphs.algorithm.ArcInspector;
import org.insa.graphs.algorithm.ArcInspectorFactory;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;


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
        int debut = 0;
        for(int i = debut; i < taille; i++){
            Node node = tabNodes.get(i);
            Frequentation frequ = new Frequentation(node);
            tabfreq[i] = frequ;
        }
        Arrays.sort(tabfreq, Comparator.comparingInt(Frequentation::getFrequentation));
        double seuil = 0; // A changer par une valeur qui sera donnée par l'utilisateur
        Path solutionFinal;
        List<ArcInspector> arcInspectors = ArcInspectorFactory.getAllFilters();
        ShortestPathData sPData = new ShortestPathData(data.getGraph(), data.getOrigin(), data.getDestination(), arcInspectors.get(0));
        DijkstraAlgorithm graphDij = new DijkstraAlgorithm(sPData);
        ShortestPathSolution dijSol = graphDij.run();
        Path pathDij = dijSol.getPath();
        double tempsMin = pathDij.getMinimumTravelTime();
        int milieu = taille/2;

        Graph graphe_prov = data.getGraph();
        while (tabNodes.size() != 0) {
            milieu = (debut + taille)/2;
            DijkstraAlgorithmV2 nvGraphDij = new DijkstraAlgorithmV2(sPData, tabNodes);
            ShortestPathSolution nvDijSol = nvGraphDij.run();
            Path nvPathDij = dijSol.getPath();
            double nvTemps = nvPathDij.getMinimumTravelTime();
            if (nvTemps <= tempsMin + seuil){
                solution = nvDijSol;
                for(int indice = 0; indice < milieu +1; indice++){
                    tabNodes.remove(indice);
                }
                taille = taille/2;
            }
            else{
                for(int indice = milieu + 1; indice < taille; indice++){
                    tabNodes.remove(indice);
                }
                debut = milieu + 1;
                taille = taille / 2;
            }

        }
        return solution;    
        }
        // when the algorithm terminates, return the solution that has been found

    }

