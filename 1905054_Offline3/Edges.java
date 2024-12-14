public class Edges {
    private long src;
    private long dest;
    private long weight;

    public Edges(long src, long dest, long weight) {
        this.src = src;
        this.dest = dest;
        this.weight = weight;
    }

    public long getSrc() {
        return src;
    }

    public long getDest() {
        return dest;
    }

    public long getWeight() {
        return weight;
    }
}

