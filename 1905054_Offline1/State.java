import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.ToIntFunction;

public class State implements Comparable<State> {

    short[][] array;

    int key = Integer.MAX_VALUE, moves = 0;

    State parent_state;


    static ToIntFunction<State> function = (x) -> hamming_func(x.array);;



    State(short[][] grid, char heuristicVar) {

        array = grid;


        switch (heuristicVar) {

            case 'h':
                function = (x) -> hamming_func(x.array);
                break;
            case 'm':
                function = (x) -> manhattan_func(x.array);
                break;


        }

    }


    private State(short[][] grid, State parent) {

        array = grid.clone();

        parent_state = parent;

        moves = parent.moves + 1;

        key = moves + function.applyAsInt(this);

        if(is_goal_state()) {

            System.out.println("Goal state with moves = " + moves + " and key  = " + key);

        }

    }



    public static State get_state(short[][] grid, char heuristic) {

        State state = new State(grid, heuristic);

        if (!state.is_solvable()) {

            return null;

        }

        return state;
    }



    public static short[][] copy_func(short[][] array) {

        int size = array.length;

        short[][] p = new short[size][size];

        for (int i = 0; i < size; i++) {

            for (int j = 0; j < size; j++)

                p[i][j] = array[i][j];
        }

        return p;

    }



    static int difference(int x, int y) {

        return (x > y) ? x - y : y - x;

    }



    static int manhattan_func(short[][] array) {

        int manhattan_distance = 0, k = array.length;

        for (int i = 0, position = 0; i < k; i++) {

            for (int j = 0; j < k && position < k * k; j++, position++) {

                int p = array[i][j] - 1;

                if (p == -1)
                    continue;

                manhattan_distance += difference(i, p / k);

                manhattan_distance += difference(j, p % k);

            }

        }

        return manhattan_distance;
    }



    static int hamming_func(short[][] array) {

        int hamming_distance = 0, k = array.length;

        for (int i = 0, position = 1; i < k; i++) {

            for (int j = 0; j < k && position < k * k; j++, position++) {

                if (array[i][j] != position)
                    hamming_distance++;

            }

        }

        return hamming_distance;
    }



    static boolean is_solvable(short[][] array) {

        int size = array.length, val = 0, inv = 0;

        for (int i = 0; i < size; i++) {

            for (int j = 0; j < size; j++) {

                if (array[i][j] == 0) {

                    val = i;

                }

            }

        }


        for (int i = 0; i < size; i++) {

            for (int j = 0; j < size; j++) {

                if (array[i][j] == 0)
                    continue;

                for (int m = i; m < size; m++) {

                    for (int n = (m == i) ? (j + 1) : 0; n < size; n++) {

                        if (array[m][n] != 0 && array[m][n] < array[i][j])
                            inv++;

                    }
                }
            }
        }


        if (size % 2 == 1)
            return inv % 2 == 0;

        return (inv + (size - val)) % 2 == 1;

    }



    public void print_grid() {

        System.out.println("Moves => " + moves);


        System.out.println("****----------****");

        for (int i = 0, k = array.length; i < k; i++) {

            for (int j = 0; j < k; j++) {

                System.out.printf("%4d ", array[i][j]);

            }

            System.out.println();

        }

        System.out.println("****----------****");
        System.out.println();
    }


    boolean is_goal_state() {

        return hamming() == 0;

    }

    public void print_path() {

        if (parent_state != null)
            parent_state.print_path();

        print_grid();
    }



    public Collection<State> get_adjacent_states() {

        int size = array.length, varA = -1, varB = -1;

        short temp;

        for (int i = 0; i < size; i++) {

            for (int j = 0; j < size; j++) {

                if (array[i][j] == 0) {

                    varA = i;
                    varB = j;

                }
            }
        }


        List<State> list = new LinkedList<>();

        if (varA != size - 1) {
            short[][] grid = copy_func(array);

            grid[varA + 1][varB] = 0;

            grid[varA][varB] = array[varA + 1][varB];

            list.add(new State(grid, this));

        }

        if (varA != 0) {

            short[][] grid = copy_func(array);

            grid[varA - 1][varB] = 0;

            grid[varA][varB] = array[varA - 1][varB];

            list.add(new State(grid, this));

        }


        if (varB != size - 1) {

            short[][] grid = copy_func(array);

            grid[varA][varB + 1] = 0;

            grid[varA][varB] = array[varA][varB + 1];

            list.add(new State(grid, this));

        }


        if (varB != 0) {
            short[][] grid = copy_func(array);

            grid[varA][varB - 1] = 0;

            grid[varA][varB] = array[varA][varB - 1];

            list.add(new State(grid, this));

        }

        return list;

    }



    boolean is_solvable() {

        return is_solvable(array);

    }


    @Override
    public int hashCode() {

        return Arrays.deepHashCode(array);

    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof State) {

            short[][] v = ((State) obj).array;

            for (int i = 0; i < array.length; i++) {

                for (int j = 0; j < array.length; j++) {

                    if (array[i][j] != v[i][j])
                        return false;

                }

            }

            return true;

        }

        return false;
    }

    @Override
    public int compareTo(State o) {

        return Integer.compare(key, o.key);

    }



    int hamming() {

        return hamming_func(array);

    }


}

