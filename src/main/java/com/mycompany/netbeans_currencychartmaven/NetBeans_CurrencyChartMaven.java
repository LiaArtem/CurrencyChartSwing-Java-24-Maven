/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package com.mycompany.netbeans_currencychartmaven;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.time.*;
import java.util.*;
import javax.swing.JOptionPane;
import com.google.gson.stream.JsonReader;
import java.nio.charset.StandardCharsets;
/**
 *
 * @author Admin
 */
public class NetBeans_CurrencyChartMaven {

    private static final String tec_kat_curs = new File("").getAbsolutePath() + File.separator + "temp";
    
    public static void main(String[] args) {
        // Чтение курсов валют в массив                
        Curr_chart frame = new Curr_chart();
        frame.setLocationRelativeTo(null);        
        frame.setResizable(false);               
        frame.setVisible(true);                                         
    }
    
// Получить курс НБУ (С сайта JSON)
    public static String [][] getKursNbu(String mCurrCode, LocalDate [] mDate1, LocalDate [] mDate2)
    {
        String mPath = tec_kat_curs + File.separator + mCurrCode;
        File file = null;
        String p_name_date = ""; String p_name_rate = ""; String p_name_curs = "";
        double m_rate = 100;
        String [][] mArray = new String[2][];
        ArrayList<String> mArray_Date = new ArrayList<>();
        ArrayList<String> mArray_Curs = new ArrayList<>();

        ////////////////////////////////////////////////////////////////////////////////////
        // https://bank.gov.ua/control/uk/curmetal/currency/search/form/period
        // проверяем существование каталога, елс его нет создаем его
        boolean mkdirs_result = new File(mPath).mkdirs();
        if (!mkdirs_result) {
            System.out.println(mPath + " Каталог уже существует");
        }
        // ищем файлы с расширением json
        File dir = new File(mPath);
        File[] matchingFiles = dir.listFiles((dir1, name) -> name.endsWith("json"));
        // берем файл последний из списка
        assert matchingFiles != null;
        for (File file_row:matchingFiles) {
            file = file_row;
        }

        if (file == null) {
            MessageBoxError(mPath , "Не найден файл *.json в каталоге - загрузить с https://bank.gov.ua/control/uk/curmetal/currency/search/form/period");
        }

        assert file != null;
        if (!file.exists()) {
            MessageBoxError(mPath, "Не найден файл *.json в каталоге - загрузить с https://bank.gov.ua/control/uk/curmetal/currency/search/form/period");
        }

        // json
        //Official hrivnya exchange rates.json - en
        //Офіційний курс гривні щодо іноземних валют.json - uk
        String m_file_name = file.getName();
        if (m_file_name.equals("Official hrivnya exchange rates.json")) { p_name_date = "Date"; p_name_rate = "Unit"; p_name_curs = "Official hrivnya exchange rates, UAH"; }
        else if (m_file_name.equals("Офіційний курс гривні щодо іноземних валют.json")) { p_name_date = "Дата"; p_name_rate = "Кількість одиниць"; p_name_curs = "Офіційний курс гривні, грн"; }

        if (file.isFile()) {
            try {
                List<String> listDate = new ArrayList<>();
                List<Double> listCurs = new ArrayList<>();
                try (JsonReader reader = new JsonReader(new FileReader(file.getPath()))) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        reader.beginObject();
                        while (reader.hasNext()) {
                            String name = reader.nextName();
                            String name_conv = new String(name.getBytes(), StandardCharsets.UTF_8);
                            if (name_conv.equals(p_name_date)) {
                                listDate.add(reader.nextString());      // дата
                            } else if (name_conv.equals(p_name_rate)) {        // количество
                                m_rate = reader.nextDouble();
                            } else if (name_conv.equals(p_name_curs)) {        // курс
                                listCurs.add(reader.nextDouble() / m_rate);
                            } else {
                                reader.skipValue();
                            }
                        }
                        reader.endObject();
                    }
                    reader.endArray();
                }

                // после получения списков, заполняем массив нужными данными
                for (int i = 0; i < mDate1.length; i++) {
                    int num_list = 0;
                    for (String list : listDate) {
                        LocalDate list_date = getDateString(list);
                        if (list_date.compareTo(mDate1[i]) >= 0 && list_date.compareTo(mDate2[i]) <= 0) {
                            // преобразование DD.MM.YYYY в YYYYMMDD
                            list = list.substring(6, 10) + list.substring(3, 5) + list.substring(0, 2);
                            mArray_Date.add(list);
                            mArray_Curs.add(listCurs.get(num_list).toString());
                        }
                        num_list++;
                    }
                }

                if (mArray_Date.isEmpty())
                {
                    MessageBoxError("Курсы из файла \n" +  m_file_name + "\n не загружены, возможно изменилась структура", "");
                }

                // переносим в общий массив
                mArray[0] = mArray_Date.toArray(String[]::new);
                mArray[1] = mArray_Curs.toArray(String[]::new);

            } catch (IOException e) {
                MessageBoxError(e.toString(), "");
            }
        }
        return mArray;
    }
     
    // вывод диалогового окна
    public static void MessageBoxError(String infoMessage, String titleBar)
    {
       JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.ERROR_MESSAGE);
    } 
    
    // проверка значения - float
    public static boolean checkString_Float(String string) 
    {
       String string_new  = string.replace(",", ".");
       if (string_new.isEmpty() == true) { return false; }
       try {               
           Float.valueOf(string_new);
       } catch (NumberFormatException e) {
       return false;
       }
       return true;
    }    

    // Валидация даты
    public static boolean isDateValid(String m_date, String format)
    {
        if (m_date.isEmpty()) { return false; }
        try {
            DateTimeFormatter f = DateTimeFormatter.ofPattern ( format );
            LocalDate.parse ( m_date , f );
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // Преобразование текста в дату
    public static LocalDate getDateString(String m_date, String format)
    {
        if (!isDateValid(m_date, format)) { return null; }
        DateTimeFormatter f = DateTimeFormatter.ofPattern ( format );
        return LocalDate.parse ( m_date , f );
    }

    // Преобразование текста в дату
    public static LocalDate getDateString(String m_date)
    {
        return getDateString(m_date, "dd.MM.yyyy");
    }
        
}
