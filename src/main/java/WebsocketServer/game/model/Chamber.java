package WebsocketServer.game.model;

import WebsocketServer.game.enums.FieldCategory;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Chamber {
    private final List<Field> fields;
    private final FieldCategory fieldCategory;
    public Chamber(FieldCategory fieldCategory) {
        this.fieldCategory = fieldCategory;
        fields = new ArrayList<>();
    }


    public Field getField(int index){
        if(index >= 0 && index<fields.size()){
            return fields.get(index);
        }else{
            throw new IndexOutOfBoundsException("Field not present");
        }
    }

    public  void  addField (Field field){
        if(field.getFieldCategory().equals(fieldCategory)){
            fields.add(field);
        }else {
            throw  new IllegalArgumentException("Field must have same Category as Chamber");
        }
    }

    public int getSize() {
        return fields.size();
    }
}
