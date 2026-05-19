package org.insa.graphs.gui.simple;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.insa.graphs.algorithm.ArcInspector;
import org.insa.graphs.algorithm.ArcInspectorFactory;
import org.insa.graphs.algorithm.shortestpath.BellmanFordAlgorithm;
import org.insa.graphs.algorithm.shortestpath.DijkstraAlgorithm;
import org.insa.graphs.algorithm.shortestpath.ShortestPathData;
import org.insa.graphs.algorithm.shortestpath.ShortestPathSolution;
import org.insa.graphs.gui.drawing.Drawing;
import org.insa.graphs.gui.drawing.components.BasicDrawing;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;
import org.insa.graphs.model.Point;
import org.insa.graphs.model.io.BinaryGraphReader;
import org.insa.graphs.model.io.GraphReader;

public class Launch {

    /**
     * Create a new Drawing inside a JFrame an return it.
     *
     * @return The created drawing.
     * @throws Exception if something wrong happens when creating the graph.
     */
    public static Drawing createDrawing() throws Exception {
        BasicDrawing basicDrawing = new BasicDrawing();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("BE Graphes - Launch");
                frame.setLayout(new BorderLayout());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
                frame.setSize(new Dimension(800, 600));
                frame.setContentPane(basicDrawing);
                frame.validate();
            }
        });
        return basicDrawing;
    }

    public static void main(String[] args) throws Exception {
        // visit these directory to see the list of available files on commetud.
        final String mapName
                = "/mnt/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/guadeloupe.mapgr";
        final String pathName
                = "/mnt/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Paths/path_fr31insa_rangueil_r2.path";

        final Graph graph;
        Path pathDij, pathBell;
        // create a graph reader
        try (final GraphReader reader = new BinaryGraphReader(new DataInputStream(
                new BufferedInputStream(new FileInputStream(mapName))))) {

            // TODO: read the graph OK
            graph = reader.read();
        }

        // create the drawing
        final Drawing drawing = createDrawing();

        // TODO: draw the graph on the drawing OK
        drawing.drawGraph(graph);

        //chemin inexistant, chemin de longueur nulle, trajet court, trajet long
        //List<String> resultatTest = new ArrayList<String>();
        //resultatTest.add("INEXISTANT");
        //resultatTest.add("NULLE");
        //resultatTest.add("REUSSITE_C");
        //resultatTest.add("REUSSITE_L");
        List<Node> list_Noeud = graph.getNodes();
        List<Node> point = new ArrayList<>();
        point.add(list_Noeud.get(15782)); //Debut inexistant
        point.add(list_Noeud.get(19906)); //Fin inexistant
        point.add(list_Noeud.get(15782));//Debut nulle
        point.add(list_Noeud.get(15782));//Fin nulle
        point.add(list_Noeud.get(12085));//Debut court
        point.add(list_Noeud.get(11560));//Fin court
        point.add(list_Noeud.get(11602));//Debut long
        point.add(list_Noeud.get(11710));//Fin long

        Node idOrigin;
        Node idDestination;
        int somme = 0;

        for (int j = 0; j < 4; j++) {
            idOrigin = point.get(2 * j);
            idDestination = point.get((2 * j) + 1);

            List<ArcInspector> arcInspectors = ArcInspectorFactory.getAllFilters();
            ShortestPathData sPData = new ShortestPathData(graph, idOrigin, idDestination, arcInspectors.get(0));

            DijkstraAlgorithm graphDij = new DijkstraAlgorithm(sPData);
            BellmanFordAlgorithm graphBell = new BellmanFordAlgorithm(sPData);

            ShortestPathSolution dijSol = graphDij.run();
            ShortestPathSolution bellSol = graphBell.run();

            pathDij = dijSol.getPath();
            pathBell = bellSol.getPath();
            // J'ai le path de mes Bellman et de Dijkstra Il faut maintenant que je test si c OK

            boolean result = true;

            if (j == 0) {
                if (pathBell == null && pathDij == null) {
                    System.err.println("REUSSITE : Les points ne sont pas atteignables.\n");
                    System.err.println("TEST numéro " + j + " réussis.\n");
                    somme = somme + 1;
                } else {
                    System.err.println("ECHEC : Les points ne sont pas atteignables.\n");
                    System.err.println("TEST numéro " + j + " non réussis.\n");
                }
            } else {
                List<Arc> arcDij = pathDij.getArcs();
                if (j == 1) {
                    if (arcDij.isEmpty()) {
                        System.err.println("REUSSITE : Les points ne sont pas atteignables.\n");
                        System.err.println("TEST numéro " + j + " réussis.\n");
                        somme = somme + 1;
                    } else {
                        System.err.println("ECHEC : Les points ne sont pas atteignables.\n");
                        System.err.println("TEST numéro " + j + " non réussis.\n");
                    }
                } else {
                    List<Arc> arcBell = pathBell.getArcs();
                    int tailleBell = arcBell.size();
                    if (tailleBell != arcBell.size()) { //pas la même taille donc c tchao
                        System.err.println("ECHEC : Les chemins ne font pas la même taille.\n");
                        System.err.println("TEST numéro " + j + " non réussis.\n");
                    } else {
                        if (j == 2) {
                            for (int i = 0; i < tailleBell; i++) {
                                Node nodeBell = arcBell.get(i).getOrigin();
                                Node nodeDij = arcDij.get(i).getOrigin();
                                if (nodeBell.getId() != nodeDij.getId()) {
                                    result = false;
                                    System.err.println("ECHEC : Les chemins court ne sont pas identiques.\n");
                                    System.err.println("TEST numéro " + j + " non réussis.\n");
                                    break;
                                }
                            }
                            if (result == true) {
                                System.err.println("REUSSITE : Le parcour de chemin court sont identiques.\n");
                                System.err.println("TEST numéro " + j + " réussis.\n");
                                somme = somme + 1;
                            }
                            result = true;
                        }
                        if (j == 3) {
                            for (int i = 0; i < tailleBell; i++) {
                                Node nodeBell = arcBell.get(i).getOrigin();
                                Node nodeDij = arcDij.get(i).getOrigin();
                                if (nodeBell.getId() != nodeDij.getId()) {
                                    result = false;
                                    System.err.println("ECHEC : Les chemins long ne sont pas identiques.\n");
                                    System.err.println("TEST numéro " + j + " non réussis.\n");
                                    break;
                                }
                            }
                            if (result == true) {
                                System.err.println("REUSSITE : Le parcour de chemin long sont identiques.\n");
                                System.err.println("TEST numéro " + j + " réussis.\n");
                                somme = somme + 1;
                            }

                        }
                    }
                }
            }
        }

        System.err.println("GLOBAL : Le nombre total de test réussi est de " + somme);
    }

    /*  TODO: create a path reader
        System.out.println("TESSSSSSSSSSSSST\n");
        try (final PathReader pathReader = null) { baleck c pour comparer avec un fichier, faudra utiliser un binary

            // TODO: read the path OK
            path = pathReader.readPath(graph);
        }
         System.out.println("TESSSSSSSSSSSSST1111111111111\n");

        // TODO: draw the path on the drawing Ok (pareil qu'au dessus)

        drawing.drawGraph(graph); */
}
