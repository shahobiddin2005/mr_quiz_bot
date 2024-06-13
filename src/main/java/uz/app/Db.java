package uz.app;

import uz.app.entity.Answer;
import uz.app.entity.Test;

import java.util.ArrayList;

public class Db {
    public ArrayList<Test> tests = new ArrayList<>();

    public void adds(){
        ArrayList<Answer> ans1 = new ArrayList<>();
        ans1.add(new Answer("3", false));
        ans1.add(new Answer("4", true));
        ans1.add(new Answer("5", false));
        ans1.add(new Answer("6", false));
        Test test = new Test("2x2=?", ans1, null);

        tests.add(test);

        ArrayList<Answer> ans2 = new ArrayList<>();
        ans2.add(new Answer("3", false));
        ans2.add(new Answer("4", false));
        ans2.add(new Answer("5", false));
        ans2.add(new Answer("8", true));
        Test test2 = new Test("2x4=?", ans2, null);

        tests.add(test2);

        ArrayList<Answer> ans3 = new ArrayList<>();
        ans3.add(new Answer("3", false));
        ans3.add(new Answer("4", false));
        ans3.add(new Answer("6", true));
        ans3.add(new Answer("5", false));
        Test test3 = new Test("3x2=?", ans3, null);

        tests.add(test3);

        ArrayList<Answer> ans4 = new ArrayList<>();
        ans4.add(new Answer("12", true));
        ans4.add(new Answer("41", false));
        ans4.add(new Answer("52", false));
        ans4.add(new Answer("18", false));
        Test test4 = new Test("3x4=?", ans4, null);

        tests.add(test4);

    }
}
