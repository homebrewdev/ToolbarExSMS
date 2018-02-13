package com.example.toolbarex;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.view.View;
import android.widget.TextView;
import java.lang.String;

import java.io.IOException;
import java.util.Random;

/**
 * Created by Riff on 09.06.2016.
 */
public class WordActivity extends AppCompatActivity {

    private EditText generatedWordLength; //поле для ввода числа букв в генерируемом слове
    private TextView generatedWord; //поле для выводимого на экран сгенерированного слова
    String generatedWordString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);

        generatedWordLength = (EditText) findViewById(R.id.editTextword);
        generatedWord = (TextView) findViewById(R.id.textGenerateWordView);

    }

    // обработчик нажатия кнопки - Сгенерировать слово, с проверкой
    // что пользователь ввел слово в диапазоне от 1 до 10 пар букв
    public void onclick(View view) throws IOException {
        switch (view.getId()) {
            case R.id.btnGenerateWord:
                //вызываем метод генерации слова
                if ((generatedWordLength.getText().length() == 0) ||
                        (Integer.valueOf(generatedWordLength.getText().toString()) > 10)) {
                    //выводим предупреждение, чтобы ввели число в диапазоне от 1 до 10
                    generatedWord.setText(R.string.WarningEmptyNumLengthOfWord);
                } else {
                    //ну если проверка прошла и все хорошо, то выводим-генерируем слово
                generatedWord.setText(generateWord());
                break;}
        }
    }

    //пишем тело метода генерации слова из случайных согласных и гласных букв
    // словарь согласных и гласный прописан в ресурсе строк r.string soglas и glasniy
    private String generateWord () {

        generatedWordString = ""; //инициализируем пустую
        //создаем генератор случайных слов
        //задаем начальные параметры генератора
        Random rand = new Random(System.currentTimeMillis());

        //делаем цикл который будет брать случайным образом букву поочередно из словаря согласных и гласных
        // и составлять генерируемое уникальное слово
        int generatedWordLengthNum = Integer.valueOf(generatedWordLength.getText().toString());

        //вытаскиваем строку со словарем согласных из ресурса r.string
        String soglas = getResources().getString(R.string.soglas);
        //вытаскиваем строку со словарем гласных из ресурса r.string
        String glasniy = getResources().getString(R.string.glasniy);

        //цикл генерации согласная+гласная, за 1 цикл формируется пара : согласная + гласная
        for (int i = 0; i < generatedWordLengthNum ; i++) {

            //генерируем индекс случайным образом
            int generatedSymbolIndx = rand.nextInt(soglas.length());
            char charSoglas = soglas.charAt(generatedSymbolIndx); // и берем случайную согласную букву

            //еще раз генерируем индекс случайным образом
            generatedSymbolIndx = rand.nextInt(glasniy.length());
            char charGlasniy = glasniy.charAt(generatedSymbolIndx);// и берем случайную гласную букву

            //складываем пару : согласная + гласная и добавляем к финальной строке
            generatedWordString = generatedWordString + charSoglas + charGlasniy;
        }
        return generatedWordString;
    }
}
