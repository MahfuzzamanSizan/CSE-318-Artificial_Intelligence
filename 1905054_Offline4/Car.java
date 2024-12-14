import java.util.*;
class Car {
    private String buying;
    private String maint;
    private String doors;
    private String persons;
    private String lug_boot;
    private String safety;
    private String label;


    public String get_label()
    {

        return label;

    }



    public String get_attribute(String attribute_name)

    {
        if(attribute_name=="buying") {

            return buying;
            
        }
        else if (attribute_name=="maint") {

            return maint;
            
        } 
        else if (attribute_name=="doors") {

            return doors;
            
        } 
        else if (attribute_name=="persons") {

            return persons;
            
        } 
        else if (attribute_name=="lug_boot") {

            return lug_boot;
            
        } 
        else if (attribute_name=="safety") {

            return safety;
            
        }

        else return null;

    }
    public Car(String buying, String maint, String doors, String persons, String lug_boot, String safety, String label)
    {
        this.buying = buying;

        this.maint = maint;

        this.doors = doors;

        this.persons = persons;

        this.lug_boot = lug_boot;

        this.safety = safety;

        this.label = label;
    }
}

class DTnode {

    private String class_label;
    private String attribute_name;
    private Map<String, DTnode> child_node;
    private List<Car> data;

    public DTnode(String attribute_name)
    {

        this.class_label = null;

        this.attribute_name = attribute_name;

        this.child_node = new HashMap<>();

    }


    public String get_class_label()
    {

        return class_label;

    }

    public String get_attribute()
    {

        return attribute_name;

    }

    public Map<String, DTnode> get_children()
    {

        return child_node;


    }
    public void set_data(List<Car> data)
    {

        this.data = data;

    }
    public List<Car> get_data()
    {

        return data;

    }


}

