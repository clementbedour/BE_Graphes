package org.insa.graphs.algorithm.shortestpath;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.insa.graphs.algorithm.AbstractInputData;
import org.insa.graphs.algorithm.AbstractSolution;
import org.insa.graphs.algorithm.ArcInspector;
import org.insa.graphs.algorithm.ArcInspectorFactory;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;

public class Ecart extends ShortestPathAlgorithm {

    public Ecart(ShortestPathData data) {
        super(data);
    }
    @Override
    protected ShortestPathSolution doRun() {
        final ShortestPathData data = getInputData();
        System.out.println("Test Ecart");
        
        double tolerance = 1.2;
        
        List<Node> tabNodes = data.getGraph().getNodes();
        int taille = tabNodes.size();
        Frequentation[] tabfreq = new Frequentation[taille];
        
        // Init du tab
        for(int i = 0; i < taille; i++){
            Node node = tabNodes.get(i);
            tabfreq[i] = new Frequentation(node);
        }
        
        // Tri du tab dans l'odre croissant de fréquentation
        Arrays.sort(tabfreq, Comparator.comparingInt(Frequentation::getFrequentation));
        

        List<ArcInspector> arcInspectors = ArcInspectorFactory.getAllFilters();
        ShortestPathData sPData = new ShortestPathData(data.getGraph(), data.getOrigin(), data.getDestination(), arcInspectors.get(0));
        DijkstraAlgorithm graphDij = new DijkstraAlgorithm(sPData);
        System.out.println("Test Dij1 debut");
        ShortestPathSolution dijSol = graphDij.run();
        System.out.println("Test Dij1 fin");
        
        // Si aucun chemin de base n'existe, on s'arrête là
        if (!dijSol.isFeasible()) {
            return new ShortestPathSolution(data, AbstractSolution.Status.INFEASIBLE);
        }
        
        Path pathDij = dijSol.getPath();
        
        
        double coutOptimal;
        if (data.getMode() == AbstractInputData.Mode.TIME) {
            coutOptimal = pathDij.getMinimumTravelTime();
        } else {
            coutOptimal = pathDij.getLength();
        }
        double coutMaxAutorise = coutOptimal * tolerance;

       
        ShortestPathSolution bestSolution = dijSol; // Par défaut, la solution est le chemin normal
        int debut = 0;
        int fin = taille - 1;
        double frequence = 0;
        int[] carteFreq = new int[taille];
        for (int i = 0; i < taille; i++) {
            Node node = tabNodes.get(i);
            carteFreq[node.getId()] = node.getNumberOfSuccessors(); 
        }
        while (debut <= fin) {
            int milieu = (debut + fin) / 2;
            
            // On récupère la valeur du seuil qu'on teste actuellement
            int seuilTest = tabfreq[milieu].getFrequentation();
            
            // On lance le nouveau Dijkstra avec la carte et le seuil !
            DijkstraAlgorithm nvGraphDij = new DijkstraAlgorithm(data, carteFreq, seuilTest);
            ShortestPathSolution nvDijSol = nvGraphDij.run();
            
            if (nvDijSol.isFeasible()) {
                double nvCout = (data.getMode() == AbstractInputData.Mode.TIME) 
                                ? nvDijSol.getPath().getMinimumTravelTime() 
                                : nvDijSol.getPath().getLength();
                
                if (nvCout <= coutMaxAutorise) {
                    bestSolution = nvDijSol;
                    fin = milieu - 1; // On essaie d'être encore plus strict sur la fréquentation
                } else {
                    debut = milieu + 1; // Trajet trop long, on doit relâcher le seuil
                }
            } else {      
                debut = milieu + 1; // Trajet impossible, on doit relâcher le seuil
            }
        }
        return bestSolution;
}
}