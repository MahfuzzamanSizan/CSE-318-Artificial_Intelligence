import java.util.*;


public class Graph {

    private long V, E;
    private long maxCut;
    private long simpleLocalIterations;
    private Set<Long> X, Y; // Partitions
    private List<Pair<Long, Long>>[] adj;
    private List<Long> randomMaxCutValues;
    private List<Long> simpleGreedyMaxCutValues;
    private List<Long> semiGreedyMaxCutValues;
    private List<Long> localSearchValues;

    private long maxIterations = 20;

    public Edges[] edges;


    public void printValues() {
        System.out.println("Number of vertices: " + V);
        System.out.println("Number of edges: " + E);
        System.out.println("Simple randomized: " + getRandomAverage());
        System.out.println("Simple greedy: " + getSimpleGreedyAverage());
        System.out.println("Semi greedy: " + getSemiGreedyAverage());
        System.out.println("Simple local iteration: " + getSimpleLocalIterations());
        System.out.println("Simple local average: " + getSimpleLocalAverage());
        System.out.println("GRASP iteration: " + maxIterations);
        System.out.println("GRASP best value: " + maxCut);
    }


    public Graph(long V, long E) {
        this.V = V;
        this.E = E;
        this.adj = new ArrayList[(int) V];
        for (int i = 0; i < V; i++) {
            this.adj[i] = new ArrayList<>();
        }
        this.edges = new Edges[(int) E];
        this.simpleLocalIterations = 0;

        this.X = new HashSet<>();
        this.Y = new HashSet<>();

        this.randomMaxCutValues = new ArrayList<>();
        this.simpleGreedyMaxCutValues = new ArrayList<>();
        this.semiGreedyMaxCutValues = new ArrayList<>();
        this.localSearchValues = new ArrayList<>();
    }

    public void addEdge(long u, long v, long w) {
        adj[(int) u].add(new Pair<>(v, w));
    }


    public long calculateMaxCut() {
        long cutWeight = 0;
        for (int i = 0; i < E; i++) {
            long src = edges[i].getSrc();
            long dest = edges[i].getDest();

            if ((X.contains(src) && Y.contains(dest)) || (X.contains(dest) && Y.contains(src))) {
                cutWeight += edges[i].getWeight();
            }
        }
        return cutWeight;
    }

    public void semiGreedyMaxCut() {
        Random random = new Random();
        double a = random.nextDouble();
        greedyMaxCut(a);
        long cutWeight = calculateMaxCut();
        semiGreedyMaxCutValues.add(cutWeight);
    }

    public void randomMaxCut() {
        greedyMaxCut(0.0);
        long cutWeight = calculateMaxCut();
        randomMaxCutValues.add(cutWeight);
    }

    public void simpleGreedyMaxCut() {
        greedyMaxCut(1.0);
        long cutWeight = calculateMaxCut();
        simpleGreedyMaxCutValues.add(cutWeight);
    }

