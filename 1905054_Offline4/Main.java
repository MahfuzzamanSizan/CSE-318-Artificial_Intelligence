import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private DTnode root_node;

    public Main()
    {

        this.root_node = null;

    }

    private double entropy_calculation(List<Car> subset)
    {

        int total_instances = subset.size();

        double entropy = 0.0;


        Map<String, Long> class_count = subset.stream().collect(Collectors.groupingBy(Car::get_label, Collectors.counting()));



        for (String label : class_count.keySet())
        {
            double probability = (double) class_count.get(label) / total_instances;

            entropy = entropy - (probability * Math.log(probability)) ;

        }

        return entropy;
    }



    public double information_gain_calculation(List<Car> subset, String attribute_value)
    {

        double entropy_after_splitting = 0.0;

        double entropy_before_splitting = entropy_calculation(subset);

        Map<String, List<Car>> attribute_to_subset_map = subset.stream().collect(Collectors.groupingBy(instance -> instance.get_attribute(attribute_value)));



        for (String value : attribute_to_subset_map.keySet())
        {

            List<Car> subset_value = attribute_to_subset_map.get(value);

            double weight = (double) subset_value.size() / subset.size();

            entropy_after_splitting = entropy_after_splitting+ (weight * entropy_calculation(subset_value));

        }

        double gain = entropy_before_splitting - entropy_after_splitting;

        return gain;
    }



    public DTnode learn_decision_tree(List<Car> data, Set<String> attribute)
    {
        if (data.stream().map(Car::get_label).distinct().count() == 1)
        {
            DTnode leaf_node = new DTnode(data.get(0).get_label());

            leaf_node.set_data(data);

            return leaf_node;
        }

        if (attribute.isEmpty())
        {
            DTnode leaf_node = new DTnode(get_majority_class(data));

            leaf_node.set_data(data);

            return leaf_node;
        }


        // choosing best attribute

        double maximum_information_gain = Double.NEGATIVE_INFINITY;

        String str = null;



        for (String attribute_name : attribute)
        {
            double information_gain = information_gain_calculation(data, attribute_name);

            if (information_gain > maximum_information_gain)
            {
                maximum_information_gain = information_gain;

                str = attribute_name;

            }
        }

        String best_attribute = str;



        DTnode node = new DTnode(best_attribute);

        node.set_data(data);

        Map<String, List<Car>> attribute_to_subset_map = data.stream().collect(Collectors.groupingBy(instance -> instance.get_attribute(best_attribute)));



        for (String value : attribute_to_subset_map.keySet())
        {
            List<Car> subset = attribute_to_subset_map.get(value);

            Set<String> remaining_attributes = attribute.stream().filter(attr -> !attr.equals(best_attribute)).collect(Collectors.toSet());

            DTnode child_node = learn_decision_tree(subset, remaining_attributes);

            node.set_data(data);

            node.get_children().put(value, child_node);
        }


        return node;
    }


    private static String get_majority_class(List<Car> data)
    {
        long maximum_count = 0;

        String majority_class = null;

        Map<String, Long> class_count = data.stream().collect(Collectors.groupingBy(Car::get_label, Collectors.counting()));




        for (String label : class_count.keySet())
        {
            long count = class_count.get(label);

            if (count > maximum_count)
            {
                maximum_count = count;

                majority_class = label;
            }
        }

        return majority_class;
    }


    // recursive function call
    private String predict_method(Car car_instance, DTnode node)
    {
        if (node.get_class_label() != null)
        {
            return node.get_class_label();
        }

        String attribute_value = car_instance.get_attribute(node.get_attribute());

        DTnode next_node = node.get_children().get(attribute_value);

        if (next_node == null)
        {

            return get_majority_class(node.get_data());

        }

        String str = predict_method(car_instance, next_node);

        return str;

    }

    public double evaluate_method(List<Car> test_set, DTnode tree)

    {
        int correct_predictions = 0;

        for (Car car_instance : test_set) {

            String predicted_label = predict_method(car_instance, tree);

            String actual_label = car_instance.get_label();

            if (predicted_label != null && actual_label != null && predicted_label.equals(actual_label))
            {

                correct_predictions++;

            }
        }

        double result = (double) correct_predictions / test_set.size();

        return result;

    }



    private static double calculate_mean(List<Double> values)

    {
        double summation = 0.0;

        for (double value : values)
        {
            summation += value;
        }

        int no_of_values = values.size();

        double mean = summation / no_of_values;

        return mean;
    }

    private static double calculate_standard_deviation(List<Double> values)

    {
        double mean = calculate_mean(values);

        double sum_of_squared_differences = 0.0;

        for (double value : values)
        {
            double difference = value - mean;

            sum_of_squared_differences = sum_of_squared_differences + (difference * difference);
        }

        int no_of_values = values.size();

        double standard_deviation = Math.sqrt(sum_of_squared_differences / no_of_values);

        return standard_deviation;
    }






    public static void main(String[] args) throws IOException

    {
        List<Car> dataset1 = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader("car.data"));

        String line;


        while ((line = reader.readLine()) != null)
        {

            String[] Attribute_values = line.split(",");

            int no_of_attributes = 7;

            if (Attribute_values.length == no_of_attributes)
            {
                Car car_instance = new Car(Attribute_values[0], Attribute_values[1], Attribute_values[2], Attribute_values[3], Attribute_values[4], Attribute_values[5], Attribute_values[6]);

                dataset1.add(car_instance);
            }

        }

        System.out.println("\t" + dataset1.size() + " instances loaded----->");
        System.out.println();

        reader.close();

        List<Car> dataset = dataset1;



        List<Double> accuracies = new ArrayList<>();

        Set<String> attribute_name = Set.of("buying", "maint", "doors", "persons", "lug_boot", "safety");

        int num_of_experiments = 20;



        for (int i = 1; i <= num_of_experiments; i++)

        {
            Collections.shuffle(dataset);

            int training_size = (int) (0.8 * dataset.size());

            int total_size = dataset.size();

            List<Car> training_set = dataset.subList(0, training_size);

            List<Car> test_set = dataset.subList(training_size, total_size );


            Main tree = new Main();

            DTnode learned_tree = tree.learn_decision_tree(training_set, attribute_name);

            double accuracy = tree.evaluate_method(test_set, learned_tree);

            accuracies.add(accuracy);

        }

        double mean_accuracy = calculate_mean(accuracies);

        double standard_deviation = calculate_standard_deviation(accuracies);

        System.out.println("\tMean Accuracy: " + mean_accuracy);
        System.out.println();

        System.out.println("\tStandard Deviation of Accuracy: " + standard_deviation);
        System.out.println();

    }

}