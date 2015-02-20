package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import javafx.util.Pair;
import neural.NeuralNet;
/*
 Parses .net files of the form:
 \n
 neuron_name\n
 connected_neuron_name connection_weight\n
 connected_neuron_name connection_weight\n
 connected_neuron_name connection_weight\n
 connected_neuron_name connection_weight\n
 \n
 neuron_name\n
 connected_neuron_name connection_weight\n
 connected_neuron_name connection_weight\n
 connected_neuron_name connection_weight\n
 */

public final class Parser {

    public static NeuralNet load(final File file, final boolean preserveNeuronNames) {
        final TreeMap<String, ArrayList<Pair<String, Integer>>> treeMap = new TreeMap(); // holds Key-Value pairs of the form < neuron_name, ArrayList<<connected_neuron_name, connection_weight>> >
//        int newID = 0; // we'll increment this with each addition of a neuron so that each one has a unique ID
        try (final BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String readLine;
            ArrayList<Pair<String, Integer>> currList = null; // currList holds the ID of the current neuron at its head in the form <null, ID>, and all of its connections in the form <neuron_name, weight_of_connection>
            while ((readLine = bufferedReader.readLine()) != null) {
                final int len = readLine.length();
                if (len == 0) { // if we reach a blank line, the current connection list is done and we start a new entry in the hashmap
                    currList = new ArrayList<>();
                    currList.add(null);//new Pair(null, newID++)); // put the new ID at the head of the list for later access
                    readLine = bufferedReader.readLine();
                    treeMap.put(readLine, currList); // add the new neuron to the hashmap
                } else { // we are currently in the list of connections
                    final int spaceIndex = readLine.indexOf(' ');
                    currList.add(new Pair(readLine.substring(0, spaceIndex), Integer.parseInt(readLine.substring(spaceIndex + 1, len)))); // so we add the new one to currList
                }
            }
        } catch (final IOException e) {
            System.out.println(file + " could not be opened!");
            return null;
        }
        int uniqueID = 0;
        for (final ArrayList<Pair<String, Integer>> list : treeMap.values()) {
            list.set(0, new Pair<>(null, uniqueID++));
        }
        // now we go through every entry in the hashmap, and fill in our int[][]. Each neuron has its own int[] at the index specified by its unique ID, of the form [accumulated_energy, connection_id, connection_weight, connection_id, connection_weight, ...]
        final int[][] neuralNet = new int[uniqueID][];// new int[newID][];
//        treeMap.forEach((final String neuronName, final ArrayList<Pair<String, Integer>> list) -> {
        treeMap.values().stream().forEach((final ArrayList<Pair<String, Integer>> list) -> {
            final int len = list.size();
            final int[] ints = new int[(len << 1)]; // we split the pairs apart and add two spots for the accumulated energy, so the size has to be twice the original (excluding the null,ID pair) plus one
            for (int i = len - 1; i != 0; i--) {
                final Pair<String, Integer> pair = list.get(i);
                final int twiceI = (i << 1);
                ints[twiceI] = treeMap.get(pair.getKey()).get(0).getValue();
                ints[twiceI + 1] = pair.getValue();
            }
            neuralNet[list.get(0).getValue()] = ints;
        });
        if (preserveNeuronNames) {
            final String[] translationTable = new String[neuralNet.length];
            treeMap.forEach((final String neuronName, final ArrayList<Pair<String, Integer>> pairList) -> {
                translationTable[pairList.get(0).getValue()] = neuronName;
            });
            return new NeuralNet(neuralNet, translationTable);
        }
        return new NeuralNet(neuralNet);
    }
}
