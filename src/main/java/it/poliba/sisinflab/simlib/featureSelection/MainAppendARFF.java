/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.poliba.sisinflab.simlib.featureSelection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Corrado
 */
public class MainAppendARFF {
    
    public static void main(String[] args) throws IOException, Exception{
                   
            //File fp = new File("D:/cleanarff/append550.1.arff");
            //specifico il path relativo al primo file di append
            String pathfirst ="D:/cleanarff/append550.1.arff";
            //specifico path dei restanti file
            String pathfile ="D:/cleanarff/550.1/";
            //specifico gli id degli item di cui voglio fare l'append
            for(int i=1;i<865;i++)
            {
                String fileinput = pathfile+i+"igdatasetALLfilmprop.arff";
                if(new File(fileinput).exists()) 
                   {
                        List<String> instances = readFile(fileinput);
                        Path file = Paths.get(pathfirst);
                        Files.write(file, instances, Charset.forName("UTF-8"),StandardOpenOption.APPEND);
                        System.out.println("Appended file : "+fileinput);
                   }
            }
    }

    private static List<String> readFile(String filename)throws Exception
    {
        String line = null;
        List<String> records = new ArrayList<String>();

        BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
        do{
            line = bufferedReader.readLine();
        }while (!line.startsWith("@data"));

        while(line!=null){

            line = bufferedReader.readLine();
            if(line!=null)
            {
                records.add(line);
            }
        }
        bufferedReader.close();
        return records;
    }

}
