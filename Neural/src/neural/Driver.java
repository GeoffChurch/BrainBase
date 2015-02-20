package neural;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Parser;

public final class Driver {

    final static Object lock = new Object();
//    static volatile int totalIPs = 0;
//    static volatile int goodIPs = 0;
    final static String IPCOMMAND = "ping %d.%d.%d.%d";
    final static String[] bumpNeuronNames = {"FLPR", "FLPL", "ASHL", "ASHR", "IL1VL", "IL1VR", "OLQDL", "OLQDR", "OLQVR", "OLQVL"};
    final static String[] foodNeuronNames = {"ADFL", "ADFR", "ASGR", "ASGL", "ASIL", "ASIR", "ASJR", "ASJL"};
    final static String[] muscleNeuronNames = {
        "MDL01", "MDR01",
        "MDL02", "MDR02",
        "MDL03", "MDR03",
        "MDL04", "MDR04",
        "MDL05", "MDR05",
        "MDL06", "MDR06",
        "MDL07", "MDR07",
        "MDL08", "MDR08",
        "MDL09", "MDR09",
        "MDL10", "MDR10",
        "MDL11", "MDR11",
        "MDL12", "MDR12",
        "MDL13", "MDR13",
        "MDL14", "MDR14",
        "MDL15", "MDR15"};
    final static int[] powersOfTwo = new int[muscleNeuronNames.length];

    static {
        for (int i = powersOfTwo.length - 1; i >= 0; i--) {
            powersOfTwo[i] = 1 << i;
        }
    }
    final static String networksPath = "src/networks/%s.net";

    static enum NET {

        c_elegans
    }

    public static void main(final String[] args) {
        final NeuralNet n = Parser.load(new File(String.format(networksPath, NET.c_elegans.name())), true);
//        System.out.println(n);
//        final Random rand = new Random();
        final int[][] bumpNeurons = new int[bumpNeuronNames.length][];
        final int[][] foodNeurons = new int[foodNeuronNames.length][];
        final int[][] muscleNeurons = new int[muscleNeuronNames.length][];
        for (int i = bumpNeurons.length - 1; i >= 0; i--) {
            bumpNeurons[i] = n.get(bumpNeuronNames[i]);
        }
        for (int i = foodNeurons.length - 1; i >= 0; i--) {
            foodNeurons[i] = n.get(foodNeuronNames[i]);
        }
        for (int i = muscleNeurons.length - 1; i >= 0; i--) {
            muscleNeurons[i] = n.get(muscleNeuronNames[i]);
        }
        for (int i = 0; i != 100000; i++) // start by saturating the network
        {
            for (final int[] foodNeuron : foodNeurons) {
                foodNeuron[n.getCurrState()] = NeuralNet.THRESHOLD;
            }
            n.step();
        }
//        final long startTime = System.currentTimeMillis();
//        int count = 0;
//        for (; count != 100000; count++) {
        for (;;) {
            n.step();
            final int currState = n.getCurrState();
            final int nextState = currState ^ 1;
            int IPInt = 0;
            for (int i = muscleNeurons.length - 1; i >= 0; i--) {
                final int[] muscleNeuron = muscleNeurons[i];
                final int currAccum = muscleNeuron[currState];
                if (currAccum != 0) {//NeuralNet.THRESHOLD) {
                    IPInt |= powersOfTwo[i];
                    IPInt *= currAccum;
                    muscleNeuron[nextState] = 0;
                }
            }
            if (IPInt != 0) {
                try {
//                    synchronized (lock) {
//                        totalIPs++;
//                    }
                    final InetAddress address = InetAddress.getByAddress(new byte[]{(byte) (IPInt >>> 24), (byte) (IPInt >>> 16), (byte) (IPInt >>> 8), (byte) IPInt});
//                    System.out.println(address);
//                    byte[] bytes = new byte[4];
//                    rand.nextBytes(bytes);
//                    final InetAddress address = InetAddress.getByAddress(bytes);
//                    final InetAddress address = InetAddress.getByAddress(new byte[]{(byte) 127, (byte) 0, (byte) 0, (byte) 1});
                    final Runnable runnable = () -> {
                        try {
                            if (address.isReachable(1000)) {
//                                synchronized (lock) {
//                                    goodIPs++;
                                System.out.println(address.getHostName());
//                                    System.out.println(String.format("success rate: %.10f%%", (double) 100 * goodIPs / totalIPs));
                                for (final int[] foodNeuron : foodNeurons) {
                                    foodNeuron[0] = foodNeuron[1] = NeuralNet.THRESHOLD;
                                }
//                                }
                            } else {
                                for (final int[] bumpNeuron : bumpNeurons) {
                                    bumpNeuron[0] = bumpNeuron[1] = 50;
                                }
//                                System.out.println(address + " NOT reachable!");
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(Driver.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    };
                    new Thread(runnable).start();
//                    final Process p = Runtime.getRuntime().exec(String.format(IPCOMMAND, (byte) IPInt >>> 24, (byte) IPInt >>> 16, (byte) IPInt >>> 8, (byte) IPInt));
//                    p.waitFor();
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        System.out.println(line + "\n");
//                    }
//                } catch (UnknownHostException ex) {
//                    Logger.getLogger(Driver.class.getName()).log(Level.SEVERE, null, ex);
//                } catch (IOException ex) {
//                    Logger.getLogger(Driver.class.getName()).log(Level.SEVERE, null, ex);
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(Driver.class.getName()).log(Level.SEVERE, null, ex);
//                }
                } catch (final UnknownHostException ex) {
                    Logger.getLogger(Driver.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
//            System.out.println(Integer.toBinaryString(IPInt));
//            if (IPInt != 0) {
//                System.out.println("\nMOVE (" + IPInt + ")");
//            }
//            try {
////                Thread.sleep(100);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(Driver.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
//        System.out.println("avg Hz: " + ((double) count / (System.currentTimeMillis() - startTime)));
    }
}