    public void greedyMaxCut(double alpha) {
        X.clear();
        Y.clear();
        double a = alpha;
        long wmin = Long.MAX_VALUE;
        long wmax = Long.MIN_VALUE;

        for (int i = 0; i < E; i++) {
            long weight = edges[i].getWeight();
            if (weight < wmin) {
                wmin = weight;
            }
            if (weight > wmax) {
                wmax = weight;
            }
        }

        double u = wmin + a * (wmax - wmin);
        List<Long> RCLeIndices = new Vector<>();

        for (int i = 0; i < E; i++) {
            if (edges[i].getWeight() >= u) {
                RCLeIndices.add((long) i);
            }
        }

        Random random = new Random();
        int randomIndex = random.nextInt(RCLeIndices.size());
        long randomEdgeIndex = RCLeIndices.get(randomIndex);
        long randomEdgeSrc = edges[(int) randomEdgeIndex].getSrc();
        long randomEdgeDest = edges[(int) randomEdgeIndex].getDest();
        X.add(randomEdgeSrc);
        Y.add(randomEdgeDest);

        Set<Long> VPrime = new HashSet<>();

        while (X.size() + Y.size() != V) {
            VPrime.clear();

            for (long i = 0; i < V; i++) {
                if (!X.contains(i) && !Y.contains(i)) {
                    VPrime.add(i);
                }
            }

            long sumX = 0;
            long sumY = 0;
            List<Pair<Long, Long>> sx = new Vector<>();
            List<Pair<Long, Long>> sy = new Vector<>();

            for (long v : VPrime) {
                for (Pair<Long, Long> it : adj[(int) v]) {
                    if (X.contains(it.first)) {
                        sumY += it.second;
                    }
                    if (Y.contains(it.first)) {
                        sumX += it.second;
                    }
                }
                sx.add(new Pair<>(v, sumX));
                sy.add(new Pair<>(v, sumY));
                sumX = 0;
                sumY = 0;
            }

            long maxSX = Long.MIN_VALUE;
            long maxSY = Long.MIN_VALUE;
            long maxSXIndex = -1;
            long maxSYIndex = -1;

            for (int i = 0; i < sx.size(); i++) {
                if (sx.get(i).second > maxSX) {
                    maxSX = sx.get(i).second;
                    maxSXIndex = i;
                }
                if (sy.get(i).second > maxSY) {
                    maxSY = sy.get(i).second;
                    maxSYIndex = i;
                }
            }

            long minSX = Long.MAX_VALUE;
            long minSY = Long.MAX_VALUE;
            long minSXIndex = -1;
            long minSYIndex = -1;

            for (int i = 0; i < sx.size(); i++) {
                if (sx.get(i).second < minSX) {
                    minSX = sx.get(i).second;
                    minSXIndex = i;
                }
                if (sy.get(i).second < minSY) {
                    minSY = sy.get(i).second;
                    minSYIndex = i;
                }
            }

            wmin = Math.min(minSX, minSY);
            wmax = Math.max(maxSX, maxSY);
            u = wmin + a * (wmax - wmin);
            List<Long> RCLvIndices = new Vector<>();

            for (long v : VPrime) {
                long sxv = 0;
                long syv = 0;

                for (int i = 0; i < sx.size(); i++) {
                    if (sx.get(i).first == v) {
                        sxv = sx.get(i).second;
                    }
                    if (sy.get(i).first == v) {
                        syv = sy.get(i).second;
                    }
                }

                long maxsv = Math.max(sxv, syv);

                if (maxsv >= u) {
                    RCLvIndices.add(v);
                }
            }

            if (RCLvIndices.size() == 0) {
                continue;
            }

            randomIndex = random.nextInt(RCLvIndices.size());
            long randomVIndex = RCLvIndices.get(randomIndex);
            long randomV = randomVIndex;

            if (getSumX(randomV) > getSumY(randomV)) {
                X.add(randomV);
            } else {
                Y.add(randomV);
            }
        }
    }


    public void localSearch() {
        boolean change = true;
        while (change) {
            change = false;
            simpleLocalIterations++;
            for (long i = 0; i < V; i++) {
                if (change) break;
                if (X.contains(i)) {
                    if (getSumX(i) < getSumY(i)) {
                        X.remove(i);
                        Y.add(i);
                        change = true;
                    }
                } else {
                    if (getSumX(i) > getSumY(i)) {
                        Y.remove(i);
                        X.add(i);
                        change = true;
                    }
                }
            }
        }
        localSearchValues.add(calculateMaxCut());
    }


    public long getSimpleLocalIterations() {
        return simpleLocalIterations / maxIterations;
    }

    public long getSimpleLocalAverage() {
        long average = 0;
        long size = localSearchValues.size();

        for (long i = 0; i < size; i++) {
            average += localSearchValues.get((int) i);
        }

        average /= size;
        return average;
    }

    public long getRandomAverage() {
        long average = 0;
        long size = randomMaxCutValues.size();

        for (long i = 0; i < size; i++) {
            average += randomMaxCutValues.get((int) i);
        }

        average /= size;
        return average;
    }

    public long getSimpleGreedyAverage() {
        long average = 0;
        long size = simpleGreedyMaxCutValues.size();

        for (long i = 0; i < size; i++) {
            average += simpleGreedyMaxCutValues.get((int) i);
        }

        average /= size;
        return average;
    }

    public long getSemiGreedyAverage() {
        long average = 0;
        long size = semiGreedyMaxCutValues.size();

        for (long i = 0; i < size; i++) {
            average += semiGreedyMaxCutValues.get((int) i);
        }

        average /= size;
        return average;
    }

    
    public void GRASP() {
        maxCut = Long.MIN_VALUE;

        for (long i = 0; i < maxIterations; i++) {
            randomMaxCut();

            if (i == 0) {
                simpleGreedyMaxCut();
            }

            semiGreedyMaxCut();
            localSearch();

            if (i == 0) {
                maxCut = calculateMaxCut();
            } else {
                long cutWeight = calculateMaxCut();
                if (cutWeight > maxCut) {
                    maxCut = cutWeight;
                }
            }
        }
    }


    public long getSumX(long v) {
        long sumX = 0;
        for (Pair<Long, Long> edge : adj[(int) v]) {
            if (Y.contains(edge.first)) {
                sumX += edge.second;
            }
        }
        return sumX;
    }

    public long getSumY(long v) {
        long sumY = 0;
        for (Pair<Long, Long> edge : adj[(int) v]) {
            if (X.contains(edge.first)) {
                sumY += edge.second;
            }
        }
        return sumY;
    }


    class Pair<T, U> {
        T first;
        U second;

        public Pair(T first, U second) {
            this.first = first;
            this.second = second;
        }
    }


}


