package uz.app;

import uz.app.entity.Answer;
import uz.app.entity.Test;

import java.util.ArrayList;
import java.util.List;

public class Db {
    public ArrayList<Test> tests = new ArrayList<>();

    public void adds(){
        ArrayList<Answer> ans1 = new ArrayList<>();
        ans1.add(new Answer("Bu oddiy massiv", false));
        ans1.add(new Answer("Ma`lumotlarni saqlash uchun struktura", true));
        ans1.add(new Answer("Javada arrayList mavjud emas", false));
        ans1.add(new Answer("javoblar to`g`ri", false));
        Test test = new Test("ArrayList nima?", ans1, null);

        tests.add(test);

        ArrayList<Answer> ans2 = new ArrayList<>();
        ans2.add(new Answer("Barchasi to`gri", false));
        ans2.add(new Answer("Avtomobil zapchasti ", false));
        ans2.add(new Answer("Kirmashina", false));
        ans2.add(new Answer("ByteCode o`qidigan qurilma", true));
        Test test2 = new Test("JVM nima?", ans2, null);

        tests.add(test2);

        ArrayList<Answer> ans3 = new ArrayList<>();
        ans3.add(new Answer("Ma`lumot saqlanmaydi", false));
        ans3.add(new Answer("Oddiy massivdaqa saqlanadi", false));
        ans3.add(new Answer("Key va value qiymatlari orqali saqlanadi", true));
        ans3.add(new Answer("Barcha javoblar to`g`ri", false));
        Test test3 = new Test("Hashmapda ma`lumot qanday saqlanadi?", ans3, null);

        tests.add(test3);

        ArrayList<Answer> ans4 = new ArrayList<>();
        ans4.add(new Answer("Java Development Kit", true));
        ans4.add(new Answer("bytcode o`qidigan qurilma", false));
        ans4.add(new Answer("Bu freymwork", false));
        ans4.add(new Answer("MultiThreadingga doir narsa", false));
        Test test4 = new Test("JDK nima?", ans4, null);

        tests.add(test4);

        ArrayList<Answer> ans5 = new ArrayList<>();
        ans5.add(new Answer("Bo`sh massiv", false));
        ans5.add(new Answer("Qatorlar soni berilib ustunlari soni o`zgaruvchi massiv", true));
        ans5.add(new Answer("Uzunligi katta bo`lgan massiv", false));
        ans5.add(new Answer("javoblar to`gri", false));
        Test test5 = new Test("jagged array nima?", ans5, null);

        tests.add(test5);

        ArrayList<Answer> ans6 = new ArrayList<>();
        ans6.add(new Answer("Barcha javoblar to`gri", false));
        ans6.add(new Answer("Database bu", false));
        ans6.add(new Answer("Collection framework bu", false));
        ans6.add(new Answer("Virtual protsessor", true));
        Test test6 = new Test("Thread nima?", ans6, null);

        tests.add(test6);

        ArrayList<Answer> ans7 = new ArrayList<>();
        ans7.add(new Answer("Bu basseyn", false));
        ans7.add(new Answer("To`g`ri javob yo`q", false));
        ans7.add(new Answer("threadlarni o`zida saqlovchi pool", true));
        ans7.add(new Answer("Barcha javoblar to`g`ri", false));
        Test test7 = new Test("ThreadPool nima?", ans7, null);

        tests.add(test7);

        ArrayList<Answer> ans8 = new ArrayList<>();
        ans8.add(new Answer("legacy collection ichidagi klass", true));
        ans8.add(new Answer("to`g`ri javob yo`q", false));
        ans8.add(new Answer("barcha javoblar to`gri", false));
        ans8.add(new Answer("JDK ning komponentasi", false));
        Test test8 = new Test("Properties nima?", ans8, null);

        tests.add(test8);

        ArrayList<Answer> ans9 = new ArrayList<>();
        ans9.add(new Answer("String mutable", false));
        ans9.add(new Answer("Ha to`g`ri", true));
        ans9.add(new Answer("Yo`q unday emas", false));
        ans9.add(new Answer("To`gri javob yo`q", false));
        Test test9 = new Test("String immutable mi?", ans9, null);

        tests.add(test9);

        ArrayList<Answer> ans10 = new ArrayList<>();
        ans10.add(new Answer("Aloqa tizimga aloqador termin", false));
        ans10.add(new Answer("hashcode yasab beradi", false));
        ans10.add(new Answer("Barcha javoblar to`g`ri", false));
        ans10.add(new Answer("Ma`lumotlarni saqlash uchun struktura", true));
        Test test10 = new Test("Hashset nima?", ans10, null);

        tests.add(test10);
    }
}
