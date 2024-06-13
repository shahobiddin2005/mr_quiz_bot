package uz.app.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Test implements Cloneable {
    private String question;
    private List<Answer> answers;
    private String selectedAns = null;

    @Override
    public Test clone() throws CloneNotSupportedException {
        return (Test) super.clone();
    }
}
