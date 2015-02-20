package neural;

import java.util.Arrays;

public final class NeuralNet {

    static final int THRESHOLD = 30;
    private final int[][] neurons;
    private final String[] translationTable;
    private int currState = 0, nextState = 1;

    public NeuralNet(final int[][] neurons) {
        this.neurons = neurons;
        this.translationTable = null;
    }

    public NeuralNet(final int[][] neurons, final String[] translationTable) {
        this.neurons = neurons;
        this.translationTable = translationTable;
    }

    void step() {//naive method - no queueing
        // fire all neurons which are over the threshold
//        System.out.println("\n\nSTEP:");
        for (int i = neurons.length - 1; i >= 0; i--) {
            final int[] currNeuron = neurons[i]; // if we put this inside the if statement, if neuron firing is sparse it'll save time by not storing the array in most cases
            if (currNeuron[currState] >= THRESHOLD) {
                //fire neuron
//                System.out.println(translationTable[i] + " fired!");
                currNeuron[nextState] = 0;
                for (int j = currNeuron.length - 1; j != 1; j -= 2) {
                    int[] a = neurons[currNeuron[j - 1]];
                    a[nextState] += currNeuron[j];
                }
            }
        }
        // copy each neuron to its value at the end of the last state
        for (int i = neurons.length - 1; i >= 0; i--) {
            final int[] currNeuron = neurons[i];
            currNeuron[currState] = currNeuron[nextState];
        }
        // swap currState and nextState
        nextState = currState;
        currState ^= 1;
    }

    /**
     * @param neuronName
     * @return pointer to the neuron's data (changes are reflected in the
     * NeuralNet)
     */
    int[] get(final String neuronName) {
        return neurons[Arrays.binarySearch(translationTable, neuronName)];
    }

    /**
     *
     * @return Formatted printout of the neural network
     */
    @Override
    public String toString() {
        return translationTable == null ? rawToString() : translatedToString();
    }

    private String rawToString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i != neurons.length; i++) {
            final int[] connections = neurons[i];
            sb.append("\n\n").append(i).append(" (").append(connections[0]).append(", ").append(connections[1]).append("):\n");
            for (int j = 2; j != connections.length; j++) {
                sb.append(connections[j]).append(' ').append(connections[++j]).append('\n');
            }
        }
        return sb.toString();
    }

    private String translatedToString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i != neurons.length; i++) {
            final int[] connections = neurons[i];
            sb.append("\n\n").append(i).append(' ').append(translationTable[i]).append(" (").append(connections[0]).append(", ").append(connections[1]).append("):\n");
            for (int j = 2; j != connections.length; j++) { // skip the first two ints, which just hold the accumulated energy of each neuron
                sb.append(translationTable[connections[j]]).append(' ').append(connections[++j]).append('\n');
            }
        }
        return sb.toString();
    }

    int getCurrState() {
        return currState;
    }

    int getNextState() {
        return nextState;
    }
}
