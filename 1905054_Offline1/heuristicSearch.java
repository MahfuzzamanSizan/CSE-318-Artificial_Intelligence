import java.io.*;
import java.util.*;



public class heuristicSearch{

    HashSet<State> close_set = new HashSet<>();

    HashMap<State, Integer> open_set = new HashMap<>();

    Queue<State>  queue =  new PriorityQueue<>(State::compareTo);


    int explored_node, expanded_node, var=0;

    State start_state;

    public heuristicSearch(State start_state) {

        this.start_state = start_state;

        expanded_node = 0;

        explored_node= 0;

    }

    public static void main(String[] args) throws IOException {


        try (Scanner scn = new Scanner(System.in)) {

            System.out.println("Give the size of the grid : ");


            short size = Short.parseShort(scn.nextLine());


            short[][] grid = new short[size][size];

            String str;

            System.out.println("Give the start configuration : ");

            for (int i = 0; i < size; i++) {

                for (int j = 0; j < size; j++) {

                    str = scn.next();

                    grid[i][j] = (str.equals("*")) ? 0 : Short.valueOf(str);

                }

                scn.nextLine();

            }


            for(String p : new String[]{"Manhattan", "Hamming"}) {

                System.out.println(p);

                State state = State.get_state(grid, p.charAt(0));

                if (state != null) {

                    System.out.println();

                    //System.out.println("The board is solvable.");

                    heuristicSearch obj = new heuristicSearch(state);

                    State goal = obj.run_func();

                    System.out.println("Minimum number of moves: " + goal.moves);

                    System.out.println();

                    //System.out.println("# explored nodes: " + obj.explored_node);

                    //System.out.println("# expanded nodes: " + obj.expanded_node);

                    goal.print_path();

                }

                else {

                    System.out.println("Unsolvable puzzle");

                }

                System.out.println();

            }

        }

        catch (OutOfMemoryError e) {

            System.out.println(Runtime.getRuntime().totalMemory());

        }


    }



    public State run_func() {

        queue.clear();

        close_set.clear();

        queue.offer(start_state);

        State new_state;

        int distance=0;

        while (!queue.isEmpty()) {

            new_state = queue.poll();

            if(new_state.is_goal_state())
            {

                return new_state;

            }

            if(!close_set.contains(new_state)) {

                expanded_node++;

                close_set.add(new_state);

                Collection<State> obj = new_state.get_adjacent_states();

                for(State v:obj){

                    if(close_set.contains(v)) {

                        var++;

                        continue;

                    }

                    int value = open_set.getOrDefault(v, -5);

                    if(value == -5){

                        queue.add(v);

                        open_set.put(v, v.key);

                        explored_node++;


                    }

                    else {

                        if(value > v.key) {

                            queue.add(v);

                            open_set.put(v, v.key);

                        }
                    }
                }
            }
        }


        return null;

    }



}
